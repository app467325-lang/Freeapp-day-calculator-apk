package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.model.PortraitStyle
import com.example.model.Reciter

@Composable
fun ReciterPortrait(
    reciter: Reciter,
    modifier: Modifier = Modifier
) {
    var imageLoadFailed by remember(reciter.id) { mutableStateOf(false) }

    Box(modifier = modifier) {
        // Always draw the custom SVG portrait underneath as fallback or primary
        ReciterVectorPortraitCanvas(
            style = reciter.portraitStyle,
            modifier = Modifier.fillMaxSize()
        )

        // If the reciter has an external photo URL (e.g. Mishary or Abdul Basit), render with Coil
        if (reciter.photoUrl != null && !imageLoadFailed) {
            AsyncImage(
                model = reciter.photoUrl,
                contentDescription = reciter.name,
                contentScale = ContentScale.Crop,
                onError = { imageLoadFailed = true },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun ReciterVectorPortraitCanvas(
    style: PortraitStyle,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.background(style.bgBottom)) {
        val w = size.width
        val h = size.height
        val sx = w / 300f
        val sy = h / 300f

        // 1. Radial Background
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(style.bgTop, style.bgBottom),
                center = Offset(w * 0.5f, h * 0.4f),
                radius = w * 0.75f
            ),
            size = size
        )

        // 2. Shoulders and Robe: M35 300 C40 230 85 210 150 210 C215 210 260 230 265 300 Z
        val robePath = Path().apply {
            moveTo(35f * sx, 300f * sy)
            cubicTo(40f * sx, 230f * sy, 85f * sx, 210f * sy, 150f * sx, 210f * sy)
            cubicTo(215f * sx, 210f * sy, 260f * sx, 230f * sy, 265f * sx, 300f * sy)
            close()
        }
        drawPath(path = robePath, color = style.robeColor)

        // 3. Flowing Ghutra / Shemagh:
        // M58 300 C60 155 75 42 150 42 C225 42 240 155 242 300 C215 260 205 190 198 150 L102 150 C95 190 85 260 58 300 Z
        val ghutraBrush = Brush.linearGradient(
            colors = listOf(style.headwearColor, style.headwearShade),
            start = Offset(58f * sx, 42f * sy),
            end = Offset(242f * sx, 300f * sy)
        )
        val ghutraPath = Path().apply {
            moveTo(58f * sx, 300f * sy)
            cubicTo(60f * sx, 155f * sy, 75f * sx, 42f * sy, 150f * sx, 42f * sy)
            cubicTo(225f * sx, 42f * sy, 240f * sx, 155f * sy, 242f * sx, 300f * sy)
            cubicTo(215f * sx, 260f * sy, 205f * sx, 190f * sy, 198f * sx, 150f * sy)
            lineTo(102f * sx, 150f * sy)
            cubicTo(95f * sx, 190f * sy, 85f * sx, 260f * sy, 58f * sx, 300f * sy)
            close()
        }
        drawPath(path = ghutraPath, brush = ghutraBrush)

        // 4. Neck: rect x="122" y="175" width="56" height="45" rx="18"
        drawRoundRect(
            color = style.skinDark,
            topLeft = Offset(122f * sx, 175f * sy),
            size = Size(56f * sx, 45f * sy),
            cornerRadius = CornerRadius(18f * sx, 18f * sy)
        )

        // 5. Face: M98 105 C98 62 202 62 202 105 C202 168 182 206 150 206 C118 206 98 168 98 105 Z
        val skinBrush = Brush.verticalGradient(
            colors = listOf(style.skinLight, style.skinDark),
            startY = 62f * sy,
            endY = 206f * sy
        )
        val facePath = Path().apply {
            moveTo(98f * sx, 105f * sy)
            cubicTo(98f * sx, 62f * sy, 202f * sx, 62f * sy, 202f * sx, 105f * sy)
            cubicTo(202f * sx, 168f * sy, 182f * sx, 206f * sy, 150f * sx, 206f * sy)
            cubicTo(118f * sx, 206f * sy, 98f * sx, 168f * sy, 98f * sx, 105f * sy)
            close()
        }
        drawPath(path = facePath, brush = skinBrush)

        // 6. Upper Ghutra Fold around face:
        // M84 120 C84 54 114 36 150 36 C186 36 216 54 216 120 C210 78 184 62 150 62 C116 62 90 78 84 120 Z
        val upperFoldPath = Path().apply {
            moveTo(84f * sx, 120f * sy)
            cubicTo(84f * sx, 54f * sy, 114f * sx, 36f * sy, 150f * sx, 36f * sy)
            cubicTo(186f * sx, 36f * sy, 216f * sx, 54f * sy, 216f * sx, 120f * sy)
            cubicTo(210f * sx, 78f * sy, 184f * sx, 62f * sy, 150f * sx, 62f * sy)
            cubicTo(116f * sx, 62f * sy, 90f * sx, 78f * sy, 84f * sx, 120f * sy)
            close()
        }
        drawPath(path = upperFoldPath, brush = ghutraBrush)

        // 7. Agal (if enabled): ellipse cx="150" cy="54" rx="54" ry="11"
        if (style.hasAgal) {
            drawOval(
                color = Color(0xFF111827),
                topLeft = Offset((150f - 54f) * sx, (54f - 11f) * sy),
                size = Size(108f * sx, 22f * sy)
            )
            drawOval(
                color = Color(0xFF374151),
                topLeft = Offset((150f - 54f) * sx, (54f - 11f) * sy),
                size = Size(108f * sx, 22f * sy),
                style = Stroke(width = 2f * sx)
            )
        }

        // 8. Eyebrows: M115 114 Q128 108 139 113 and M161 113 Q172 108 185 114
        val browPath = Path().apply {
            moveTo(115f * sx, 114f * sy)
            quadraticTo(128f * sx, 108f * sy, 139f * sx, 113f * sy)
            moveTo(161f * sx, 113f * sy)
            quadraticTo(172f * sx, 108f * sy, 185f * sx, 114f * sy)
        }
        drawPath(
            path = browPath,
            color = Color(0xFF1C1917),
            style = Stroke(width = 4.2f * sx, cap = StrokeCap.Round)
        )

        // 9. Eyes: ellipse cx="127" cy="123" rx="6.5" ry="4.2" & cx="173"
        drawOval(
            color = Color(0xFF18181B),
            topLeft = Offset((127f - 6.5f) * sx, (123f - 4.2f) * sy),
            size = Size(13f * sx, 8.4f * sy)
        )
        drawOval(
            color = Color(0xFF18181B),
            topLeft = Offset((173f - 6.5f) * sx, (123f - 4.2f) * sy),
            size = Size(13f * sx, 8.4f * sy)
        )
        drawCircle(
            color = Color.White,
            radius = 1.6f * sx,
            center = Offset(125f * sx, 122f * sy)
        )
        drawCircle(
            color = Color.White,
            radius = 1.6f * sx,
            center = Offset(171f * sx, 122f * sy)
        )

        // 10. Nose: M150 118 L146 146 Q150 150 155 146
        val nosePath = Path().apply {
            moveTo(150f * sx, 118f * sy)
            lineTo(146f * sx, 146f * sy)
            quadraticTo(150f * sx, 150f * sy, 155f * sx, 146f * sy)
        }
        drawPath(
            path = nosePath,
            color = Color(0xFFB48364),
            style = Stroke(width = 2.5f * sx, cap = StrokeCap.Round)
        )

        // 11. Full Beard:
        // M97 132 C99 156 114 166 130 164 C138 162 144 159 150 159 C156 159 162 162 170 164 C186 166 201 156 203 132 C205 178 190 226 150 228 C110 226 95 178 97 132 Z
        val beardPath = Path().apply {
            moveTo(97f * sx, 132f * sy)
            cubicTo(99f * sx, 156f * sy, 114f * sx, 166f * sy, 130f * sx, 164f * sy)
            cubicTo(138f * sx, 162f * sy, 144f * sx, 159f * sy, 150f * sx, 159f * sy)
            cubicTo(156f * sx, 159f * sy, 162f * sx, 162f * sy, 170f * sx, 164f * sy)
            cubicTo(186f * sx, 166f * sy, 201f * sx, 156f * sy, 203f * sx, 132f * sy)
            cubicTo(205f * sx, 178f * sy, 190f * sx, 226f * sy, 150f * sx, 228f * sy)
            cubicTo(110f * sx, 226f * sy, 95f * sx, 178f * sy, 97f * sx, 132f * sy)
            close()
        }
        drawPath(path = beardPath, color = style.beardColor)

        // 12. Mustache & Mouth:
        // M128 162 Q150 154 172 162 Q160 168 150 166 Q140 168 128 162 Z
        val mustachePath = Path().apply {
            moveTo(128f * sx, 162f * sy)
            quadraticTo(150f * sx, 154f * sy, 172f * sx, 162f * sy)
            quadraticTo(160f * sx, 168f * sy, 150f * sx, 166f * sy)
            quadraticTo(140f * sx, 168f * sy, 128f * sx, 162f * sy)
            close()
        }
        drawPath(path = mustachePath, color = Color(0xFF18181B))

        val mouthPath = Path().apply {
            moveTo(139f * sx, 170f * sy)
            quadraticTo(150f * sx, 175f * sy, 161f * sx, 170f * sy)
        }
        drawPath(
            path = mouthPath,
            color = Color(0xFF996B52),
            style = Stroke(width = 2f * sx, cap = StrokeCap.Round)
        )
    }
}
