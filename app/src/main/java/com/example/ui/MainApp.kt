package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.R
import com.example.core.data.CreatorRepository
import com.example.core.data.WorkspaceRepository
import com.example.core.model.PipelineStage
import com.example.ui.components.ChannelSelectorTopBar
import com.example.ui.components.StudioGlassCard
import com.example.ui.components.formatNumberPersian
import com.example.ui.screens.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

enum class MainDestination {
    DASHBOARD,
    ANALYTICS,
    AI_COACH,
    STUDIO_TOOLS,
    WORKSPACE,
    MONETIZATION,
    VIDEO_ANALYZER,
    CHANNEL_DOCTOR,
    SETTINGS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    creatorRepository: CreatorRepository = CreatorRepository.instance,
    workspaceRepository: WorkspaceRepository = WorkspaceRepository.instance
) {
    val coroutineScope = rememberCoroutineScope()

    val currentChannel by creatorRepository.currentChannel.collectAsStateWithLifecycle()
    val availableChannels by creatorRepository.availableChannels.collectAsStateWithLifecycle()
    val statsOverview by creatorRepository.statsOverview.collectAsStateWithLifecycle()
    val videos by creatorRepository.videos.collectAsStateWithLifecycle()

    val pipelineItems by workspaceRepository.pipelineItems.collectAsStateWithLifecycle()
    val contentIdeas by workspaceRepository.contentIdeas.collectAsStateWithLifecycle()
    val experiments by workspaceRepository.experiments.collectAsStateWithLifecycle()
    val creatorXp by workspaceRepository.creatorXp.collectAsStateWithLifecycle()
    val streak by workspaceRepository.currentStreak.collectAsStateWithLifecycle()
    val achievements by workspaceRepository.achievements.collectAsStateWithLifecycle()
    val goals by workspaceRepository.goals.collectAsStateWithLifecycle()
    val strategyPhases by workspaceRepository.strategyPhases.collectAsStateWithLifecycle()
    val scripts by workspaceRepository.scripts.collectAsStateWithLifecycle()

    val monetizationProgress = remember(currentChannel, statsOverview) {
        creatorRepository.getMonetizationProgress()
    }

    var currentScreen by remember { mutableStateOf(MainDestination.DASHBOARD) }
    var selectedVideoIdForAnalyzer by remember { mutableStateOf<String?>(null) }
    var studioToolsInitialTab by remember { mutableIntStateOf(0) }
    var currentLanguage by remember { mutableStateOf("fa") }

    // Channel Switcher Bottom Sheet
    var showChannelSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // Handle Back Press on secondary screens
    if (currentScreen != MainDestination.DASHBOARD) {
        BackHandler {
            currentScreen = MainDestination.DASHBOARD
        }
    }

    val context = LocalContext.current
    val currentConfig = LocalConfiguration.current
    val localizedConfiguration = remember(currentLanguage) {
        android.content.res.Configuration(currentConfig).apply {
            setLocale(java.util.Locale(currentLanguage))
        }
    }
    val localizedContext = remember(currentLanguage) {
        context.createConfigurationContext(localizedConfiguration)
    }

    val layoutDirection = if (currentLanguage == "en" || currentLanguage == "tr" || currentLanguage == "es") LayoutDirection.Ltr else LayoutDirection.Rtl

    CompositionLocalProvider(
        LocalConfiguration provides localizedConfiguration,
        LocalContext provides localizedContext,
        LocalLayoutDirection provides layoutDirection
    ) {
        Scaffold(
            containerColor = StudioBackground,
            topBar = {
                Surface(
                    color = StudioSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
                ) {
                    if (currentScreen == MainDestination.DASHBOARD || currentScreen == MainDestination.ANALYTICS || currentScreen == MainDestination.AI_COACH || currentScreen == MainDestination.STUDIO_TOOLS || currentScreen == MainDestination.WORKSPACE) {
                        ChannelSelectorTopBar(
                            channel = currentChannel,
                            onSwitchChannelClick = { showChannelSheet = true },
                            onDoctorClick = { currentScreen = MainDestination.CHANNEL_DOCTOR }
                        )
                    } else {
                        // Sub-screen header with Back button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { currentScreen = MainDestination.DASHBOARD }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "بازگشت",
                                    tint = TextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when (currentScreen) {
                                    MainDestination.MONETIZATION -> stringResource(R.string.title_monetization)
                                    MainDestination.VIDEO_ANALYZER -> stringResource(R.string.title_video_analyzer)
                                    MainDestination.CHANNEL_DOCTOR -> stringResource(R.string.title_channel_doctor)
                                    MainDestination.SETTINGS -> stringResource(R.string.title_settings)
                                    else -> stringResource(R.string.app_name)
                                },
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            },
            bottomBar = {
                NavigationBar(
                    containerColor = StudioSurface,
                    contentColor = TextPrimary,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentScreen == MainDestination.DASHBOARD,
                        onClick = { currentScreen = MainDestination.DASHBOARD },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "داشبورد") },
                        label = { Text(stringResource(R.string.tab_dashboard), fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = YouTubeRed,
                            selectedTextColor = YouTubeRed,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = YouTubeRed.copy(alpha = 0.15f)
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen == MainDestination.ANALYTICS,
                        onClick = { currentScreen = MainDestination.ANALYTICS },
                        icon = { Icon(Icons.Default.BarChart, contentDescription = "آنالیتیکس") },
                        label = { Text(stringResource(R.string.tab_analytics), fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MetricCyan,
                            selectedTextColor = MetricCyan,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = MetricCyan.copy(alpha = 0.15f)
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen == MainDestination.AI_COACH,
                        onClick = { currentScreen = MainDestination.AI_COACH },
                        icon = { Icon(Icons.Default.SmartToy, contentDescription = "مربی AI") },
                        label = { Text(stringResource(R.string.tab_ai_coach), fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AiPurple,
                            selectedTextColor = AiPurple,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = AiPurple.copy(alpha = 0.15f)
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen == MainDestination.STUDIO_TOOLS,
                        onClick = { currentScreen = MainDestination.STUDIO_TOOLS },
                        icon = { Icon(Icons.Default.AutoFixHigh, contentDescription = "ابزارها") },
                        label = { Text(stringResource(R.string.tab_studio_tools), fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = WarningAmber,
                            selectedTextColor = WarningAmber,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = WarningAmber.copy(alpha = 0.15f)
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen == MainDestination.WORKSPACE || currentScreen == MainDestination.SETTINGS || currentScreen == MainDestination.MONETIZATION,
                        onClick = { currentScreen = MainDestination.WORKSPACE },
                        icon = { Icon(Icons.Default.Workspaces, contentDescription = "میزکار") },
                        label = { Text(stringResource(R.string.tab_workspace), fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GrowthGreen,
                            selectedTextColor = GrowthGreen,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = GrowthGreen.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentScreen) {
                    MainDestination.DASHBOARD -> {
                        DashboardScreen(
                            channel = currentChannel,
                            stats = statsOverview,
                            videos = videos,
                            onNavigateToVideoAnalyzer = { vidId ->
                                selectedVideoIdForAnalyzer = vidId
                                currentScreen = MainDestination.VIDEO_ANALYZER
                            },
                            onNavigateToAiCoach = { currentScreen = MainDestination.AI_COACH },
                            onNavigateToMonetization = { currentScreen = MainDestination.MONETIZATION },
                            onNavigateToDoctor = { currentScreen = MainDestination.CHANNEL_DOCTOR },
                            onNavigateToPlanner = { currentScreen = MainDestination.WORKSPACE },
                            onNavigateToTitleStudio = {
                                studioToolsInitialTab = 1
                                currentScreen = MainDestination.STUDIO_TOOLS
                            }
                        )
                    }

                    MainDestination.ANALYTICS -> {
                        AnalyticsScreen(
                            channel = currentChannel,
                            stats = statsOverview,
                            videos = videos
                        )
                    }

                    MainDestination.AI_COACH -> {
                        AiCoachScreen(
                            channel = currentChannel,
                            stats = statsOverview
                        )
                    }

                    MainDestination.STUDIO_TOOLS -> {
                        StudioToolsScreen(
                            ideas = contentIdeas,
                            onAddIdeaToPipeline = { idea ->
                                workspaceRepository.addPipelineItem(
                                    title = idea.title,
                                    stage = PipelineStage.IDEA,
                                    type = idea.type,
                                    date = "هفته جاری"
                                )
                                currentScreen = MainDestination.WORKSPACE
                            },
                            initialTab = studioToolsInitialTab
                        )
                    }

                    MainDestination.WORKSPACE -> {
                        WorkspaceScreen(
                            pipelineItems = pipelineItems,
                            scripts = scripts,
                            experiments = experiments,
                            onUpdateStage = { id, stage ->
                                workspaceRepository.updateItemStage(id, stage)
                            },
                            onAddNewPipelineItem = { title, stage, type, date ->
                                workspaceRepository.addPipelineItem(title, stage, type, date)
                            }
                        )
                    }

                    MainDestination.MONETIZATION -> {
                        MonetizationScreen(
                            monetizationProgress = monetizationProgress,
                            onNavigateBack = { currentScreen = MainDestination.DASHBOARD }
                        )
                    }

                    MainDestination.VIDEO_ANALYZER -> {
                        VideoAnalyzerScreen(
                            videos = videos,
                            stats = statsOverview,
                            initialVideoId = selectedVideoIdForAnalyzer,
                            onNavigateToTitleStudio = {
                                studioToolsInitialTab = 1
                                currentScreen = MainDestination.STUDIO_TOOLS
                            }
                        )
                    }

                    MainDestination.CHANNEL_DOCTOR -> {
                        ChannelDoctorScreen(
                            channel = currentChannel,
                            stats = statsOverview,
                            videos = videos,
                            xp = creatorXp,
                            streak = streak,
                            achievements = achievements,
                            goals = goals,
                            strategyPhases = strategyPhases,
                            onFixIssueClick = {
                                studioToolsInitialTab = 1
                                currentScreen = MainDestination.STUDIO_TOOLS
                            }
                        )
                    }

                    MainDestination.SETTINGS -> {
                        SettingsScreen(
                            currentChannel = currentChannel,
                            availableChannels = availableChannels,
                            onSwitchChannel = { id ->
                                creatorRepository.switchChannel(id)
                                currentScreen = MainDestination.DASHBOARD
                            },
                            onAddCustomChannel = { title, handle, category ->
                                creatorRepository.addCustomChannel(title, handle, category)
                                currentScreen = MainDestination.DASHBOARD
                            },
                            onConnectYouTubeApi = { apiKey, channelId ->
                                coroutineScope.launch {
                                    creatorRepository.trySyncFromYouTubeApi(apiKey, channelId)
                                }
                            },
                            currentLanguage = currentLanguage,
                            onLanguageChange = { currentLanguage = it }
                        )
                    }
                }
            }

            // Quick floating speed action for Settings and Doctor from everywhere
            if (currentScreen == MainDestination.WORKSPACE || currentScreen == MainDestination.DASHBOARD) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 75.dp, start = 16.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    FloatingActionButton(
                        onClick = {
                            currentScreen = if (currentScreen == MainDestination.SETTINGS) MainDestination.DASHBOARD else MainDestination.SETTINGS
                        },
                        containerColor = StudioCardHover,
                        contentColor = TextPrimary,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(46.dp)
                            .border(1.dp, StudioBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (currentScreen == MainDestination.SETTINGS) Icons.Default.Close else Icons.Default.Settings,
                            contentDescription = "تنظیمات",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Channel Switcher Sheet
        if (showChannelSheet) {
            ModalBottomSheet(
                onDismissRequest = { showChannelSheet = false },
                sheetState = sheetState,
                containerColor = StudioSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "انتخاب کانال فعال (Switch Channel)",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    availableChannels.forEach { chan ->
                        val isCurrent = chan.id == currentChannel.id
                        StudioGlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    creatorRepository.switchChannel(chan.id)
                                    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                                        showChannelSheet = false
                                    }
                                },
                            borderColor = if (isCurrent) YouTubeRed else StudioBorder,
                            backgroundColor = if (isCurrent) StudioCardHover else StudioCard
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .border(1.dp, if (isCurrent) YouTubeRed else Color.Transparent, CircleShape)
                                ) {
                                    AsyncImage(model = chan.avatarUrl, contentDescription = null, modifier = Modifier.fillMaxSize())
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = chan.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = "${chan.customUrl} • ${formatNumberPersian(chan.subscriberCount)} سابسکرایبر", color = TextSecondary, fontSize = 11.sp)
                                }

                                if (isCurrent) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = YouTubeRed, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                                showChannelSheet = false
                                currentScreen = MainDestination.SETTINGS
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StudioCardHover),
                        modifier = Modifier.fillMaxWidth().border(1.dp, MetricCyan, RoundedCornerShape(10.dp))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = MetricCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "مدیریت کانال‌ها یا اتصال اکانت جدید", color = MetricCyan, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
