package com.berco.spaceflightnews.core.data.fake

import kotlin.time.Clock
import kotlin.time.Instant

/** kotlin.time.Clock has no `fixed` factory, so tests supply their own. */
fun fixedClock(instant: Instant): Clock = object : Clock {
    override fun now(): Instant = instant
}
