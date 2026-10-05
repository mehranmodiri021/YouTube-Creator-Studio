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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.ScheduleSend
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.core.engine.RecommendationEngine
import com.example.core.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    channel: ChannelProfile,
    stats: ChannelStatsOverview,
    videos: List<VideoItem>,
    onNavigateToVideoAnalyzer: (String) -> Unit,
    onNavigateToAiCoach: () -> Unit,
    onNavigateToMonetization: () -> Unit,
    onNavigateToDoctor: () -> Unit,
    onNavigateToPlanner: () -> Unit,
    onNavigateToTitleStudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    val alerts = remember(channel, stats, videos) {
        RecommendationEngine.generateSmartAlerts(channel, stats, videos)
    }

    val dailyActions = remember(channel, stats) {
        RecommendationEngine.getDailySmartActionItems(channel, stats)
    }

    val bestVideo = remember(videos) { videos.maxByOrNull { it.views } }
    val worstVideo = remember(videos) { videos.filter { !it.isShort }.minByOrNull { it.views } }

    var selectedChartMetric by remember { mutableStateOf("views") } // views, subs, watchTime

    val chartPoints = remember(selectedChartMetric, stats) {
        when (selectedChartMetric) {
            "views" -> listOf(18f, 22f, 20f, 28f, 32f, 31f, 38.4f)
            "subs" -> listOf(10f, 12f, 15f, 18f, 22f, 24f, 29.5f)
            else -> listOf(120f, 150f, 140f, 190f, 210f, 230f, 280f)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(StudioBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Module 29: Smart Dynamic Home Screen Banner
        item {
            SmartHomeDynamicBanner(
                stats = stats,
                onFixCtrClick = onNavigateToTitleStudio,
                onCoachClick = onNavigateToAiCoach
            )
        }

        // Quick Metric Grid (Subscribers, Views, Watch Time, CTR)
        item {
            SectionHeader(
                title = "آمار لحظه‌ای کانال",
                subtitle = "عملکرد ۲۸ روز گذشته در مقایسه با دوره قبل",
                icon = { Icon(Icons.Default.Speed, contentDescription = null, tint = MetricCyan) }
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard(
                    title = "سابسکرایبر کل",
                    value = formatNumberPersian(stats.subscribers),
                    subtitle = "+${channel.dailyGrowth} در ۲۴ ساعت گذشته",
                    deltaPercent = stats.subscribersDeltaPercent,
                    icon = { Icon(Icons.Default.People, contentDescription = null, tint = MetricCyan) },
                    accentColor = MetricCyan,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToMonetization
                )

                MetricCard(
                    title = "بازدید (Views)",
                    value = formatNumberPersian(stats.views),
                    subtitle = "رشد ماهانه عالی",
                    deltaPercent = stats.viewsDeltaPercent,
                    icon = { Icon(Icons.Default.PlayCircle, contentDescription = null, tint = YouTubeRed) },
                    accentColor = YouTubeRed,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard(
                    title = "واچ‌تایم (ساعت)",
                    value = String.format(java.util.Locale.US, "%.0f", stats.watchTimeHours),
                    subtitle = "AVD: ${stats.averageViewDurationSeconds / 60}:${String.format(java.util.Locale.US, "%02d", stats.averageViewDurationSeconds % 60)}",
                    deltaPercent = stats.watchTimeDeltaPercent,
                    icon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = GrowthGreen) },
                    accentColor = GrowthGreen,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "نرخ کلیک (CTR)",
                    value = "${stats.ctrPercent}%",
                    subtitle = "ایمپرشن: ${formatNumberPersian(stats.impressions)}",
                    deltaPercent = stats.ctrDeltaPercent,
                    icon = { Icon(Icons.Default.AdsClick, contentDescription = null, tint = WarningAmber) },
                    accentColor = WarningAmber,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToTitleStudio
                )
            }
        }

        // Secondary Metrics Row (APV, Impressions, Likes, Comments, Shares)
        item {
            StudioGlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    MiniStatItem(label = "میانگین تماشا (APV)", value = "${stats.averagePercentageViewed.toInt()}%", icon = Icons.Default.Visibility)
                    MiniStatItem(label = "لایک‌ها", value = formatNumberPersian(stats.likesCount), icon = Icons.Default.ThumbUp)
                    MiniStatItem(label = "کامنت‌ها", value = formatNumberPersian(stats.commentsCount), icon = Icons.AutoMirrored.Filled.Comment)
                    MiniStatItem(label = "اشتراک‌گذاری", value = formatNumberPersian(stats.sharesCount), icon = Icons.Default.Share)
                }
            }
        }

        // Growth Chart with metric selector tabs
        item {
            StudioGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "نمودار پویای رشد",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            ChartTabChip(label = "بازدید", isSelected = selectedChartMetric == "views") { selectedChartMetric = "views" }
                            ChartTabChip(label = "دنبال‌کننده", isSelected = selectedChartMetric == "subs") { selectedChartMetric = "subs" }
                            ChartTabChip(label = "واچ‌تایم", isSelected = selectedChartMetric == "watchTime") { selectedChartMetric = "watchTime" }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    SmoothLineChart(
                        points = chartPoints,
                        lineColor = when (selectedChartMetric) {
                            "views" -> YouTubeRed
                            "subs" -> MetricCyan
                            else -> GrowthGreen
                        }
                    )
                }
            }
        }

        // "امروز چه کاری انجام بدهم؟" - Daily Smart Action Recommendations
        item {
            StudioGlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = AiPurple.copy(alpha = 0.4f),
                glowColor = GlowPurple
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(AiPurple.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AiPurple, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "امروز چه کاری انجام بدهم؟",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }

                        TextButton(onClick = onNavigateToAiCoach) {
                            Text(text = "مشاوره با مربی AI", color = AiPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    dailyActions.forEachIndexed { idx, action ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(StudioCardHover)
                                    .border(1.dp, AiPurple.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "${idx + 1}", color = AiPurple, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = action,
                                color = TextSecondary,
                                fontSize = 13.sp,
                                lineHeight = 20.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Module 21: Smart Alerts Feed
        item {
            SectionHeader(
                title = "هشدارهای هوشمند الگوریتم",
                subtitle = "نکات حیاتی نیازمند اقدام در کانال",
                icon = { Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = WarningAmber) }
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                alerts.take(2).forEach { alert ->
                    val alertColor = when (alert.type) {
                        AlertType.CTR_DROP -> CriticalRed
                        AlertType.GROWTH_SURGE -> GrowthGreen
                        AlertType.MILESTONE_NEAR -> WarningAmber
                        else -> MetricCyan
                    }

                    StudioGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = alertColor.copy(alpha = 0.3f)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(alertColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (alert.type) {
                                        AlertType.CTR_DROP -> Icons.Default.Warning
                                        AlertType.GROWTH_SURGE -> Icons.Default.RocketLaunch
                                        AlertType.MILESTONE_NEAR -> Icons.Default.EmojiEvents
                                        else -> Icons.Default.Lightbulb
                                    },
                                    contentDescription = null,
                                    tint = alertColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = alert.title,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = alert.message,
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Best & Worst Performing Video Comparison
        item {
            SectionHeader(
                title = "بهترین و بدترین ویدیوی اخیر",
                subtitle = "مقایسه رفتار مخاطب برای الگوبرداری",
                icon = { Icon(Icons.AutoMirrored.Filled.CompareArrows, contentDescription = null, tint = MetricCyan) }
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (bestVideo != null) {
                    VideoMiniCard(
                        video = bestVideo,
                        badgeText = "🏆 بهترین",
                        badgeColor = GrowthGreen,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToVideoAnalyzer(bestVideo.id) }
                    )
                }

                if (worstVideo != null) {
                    VideoMiniCard(
                        video = worstVideo,
                        badgeText = "⚠️ نیازمند بهینه‌سازی",
                        badgeColor = CriticalRed,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToVideoAnalyzer(worstVideo.id) }
                    )
                }
            }
        }

        // Best Upload Time & Smart Publishing Summary
        item {
            StudioGlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(YouTubeRed.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ScheduleSend, contentDescription = null, tint = YouTubeRed, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "بهترین زمان انتشار امروز", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "پنجشنبه ساعت ۱۸:۳۰ تا ۲۱:۰۰ (اوج ترافیک)", color = GrowthGreen, fontSize = 12.sp)
                        }
                    }

                    IconButton(onClick = onNavigateToPlanner) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "تقویم", tint = TextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
fun SmartHomeDynamicBanner(
    stats: ChannelStatsOverview,
    onFixCtrClick: () -> Unit,
    onCoachClick: () -> Unit
) {
    val bannerText: String
    val bannerAction: String
    val bannerColor: Color
    val bannerIcon: @Composable () -> Unit

    if (stats.ctrPercent < 6.0) {
        bannerText = "🔴 مهم‌ترین چالش امروز کانال: نرخ کلیک (CTR) کمتر از حد استاندارد است."
        bannerAction = "اصلاح کاور و تایتل"
        bannerColor = CriticalRed
        bannerIcon = { Icon(Icons.Default.ReportProblem, contentDescription = null, tint = CriticalRed) }
    } else {
        bannerText = "🟢 وضعیت کانال صعودی است! امروز بهترین فرصت برای ضبط ویدیوی جدید است."
        bannerAction = "برنامه رشد هوشمند"
        bannerColor = GrowthGreen
        bannerIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GrowthGreen) }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(bannerColor.copy(alpha = 0.2f), StudioCard)
                )
            )
            .border(1.dp, bannerColor.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                bannerIcon()
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = bannerText,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { if (stats.ctrPercent < 6.0) onFixCtrClick() else onCoachClick() },
                colors = ButtonDefaults.buttonColors(containerColor = bannerColor),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(text = bannerAction, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MiniStatItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text(text = label, color = TextMuted, fontSize = 10.sp)
    }
}

@Composable
fun ChartTabChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) YouTubeRed else StudioCardHover)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else TextSecondary,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun VideoMiniCard(
    video: VideoItem,
    badgeText: String,
    badgeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    StudioGlassCard(
        modifier = modifier.clickable { onClick() },
        borderColor = badgeColor.copy(alpha = 0.3f)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                AsyncImage(
                    model = video.thumbnailUrl,
                    contentDescription = video.title,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = badgeText, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = video.title,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "${formatNumberPersian(video.views)} ویو", color = TextSecondary, fontSize = 11.sp)
                Text(text = "CTR: ${video.ctr}%", color = if (video.ctr >= 7.0) GrowthGreen else CriticalRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
