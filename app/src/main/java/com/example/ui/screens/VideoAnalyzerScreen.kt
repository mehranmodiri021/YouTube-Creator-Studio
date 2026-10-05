package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.core.engine.AnalyticsEngine
import com.example.core.model.ChannelStatsOverview
import com.example.core.model.VideoItem
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun VideoAnalyzerScreen(
    videos: List<VideoItem>,
    stats: ChannelStatsOverview,
    initialVideoId: String? = null,
    onNavigateToTitleStudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedVideoId by remember { mutableStateOf(initialVideoId ?: videos.firstOrNull()?.id ?: "") }
    val currentVideo = remember(selectedVideoId, videos) {
        videos.find { it.id == selectedVideoId } ?: videos.first()
    }

    val evaluation = remember(currentVideo, stats) {
        AnalyticsEngine.evaluateVideo(currentVideo, stats.views / 4, stats.ctrPercent)
    }

    var selectedTimeframe by remember { mutableStateOf("28d") } // 24h, 48h, 7d, 28d

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(StudioBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Video Selector Carousel
        item {
            Text(
                text = "انتخاب ویدیو جهت آنالیز موشکافانه",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(videos) { vid ->
                    val isSelected = vid.id == currentVideo.id
                    Box(
                        modifier = Modifier
                            .width(130.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) StudioCardHover else StudioCard)
                            .border(1.5.dp, if (isSelected) YouTubeRed else StudioBorder, RoundedCornerShape(10.dp))
                            .clickable { selectedVideoId = vid.id }
                            .padding(6.dp)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(65.dp)
                                    .clip(RoundedCornerShape(6.dp))
                            ) {
                                AsyncImage(model = vid.thumbnailUrl, contentDescription = null, modifier = Modifier.fillMaxSize())
                                if (vid.isShort) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(4.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(YouTubeRed)
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(text = "Shorts", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = vid.title,
                                color = if (isSelected) TextPrimary else TextSecondary,
                                fontSize = 11.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // Active Video Header & Score Badge
        item {
            StudioGlassCard(modifier = Modifier.fillMaxWidth().testTag("video_detail_card")) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentVideo.title,
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 22.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "منتشر شده در ${currentVideo.publishedAt} • مدت: ${currentVideo.durationSeconds / 60} دقیقه و ${currentVideo.durationSeconds % 60} ثانیه",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Video Score Circle
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(if (evaluation.score >= 80) GrowthGreen.copy(alpha = 0.2f) else WarningAmber.copy(alpha = 0.2f))
                                .border(2.dp, if (evaluation.score >= 80) GrowthGreen else WarningAmber, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${evaluation.score}",
                                    color = if (evaluation.score >= 80) GrowthGreen else WarningAmber,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp
                                )
                                Text(text = "نمره", color = TextMuted, fontSize = 9.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Timeframe Chips (24h, 48h, 7d, 28d)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("24h" to "۲۴ ساعت اول", "48h" to "۴۸ ساعت", "7d" to "۷ روز", "28d" to "۲۸ روز").forEach { (tf, label) ->
                            val isSel = selectedTimeframe == tf
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSel) MetricCyan.copy(alpha = 0.2f) else StudioCardHover)
                                    .border(1.dp, if (isSel) MetricCyan else StudioBorder, RoundedCornerShape(12.dp))
                                    .clickable { selectedTimeframe = tf }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSel) MetricCyan else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }

        // Metrics Grid for this Video
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard(
                    title = "بازدید کل",
                    value = formatNumberPersian(currentVideo.views),
                    subtitle = "${if (currentVideo.performanceVsAverage >= 0) "+" else ""}${currentVideo.performanceVsAverage.toInt()}% نسبت به میانگین",
                    deltaPercent = currentVideo.performanceVsAverage,
                    icon = { Icon(Icons.Default.PlayCircle, contentDescription = null, tint = YouTubeRed) },
                    accentColor = YouTubeRed,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "نرخ کلیک (CTR)",
                    value = "${currentVideo.ctr}%",
                    subtitle = if (currentVideo.ctr >= 7.0) "بالاتر از نرمال" else "نیاز به کاور بهتر",
                    deltaPercent = if (currentVideo.ctr >= 7.0) 2.2 else -1.5,
                    icon = { Icon(Icons.Default.AdsClick, contentDescription = null, tint = WarningAmber) },
                    accentColor = WarningAmber,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard(
                    title = "درصد تماشا (APV)",
                    value = "${currentVideo.averagePercentageViewed.toInt()}%",
                    subtitle = "AVD: ${currentVideo.averageViewDurationSeconds / 60}:${String.format(java.util.Locale.US, "%02d", currentVideo.averageViewDurationSeconds % 60)}",
                    icon = { Icon(Icons.Default.Visibility, contentDescription = null, tint = MetricCyan) },
                    accentColor = MetricCyan,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "جذب سابسکرایبر",
                    value = "+${currentVideo.subscribersGained}",
                    subtitle = "${currentVideo.likes} لایک • ${currentVideo.comments} کامنت",
                    icon = { Icon(Icons.Default.PersonAdd, contentDescription = null, tint = GrowthGreen) },
                    accentColor = GrowthGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Module 6: AI Video Diagnosis & Retention Curve
        item {
            SectionHeader(
                title = "تشخیص هوشمند ویدیو (AI Video Diagnosis)",
                subtitle = "تحلیل نقاط ضعف و افت‌های احتمالی در طول ویدیو",
                icon = { Icon(Icons.Default.Psychology, contentDescription = null, tint = AiPurple) }
            )

            StudioGlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = AiPurple.copy(alpha = 0.4f),
                glowColor = GlowPurple
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "منحنی نگه‌داشت ثانیه‌ای (Audience Retention Curve)",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    RetentionDropChart(retentionPoints = currentVideo.retentionCurve)

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "💡 نظر کارشناسی هوش مصنوعی:",
                        color = AiPurple,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = evaluation.recommendation,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        lineHeight = 19.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onNavigateToTitleStudio,
                        colors = ButtonDefaults.buttonColors(containerColor = AiPurple),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "بهینه‌سازی تایتل و تامبنیل در Studio", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Strengths & Weaknesses
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Strengths
                StudioGlassCard(modifier = Modifier.weight(1f), borderColor = GrowthGreen.copy(alpha = 0.3f)) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ThumbUp, contentDescription = null, tint = GrowthGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "نقاط قوت", color = GrowthGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        evaluation.strengths.forEach { s ->
                            Text(text = "• $s", color = TextSecondary, fontSize = 11.sp, lineHeight = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }

                // Weaknesses
                StudioGlassCard(modifier = Modifier.weight(1f), borderColor = CriticalRed.copy(alpha = 0.3f)) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ThumbDown, contentDescription = null, tint = CriticalRed, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "نقاط ضعف", color = CriticalRed, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        evaluation.weaknesses.forEach { w ->
                            Text(text = "• $w", color = TextSecondary, fontSize = 11.sp, lineHeight = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
    }
}
