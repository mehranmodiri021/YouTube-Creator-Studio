package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.example.core.engine.AiIntelligenceService
import com.example.core.model.ChannelProfile
import com.example.core.model.ChannelStatsOverview
import com.example.core.model.ChatMessage
import com.example.ui.components.StudioGlassCard
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AiCoachScreen(
    channel: ChannelProfile,
    stats: ChannelStatsOverview,
    videos: List<com.example.core.model.VideoItem> = emptyList(),
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(
                    id = "msg_welcome",
                    text = "سلام ${channel.title}! من مربی هوش مصنوعی رشد کانال شما هستم.\nآمار لحظه‌ای کانال شما شامل ${channel.subscriberCount} سابسکرایبر در نیش ${channel.category}، نرخ کلیک ${stats.ctrPercent}٪ و میانگین تماشای ${stats.averagePercentageViewed.toInt()}٪ را تحلیل کرده‌ام.\nامروز می‌خواهید چه بخشی از کانال را تقویت کنیم؟",
                    isUser = false,
                    quickActions = listOf(
                        "چرا بازدیدم کم شده؟",
                        "چرا سابسکرایبرها رشد نمی‌کنند؟",
                        "چه ویدیویی بسازم؟",
                        "برنامه رشد ۷ روزه",
                        "نقشه راه مانیتایز"
                    )
                )
            )
        )
    }

    var userInput by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val quickQuestions = listOf(
        "چرا بازدیدم کم شده؟",
        "چرا سابسکرایبرها رشد نمی‌کنند؟",
        "چه ویدیویی برای کانالم بسازم؟",
        "چرا این ویدیو وایرال نشد؟",
        "برنامه رشد ۷ روزه",
        "برنامه رشد ۳۰ روزه",
        "راهکار افزایش واچ‌تایم"
    )

    fun sendUserMessage(text: String) {
        if (text.isBlank() || isLoading) return
        val userMsg = ChatMessage(id = "user_${System.currentTimeMillis()}", text = text, isUser = true)
        messages = messages + userMsg
        userInput = ""
        isLoading = true

        coroutineScope.launch {
            listState.animateScrollToItem(messages.size - 1)
            val replyText = AiIntelligenceService.consultGrowthCoach(text, channel, stats, videos)
            val coachMsg = ChatMessage(
                id = "ai_${System.currentTimeMillis()}",
                text = replyText,
                isUser = false,
                quickActions = listOf("ایده‌های پیشنهادی جدید", "بررسی کاور و تایتل", "مرحله بعدی چیست؟")
            )
            messages = messages + coachMsg
            isLoading = false
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioBackground)
    ) {
        // Coach Status Header
        StudioGlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            borderColor = AiPurple.copy(alpha = 0.35f),
            glowColor = GlowPurple
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(AiPurple.copy(alpha = 0.2f))
                        .border(1.5.dp, AiPurple, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = "AI Coach",
                        tint = AiPurple,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "AI Growth Coach • مربی هوشمند رشد",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(GrowthGreen)
                        )
                    }
                    Text(
                        text = "متصل به آمار زنده کانال «${channel.title}»",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Quick Preset Suggestions
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickQuestions) { q ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(StudioCardHover)
                        .border(1.dp, StudioBorder, RoundedCornerShape(20.dp))
                        .clickable { sendUserMessage(q) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = q, color = TextPrimary, fontSize = 12.sp)
                }
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(messages) { msg ->
                ChatBubble(message = msg, onActionClick = { sendUserMessage(it) })
            }

            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(StudioCard)
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = AiPurple
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "مربی هوش مصنوعی در حال تحلیل الگوریتم و تدوین پاسخ...",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Input Field
        Surface(
            color = StudioSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    placeholder = {
                        Text(text = "سؤال خود را درباره رشد کانال بپرسید...", color = TextMuted, fontSize = 13.sp)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_coach_input"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = StudioCard,
                        unfocusedContainerColor = StudioCard,
                        focusedBorderColor = AiPurple,
                        unfocusedBorderColor = StudioBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.width(10.dp))

                IconButton(
                    onClick = { sendUserMessage(userInput) },
                    enabled = userInput.isNotBlank() && !isLoading,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(if (userInput.isNotBlank() && !isLoading) AiPurple else StudioCardHover)
                        .testTag("ai_coach_send_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "ارسال",
                        tint = if (userInput.isNotBlank() && !isLoading) Color.White else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    onActionClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (message.isUser) 16.dp else 4.dp,
                        bottomEnd = if (message.isUser) 4.dp else 16.dp
                    )
                )
                .background(if (message.isUser) YouTubeRed else StudioCard)
                .border(1.dp, if (message.isUser) Color.Transparent else StudioBorder, RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Text(
                text = message.text,
                color = TextPrimary,
                fontSize = 13.sp,
                lineHeight = 22.sp
            )
        }

        if (message.quickActions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                message.quickActions.forEach { act ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(StudioCardHover)
                            .border(1.dp, AiPurple.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                            .clickable { onActionClick(act) }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(text = act, color = AiPurple, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
