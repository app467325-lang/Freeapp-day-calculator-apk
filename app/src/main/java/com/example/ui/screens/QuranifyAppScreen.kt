package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.AppTab
import com.example.ui.components.FloatingMiniPlayer
import com.example.ui.components.FloatingToastNotification
import com.example.ui.components.LiquidGlassNavigationBar
import com.example.ui.components.ReciterPortrait
import com.example.ui.components.TopHeaderBar
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentRoyalBlue
import com.example.ui.theme.BgDarkDeep
import com.example.ui.theme.BgDarkFrame
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.HeartRed
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.TextMuted
import com.example.viewmodel.QuranUiState
import com.example.viewmodel.QuranViewModel
import com.example.viewmodel.formatTimeSeconds

@Composable
fun QuranifyAppScreen(
    viewModel: QuranViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Handle system back navigation for Full Player Modal and secondary tabs
    if (uiState.isFullPlayerOpen) {
        BackHandler {
            viewModel.setFullPlayerOpen(false)
        }
    } else if (uiState.activeTab != AppTab.HOME) {
        BackHandler {
            viewModel.selectTab(AppTab.HOME)
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(BgDarkDeep),
            contentAlignment = Alignment.Center
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 520.dp)
                    .background(BgDarkFrame)
                    .drawBehind {
                        val w = size.width
                        val h = size.height
                        // Radial blue glow top right (80% 8%)
                        drawRect(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0x381D4ED8),
                                    Color.Transparent
                                ),
                                center = Offset(w * 0.8f, h * 0.08f),
                                radius = w * 0.65f
                            )
                        )
                        // Radial cyan glow lower left (15% 70%)
                        drawRect(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0x2E0E7490),
                                    Color.Transparent
                                ),
                                center = Offset(w * 0.15f, h * 0.7f),
                                radius = w * 0.7f
                            )
                        )
                    }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                ) {
                    // Top Bar: Premium Pill + Equalizer Button
                    TopHeaderBar(
                        isPlaying = uiState.isPlaying,
                        onPremiumClick = viewModel::onPremiumClick,
                        onEqualizerClick = viewModel::togglePlay
                    )

                    // Active Tab View Section
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        when (uiState.activeTab) {
                            AppTab.HOME -> HomeTabContent(
                                uiState = uiState,
                                onExploreNewReciters = viewModel::exploreNewReciters,
                                onOpenFullPlayer = { viewModel.setFullPlayerOpen(true) },
                                onContinueListen = viewModel::togglePlay,
                                onNavigateTab = { viewModel.selectTab(it) },
                                onOpenPlaylistFilter = viewModel::openPlaylistWithFilter,
                                onSelectSurah = viewModel::selectSurahByNum,
                                onToggleLikeSurah = viewModel::toggleLikeSurah
                            )

                            AppTab.RECITERS -> RecitersTabContent(
                                uiState = uiState,
                                onSelectReciter = viewModel::selectReciter
                            )

                            AppTab.PLAYLISTS -> PlaylistsTabContent(
                                uiState = uiState,
                                onFilterChange = viewModel::setPlaylistFilter,
                                onSelectSurah = viewModel::selectSurahByNum,
                                onToggleLikeSurah = viewModel::toggleLikeSurah
                            )

                            AppTab.SETTINGS -> SettingsTabContent(
                                uiState = uiState,
                                onToggleLiquidGlass = viewModel::toggleLiquidGlass,
                                onToggleHighQuality = viewModel::toggleHighQualityAudio,
                                onToggleAutoNext = viewModel::toggleAutoNext
                            )

                            AppTab.SEARCH -> SearchTabContent(
                                uiState = uiState,
                                onQueryChange = viewModel::updateSearchQuery,
                                onSelectSurah = viewModel::selectSurahByNum,
                                onToggleLikeSurah = viewModel::toggleLikeSurah,
                                onSelectReciter = viewModel::selectReciter
                            )
                        }
                    }
                }

                // Floating Mini-Player + Liquid Glass Bottom Navigation Bar
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FloatingMiniPlayer(
                        surah = uiState.currentSurah,
                        reciter = uiState.currentReciter,
                        isPlaying = uiState.isPlaying,
                        progressFraction = uiState.progressFraction,
                        onOpenFullPlayer = { viewModel.setFullPlayerOpen(true) },
                        onTogglePlay = viewModel::togglePlay,
                        onNextTrack = viewModel::nextTrack,
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )

                    LiquidGlassNavigationBar(
                        activeTab = uiState.activeTab,
                        isLiquidGlassEnabled = uiState.isLiquidGlassEnabled,
                        onSelectTab = { viewModel.selectTab(it) }
                    )
                }

                // Full-Screen Player Modal
                AnimatedVisibility(
                    visible = uiState.isFullPlayerOpen,
                    enter = slideInVertically(
                        animationSpec = tween(
                            durationMillis = 380,
                            easing = CubicBezierEasing(0.22f, 1f, 0.36f, 1f)
                        ),
                        initialOffsetY = { it }
                    ),
                    exit = slideOutVertically(
                        animationSpec = tween(
                            durationMillis = 320,
                            easing = CubicBezierEasing(0.22f, 1f, 0.36f, 1f)
                        ),
                        targetOffsetY = { it }
                    ),
                    modifier = Modifier.fillMaxSize()
                ) {
                    FullPlayerModal(
                        uiState = uiState,
                        onClose = { viewModel.setFullPlayerOpen(false) },
                        onToggleFav = { viewModel.toggleLikeSurah(uiState.currentSurah.num) },
                        onSeekFraction = viewModel::seekToFraction,
                        onPrevTrack = viewModel::prevTrack,
                        onTogglePlay = viewModel::togglePlay,
                        onNextTrack = viewModel::nextTrack
                    )
                }

                // Floating Toast Message
                FloatingToastNotification(
                    message = uiState.toastMessage,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                        .padding(top = 68.dp)
                )
            }
        }
    }
}

@Composable
private fun FullPlayerModal(
    uiState: QuranUiState,
    onClose: () -> Unit,
    onToggleFav: () -> Unit,
    onSeekFraction: (Float) -> Unit,
    onPrevTrack: () -> Unit,
    onTogglePlay: () -> Unit,
    onNextTrack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLiked = uiState.likedSurahNums.contains(uiState.currentSurah.num)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BgDarkFrame)
            .drawBehind {
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            AccentRoyalBlue.copy(alpha = 0.38f),
                            BgDarkFrame.copy(alpha = 0.98f)
                        ),
                        center = Offset(size.width * 0.5f, size.height * 0.2f),
                        radius = size.width * 0.95f
                    )
                )
            }
            .clickable(enabled = true, onClick = {}) // Prevent clicks passing through
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 26.dp, vertical = 24.dp)
            .testTag("full_player_modal")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Close Down Chevron + يتم التشغيل الآن + Favorite Heart
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                        .clickable(onClick = onClose)
                        .testTag("close_full_player_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "إغلاق المشغل",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = "يتم التشغيل الآن",
                    color = Color.White,
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.5.sp
                )

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                        .clickable(onClick = onToggleFav)
                        .testTag("fp_fav_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "المفضلة",
                        tint = if (isLiked) HeartRed else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Artwork Portrait (240dp x 240dp with 38dp corner radius)
            val artShape = RoundedCornerShape(38.dp)
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .shadow(28.dp, artShape)
                    .clip(artShape)
                    .border(2.dp, Color.White.copy(alpha = 0.18f), artShape)
            ) {
                ReciterPortrait(
                    reciter = uiState.currentReciter,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Titles
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${uiState.currentSurah.en} - ${uiState.currentSurah.ar}",
                    color = Color.White,
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 25.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = uiState.currentReciter.name,
                    color = TextMuted,
                    fontFamily = CairoFontFamily,
                    fontSize = 17.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Timeline Seekbar & Times (LTR so progress moves smoothly with time)
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Slider(
                        value = uiState.progressFraction,
                        onValueChange = onSeekFraction,
                        colors = SliderDefaults.colors(
                            thumbColor = AccentCyan,
                            activeTrackColor = AccentCyan,
                            inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("fp_seek_slider")
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatTimeSeconds(uiState.currentPositionMs),
                            color = TextMuted,
                            fontFamily = InterFontFamily,
                            fontSize = 13.sp
                        )
                        Text(
                            text = formatTimeSeconds(uiState.durationMs),
                            color = TextMuted,
                            fontFamily = InterFontFamily,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Playback Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                IconButton(
                    onClick = onPrevTrack,
                    modifier = Modifier
                        .size(52.dp)
                        .testTag("fp_prev_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.FastRewind,
                        contentDescription = "السورة السابقة",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .shadow(18.dp, CircleShape, ambientColor = Color.White, spotColor = Color.White)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable(onClick = onTogglePlay)
                        .testTag("fp_play_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (uiState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (uiState.isPlaying) "إيقاف مؤقت" else "تشغيل",
                        tint = BgDarkFrame,
                        modifier = Modifier.size(34.dp)
                    )
                }

                IconButton(
                    onClick = onNextTrack,
                    modifier = Modifier
                        .size(52.dp)
                        .testTag("fp_next_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.FastForward,
                        contentDescription = "السورة التالية",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}
