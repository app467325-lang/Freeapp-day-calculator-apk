package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.AppTab
import com.example.model.PlaylistFilter
import com.example.model.QuranDataRepository
import com.example.viewmodel.QuranViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app name and verify Quranify data and ViewModel actions`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Quranify", appName)

        // Verify all 6 reciters and 10 surahs exist
        assertEquals(6, QuranDataRepository.reciters.size)
        assertEquals(10, QuranDataRepository.surahs.size)
        assertEquals("عبد الرحمن الزواوي", QuranDataRepository.reciters.first().name)

        val vm = QuranViewModel(context as Application)
        assertEquals(AppTab.HOME, vm.uiState.value.activeTab)
        assertEquals("1. Al-Fatihah", vm.uiState.value.currentSurah.en)

        // Explore new reciters banner switches to Reciters tab
        vm.exploreNewReciters()
        assertEquals(AppTab.RECITERS, vm.uiState.value.activeTab)

        // Playlist filtering
        vm.openPlaylistWithFilter(PlaylistFilter.FOCUS)
        assertEquals(AppTab.PLAYLISTS, vm.uiState.value.activeTab)
        assertEquals(PlaylistFilter.FOCUS, vm.uiState.value.playlistFilter)

        // Like / unlike surah
        assertFalse(vm.uiState.value.likedSurahNums.contains(2))
        vm.toggleLikeSurah(2)
        assertTrue(vm.uiState.value.likedSurahNums.contains(2))
    }
}
