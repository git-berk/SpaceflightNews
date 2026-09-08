package com.berco.spaceflightnews.core.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.ZoneId
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

class DateFormatterTest {

    private val formatter = DateFormatter(ZoneId.of("UTC"), Locale.US)
    private val now = Instant.parse("2026-09-05T12:00:00Z")

    private fun ago(elapsed: Duration) = formatter.format(now - elapsed, now)

    @Test
    fun `no date yields no label`() {
        assertNull(formatter.format(null, now))
    }

    @Test
    fun `under a minute reads as just now`() {
        assertEquals("Just now", ago(30.seconds))
    }

    @Test
    fun `minutes within the hour`() {
        assertEquals("5m ago", ago(5.minutes))
        assertEquals("59m ago", ago(59.minutes))
    }

    @Test
    fun `hours up to a day`() {
        assertEquals("2h ago", ago(2.hours))
        assertEquals("23h ago", ago(23.hours))
    }

    @Test
    fun `the second day reads as yesterday`() {
        assertEquals("Yesterday", ago(24.hours))
        assertEquals("Yesterday", ago(47.hours))
    }

    @Test
    fun `past 48 hours switches to an absolute date, as the design specifies`() {
        assertEquals("3 Sep 2026", ago(48.hours))
        assertEquals("1 Jan 2020", formatter.format(Instant.parse("2020-01-01T08:00:00Z"), now))
    }

    @Test
    fun `the month abbreviation follows the locale`() {
        val uk = DateFormatter(ZoneId.of("UTC"), Locale.UK)

        assertEquals("3 Sept 2026", uk.format(now - 48.hours, now))
    }

    @Test
    fun `future timestamps do not render as negative`() {
        assertEquals("Just now", formatter.format(now + 5.minutes, now))
    }
}
