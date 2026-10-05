package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.core.model.ChannelProfile
import com.example.ui.components.SectionHeader
import com.example.ui.components.StudioGlassCard
import com.example.ui.components.formatNumberPersian
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    currentChannel: ChannelProfile,
    availableChannels: List<ChannelProfile>,
    onSwitchChannel: (String) -> Unit,
    onAddCustomChannel: (String, String, String) -> Unit,
    onConnectYouTubeApi: (String, String) -> Unit,
    currentLanguage: String,
    onLanguageChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var showAddChannelDialog by remember { mutableStateOf(false) }
    var newChannelTitle by remember { mutableStateOf("") }
    var newChannelHandle by remember { mutableStateOf("") }
    var newChannelCategory by remember { mutableStateOf("تکنولوژی") }

    var ytApiKeyInput by remember { mutableStateOf("") }
    var ytChannelIdInput by remember { mutableStateOf("") }

    var biometricLockEnabled by remember { mutableStateOf(false) }
    var smartAlertsEnabled by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(StudioBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Module 25: Multi-Channel Manager
        item {
            SectionHeader(
                title = "مدیریت چندکاناله (Multi-Channel Workspace)",
                subtitle = "جابه‌جایی سریع میان کانال‌های مختلف یوتیوب",
                icon = { Icon(Icons.Default.AccountCircle, contentDescription = null, tint = MetricCyan) }
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                availableChannels.forEach { chan ->
                    val isSelected = chan.id == currentChannel.id
                    StudioGlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSwitchChannel(chan.id) },
                        borderColor = if (isSelected) YouTubeRed else StudioBorder,
                        backgroundColor = if (isSelected) StudioCardHover else StudioCard
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, if (isSelected) YouTubeRed else Color.Transparent, CircleShape)
                            ) {
                                AsyncImage(model = chan.avatarUrl, contentDescription = null, modifier = Modifier.fillMaxSize())
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = chan.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(
                                    text = "${chan.customUrl} • ${formatNumberPersian(chan.subscriberCount)} دنبال‌کننده • ${chan.category}",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }

                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(YouTubeRed.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(text = "فعال", color = YouTubeRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = { showAddChannelDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = StudioCardHover),
                    modifier = Modifier.fillMaxWidth().border(1.dp, MetricCyan, RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = MetricCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "اتصال کانال جدید یوتیوب", color = MetricCyan, fontSize = 13.sp)
                }
            }
        }

        // Live YouTube Data API & Gemini Settings
        item {
            SectionHeader(
                title = "اتصال به YouTube API و Gemini AI",
                subtitle = "برای همگام‌سازی لحظه‌ای کانال با گوگل",
                icon = { Icon(Icons.Default.CloudSync, contentDescription = null, tint = YouTubeRed) }
            )

            StudioGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "کلید YouTube Data API v3:", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = ytApiKeyInput,
                        onValueChange = { ytApiKeyInput = it },
                        placeholder = { Text("AIzaSy...", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth().testTag("yt_api_key_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = StudioCardHover,
                            unfocusedContainerColor = StudioCardHover,
                            focusedBorderColor = YouTubeRed,
                            unfocusedBorderColor = StudioBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "شناسه یا هندل کانال (مثلاً @MrBeast یا Channel ID):", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = ytChannelIdInput,
                        onValueChange = { ytChannelIdInput = it },
                        placeholder = { Text("@ChannelHandle یا UC...", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = StudioCardHover,
                            unfocusedContainerColor = StudioCardHover,
                            focusedBorderColor = YouTubeRed,
                            unfocusedBorderColor = StudioBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (ytApiKeyInput.isNotBlank() && ytChannelIdInput.isNotBlank()) {
                                onConnectYouTubeApi(ytApiKeyInput, ytChannelIdInput)
                                Toast.makeText(context, "در حال اتصال به سرورهای YouTube API...", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "لطفاً هر دو فیلد کلید API و هندل را پر کنید", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = YouTubeRed),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "همگام‌سازی با یوتیوب زنده", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Module 36: Export Center
        item {
            SectionHeader(
                title = "مرکز خروجی و گزارش‌ها (Export Center)",
                subtitle = "تهیه گزارش متنی، فرمت PDF و خروجی داده‌های آنالیتیکس",
                icon = { Icon(Icons.Default.FileDownload, contentDescription = null, tint = GrowthGreen) }
            )

            StudioGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "گزارش خلاصه وضعیت کانال:", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "کانال: ${currentChannel.title}\nسابسکرایبر: ${currentChannel.subscriberCount}\nبازدید کل: ${currentChannel.viewCount}\nوضعیت سلامت: ${currentChannel.healthScore}٪",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                val reportText = """
                                    YouTube Creator Studio Report
                                    Channel: ${currentChannel.title} (${currentChannel.customUrl})
                                    Subscribers: ${currentChannel.subscriberCount}
                                    Total Views: ${currentChannel.viewCount}
                                    Health Score: ${currentChannel.healthScore}/100
                                    Exported at: 2026-10-04
                                """.trimIndent()
                                clipboardManager.setText(AnnotatedString(reportText))
                                Toast.makeText(context, "گزارش در کلیپ‌بورد کپی شد", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StudioCardHover),
                            modifier = Modifier.border(1.dp, GrowthGreen, RoundedCornerShape(10.dp))
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = GrowthGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "کپی خلاصه گزارش", color = GrowthGreen, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Module 27: Multi-Language Selector
        item {
            SectionHeader(
                title = "زبان و جهت صفحه (Language & RTL)",
                subtitle = "پشتیبانی کامل از زبان‌های بین‌المللی",
                icon = { Icon(Icons.Default.Language, contentDescription = null, tint = MetricCyan) }
            )

            StudioGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("فارسی (پیش‌فرض RTL)", "English (LTR)", "العربية (RTL)", "Türkçe", "Français", "Deutsch").forEach { lang ->
                        val isSel = currentLanguage.contains(lang.take(4)) || (lang.contains("فارسی") && currentLanguage == "fa")
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) StudioCardHover else Color.Transparent)
                                .clickable {
                                    val code = when {
                                        lang.contains("فارسی") -> "fa"
                                        lang.contains("English") -> "en"
                                        lang.contains("العربية") -> "ar"
                                        lang.contains("Türkçe") -> "tr"
                                        else -> "fa"
                                    }
                                    onLanguageChange(code)
                                }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = lang, color = if (isSel) YouTubeRed else TextPrimary, fontSize = 13.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                            if (isSel) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = YouTubeRed, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        // Module 26: Security & Privacy
        item {
            SectionHeader(
                title = "امنیت و حریم خصوصی (Security & Privacy)",
                subtitle = "قفل بیومتریک، پاکسازی حافظه و عدم ذخیره رمزعبور",
                icon = { Icon(Icons.Default.Security, contentDescription = null, tint = GrowthGreen) }
            )

            StudioGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "قفل بیومتریک و اثرانگشت", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "الزام تایید هویت جهت ورود به استودیو", color = TextMuted, fontSize = 11.sp)
                        }
                        Switch(
                            checked = biometricLockEnabled,
                            onCheckedChange = { biometricLockEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = GrowthGreen, checkedTrackColor = GrowthGreen.copy(alpha = 0.3f))
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "هشدارهای هوشمند الگوریتم", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "دریافت نوتیفیکیشن افت CTR یا جهش بازدید", color = TextMuted, fontSize = 11.sp)
                        }
                        Switch(
                            checked = smartAlertsEnabled,
                            onCheckedChange = { smartAlertsEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = YouTubeRed, checkedTrackColor = YouTubeRed.copy(alpha = 0.3f))
                        )
                    }

                    Button(
                        onClick = { Toast.makeText(context, "کش محلی استودیو پاکسازی شد", Toast.LENGTH_SHORT).show() },
                        colors = ButtonDefaults.buttonColors(containerColor = StudioCardHover),
                        modifier = Modifier.fillMaxWidth().border(1.dp, StudioBorder, RoundedCornerShape(10.dp))
                    ) {
                        Text(text = "پاکسازی کش محلی (Clear Cache)", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }

        // About & Version
        item {
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "YouTube Creator Studio Workspace • نسخه ۲.۵ حرفه‌ای", color = TextMuted, fontSize = 11.sp)
                    Text(text = "طراحی شده برای یوتیوبرهای پیشرو فارسی‌زبان", color = TextMuted, fontSize = 10.sp)
                }
            }
        }
    }

    if (showAddChannelDialog) {
        AlertDialog(
            onDismissRequest = { showAddChannelDialog = false },
            title = { Text(text = "افزودن کانال جدید", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newChannelTitle,
                        onValueChange = { newChannelTitle = it },
                        placeholder = { Text("نام کانال...", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newChannelHandle,
                        onValueChange = { newChannelHandle = it },
                        placeholder = { Text("هندل یوتیوب (مثلاً @MyTechChannel)...", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newChannelCategory,
                        onValueChange = { newChannelCategory = it },
                        placeholder = { Text("دسته‌بندی (گیمینگ، آموزش، ولاگ...)", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newChannelTitle.isNotBlank() && newChannelHandle.isNotBlank()) {
                            onAddCustomChannel(newChannelTitle, newChannelHandle, newChannelCategory)
                            showAddChannelDialog = false
                            Toast.makeText(context, "کانال «$newChannelTitle» اضافه شد", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = YouTubeRed)
                ) {
                    Text("افزودن کانال", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddChannelDialog = false }) {
                    Text("انصراف", color = TextSecondary)
                }
            },
            containerColor = StudioCard
        )
    }
}
