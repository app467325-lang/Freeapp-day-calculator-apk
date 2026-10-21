package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.PersonSearch
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppTab
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.CairoFontFamily
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun LiquidGlassNavigationBar(
    activeTab: AppTab,
    isLiquidGlassEnabled: Boolean,
    onSelectTab: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = AppTab.entries
    val density = LocalDensity.current

    var barWidthPx by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    // Note: In RTL, index 0 (Home - الرئيسية) is at the rightmost slot (x = barWidthPx * 4.5 / 5),
    // and index 4 (Search - بحث) is at the leftmost slot (x = barWidthPx * 0.5 / 5).
    fun getSlotCenterX(index: Int, totalWidth: Float): Float {
        if (totalWidth <= 0f) return 0f
        val slotWidth = totalWidth / tabs.size
        // RTL slot index from left: (tabs.size - 1 - index)
        val ltrSlot = (tabs.size - 1 - index).coerceIn(0, tabs.size - 1)
        return (ltrSlot + 0.5f) * slotWidth
    }

    var lensCurrentX by remember { mutableFloatStateOf(0f) }
    var lensTargetX by remember { mutableFloatStateOf(0f) }
    var lensVelocityX by remember { mutableFloatStateOf(0f) }

    // Sync target X when activeTab or barWidthPx changes outside drag
    LaunchedEffect(activeTab, barWidthPx) {
        if (barWidthPx > 0f && !isDragging) {
            val target = getSlotCenterX(activeTab.index, barWidthPx)
            if (lensCurrentX == 0f) {
                lensCurrentX = target
            }
            lensTargetX = target
        }
    }

    // 60fps Liquid Spring & Stretch Physics Loop
    LaunchedEffect(barWidthPx) {
        if (barWidthPx <= 0f) return@LaunchedEffect
        while (true) {
            withFrameNanos {
                val stiffness = if (isDragging) 0.28f else 0.16f
                val damping = if (isDragging) 0.68f else 0.74f

                val force = (lensTargetX - lensCurrentX) * stiffness
                lensVelocityX = (lensVelocityX + force) * damping
                lensCurrentX += lensVelocityX
            }
        }
    }

    val speed = abs(lensVelocityX).coerceAtMost(26f)
    val baseWidthDp = if (isDragging) 98f else 82f
    val baseHeightDp = if (isDragging) 64f else 56f
    val dynamicWidthDp = if (isLiquidGlassEnabled) baseWidthDp + (speed * 0.6f) else 82f
    val dynamicHeightDp = if (isLiquidGlassEnabled) (baseHeightDp - (speed * 0.15f)).coerceAtLeast(46f) else 56f

    val dynamicWidthPx = with(density) { dynamicWidthDp.dp.toPx() }
    val lensLeftPx = (lensCurrentX - dynamicWidthPx / 2f).coerceIn(
        0f,
        (barWidthPx - dynamicWidthPx).coerceAtLeast(0f)
    )

    // Render outer container in LTR coordinate space so X offsets are deterministic,
    // while keeping RTL order of tabs inside the row
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(68.dp)
                .onSizeChanged { size ->
                    val newWidth = size.width.toFloat()
                    if (newWidth > 0f && barWidthPx == 0f) {
                        barWidthPx = newWidth
                        val initialCenter = getSlotCenterX(activeTab.index, newWidth)
                        lensCurrentX = initialCenter
                        lensTargetX = initialCenter
                    } else {
                        barWidthPx = newWidth
                    }
                }
                .shadow(
                    elevation = 24.dp,
                    shape = CircleShape,
                    ambientColor = Color.Black,
                    spotColor = Color.Black
                )
                .clip(CircleShape)
                .background(Color(0xB8161A23))
                .border(1.dp, Color(0x21FFFFFF), CircleShape)
                .pointerInput(barWidthPx) {
                    if (barWidthPx <= 0f) return@pointerInput
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        isDragging = true
                        val marginPx = 36.dp.toPx()

                        fun updateFromPointer(x: Float) {
                            val clampedX = x.coerceIn(marginPx, (barWidthPx - marginPx).coerceAtLeast(marginPx))
                            lensTargetX = clampedX

                            // Find closest tab slot
                            var closestIdx = 0
                            var minDist = Float.MAX_VALUE
                            for (i in tabs.indices) {
                                val center = getSlotCenterX(i, barWidthPx)
                                val dist = abs(clampedX - center)
                                if (dist < minDist) {
                                    minDist = dist
                                    closestIdx = i
                                }
                            }
                            val newTab = AppTab.fromIndex(closestIdx)
                            if (newTab != activeTab) {
                                onSelectTab(newTab)
                            }
                        }

                        updateFromPointer(down.position.x)

                        do {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull()
                            if (change != null && change.pressed) {
                                updateFromPointer(change.position.x)
                                change.consume()
                            }
                        } while (event.changes.any { it.pressed })

                        isDragging = false
                        lensTargetX = getSlotCenterX(activeTab.index, barWidthPx)
                    }
                }
                .testTag("bottom_nav_pill")
        ) {
            // Base Tab Row (RTL order: Home on right, Search on left)
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tabs.forEach { tab ->
                        val isSelected = tab == activeTab
                        NavTabItem(
                            tab = tab,
                            isSelected = isSelected,
                            isMagnified = false,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .testTag("nav_tab_${tab.id}")
                        )
                    }
                }
            }

            // Liquid Glass Morphing Droplet Lens with Inner Magnified Track
            if (barWidthPx > 0f) {
                val barWidthDp = with(density) { barWidthPx.toDp() }
                Box(
                    modifier = Modifier
                        .offset { IntOffset(lensLeftPx.roundToInt(), 0) }
                        .width(dynamicWidthDp.dp)
                        .height(dynamicHeightDp.dp)
                        .align(Alignment.CenterStart)
                        .clip(RoundedCornerShape(999.dp))
                ) {
                    // Counter-translated inner track showing magnified, refracted icons under the droplet
                    Box(
                        modifier = Modifier
                            .width(barWidthDp)
                            .height(68.dp)
                            .align(Alignment.CenterStart)
                            .offset { IntOffset((-lensLeftPx).roundToInt(), 0) }
                    ) {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                tabs.forEach { tab ->
                                    NavTabItem(
                                        tab = tab,
                                        isSelected = true,
                                        isMagnified = isLiquidGlassEnabled,
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                    )
                                }
                            }
                        }
                    }

                    // Specular Liquid Glass Overlay, Rim Highlights & Chromatic Refraction Glow
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val corner = CornerRadius(h / 2f, h / 2f)

                        // Radial glass refraction surface fill
                        drawRoundRect(
                            brush = Brush.radialGradient(
                                colors = if (isLiquidGlassEnabled) {
                                    listOf(
                                        Color.White.copy(alpha = 0.36f),
                                        Color.White.copy(alpha = 0.12f),
                                        AccentCyan.copy(alpha = 0.14f),
                                        Color.White.copy(alpha = 0.24f)
                                    )
                                } else {
                                    listOf(
                                        Color.White.copy(alpha = 0.18f),
                                        Color.White.copy(alpha = 0.08f)
                                    )
                                },
                                center = Offset(w * 0.5f, h * 0.18f),
                                radius = w * 0.85f
                            ),
                            cornerRadius = corner
                        )

                        // Upper specular reflection arc (liquid lens dome shine)
                        if (isLiquidGlassEnabled) {
                            drawRoundRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.62f),
                                        Color.Transparent
                                    ),
                                    startY = 3.dp.toPx(),
                                    endY = h * 0.42f
                                ),
                                topLeft = Offset(w * 0.12f, 3.dp.toPx()),
                                size = Size(w * 0.76f, h * 0.36f),
                                cornerRadius = CornerRadius(h, h)
                            )

                            // Bottom cyan chromatic refraction edge glow
                            drawRoundRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        AccentCyan.copy(alpha = if (isDragging) 0.55f else 0.38f)
                                    ),
                                    startY = h * 0.65f,
                                    endY = h
                                ),
                                cornerRadius = corner
                            )
                        }

                        // Outer crisp glass rim border
                        drawRoundRect(
                            color = Color.White.copy(alpha = if (isDragging) 0.88f else 0.58f),
                            cornerRadius = corner,
                            style = Stroke(width = if (isDragging) 1.8.dp.toPx() else 1.4.dp.toPx())
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NavTabItem(
    tab: AppTab,
    isSelected: Boolean,
    isMagnified: Boolean,
    modifier: Modifier = Modifier
) {
    val icon: ImageVector = when (tab) {
        AppTab.HOME -> Icons.Filled.Home
        AppTab.RECITERS -> Icons.Outlined.PersonSearch
        AppTab.PLAYLISTS -> Icons.Filled.GridView
        AppTab.SETTINGS -> Icons.Outlined.Settings
        AppTab.SEARCH -> Icons.Outlined.Search
    }

    val tint = if (isSelected || isMagnified) Color.White else Color.White.copy(alpha = 0.65f)
    val scale = if (isMagnified) 1.16f else 1f

    Column(
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = tab.labelAr,
                tint = tint,
                modifier = Modifier.size(23.dp)
            )
            if (tab == AppTab.RECITERS) {
                Icon(
                    imageVector = Icons.Outlined.GraphicEq,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier
                        .size(11.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 5.dp, y = (-2).dp)
                )
            }
        }
        Text(
            text = tab.labelAr,
            color = tint,
            fontFamily = CairoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 11.5.sp,
            lineHeight = 14.sp,
            maxLines = 1
        )
    }
}
