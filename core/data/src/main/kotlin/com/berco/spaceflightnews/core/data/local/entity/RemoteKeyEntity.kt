package com.berco.spaceflightnews.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Single-row pagination state for the feed. Room knows how many articles it
 * holds but not which network offset comes next, nor which snapshot the current
 * pagination session is pinned to, so both are persisted here.
 */
@Entity(tableName = "remote_keys")
data class RemoteKeyEntity(
    @PrimaryKey val id: Int = SINGLE_ROW_ID,
    /** Null once the API reports no further pages. */
    val nextOffset: Int?,
    val snapshotIso: String,
    val lastRefreshedAtMillis: Long,
) {
    companion object {
        const val SINGLE_ROW_ID = 0
    }
}
