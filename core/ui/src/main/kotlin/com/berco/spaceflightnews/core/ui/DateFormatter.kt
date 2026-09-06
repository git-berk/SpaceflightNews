package com.berco.spaceflightnews.core.ui

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

/**
 * Relative under 48 hours, absolute after, matching the design spec.
 * Returns null when the article has no plausible date so callers omit the slot.
 */
class DateFormatter(
    private val zoneId: ZoneId = ZoneId.systemDefault(),
    private val locale: Locale = Locale.getDefault(),
) {
    private val absolute: DateTimeFormatter
        get() = DateTimeFormatter.ofPattern("d MMM yyyy", locale)

    fun format(instant: Instant?, now: Instant = Instant.now()): String? {
        if (instant == null) return null

        val minutes = ChronoUnit.MINUTES.between(instant, now)
        // Future-dated articles read as brand new rather than negative.
        if (minutes < 1) return "Just now"

        val hours = ChronoUnit.HOURS.between(instant, now)
        return when {
            hours < 1 -> "${minutes}m ago"
            hours < 24 -> "${hours}h ago"
            hours < 48 -> "Yesterday"
            else -> absolute.format(instant.atZone(zoneId))
        }
    }
}
