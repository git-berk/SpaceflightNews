package com.berco.spaceflightnews.core.ui

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.time.toJavaInstant

/**
 * Relative under 48 hours, absolute after, matching the design spec.
 * Returns null when the article has no plausible date so callers omit the slot.
 */
class DateFormatter(
    private val zoneId: ZoneId = ZoneId.systemDefault(),
    private val locale: Locale = Locale.getDefault(),
) {
    private val absolute: DateTimeFormatter =
        DateTimeFormatter.ofPattern("d MMM yyyy", locale)

    fun format(instant: Instant?, now: Instant = Clock.System.now()): String? {
        if (instant == null) return null

        val elapsed = now - instant
        // Future-dated articles read as brand new rather than negative.
        if (elapsed.inWholeMinutes < 1) return "Just now"

        return when {
            elapsed.inWholeHours < 1 -> "${elapsed.inWholeMinutes}m ago"
            elapsed.inWholeHours < 24 -> "${elapsed.inWholeHours}h ago"
            elapsed.inWholeHours < 48 -> "Yesterday"
            else -> absolute.format(instant.toJavaInstant().atZone(zoneId))
        }
    }
}
