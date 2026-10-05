package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.core.model.ChannelProfile
import com.example.ui.theme.*
import java.text.DecimalFormat
import java.util.Locale

@Composable
fun StudioGlassCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    backgroundColor: Color = StudioCard,
    borderColor: Color = StudioBorder,
    glowColor: Color? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .then(if (glowColor != null) Modifier.shadow(8.dp, shape, ambientColor = glowColor, spotColor = glowColor) else Modifier)
            .clip(shape)
            .background(backgroundColor)
            .border(1.dp, borderColor, shape)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(16.dp),
        content = content
    )
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String? = null,
    deltaPercent: Double? = null,
    icon: @Composable () -> Unit,
    accentColor: Color = MetricCyan,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    StudioGlassCard(
        modifier = modifier.testTag("metric_card_${title.replace(" ", "_")}"),
        onClick = onClick,
        borderColor = accentColor.copy(alpha = 0.25f)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }

                if (deltaPercent != null) {
                    val isPositive = deltaPercent >= 0
                    val deltaBg = if (isPositive) GrowthGreen.copy(alpha = 0.15f) else CriticalRed.copy(alpha = 0.15f)
                    val deltaFg = if (isPositive) GrowthGreen else CriticalRed

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(deltaBg)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isPositive) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                            contentDescription = null,
                            tint = deltaFg,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${if (isPositive) "+" else ""}${String.format(Locale.US, "%.1f", deltaPercent)}%",
                            color = deltaFg,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                color = TextSecondary,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            if (subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * Interactive Bezier Line Chart for Channel Growth
 */
@Composable
fun SmoothLineChart(
    points: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = YouTubeRed,
    gradientFill: Boolean = true
) {
    if (points.isEmpty()) return

    val maxVal = (points.maxOrNull() ?: 1f).coerceAtLeast(1f)
    val minVal = (points.minOrNull() ?: 0f).coerceAtLeast(0f)
    val range = (maxVal - minVal).coerceAtLeast(0.1f)

    var animationTrigger by remember { mutableStateOf(0f) }
    val progress by animateFloatAsState(
        targetValue = animationTrigger,
        animationSpec = tween(durationMillis = 900),
        label = "chartAnim"
    )

    LaunchedEffect(Unit) {
        animationTrigger = 1f
    }

    Canvas(modifier = modifier.fillMaxWidth().height(140.dp)) {
        val width = size.width
        val height = size.height
        val spacing = width / (points.size - 1).coerceAtLeast(1)

        val path = Path()
        val fillPath = Path()

        val normalizedPoints = points.mapIndexed { index, p ->
            val normalizedY = 1f - ((p - minVal) / range)
            val y = height * 0.1f + (normalizedY * (height * 0.8f))
            val animatedY = height - ((height - y) * progress)
            Offset(index * spacing, animatedY)
        }

        if (normalizedPoints.isNotEmpty()) {
            path.moveTo(normalizedPoints[0].x, normalizedPoints[0].y)
            fillPath.moveTo(normalizedPoints[0].x, height)
            fillPath.lineTo(normalizedPoints[0].x, normalizedPoints[0].y)

            for (i in 0 until normalizedPoints.size - 1) {
                val p0 = normalizedPoints[i]
                val p1 = normalizedPoints[i + 1]
                val controlPoint1 = Offset(p0.x + (p1.x - p0.x) / 2, p0.y)
                val controlPoint2 = Offset(p0.x + (p1.x - p0.x) / 2, p1.y)
                path.cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, p1.x, p1.y)
                fillPath.cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, p1.x, p1.y)
            }

            fillPath.lineTo(normalizedPoints.last().x, height)
            fillPath.close()

            if (gradientFill) {
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(lineColor.copy(alpha = 0.35f), lineColor.copy(alpha = 0.0f))
                    )
                )
            }

            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Draw glowing peak point
            val lastPoint = normalizedPoints.last()
            drawCircle(
                color = lineColor,
                radius = 5.dp.toPx(),
                center = lastPoint
            )
            drawCircle(
                color = Color.White,
                radius = 2.5.dp.toPx(),
                center = lastPoint
            )
        }
    }
}

/**
 * Retention Curve Chart for Video Analyzer
 */
@Composable
fun RetentionDropChart(
    retentionPoints: List<Float>,
    modifier: Modifier = Modifier
) {
    val labels = listOf("شروع (0s)", "۳۰ ثانیه", "۱ دقیقه", "۲ دقیقه", "پایان")
    val pts = if (retentionPoints.size >= 5) retentionPoints else listOf(100f, 72f, 58f, 49f, 42f)

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
            val width = size.width
            val height = size.height
            val spacing = width / (pts.size - 1)

            // Guidelines
            for (level in listOf(1.0f, 0.5f, 0.0f)) {
                val y = height * (1f - level * 0.85f)
                drawLine(
                    color = StudioBorder,
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            val path = Path()
            pts.forEachIndexed { i, p ->
                val x = i * spacing
                val y = height * 0.1f + ((100f - p) / 100f) * (height * 0.8f)
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)

                // Draw node
                val isDrop = i == 1 && pts[0] - p > 25f
                drawCircle(
                    color = if (isDrop) CriticalRed else GrowthGreen,
                    radius = 4.dp.toPx(),
                    center = Offset(x, y)
                )
            }

            drawPath(
                path = path,
                color = MetricCyan,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            labels.forEachIndexed { idx, lbl ->
                Text(
                    text = "$lbl\n${pts.getOrNull(idx)?.toInt() ?: 0}%",
                    color = TextMuted,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Traffic Sources Bar Chart
 */
@Composable
fun TrafficSourcesList(
    sources: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        sources.forEach { (sourceName, percent) ->
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = sourceName, color = TextPrimary, fontSize = 13.sp)
                    Text(text = "${String.format(Locale.US, "%.1f", percent)}%", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(StudioCardHover)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth((percent / 100f).toFloat().coerceIn(0f, 1f))
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(YouTubeRed, MetricCyan)
                                )
                            )
                    )
                }
            }
        }
    }
}

/**
 * Circular Radial Progress
 */
@Composable
fun CircularProgressIndicatorWithLabel(
    progress: Float,
    label: String,
    valueText: String,
    modifier: Modifier = Modifier,
    color: Color = YouTubeRed,
    strokeWidth: Dp = 8.dp,
    size: Dp = 100.dp
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = strokeWidth.toPx()
            val arcSize = Size(this.size.width - stroke, this.size.height - stroke)
            val topLeft = Offset(stroke / 2, stroke / 2)

            // Background circle
            drawArc(
                color = StudioBorder,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            // Progress arc
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * progress.coerceIn(0f, 1f),
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = valueText, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = label, color = TextMuted, fontSize = 10.sp)
        }
    }
}

@Composable
fun ChannelSelectorTopBar(
    channel: ChannelProfile,
    onSwitchChannelClick: () -> Unit,
    onDoctorClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { onSwitchChannelClick() }
                .padding(vertical = 4.dp, horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, YouTubeRed, CircleShape)
            ) {
                AsyncImage(
                    model = channel.avatarUrl,
                    contentDescription = "آواتار کانال",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = channel.title,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "تغییر کانال",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = "${channel.customUrl} • ${formatNumberPersian(channel.subscriberCount)} سابسکرایبر",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        // Channel Health Score & Doctor trigger
        Button(
            onClick = onDoctorClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = StudioCardHover
            ),
            shape = RoundedCornerShape(20.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.border(1.dp, GrowthGreen.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
        ) {
            Icon(
                imageVector = Icons.Default.Healing,
                contentDescription = null,
                tint = GrowthGreen,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "سلامت ${channel.healthScore}٪",
                color = GrowthGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Column {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        }

        if (actionText != null && onActionClick != null) {
            TextButton(onClick = onActionClick) {
                Text(text = actionText, color = MetricCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

fun formatNumberPersian(num: Long): String {
    return when {
        num >= 1_000_000 -> String.format(Locale.US, "%.1fM", num / 1_000_000.0)
        num >= 1_000 -> String.format(Locale.US, "%.1fK", num / 1_000.0)
        else -> DecimalFormat("#,###").format(num)
    }
}
