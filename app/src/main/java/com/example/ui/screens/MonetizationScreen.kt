package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun MonetizationScreen(
    monetizationProgress: MonetizationProgress,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedScenario by remember { mutableStateOf("current") } // current, medium, fast

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(StudioBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status Hero Banner
        item {
            val isMonetized = monetizationProgress.isFullyMonetized
            StudioGlassCard(
                modifier = Modifier.fillMaxWidth().testTag("monetization_hero_banner"),
                borderColor = if (isMonetized) GrowthGreen.copy(alpha = 0.5f) else YouTubeRed.copy(alpha = 0.5f),
                glowColor = if (isMonetized) GlowGreen else GlowRed
            ) {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(if (isMonetized) GrowthGreen.copy(alpha = 0.2f) else YouTubeRed.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isMonetized) Icons.Default.MonetizationOn else Icons.Default.HourglassTop,
                            contentDescription = null,
                            tint = if (isMonetized) GrowthGreen else YouTubeRed,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isMonetized) "🎉 تبریک! کانال شما واجد شرایط درآمدزایی است" else "مرکز اختصاصی درآمدزایی و مانیتایز یوتیوب",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isMonetized) "درآمد ارزی فعال • دسترسی به AdSense و Super Thanks"
                        else "مسیر دستیابی به ۱,۰۰۰ سابسکرایبر و ۴,۰۰۰ ساعت واچ‌تایم عمومی",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // The 3 Core Requirements Progress Bars
        item {
            SectionHeader(
                title = "وضعیت معیارهای رسمی یوتیوب",
                subtitle = "پیش‌نیازهای پیوستن به برنامه همکاران یوتیوب (YPP)",
                icon = { Icon(Icons.Default.Verified, contentDescription = null, tint = MetricCyan) }
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // 1. Subscribers
                val subProgress = (monetizationProgress.subscribers.toFloat() / monetizationProgress.targetSubscribers.toFloat()).coerceIn(0f, 1f)
                RequirementProgressBarCard(
                    title = "تعداد سابسکرایبر (Subscribers)",
                    current = "${monetizationProgress.subscribers}",
                    target = "۱,۰۰۰",
                    progress = subProgress,
                    isCompleted = monetizationProgress.subscribers >= 1000,
                    color = MetricCyan,
                    helperText = if (monetizationProgress.subscribers < 1000)
                        "${1000 - monetizationProgress.subscribers} سابسکرایبر دیگر تا هدف باقی مانده"
                        else "معیار با موفقیت تکمیل شد!"
                )

                // 2. Watch Hours
                val watchHoursProgress = (monetizationProgress.watchHours.toFloat() / monetizationProgress.targetWatchHours.toFloat()).coerceIn(0f, 1f)
                RequirementProgressBarCard(
                    title = "ساعات تماشای عمومی (Watch Hours)",
                    current = "${monetizationProgress.watchHours.toInt()}",
                    target = "۴,۰۰۰ ساعت",
                    progress = watchHoursProgress,
                    isCompleted = monetizationProgress.watchHours >= 4000.0,
                    color = GrowthGreen,
                    helperText = if (monetizationProgress.watchHours < 4000.0)
                        "${(4000.0 - monetizationProgress.watchHours).toInt()} ساعت دیگر برای ورود به ادسنس"
                        else "معیار با موفقیت تکمیل شد!"
                )

                // 3. Shorts Views (Alternative path)
                val shortsProgress = (monetizationProgress.shortsViews.toFloat() / monetizationProgress.targetShortsViews.toFloat()).coerceIn(0f, 1f)
                RequirementProgressBarCard(
                    title = "یا ۱۰ میلیون بازدید شورتس (Shorts Views)",
                    current = formatNumberPersian(monetizationProgress.shortsViews),
                    target = "۱۰ میلیون (در ۹۰ روز)",
                    progress = shortsProgress,
                    isCompleted = monetizationProgress.shortsViews >= 10_000_000,
                    color = WarningAmber,
                    helperText = "مسیر جایگزین با محتوای کوتاه عمودی"
                )
            }
        }

        // ETA Projections & Growth Velocity Calculator (Scenarios)
        item {
            StudioGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "تحلیل رگرسیون و تخمین زمان مانیتایز (WMA)",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MetricCyan.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "گلوگاه: ${monetizationProgress.limitingFactor}",
                                color = MetricCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "محاسبه چندمتغیره بر اساس میانگین متحرک وزنی ۷ و ۳۰ روزه با اعمال نرخ شتاب:",
                        color = TextMuted,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Regression Metrics Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(StudioCardHover)
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "سرعت سابسکرایبر", color = TextMuted, fontSize = 10.sp)
                            Text(
                                text = "+${monetizationProgress.subscribersDailyVelocity} / روز",
                                color = YouTubeRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column {
                            Text(text = "سرعت واچ‌تایم", color = TextMuted, fontSize = 10.sp)
                            Text(
                                text = "+${monetizationProgress.watchHoursDailyVelocity} ساعت / روز",
                                color = MetricCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column {
                            Text(text = "شتاب رشد (Acceleration)", color = TextMuted, fontSize = 10.sp)
                            Text(
                                text = "${if (monetizationProgress.accelerationRatePercent >= 0) "+" else ""}${monetizationProgress.accelerationRatePercent}٪",
                                color = if (monetizationProgress.accelerationRatePercent >= 0) GrowthGreen else WarningAmber,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ScenarioBox(
                            title = "روند فعلی",
                            days = "${monetizationProgress.daysRemainingCurrentPace} روز",
                            desc = "با سرعت جاری محتوا",
                            isSelected = selectedScenario == "current",
                            color = MetricCyan,
                            modifier = Modifier.weight(1f)
                        ) { selectedScenario = "current" }

                        ScenarioBox(
                            title = "سناریوی بهینه",
                            days = "${monetizationProgress.daysRemainingMediumPace} روز",
                            desc = "+۱ لانگ‌ویدیو +۲ شورتس",
                            isSelected = selectedScenario == "medium",
                            color = WarningAmber,
                            modifier = Modifier.weight(1f)
                        ) { selectedScenario = "medium" }

                        ScenarioBox(
                            title = "رشد تهاجمی 🚀",
                            days = "${monetizationProgress.daysRemainingFastPace} روز",
                            desc = "شورتس روزانه + ۲ لانگ",
                            isSelected = selectedScenario == "fast",
                            color = GrowthGreen,
                            modifier = Modifier.weight(1f)
                        ) { selectedScenario = "fast" }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Confidence Interval Notice
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(StudioBackground.copy(alpha = 0.6f))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.QueryStats, contentDescription = null, tint = MetricCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "بازه برآورد آماری با ضریب اطمینان ۸۵٪: بین ${monetizationProgress.confidenceIntervalDays.first} الی ${monetizationProgress.confidenceIntervalDays.second} روز آینده",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Monetization Preparation Checklist
        item {
            SectionHeader(
                title = "چک‌لیست آماده‌سازی قبل از ارسال درخواست",
                subtitle = "جلوگیری از ریجکت شدن کانال توسط ناظر یوتیوب",
                icon = { Icon(Icons.AutoMirrored.Filled.FactCheck, contentDescription = null, tint = GrowthGreen) }
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                monetizationProgress.checklistItems.forEach { item ->
                    StudioGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = if (item.isPassed) GrowthGreen.copy(alpha = 0.3f) else WarningAmber.copy(alpha = 0.3f)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (item.isPassed) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (item.isPassed) GrowthGreen else WarningAmber,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.description,
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Warning & Policy Block (Preventing Monetization Blockers)
        item {
            StudioGlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = WarningAmber.copy(alpha = 0.4f),
                backgroundColor = StudioCardHover
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.GppMaybe,
                        contentDescription = null,
                        tint = WarningAmber,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "نکات حیاتی برای یوتیوبرهای ایرانی:",
                            color = WarningAmber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "۱. از هوش مصنوعی برای تولید صدای رباتیک کامل استفاده نکنید؛ یوتیوب صدای مصنوعی بدون ارزش انسانی را رد می‌کند.\n۲. حساب ادسنس باید در کشور غیرتحریمی (مثل ترکیه، امارات، آلمان و...) با آدرس و کارت شناسایی معتبر ثبت شود.\n۳. موسیقی‌های پس‌زمینه را فقط از YouTube Audio Library رسمی انتخاب کنید.",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        // Post-Monetization Roadmap
        item {
            StudioGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "مراحل بعد از رسیدن به شرایط درآمدزایی",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    PostMonetizeStep(stepNum = "۱", title = "ارسال درخواست در تب Earn استودیو", desc = "بررسی خودکار کانال و تطبیق شرایط")
                    PostMonetizeStep(stepNum = "۲", title = "اتصال به حساب گوگل ادسنس", desc = "تعیین هویت و وارد کردن مشخصات بانکی")
                    PostMonetizeStep(stepNum = "۳", title = "بررسی نهایی انسانی توسط یوتیوب", desc = "معمولاً بین ۲ تا ۷ روز کاری طول می‌کشد")
                    PostMonetizeStep(stepNum = "۴", title = "فعال‌سازی تبلیغات روی تمام ویدیوها", desc = "شروع به کار ماشین دلاری کانال!")
                }
            }
        }
    }
}

@Composable
fun RequirementProgressBarCard(
    title: String,
    current: String,
    target: String,
    progress: Float,
    isCompleted: Boolean,
    color: Color,
    helperText: String
) {
    StudioGlassCard(modifier = Modifier.fillMaxWidth(), borderColor = color.copy(alpha = 0.3f)) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "$current / $target",
                    color = color,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(StudioCardHover)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(5.dp))
                        .background(color)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = helperText, color = TextMuted, fontSize = 11.sp)
                Text(text = "${(progress * 100).toInt()}%", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ScenarioBox(
    title: String,
    days: String,
    desc: String,
    isSelected: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) color.copy(alpha = 0.15f) else StudioCardHover)
            .border(1.dp, if (isSelected) color else StudioBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, color = if (isSelected) color else TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = days, color = if (isSelected) color else TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = desc, color = TextMuted, fontSize = 10.sp)
        }
    }
}

@Composable
fun PostMonetizeStep(stepNum: String, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(YouTubeRed.copy(alpha = 0.2f))
                .border(1.dp, YouTubeRed, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = stepNum, color = YouTubeRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = title, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(text = desc, color = TextMuted, fontSize = 10.sp)
        }
    }
}
