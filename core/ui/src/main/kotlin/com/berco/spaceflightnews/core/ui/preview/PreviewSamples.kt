package com.berco.spaceflightnews.core.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.berco.spaceflightnews.core.model.Article
import kotlin.time.Instant

internal object PreviewSamples {

    val article = Article(
        id = 39841,
        title = "Isar Aerospace attempts launch of Spectrum rocket after months of delays",
        summary = "The European private space launch provider Isar Aerospace, " +
            "headquartered in Ottobrunn, Germany, is set to…",
        imageUrl = null,
        newsSite = "NASASpaceflight",
        authors = listOf("Justin Davenport"),
        url = "https://www.nasaspaceflight.com/2026/09/isar-onward-and-upward/",
        publishedAt = Instant.parse("2026-09-05T09:41:57Z"),
        isFavorite = false,
    )

    val shortSummary = article.copy(
        id = 39838,
        title = "Blue Origin Nets NASA Mars Orbiter Contract",
        summary = "Blue Origin will design and launch the Mars Telecommunications " +
            "Orbiter by the end of 2028.",
        newsSite = "Space Scout",
        authors = listOf("Scarlet Dominik"),
        isFavorite = true,
    )

    /** Old records arrive with no summary, no authors and no usable date. */
    val sparse = article.copy(
        id = 1,
        title = "No commercial crew test flights expected this year",
        summary = "",
        newsSite = "Spaceflight Now",
        authors = emptyList(),
        publishedAt = null,
    )
}

internal class ArticleProvider : PreviewParameterProvider<Article> {
    override val values = sequenceOf(
        PreviewSamples.article,
        PreviewSamples.shortSummary,
        PreviewSamples.sparse,
    )
}
