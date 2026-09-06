package com.berco.spaceflightnews.core.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Locale

class DateFormatterTest {

    private val formatter = DateFormatter(ZoneId.of("UTC"), Locale.US)
    private val now = Instant.parse("2026-09-05T12:00:00Z")

    private fun ago(amount: Long, unit: ChronoUnit) = formatter.format(now.minus(amount, unit), now)

    @Test
    fun `no date yields no label`() {
        assertNull(formatter.format(null, now))
    }

    @Test
    fun `under a minute reads as just now`() {
        assertEquals("Just now", ago(30, ChronoUnit.SECONDS))
    }

    @Test
    fun `minutes within the hour`() {
        assertEquals("5m ago", ago(5, ChronoUnit.MINUTES))
        assertEquals("59m ago", ago(59, ChronoUnit.MINUTES))
    }

    @Test
    fun `hours up to a day`() {
        assertEquals("2h ago", ago(2, ChronoUnit.HOURS))
        assertEquals("23h ago", ago(23, ChronoUnit.HOURS))
    }

    @Test
    fun `the second day reads as yesterday`() {
        assertEquals("Yesterday", ago(24, ChronoUnit.HOURS))
        assertEquals("Yesterday", ago(47, ChronoUnit.HOURS))
    }

    @Test
    fun `past 48 hours switches to an absolute date, as the design specifies`() {
        assertEquals("3 Sep 2026", ago(48, ChronoUnit.HOURS))
        assertEquals("1 Jan 2020", formatter.format(Instant.parse("2020-01-01T08:00:00Z"), now))
    }

    @Test
    fun `the month abbreviation follows the locale`() {
        val uk = DateFormatter(ZoneId.of("UTC"), Locale.UK)

        assertEquals("3 Sept 2026", uk.format(now.minus(48, ChronoUnit.HOURS), now))
    }

    @Test
    fun `future timestamps do not render as negative`() {
        assertEquals("Just now", formatter.format(now.plus(5, ChronoUnit.MINUTES), now))
    }
}
