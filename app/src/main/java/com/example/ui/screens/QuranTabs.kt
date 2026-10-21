package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppTab
import com.example.model.PlaylistFilter
import com.example.model.QuranDataRepository
import com.example.model.Reciter
import com.example.ui.components.PlaylistsCardsGrid
import com.example.ui.components.ReciterPortrait
import com.example.ui.components.SurahItemRow
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentLightBlue
import com.example.ui.theme.AccentRoyalBlue
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.TextMuted
import com.example.viewmodel.QuranUiState

@Composable
fun HomeTabContent(
    uiState: QuranUiState,
    onExploreNewReciters: () -> Unit,
    onOpenFullPlayer: () -> Unit,
    onContinueListen: () -> Unit,
    onNavigateTab: (AppTab) -> Unit,
    onOpenPlaylistFilter: (PlaylistFilter) -> Unit,
    onSelectSurah: (Int) -> Unit,
    onToggleLikeSurah: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val homeSurahs = QuranDataRepository.surahs.take(5)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_list"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 195.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "الرئيسية",
                color = Color.White,
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 34.sp,
                modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
            )
        }

        // New Reciters Notification Banner (تمت إضافة 3 قراء جدد)
        item {
            val bannerShape = RoundedCornerShape(26.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
                    .shadow(14.dp, bannerShape)
                    .clip(bannerShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xEB1C212B), Color(0xEB12161E))
                        )
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.08f), bannerShape)
                    .clickable(onClick = onExploreNewReciters)
                    .padding(horizontal = 18.dp, vertical = 15.dp)
                    .testTag("explore_new_reciters_banner"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .shadow(12.dp, CircleShape, ambientColor = AccentBlue, spotColor = AccentBlue)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(AccentLightBlue, AccentRoyalBlue)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(1.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Icon(
                                imageVector = Icons.Filled.GraphicEq,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "تمت إضافة 3 قراء جدد",
                            color = Color.White,
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "اضغط للاستكشاف",
                            color = TextMuted,
                            fontFamily = CairoFontFamily,
                            fontSize = 13.5.sp
                        )
                    }
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "استكشاف",
                    tint = TextMuted,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Recently Played Hero Card (تم تشغيله مؤخراً - عبد الرحمن الزواوي)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Portrait on Right (in RTL)
                val photoShape = RoundedCornerShape(30.dp)
                Box(
                    modifier = Modifier
                        .size(145.dp)
                        .shadow(18.dp, photoShape)
                        .clip(photoShape)
                        .border(1.dp, Color.White.copy(alpha = 0.14f), photoShape)
                        .clickable(onClick = onOpenFullPlayer)
                        .testTag("recent_photo_button")
                ) {
                    ReciterPortrait(
                        reciter = uiState.currentReciter,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Reciter Info & Continue Button on Left (in RTL)
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "تم تشغيله مؤخراً",
                        color = TextMuted,
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = uiState.currentReciter.name,
                        color = Color.White,
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 23.sp,
                        lineHeight = 29.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // White Pill Button: تابع الاستماع
                    Row(
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .shadow(12.dp, CircleShape, ambientColor = Color.White, spotColor = Color.White)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable(onClick = onContinueListen)
                            .padding(horizontal = 24.dp, vertical = 11.dp)
                            .testTag("continue_listen_button"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = null,
                            tint = Color(0xFF090D16),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "تابع الاستماع",
                            color = Color(0xFF090D16),
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        // Playlists Section Header (قوائم التشغيل + عرض الكل)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "قوائم التشغيل",
                    color = Color.White,
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp
                )
                Text(
                    text = "عرض الكل",
                    color = AccentBlue,
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clickable { onNavigateTab(AppTab.PLAYLISTS) }
                        .padding(vertical = 4.dp)
                        .testTag("show_all_playlists_link")
                )
            }
        }

        // Playlists Grid (المفضلة + التركيز والعمل)
        item {
            PlaylistsCardsGrid(
                onFavClick = { onOpenPlaylistFilter(PlaylistFilter.FAV) },
                onFocusClick = { onOpenPlaylistFilter(PlaylistFilter.FOCUS) },
                modifier = Modifier.padding(bottom = 18.dp)
            )
        }

        // Selected Surahs Header (السور المختارة + كل السور)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "السور المختارة",
                    color = Color.White,
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp
                )
                Text(
                    text = "كل السور",
                    color = AccentBlue,
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clickable { onNavigateTab(AppTab.SEARCH) }
                        .padding(vertical = 4.dp)
                        .testTag("show_all_surahs_link")
                )
            }
        }

        // Top 5 Selected Surahs List
        items(homeSurahs, key = { it.num }) { surah ->
            SurahItemRow(
                surah = surah,
                reciterName = uiState.currentReciter.name,
                isCurrent = uiState.currentSurah.num == surah.num,
                isLiked = uiState.likedSurahNums.contains(surah.num),
                onSelect = { onSelectSurah(surah.num) },
                onToggleLike = { onToggleLikeSurah(surah.num) }
            )
        }
    }
}

@Composable
fun RecitersTabContent(
    uiState: QuranUiState,
    onSelectReciter: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .testTag("reciters_screen")
    ) {
        Text(
            text = "القراء",
            color = Color.White,
            fontFamily = CairoFontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 34.sp,
            modifier = Modifier.padding(top = 6.dp, bottom = 16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            contentPadding = PaddingValues(bottom = 195.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(QuranDataRepository.reciters, key = { it.id }) { reciter ->
                ReciterGridCard(
                    reciter = reciter,
                    isSelected = uiState.currentReciter.id == reciter.id,
                    onClick = { onSelectReciter(reciter.id) }
                )
            }
        }
    }
}

@Composable
private fun ReciterGridCard(
    reciter: Reciter,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(24.dp)
    val bgColor = if (isSelected) Color(0xBF1E2941) else Color(0xC7161B25)
    val borderColor = if (isSelected) AccentBlue else Color.White.copy(alpha = 0.08f)

    Column(
        modifier = modifier
            .clip(shape)
            .background(bgColor)
            .border(if (isSelected) 1.5.dp else 1.dp, borderColor, shape)
            .clickable(onClick = onClick)
            .padding(14.dp)
            .testTag("reciter_card_${reciter.id}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val avatarShape = RoundedCornerShape(24.dp)
        ReciterPortrait(
            reciter = reciter,
            modifier = Modifier
                .size(96.dp)
                .clip(avatarShape)
                .border(2.dp, Color.White.copy(alpha = 0.14f), avatarShape)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = reciter.name,
            color = Color.White,
            fontFamily = CairoFontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.5.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = reciter.riwayah,
            color = TextMuted,
            fontFamily = CairoFontFamily,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun PlaylistsTabContent(
    uiState: QuranUiState,
    onFilterChange: (PlaylistFilter) -> Unit,
    onSelectSurah: (Int) -> Unit,
    onToggleLikeSurah: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredSurahs = when (uiState.playlistFilter) {
        PlaylistFilter.ALL -> QuranDataRepository.surahs
        PlaylistFilter.FAV -> QuranDataRepository.surahs.filter { uiState.likedSurahNums.contains(it.num) }
        PlaylistFilter.FOCUS -> QuranDataRepository.surahs.filter { it.tag == "focus" }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("playlists_screen"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 6.dp, bottom = 195.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "قوائم التشغيل",
                color = Color.White,
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 34.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        item {
            PlaylistsCardsGrid(
                onFavClick = { onFilterChange(PlaylistFilter.FAV) },
                onFocusClick = { onFilterChange(PlaylistFilter.FOCUS) },
                modifier = Modifier.padding(bottom = 18.dp)
            )
        }

        item {
            Text(
                text = uiState.playlistFilter.titleAr,
                color = Color.White,
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .testTag("playlist_current_title")
            )
        }

        if (filteredSurahs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "لا توجد سور في المفضلة حالياً. اضغط على رمز القلب ♥ لإضافة سور.",
                        color = TextMuted,
                        fontFamily = CairoFontFamily,
                        fontSize = 14.5.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(filteredSurahs, key = { it.num }) { surah ->
                SurahItemRow(
                    surah = surah,
                    reciterName = uiState.currentReciter.name,
                    isCurrent = uiState.currentSurah.num == surah.num,
                    isLiked = uiState.likedSurahNums.contains(surah.num),
                    onSelect = { onSelectSurah(surah.num) },
                    onToggleLike = { onToggleLikeSurah(surah.num) }
                )
            }
        }
    }
}

@Composable
fun SettingsTabContent(
    uiState: QuranUiState,
    onToggleLiquidGlass: () -> Unit,
    onToggleHighQuality: () -> Unit,
    onToggleAutoNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 6.dp, bottom = 195.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "الإعدادات",
                color = Color.White,
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 34.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        item {
            SettingToggleCard(
                title = "تأثير الزجاج السائل الفائق (Liquid Glass)",
                subtitle = "تفعيل الانكسار الضوئي والتمدد الفيزيائي للعدسة",
                isChecked = uiState.isLiquidGlassEnabled,
                onToggle = onToggleLiquidGlass,
                testTag = "toggle_liquid_glass"
            )
        }

        item {
            SettingToggleCard(
                title = "جودة التلاوة الصوتية",
                subtitle = "بث صوتي نقي بدون انقطاع (MP3 192kbps)",
                isChecked = uiState.isHighQualityAudio,
                onToggle = onToggleHighQuality,
                testTag = "toggle_hq_audio"
            )
        }

        item {
            SettingToggleCard(
                title = "الانتقال التلقائي للسورة التالية",
                subtitle = "تشغيل السورة التالية فور انتهاء الحالية",
                isChecked = uiState.isAutoNextEnabled,
                onToggle = onToggleAutoNext,
                testTag = "toggle_auto_next"
            )
        }
    }
}

@Composable
private fun SettingToggleCard(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onToggle: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(22.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color(0xC7161B25))
            .border(1.dp, Color.White.copy(alpha = 0.08f), shape)
            .clickable(onClick = onToggle)
            .padding(horizontal = 18.dp, vertical = 16.dp)
            .testTag(testTag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = title,
                color = Color.White,
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = TextMuted,
                fontFamily = CairoFontFamily,
                fontSize = 13.sp
            )
        }

        // Custom pill switch matching HTML .toggle-switch
        Box(
            modifier = Modifier
                .width(50.dp)
                .height(28.dp)
                .clip(CircleShape)
                .background(if (isChecked) AccentBlue else Color.White.copy(alpha = 0.15f))
                .padding(3.dp),
            contentAlignment = if (isChecked) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }
    }
}

@Composable
fun SearchTabContent(
    uiState: QuranUiState,
    onQueryChange: (String) -> Unit,
    onSelectSurah: (Int) -> Unit,
    onToggleLikeSurah: (Int) -> Unit,
    onSelectReciter: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val q = uiState.searchQuery.trim().lowercase()
    val matchingSurahs = QuranDataRepository.surahs.filter { s ->
        q.isEmpty() ||
            s.ar.contains(q) ||
            s.en.lowercase().contains(q) ||
            s.num.toString() == q
    }
    val matchingReciters = if (q.isNotEmpty()) {
        QuranDataRepository.reciters.filter { r ->
            r.name.contains(q) || r.riwayah.contains(q)
        }
    } else {
        emptyList()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("search_screen"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 6.dp, bottom = 195.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "بحث",
                color = Color.White,
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 34.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        item {
            val inputShape = RoundedCornerShape(20.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clip(inputShape)
                    .background(Color(0xD9191F2C))
                    .border(
                        width = 1.dp,
                        color = if (uiState.searchQuery.isNotEmpty()) AccentBlue else Color.White.copy(alpha = 0.14f),
                        shape = inputShape
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "بحث",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )

                    Box(modifier = Modifier.weight(1f)) {
                        if (uiState.searchQuery.isEmpty()) {
                            Text(
                                text = "ابحث عن سورة أو قارئ (مثل: الفاتحة، الكهف، الزواوي)...",
                                color = TextMuted,
                                fontFamily = CairoFontFamily,
                                fontSize = 14.5.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        BasicTextField(
                            value = uiState.searchQuery,
                            onValueChange = onQueryChange,
                            singleLine = true,
                            textStyle = TextStyle(
                                color = Color.White,
                                fontFamily = CairoFontFamily,
                                fontSize = 15.5.sp
                            ),
                            cursorBrush = SolidColor(AccentBlue),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("search_input")
                        )
                    }
                }
            }
        }

        if (matchingReciters.isNotEmpty()) {
            item {
                Text(
                    text = "القراء المطابقون",
                    color = Color.White,
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            items(matchingReciters, key = { "rec_${it.id}" }) { reciter ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xB8161B25))
                        .border(1.dp, AccentBlue.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                        .clickable { onSelectReciter(reciter.id) }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    ReciterPortrait(
                        reciter = reciter,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                    )
                    Column {
                        Text(
                            text = reciter.name,
                            color = Color.White,
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = reciter.riwayah,
                            color = TextMuted,
                            fontFamily = CairoFontFamily,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        items(matchingSurahs, key = { it.num }) { surah ->
            SurahItemRow(
                surah = surah,
                reciterName = uiState.currentReciter.name,
                isCurrent = uiState.currentSurah.num == surah.num,
                isLiked = uiState.likedSurahNums.contains(surah.num),
                onSelect = { onSelectSurah(surah.num) },
                onToggleLike = { onToggleLikeSurah(surah.num) }
            )
        }
    }
}
