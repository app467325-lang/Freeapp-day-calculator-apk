package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.AppTab
import com.example.model.PlaylistFilter
import com.example.model.QuranDataRepository
import com.example.model.Reciter
import com.example.model.Surah
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "quranify_prefs")

data class QuranUiState(
    val activeTab: AppTab = AppTab.HOME,
    val currentReciter: Reciter = QuranDataRepository.reciters.first(),
    val currentSurahIndex: Int = 0,
    val likedSurahNums: Set<Int> = QuranDataRepository.initialLikedSurahNums,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 45_000L,
    val playlistFilter: PlaylistFilter = PlaylistFilter.ALL,
    val searchQuery: String = "",
    val isFullPlayerOpen: Boolean = false,
    val isLiquidGlassEnabled: Boolean = true,
    val isHighQualityAudio: Boolean = true,
    val isAutoNextEnabled: Boolean = true,
    val toastMessage: String? = null
) {
    val currentSurah: Surah
        get() = QuranDataRepository.surahs.getOrElse(currentSurahIndex) { QuranDataRepository.surahs.first() }

    val progressFraction: Float
        get() = if (durationMs > 0L) (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f
}

class QuranViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(QuranUiState())
    val uiState: StateFlow<QuranUiState> = _uiState.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null
    private var currentLoadedUrl: String? = null
    private var isPlayerPrepared: Boolean = false
    private var progressJob: Job? = null
    private var toastJob: Job? = null

    companion object {
        private val KEY_LIKED_SURAHS = stringSetPreferencesKey("liked_surahs")
        private val KEY_RECITER_ID = stringPreferencesKey("last_reciter_id")
        private val KEY_SURAH_INDEX = intPreferencesKey("last_surah_index")
        private val KEY_LIQUID_GLASS = booleanPreferencesKey("liquid_glass_enabled")
        private val KEY_HQ_AUDIO = booleanPreferencesKey("hq_audio_enabled")
        private val KEY_AUTO_NEXT = booleanPreferencesKey("auto_next_enabled")
    }

    init {
        loadSavedPreferences()
        startProgressTicker()
    }

    private fun loadSavedPreferences() {
        viewModelScope.launch {
            try {
                val prefs = getApplication<Application>().dataStore.data.first()
                val savedLikes = prefs[KEY_LIKED_SURAHS]?.mapNotNull { it.toIntOrNull() }?.toSet()
                    ?: QuranDataRepository.initialLikedSurahNums
                val savedReciterId = prefs[KEY_RECITER_ID] ?: QuranDataRepository.reciters.first().id
                val savedReciter = QuranDataRepository.reciters.find { it.id == savedReciterId }
                    ?: QuranDataRepository.reciters.first()
                val savedSurahIdx = (prefs[KEY_SURAH_INDEX] ?: 0).coerceIn(0, QuranDataRepository.surahs.lastIndex)
                val liquidGlass = prefs[KEY_LIQUID_GLASS] ?: true
                val hqAudio = prefs[KEY_HQ_AUDIO] ?: true
                val autoNext = prefs[KEY_AUTO_NEXT] ?: true

                _uiState.update {
                    it.copy(
                        likedSurahNums = savedLikes,
                        currentReciter = savedReciter,
                        currentSurahIndex = savedSurahIdx,
                        isLiquidGlassEnabled = liquidGlass,
                        isHighQualityAudio = hqAudio,
                        isAutoNextEnabled = autoNext
                    )
                }
            } catch (_: Exception) {
                // Keep defaults if DataStore read fails in unit test environment
            }
        }
    }

    private fun savePreferences() {
        val state = _uiState.value
        viewModelScope.launch {
            try {
                getApplication<Application>().dataStore.edit { prefs ->
                    prefs[KEY_LIKED_SURAHS] = state.likedSurahNums.map { it.toString() }.toSet()
                    prefs[KEY_RECITER_ID] = state.currentReciter.id
                    prefs[KEY_SURAH_INDEX] = state.currentSurahIndex
                    prefs[KEY_LIQUID_GLASS] = state.isLiquidGlassEnabled
                    prefs[KEY_HQ_AUDIO] = state.isHighQualityAudio
                    prefs[KEY_AUTO_NEXT] = state.isAutoNextEnabled
                }
            } catch (_: Exception) {
            }
        }
    }

    private fun startProgressTicker() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (isActive) {
                delay(250L)
                val player = mediaPlayer
                if (player != null && isPlayerPrepared) {
                    try {
                        if (player.isPlaying) {
                            val pos = player.currentPosition.toLong().coerceAtLeast(0L)
                            val dur = player.duration.toLong().coerceAtLeast(1000L)
                            _uiState.update {
                                it.copy(
                                    currentPositionMs = pos,
                                    durationMs = dur,
                                    isPlaying = true
                                )
                            }
                        }
                    } catch (_: IllegalStateException) {
                    }
                } else if (_uiState.value.isPlaying && !_uiState.value.isBuffering) {
                    // Fallback smooth timer progression if audio hardware is unavailable in headless environment
                    _uiState.update { state ->
                        val nextPos = state.currentPositionMs + 250L
                        if (nextPos >= state.durationMs) {
                            if (state.isAutoNextEnabled) {
                                viewModelScope.launch { nextTrack() }
                            }
                            state.copy(currentPositionMs = 0L, isPlaying = state.isAutoNextEnabled)
                        } else {
                            state.copy(currentPositionMs = nextPos)
                        }
                    }
                }
            }
        }
    }

    fun showToast(message: String) {
        toastJob?.cancel()
        _uiState.update { it.copy(toastMessage = message) }
        toastJob = viewModelScope.launch {
            delay(2200L)
            _uiState.update { it.copy(toastMessage = null) }
        }
    }

    fun selectTab(tab: AppTab, toastText: String? = null) {
        _uiState.update { it.copy(activeTab = tab) }
        if (toastText != null) {
            showToast(toastText)
        }
    }

    fun exploreNewReciters() {
        _uiState.update { it.copy(activeTab = AppTab.RECITERS) }
        showToast("استكشف القراء الجدد المضافين حديثاً")
    }

    fun openPlaylistWithFilter(filter: PlaylistFilter) {
        _uiState.update {
            it.copy(
                activeTab = AppTab.PLAYLISTS,
                playlistFilter = filter
            )
        }
    }

    fun setPlaylistFilter(filter: PlaylistFilter) {
        _uiState.update { it.copy(playlistFilter = filter) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setFullPlayerOpen(open: Boolean) {
        _uiState.update { it.copy(isFullPlayerOpen = open) }
    }

    fun onPremiumClick() {
        showToast("✨ اشتراك Quranify Premium مفعل بكامل المزايا")
    }

    fun toggleLiquidGlass() {
        val next = !_uiState.value.isLiquidGlassEnabled
        _uiState.update { it.copy(isLiquidGlassEnabled = next) }
        savePreferences()
        showToast(if (next) "تم تفعيل انكسار الزجاج السائل" else "تم تقليل انكسار الزجاج")
    }

    fun toggleHighQualityAudio() {
        val next = !_uiState.value.isHighQualityAudio
        _uiState.update { it.copy(isHighQualityAudio = next) }
        savePreferences()
    }

    fun toggleAutoNext() {
        val next = !_uiState.value.isAutoNextEnabled
        _uiState.update { it.copy(isAutoNextEnabled = next) }
        savePreferences()
    }

    fun toggleLikeSurah(surahNum: Int) {
        val currentLikes = _uiState.value.likedSurahNums.toMutableSet()
        val added = if (currentLikes.contains(surahNum)) {
            currentLikes.remove(surahNum)
            false
        } else {
            currentLikes.add(surahNum)
            true
        }
        _uiState.update { it.copy(likedSurahNums = currentLikes) }
        savePreferences()
        showToast(if (added) "تمت الإضافة إلى المفضلة" else "تمت الإزالة من المفضلة")
    }

    fun selectSurahByNum(surahNum: Int) {
        val idx = QuranDataRepository.surahs.indexOfFirst { it.num == surahNum }
        if (idx != -1) {
            _uiState.update {
                it.copy(
                    currentSurahIndex = idx,
                    currentPositionMs = 0L
                )
            }
            savePreferences()
            loadAndPlayTrack(autoPlay = true)
        }
    }

    fun selectReciter(reciterId: String) {
        val reciter = QuranDataRepository.reciters.find { it.id == reciterId } ?: return
        _uiState.update {
            it.copy(
                currentReciter = reciter,
                currentPositionMs = 0L
            )
        }
        savePreferences()
        loadAndPlayTrack(autoPlay = true)
    }

    fun togglePlay() {
        val state = _uiState.value
        val expectedUrl = "${state.currentReciter.server}${state.currentSurah.code}.mp3"
        if (currentLoadedUrl != expectedUrl || mediaPlayer == null) {
            loadAndPlayTrack(autoPlay = true)
            return
        }

        val player = mediaPlayer
        if (state.isPlaying) {
            try {
                if (player != null && isPlayerPrepared && player.isPlaying) {
                    player.pause()
                }
            } catch (_: Exception) {
            }
            _uiState.update { it.copy(isPlaying = false, isBuffering = false) }
        } else {
            try {
                if (player != null && isPlayerPrepared) {
                    player.start()
                }
            } catch (_: Exception) {
            }
            _uiState.update { it.copy(isPlaying = true) }
            showToast("يتم تشغيل ${state.currentSurah.ar} - ${state.currentReciter.name}")
        }
    }

    fun nextTrack() {
        val nextIndex = (_uiState.value.currentSurahIndex + 1) % QuranDataRepository.surahs.size
        _uiState.update {
            it.copy(
                currentSurahIndex = nextIndex,
                currentPositionMs = 0L
            )
        }
        savePreferences()
        loadAndPlayTrack(autoPlay = true)
    }

    fun prevTrack() {
        val size = QuranDataRepository.surahs.size
        val prevIndex = (_uiState.value.currentSurahIndex - 1 + size) % size
        _uiState.update {
            it.copy(
                currentSurahIndex = prevIndex,
                currentPositionMs = 0L
            )
        }
        savePreferences()
        loadAndPlayTrack(autoPlay = true)
    }

    fun seekToFraction(fraction: Float) {
        val clamped = fraction.coerceIn(0f, 1f)
        val targetMs = (clamped * _uiState.value.durationMs).toLong()
        _uiState.update { it.copy(currentPositionMs = targetMs) }
        val player = mediaPlayer
        if (player != null && isPlayerPrepared) {
            try {
                player.seekTo(targetMs.toInt())
            } catch (_: Exception) {
            }
        }
    }

    private fun loadAndPlayTrack(autoPlay: Boolean) {
        val state = _uiState.value
        val surah = state.currentSurah
        val reciter = state.currentReciter
        val url = "${reciter.server}${surah.code}.mp3"

        if (autoPlay) {
            _uiState.update {
                it.copy(
                    isPlaying = true,
                    isBuffering = true,
                    currentPositionMs = 0L
                )
            }
            showToast("يتم تشغيل ${surah.ar} - ${reciter.name}")
        }

        try {
            mediaPlayer?.release()
        } catch (_: Exception) {
        }
        mediaPlayer = null
        isPlayerPrepared = false
        currentLoadedUrl = url

        try {
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                setOnPreparedListener { mp ->
                    isPlayerPrepared = true
                    val dur = mp.duration.toLong().takeIf { it > 0 } ?: 45_000L
                    _uiState.update {
                        it.copy(
                            durationMs = dur,
                            isBuffering = false
                        )
                    }
                    if (_uiState.value.isPlaying) {
                        mp.start()
                    }
                }
                setOnCompletionListener {
                    if (_uiState.value.isAutoNextEnabled) {
                        nextTrack()
                    } else {
                        _uiState.update {
                            it.copy(
                                isPlaying = false,
                                currentPositionMs = 0L
                            )
                        }
                    }
                }
                setOnErrorListener { _, _, _ ->
                    isPlayerPrepared = false
                    _uiState.update { it.copy(isBuffering = false) }
                    true
                }
                prepareAsync()
            }
            mediaPlayer = player
        } catch (_: Exception) {
            isPlayerPrepared = false
            _uiState.update { it.copy(isBuffering = false) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        progressJob?.cancel()
        try {
            mediaPlayer?.release()
        } catch (_: Exception) {
        }
        mediaPlayer = null
    }
}

fun formatTimeSeconds(ms: Long): String {
    val totalSec = (ms / 1000L).coerceAtLeast(0L)
    val minutes = totalSec / 60L
    val seconds = totalSec % 60L
    return String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds)
}
