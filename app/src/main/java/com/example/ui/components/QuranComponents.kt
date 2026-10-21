package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Reciter
import com.example.model.Surah
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.FavCardBottom
import com.example.ui.theme.FavCardMid
import com.example.ui.theme.FavCardTop
import com.example.ui.theme.FocusCardCenter
import com.example.ui.theme.FocusCardEdge
import com.example.ui.theme.FocusCardMid
import com.example.ui.theme.HeartRed
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.TextMuted

@Composable
fun TopHeaderBar(
    isPlaying: Boolean,
    onPremiumClick: () -> Unit,
    onEqualizerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Premium Pill Button (Right side in RTL)
        Box(
            modifier = Modifier
                .minimumInteractiveComponentSize()
                .shadow(
                    elevation = 12.dp,
                    shape = CircleShape,
                    ambientColor = AccentBlue,
                    spotColor = AccentBlue
                )
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xE610244A), Color(0xF20A1630))
                    )
                )
                .border(1.5.dp, AccentBlue.copy(alpha = 0.65f), CircleShape)
                .clickable(onClick = onPremiumClick)
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .testTag("premium_button"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Premium",
                color = Color.White,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.5.sp
            )
        }

        // Animated Equalizer Circle Button (Left side in RTL)
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xD9191E28))
                .border(1.dp, Color.White.copy(alpha = 0.14f), CircleShape)
                .clickable(onClick = onEqualizerClick)
                .testTag("equalizer_button"),
            contentAlignment = Alignment.Center
        ) {
            AnimatedEqualizerBars(isPlaying = isPlaying)
        }
    }
}

@Composable
fun AnimatedEqualizerBars(
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "eq_transition")

    val bar1Anim by infiniteTransition.animateFloat(
        initialValue = 5f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar1"
    )
    val bar2Anim by infiniteTransition.animateFloat(
        initialValue = 18f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar2"
    )
    val bar3Anim by infiniteTransition.animateFloat(
        initialValue = 6f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 850, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar3"
    )

    val h1 = if (isPlaying) bar1Anim else 9f
    val h2 = if (isPlaying) bar2Anim else 17f
    val h3 = if (isPlaying) bar3Anim else 12f

    Row(
        modifier = modifier.height(18.dp),
        horizontalArrangement = Arrangement.spacedBy(3.5.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Box(
            modifier = Modifier
                .width(3.5.dp)
                .height(h1.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White)
        )
        Box(
            modifier = Modifier
                .width(3.5.dp)
                .height(h2.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White)
        )
        Box(
            modifier = Modifier
                .width(3.5.dp)
                .height(h3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White)
        )
    }
}

@Composable
fun PlaylistsCardsGrid(
    onFavClick: () -> Unit,
    onFocusClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Favorites Card (المفضلة) with 3D Star
        PlaylistCardContainer(
            title = "المفضلة",
            onClick = onFavClick,
            testTag = "playlist_card_fav",
            modifier = Modifier.weight(1f)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(FavCardTop, FavCardMid, FavCardBottom),
                        center = Offset(w * 0.5f, h * 0.42f),
                        radius = w * 0.85f
                    )
                )

                // Draw smooth 3D rounded star centered in upper portion
                val starBoxSize = 104.dp.toPx()
                val startX = (w - starBoxSize) / 2f
                val startY = h * 0.12f
                val sx = starBoxSize / 120f
                val sy = starBoxSize / 120f

                // Path from HTML:
                // M60 10 C64 10 66 36 72 41 C78 46 105 42 107 47 C109 52 86 67 84 74 C82 81 94 105 89 108 C84 111 66 93 60 93 C54 93 36 111 31 108 C26 105 38 81 36 74 C34 67 11 52 13 47 C15 42 42 46 48 41 C54 36 56 10 60 10 Z
                val starPath = Path().apply {
                    moveTo(startX + 60f * sx, startY + 10f * sy)
                    cubicTo(startX + 64f * sx, startY + 10f * sy, startX + 66f * sx, startY + 36f * sy, startX + 72f * sx, startY + 41f * sy)
                    cubicTo(startX + 78f * sx, startY + 46f * sy, startX + 105f * sx, startY + 42f * sy, startX + 107f * sx, startY + 47f * sy)
                    cubicTo(startX + 109f * sx, startY + 52f * sy, startX + 86f * sx, startY + 67f * sy, startX + 84f * sx, startY + 74f * sy)
                    cubicTo(startX + 82f * sx, startY + 81f * sy, startX + 94f * sx, startY + 105f * sy, startX + 89f * sx, startY + 108f * sy)
                    cubicTo(startX + 84f * sx, startY + 111f * sy, startX + 66f * sx, startY + 93f * sy, startX + 60f * sx, startY + 93f * sy)
                    cubicTo(startX + 54f * sx, startY + 93f * sy, startX + 36f * sx, startY + 111f * sy, startX + 31f * sx, startY + 108f * sy)
                    cubicTo(startX + 26f * sx, startY + 105f * sy, startX + 38f * sx, startY + 81f * sy, startX + 36f * sx, startY + 74f * sy)
                    cubicTo(startX + 34f * sx, startY + 67f * sy, startX + 11f * sx, startY + 52f * sy, startX + 13f * sx, startY + 47f * sy)
                    cubicTo(startX + 15f * sx, startY + 42f * sy, startX + 42f * sx, startY + 46f * sy, startX + 48f * sx, startY + 41f * sy)
                    cubicTo(startX + 54f * sx, startY + 36f * sy, startX + 56f * sx, startY + 10f * sy, startX + 60f * sx, startY + 10f * sy)
                    close()
                }

                // Soft shadow behind star
                drawCircle(
                    color = Color.Black.copy(alpha = 0.28f),
                    radius = starBoxSize * 0.36f,
                    center = Offset(w * 0.5f, startY + starBoxSize * 0.58f)
                )

                drawPath(
                    path = starPath,
                    brush = Brush.linearGradient(
                        colors = listOf(Color.White, Color(0xFFF1F5F9), Color(0xFFCBD5E1)),
                        start = Offset(startX, startY),
                        end = Offset(startX + starBoxSize, startY + starBoxSize)
                    )
                )
            }
        }

        // 2. Focus & Work Card (التركيز والعمل) with Concentric Radar Rings
        PlaylistCardContainer(
            title = "التركيز والعمل",
            onClick = onFocusClick,
            testTag = "playlist_card_focus",
            modifier = Modifier.weight(1f)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val center = Offset(w * 0.18f, h * 0.55f)
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            FocusCardCenter,
                            FocusCardMid,
                            Color(0xFF1D4ED8),
                            FocusCardEdge
                        ),
                        center = center,
                        radius = w * 1.15f
                    )
                )

                // Repeating radar rings
                val stepPx = 7.dp.toPx()
                val maxR = w * 1.35f
                var r = stepPx
                while (r <= maxR) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.18f),
                        radius = r,
                        center = center,
                        style = Stroke(width = 1.dp.toPx())
                    )
                    r += stepPx
                }
            }
        }
    }
}

@Composable
private fun PlaylistCardContainer(
    title: String,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier,
    artwork: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(26.dp)
    Box(
        modifier = modifier
            .height(205.dp)
            .shadow(14.dp, shape)
            .clip(shape)
            .border(1.dp, Color.White.copy(alpha = 0.12f), shape)
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        artwork()

        // Frosted bottom bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color(0x9E0F1218))
                .border(width = 0.8.dp, color = Color.White.copy(alpha = 0.1f))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.5.sp
            )
        }
    }
}

@Composable
fun SurahItemRow(
    surah: Surah,
    reciterName: String,
    isCurrent: Boolean,
    isLiked: Boolean,
    onSelect: () -> Unit,
    onToggleLike: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(20.dp)
    val bgColor = if (isCurrent) AccentBlue.copy(alpha = 0.18f) else Color(0xB8161B25)
    val borderColor = if (isCurrent) AccentBlue.copy(alpha = 0.55f) else Color.White.copy(alpha = 0.08f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(bgColor)
            .border(1.dp, borderColor, shape)
            .clickable(onClick = onSelect)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("surah_item_${surah.num}"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.07f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = surah.num.toString(),
                    color = AccentCyan,
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Column {
                Text(
                    text = "${surah.ar} (${surah.en})",
                    color = Color.White,
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$reciterName • ${surah.verses} آية",
                    color = TextMuted,
                    fontFamily = CairoFontFamily,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        IconButton(
            onClick = onToggleLike,
            modifier = Modifier
                .size(48.dp)
                .testTag("fav_btn_${surah.num}")
        ) {
            Icon(
                imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = if (isLiked) "إزالة من المفضلة" else "إضافة إلى المفضلة",
                tint = if (isLiked) HeartRed else TextMuted,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun FloatingMiniPlayer(
    surah: Surah,
    reciter: Reciter,
    isPlaying: Boolean,
    progressFraction: Float,
    onOpenFullPlayer: () -> Unit,
    onTogglePlay: () -> Unit,
    onNextTrack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(24.dp)
    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction.coerceIn(0f, 1f),
        animationSpec = tween(200),
        label = "mini_progress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .shadow(
                elevation = 20.dp,
                shape = shape,
                ambientColor = AccentCyan.copy(alpha = 0.25f),
                spotColor = Color.Black
            )
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xE618202E), Color(0xF00E131C))
                )
            )
            .border(1.5.dp, Color(0x597DD3FC), shape)
            .clickable(onClick = onOpenFullPlayer)
            .testTag("mini_player")
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Right Side in RTL: Thumbnail + Surah Title + Reciter Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                ReciterPortrait(
                    reciter = reciter,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(13.dp))
                )

                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Text(
                            text = surah.en,
                            color = Color.White,
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = reciter.name,
                        color = TextMuted,
                        fontFamily = CairoFontFamily,
                        fontSize = 12.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Controls: Play/Pause and Double-Arrow Next
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = onTogglePlay,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("mini_play_button")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) "إيقاف مؤقت" else "تشغيل",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                IconButton(
                    onClick = onNextTrack,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("mini_next_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.FastForward,
                        contentDescription = "السورة التالية",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }

        // Bottom Progress Bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(animatedProgress)
                .height(2.5.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(AccentCyan, AccentBlue)
                    )
                )
        )
    }
}

@Composable
fun FloatingToastNotification(
    message: String?,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = message != null,
        enter = fadeIn(tween(220)) + slideInVertically(tween(220)) { -it / 2 },
        exit = fadeOut(tween(220)) + slideOutVertically(tween(220)) { -it / 2 },
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .shadow(16.dp, CircleShape)
                .clip(CircleShape)
                .background(Color(0xEB0F172A))
                .border(1.dp, AccentCyan.copy(alpha = 0.45f), CircleShape)
                .padding(horizontal = 20.dp, vertical = 9.dp)
                .testTag("floating_toast")
        ) {
            Text(
                text = message.orEmpty(),
                color = Color.White,
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.5.sp
            )
        }
    }
}
