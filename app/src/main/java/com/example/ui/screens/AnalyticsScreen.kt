package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.engine.AnalyticsEngine
import com.example.core.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun AnalyticsScreen(
    channel: ChannelProfile,
    stats: ChannelStatsOverview,
    videos: List<VideoItem>,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("نمای کلی", "دسترسی (Reach)", "تعامل (Engagement)", "مخاطبان (Audience)", "درآمد (Revenue)")

    val trafficSources = listOf(
        "YouTube Search (جستجوی یوتیوب)" to 44.5,
        "Suggested Videos (ویدیوهای پیشنهادی)" to 28.2,
        "Browse Features (صفحه اصلی و اشتراک)" to 16.8,
        "External (تلگرام، وب و شبکه‌های اجتماعی)" to 6.3,
        "Shorts Feed (فید شورتس)" to 4.2
    )

    val topCountries = listOf(
        "ایران (Iran)" to 62.0,
        "ترکیه (Turkey)" to 14.5,
        "آلمان (Germany)" to 8.2,
        "کانادا (Canada)" to 5.4,
        "سایر کشورها" to 9.9
    )

    val revenueData = remember(channel) {
        AnalyticsEngine.calculateRevenueMetrics(channel.viewCount, stats.rpmUsd)
    }

    val topConvertingVideos = remember(videos) {
        AnalyticsEngine.findTopConversionVideos(videos)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(StudioBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tab Navigation Header
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = StudioSurface,
                contentColor = YouTubeRed,
                edgePadding = 0.dp,
                divider = {},
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, StudioBorder, RoundedCornerShape(12.dp))
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTab == index) YouTubeRed else TextSecondary,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }
        }

        when (selectedTab) {
            0 -> {
                // Tab 0: Overview & Benchmarks
                item {
                    SectionHeader(
                        title = "معیارهای کلیدی ماه جاری",
                        subtitle = "عملکرد کلی کانال در مقایسه با دوره ۲۸ روز قبل",
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = null, tint = MetricCyan) }
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MetricCard(
                            title = "بازدید کل",
                            value = formatNumberPersian(stats.views),
                            deltaPercent = stats.viewsDeltaPercent,
                            icon = { Icon(Icons.Default.PlayArrow, contentDescription = null, tint = YouTubeRed) },
                            accentColor = YouTubeRed,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "ساعات واچ‌تایم",
                            value = "${stats.watchTimeHours.toInt()}h",
                            deltaPercent = stats.watchTimeDeltaPercent,
                            icon = { Icon(Icons.Default.AccessTime, contentDescription = null, tint = GrowthGreen) },
                            accentColor = GrowthGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MetricCard(
                            title = "نرخ کلیک (CTR)",
                            value = "${stats.ctrPercent}%",
                            deltaPercent = stats.ctrDeltaPercent,
                            icon = { Icon(Icons.Default.AdsClick, contentDescription = null, tint = WarningAmber) },
                            accentColor = WarningAmber,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "درصد تماشا (APV)",
                            value = "${stats.averagePercentageViewed.toInt()}%",
                            deltaPercent = 2.4,
                            icon = { Icon(Icons.Default.Visibility, contentDescription = null, tint = MetricCyan) },
                            accentColor = MetricCyan,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Module 18: Channel Benchmark
                item {
                    StudioGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "بنچمارک کانال در برابر میانگین حوزه ${channel.category}",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            BenchmarkRow(label = "نرخ کلیک (CTR)", channelValue = "${stats.ctrPercent}%", nicheAverage = "5.8%", isBetter = stats.ctrPercent > 5.8)
                            BenchmarkRow(label = "میانگین درصد تماشا (APV)", channelValue = "${stats.averagePercentageViewed.toInt()}%", nicheAverage = "42%", isBetter = stats.averagePercentageViewed > 42.0)
                            BenchmarkRow(label = "تعداد ویدیو در ماه", channelValue = "۴ ویدیو", nicheAverage = "۳.۲ ویدیو", isBetter = true)
                            BenchmarkRow(label = "نرخ تعامل (لایک/ویو)", channelValue = "۷.۸٪", nicheAverage = "۴.۵٪", isBetter = true)
                        }
                    }
                }
            }

            1 -> {
                // Tab 1: Reach & Traffic Sources
                item {
                    SectionHeader(
                        title = "منابع اصلی ترافیک (Traffic Sources)",
                        subtitle = "کاربران چگونه ویدیوهای شما را پیدا می‌کنند؟",
                        icon = { Icon(Icons.Default.TravelExplore, contentDescription = null, tint = MetricCyan) }
                    )

                    StudioGlassCard(modifier = Modifier.fillMaxWidth()) {
                        TrafficSourcesList(sources = trafficSources)
                    }
                }

                item {
                    StudioGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(text = "ایمپرشن و نرخ تبدیل به بازدید", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "یوتیوب ${formatNumberPersian(stats.impressions)} بار تامبنیل شما را به کاربران نشان داده است که از این میان ${stats.ctrPercent}٪ به بازدید واقعی تبدیل شده است.",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            2 -> {
                // Tab 2: Engagement
                item {
                    SectionHeader(
                        title = "شاخص‌های نگه‌داشت و تعامل",
                        subtitle = "کیفیت همراهی مخاطب تا انتهای ویدیو",
                        icon = { Icon(Icons.Default.HourglassBottom, contentDescription = null, tint = GrowthGreen) }
                    )

                    StudioGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(text = "میانگین ماندگاری در ویدیوها (Retention Curve)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            RetentionDropChart(retentionPoints = listOf(100f, 74f, 62f, 54f, 48f))
                        }
                    }
                }
            }

            3 -> {
                // Tab 3: Audience & Demographics
                item {
                    SectionHeader(
                        title = "دسترسی جغرافیایی و مخاطبان",
                        subtitle = "شناخت دقیق بینندگان کانال",
                        icon = { Icon(Icons.Default.Public, contentDescription = null, tint = MetricCyan) }
                    )

                    StudioGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(text = "پربازدیدترین کشورها", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            TrafficSourcesList(sources = topCountries)
                        }
                    }
                }

                // Subscribed vs Non-Subscribed
                item {
                    StudioGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicatorWithLabel(
                                progress = 0.88f,
                                label = "غیر سابسکرایب",
                                valueText = "۸۸٪",
                                color = YouTubeRed
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(text = "فرصت جذب سابسکرایبر جدید! 🎯", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "۸۸٪ از کسانی که ویدیوهای شما را دیده‌اند هنوز سابسکرایب نکرده‌اند. افزودن یک دعوت به اقدام (CTA) در دقیقه ۳ می‌تواند سابسکرایبرها را دو برابر کند.",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                // Special: Subscriber Conversion Analysis (which videos convert)
                item {
                    SectionHeader(
                        title = "ویدیوهایی با بیشترین تبدیل به سابسکرایبر",
                        subtitle = "کدام محتوا واقعاً وفاداری می‌سازد؟",
                        icon = { Icon(Icons.Default.PersonAdd, contentDescription = null, tint = GrowthGreen) }
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        topConvertingVideos.take(3).forEach { (vid, rate) ->
                            StudioGlassCard(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = vid.title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                        Text(text = "${vid.subscribersGained} سابسکرایبر جذب شده از ${formatNumberPersian(vid.views)} ویو", color = TextMuted, fontSize = 11.sp)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(GrowthGreen.copy(alpha = 0.15f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(text = "+${vid.subscribersGained} Sub", color = GrowthGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            4 -> {
                // Tab 4: Revenue Intelligence (Module 31)
                item {
                    SectionHeader(
                        title = "هوش مالی و درآمدزایی (Revenue Intelligence)",
                        subtitle = "تخمین درآمد، RPM و پرسودترین موضوعات",
                        icon = { Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = GrowthGreen) }
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MetricCard(
                            title = "RPM میانگین",
                            value = "\$${revenueData.averageRpm}",
                            subtitle = "درآمد به ازای ۱۰۰۰ ویو",
                            icon = { Icon(Icons.Default.AttachMoney, contentDescription = null, tint = GrowthGreen) },
                            accentColor = GrowthGreen,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "CPM تخمینی",
                            value = "\$${String.format(java.util.Locale.US, "%.2f", revenueData.averageCpm)}",
                            subtitle = "پرداخت تبلیغ‌دهندگان",
                            icon = { Icon(Icons.Default.Payments, contentDescription = null, tint = MetricCyan) },
                            accentColor = MetricCyan,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    StudioGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(text = "پرسودترین فرمت‌های تولید محتوا برای کانال شما", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(10.dp))

                            revenueData.topEarningFormats.forEach { (format, rpm) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = format, color = TextSecondary, fontSize = 13.sp)
                                    Text(text = rpm, color = GrowthGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BenchmarkRow(label: String, channelValue: String, nicheAverage: String, isBetter: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextSecondary, fontSize = 13.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = channelValue, color = if (isBetter) GrowthGreen else CriticalRed, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "(میانگین: $nicheAverage)", color = TextMuted, fontSize = 11.sp)
        }
    }
}
