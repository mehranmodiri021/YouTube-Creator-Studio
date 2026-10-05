package com.example.core.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Domain models for YouTube Creator Studio Workspace
 */

data class ChannelProfile(
    val id: String,
    val title: String,
    val customUrl: String,
    val description: String,
    val avatarUrl: String,
    val bannerUrl: String,
    val subscriberCount: Long,
    val viewCount: Long,
    val videoCount: Long,
    val shortsCount: Long,
    val category: String,
    val healthScore: Int, // 0 - 100
    val dailyGrowth: Long,
    val weeklyGrowth: Long,
    val monthlyGrowth: Long,
    val isMonetized: Boolean = false,
    val totalWatchHours: Double = 3120.0
)

data class ChannelStatsOverview(
    val subscribers: Long,
    val subscribersDeltaPercent: Double,
    val views: Long,
    val viewsDeltaPercent: Double,
    val watchTimeHours: Double,
    val watchTimeDeltaPercent: Double,
    val averageViewDurationSeconds: Int,
    val averagePercentageViewed: Double,
    val impressions: Long,
    val impressionsDeltaPercent: Double,
    val ctrPercent: Double,
    val ctrDeltaPercent: Double,
    val likesCount: Long,
    val commentsCount: Long,
    val sharesCount: Long,
    val estimatedRevenueUsd: Double,
    val rpmUsd: Double,
    val cpmUsd: Double
)

data class VideoItem(
    val id: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val publishedAt: String,
    val durationSeconds: Int,
    val isShort: Boolean,
    val views: Long,
    val watchTimeHours: Double,
    val likes: Long,
    val comments: Long,
    val shares: Long,
    val subscribersGained: Long,
    val ctr: Double, // e.g. 7.4%
    val averagePercentageViewed: Double, // e.g. 52.3%
    val averageViewDurationSeconds: Int,
    val videoScore: Int, // 0 - 100
    val performanceVsAverage: Double, // +45% or -12%
    val strengths: List<String> = emptyList(),
    val weaknesses: List<String> = emptyList(),
    val aiRecommendation: String = "",
    val retentionCurve: List<Float> = emptyList() // 0s, 30s, 60s, 120s, end
)

data class MonetizationProgress(
    val subscribers: Long,
    val targetSubscribers: Long = 1000,
    val watchHours: Double,
    val targetWatchHours: Double = 4000.0,
    val shortsViews: Long,
    val targetShortsViews: Long = 10_000_000,
    val tierOneUnlocked: Boolean = false, // 500 subs + 3k watch hours
    val isFullyMonetized: Boolean = false,
    val daysRemainingCurrentPace: Int = 42,
    val daysRemainingMediumPace: Int = 28,
    val daysRemainingFastPace: Int = 18,
    val limitingFactor: String = "واچ‌تایم عمومی",
    val subscribersDailyVelocity: Double = 12.0,
    val watchHoursDailyVelocity: Double = 8.5,
    val accelerationRatePercent: Double = 14.5,
    val confidenceIntervalDays: Pair<Int, Int> = Pair(24, 38), // Realistic to conservative range
    val checklistItems: List<MonetizationCheckItem> = emptyList(),
    val warnings: List<String> = emptyList()
)

data class MonetizationCheckItem(
    val id: String,
    val title: String,
    val description: String,
    val isPassed: Boolean,
    val warningLevel: WarningLevel = WarningLevel.NONE
)

enum class WarningLevel {
    NONE, ATTENTION, CRITICAL
}

data class SmartAlert(
    val id: String,
    val title: String,
    val message: String,
    val type: AlertType,
    val timestamp: String,
    val actionLabel: String? = null,
    val relatedVideoId: String? = null
)

enum class AlertType {
    GROWTH_SURGE, CTR_DROP, MILESTONE_NEAR, RETENTION_ALERT, RECOMMENDATION
}

data class ChannelDoctorReport(
    val healthScore: Int, // 0 - 100
    val criticalIssues: List<DiagnosticIssue>,
    val attentionIssues: List<DiagnosticIssue>,
    val healthyAspects: List<DiagnosticIssue>,
    val fixPlanSteps: List<String>
)

data class DiagnosticIssue(
    val id: String,
    val title: String,
    val description: String,
    val impact: String,
    val recommendedAction: String,
    val severity: WarningLevel
)

data class ContentIdea(
    val id: String,
    val title: String,
    val type: ContentType,
    val hook: String,
    val outline: List<String>,
    val thumbnailConcept: String,
    val callToAction: String,
    val targetAudience: String,
    val estimatedCompetition: String, // Low, Medium, High
    val potentialScore: Int // 0 - 100
)

enum class ContentType {
    LONG_VIDEO, SHORTS, SERIES, TUTORIAL, TRENDING, EVERGREEN
}

enum class PipelineStage {
    IDEA, SCRIPT, RECORDING, EDITING, THUMBNAIL, SCHEDULED, PUBLISHED
}

data class WorkspaceItem(
    val id: String,
    val title: String,
    val stage: PipelineStage,
    val type: ContentType,
    val deadline: String,
    val targetPublishDate: String,
    val scriptContent: String = "",
    val thumbnailIdea: String = "",
    val tags: List<String> = emptyList(),
    val notes: String = ""
)

data class ScriptSection(
    val title: String,
    val durationSeconds: Int,
    val speakerNote: String,
    val visualCue: String
)

data class ScriptDraft(
    val id: String,
    val title: String,
    val topic: String,
    val hook: String,
    val intro: String,
    val mainBody: List<ScriptSection>,
    val storyElement: String,
    val cta: String,
    val outro: String,
    val estimatedMinutes: Int
)

data class CompetitorChannel(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val subscriberCount: Long,
    val monthlyViews: Long,
    val uploadFrequencyPerWeek: Double,
    val topTopics: List<String>,
    val contentGapOpportunities: List<String>
)

data class ExperimentItem(
    val id: String,
    val title: String,
    val testType: ExperimentType, // Title, Thumbnail, Hook, PostTime
    val variantA: String,
    val variantB: String,
    val durationDays: Int,
    val resultVariantAWins: Boolean? = null,
    val winnerMetricSummary: String = "",
    val status: String // Running, Completed, Draft
)

enum class ExperimentType {
    TITLE, THUMBNAIL, HOOK, PUBLISH_TIME
}

data class CreatorAchievement(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val isUnlocked: Boolean,
    val progress: Float,
    val xpReward: Int
)

data class CreatorGoal(
    val id: String,
    val title: String,
    val current: Long,
    val target: Long,
    val unit: String,
    val deadline: String
)

data class GrowthStrategyPhase(
    val phaseTitle: String,
    val timeline: String,
    val focusArea: String,
    val actionItems: List<String>,
    val expectedOutcome: String
)

data class SeoAnalysisResult(
    val targetKeyword: String,
    val searchVolume: String,
    val competition: String,
    val overallSeoScore: Int, // 0 - 100
    val titleScore: Int,
    val descriptionScore: Int,
    val tagSuggestions: List<String>,
    val searchIntent: String,
    val optimizationTips: List<String>
)

data class ChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: String = "همین الان",
    val quickActions: List<String> = emptyList()
)

// YouTube Data API response DTOs
@JsonClass(generateAdapter = true)
data class YouTubeSearchResponse(
    @Json(name = "items") val items: List<YouTubeSearchResultItem>?
)

@JsonClass(generateAdapter = true)
data class YouTubeSearchResultItem(
    @Json(name = "id") val id: YouTubeIdInfo?,
    @Json(name = "snippet") val snippet: YouTubeSnippet?
)

@JsonClass(generateAdapter = true)
data class YouTubeIdInfo(
    @Json(name = "kind") val kind: String?,
    @Json(name = "videoId") val videoId: String?,
    @Json(name = "channelId") val channelId: String?
)

@JsonClass(generateAdapter = true)
data class YouTubeChannelListResponse(
    @Json(name = "items") val items: List<YouTubeChannelItem>?
)

@JsonClass(generateAdapter = true)
data class YouTubeChannelItem(
    @Json(name = "id") val id: String?,
    @Json(name = "snippet") val snippet: YouTubeSnippet?,
    @Json(name = "statistics") val statistics: YouTubeChannelStatistics?
)

@JsonClass(generateAdapter = true)
data class YouTubeSnippet(
    @Json(name = "title") val title: String?,
    @Json(name = "description") val description: String?,
    @Json(name = "publishedAt") val publishedAt: String?,
    @Json(name = "thumbnails") val thumbnails: YouTubeThumbnails?
)

@JsonClass(generateAdapter = true)
data class YouTubeThumbnails(
    @Json(name = "default") val defaultThumb: YouTubeThumbnailDetails?,
    @Json(name = "medium") val medium: YouTubeThumbnailDetails?,
    @Json(name = "high") val high: YouTubeThumbnailDetails?
)

@JsonClass(generateAdapter = true)
data class YouTubeThumbnailDetails(
    @Json(name = "url") val url: String?
)

@JsonClass(generateAdapter = true)
data class YouTubeChannelStatistics(
    @Json(name = "viewCount") val viewCount: String?,
    @Json(name = "subscriberCount") val subscriberCount: String?,
    @Json(name = "videoCount") val videoCount: String?
)

@JsonClass(generateAdapter = true)
data class YouTubeVideoListResponse(
    @Json(name = "items") val items: List<YouTubeVideoItem>?
)

@JsonClass(generateAdapter = true)
data class YouTubeVideoItem(
    @Json(name = "id") val id: String?,
    @Json(name = "snippet") val snippet: YouTubeSnippet?,
    @Json(name = "statistics") val statistics: YouTubeVideoStatistics?,
    @Json(name = "contentDetails") val contentDetails: YouTubeVideoContentDetails?
)

@JsonClass(generateAdapter = true)
data class YouTubeVideoStatistics(
    @Json(name = "viewCount") val viewCount: String?,
    @Json(name = "likeCount") val likeCount: String?,
    @Json(name = "commentCount") val commentCount: String?
)

@JsonClass(generateAdapter = true)
data class YouTubeVideoContentDetails(
    @Json(name = "duration") val duration: String?
)
