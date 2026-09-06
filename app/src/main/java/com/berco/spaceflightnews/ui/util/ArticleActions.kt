package com.berco.spaceflightnews.ui.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import com.berco.spaceflightnews.core.model.Article

/** Shares the publisher's own URL, which doubles as the required attribution. */
fun Context.shareArticle(article: Article) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, article.title)
        putExtra(Intent.EXTRA_TEXT, "${article.title}\n\n${article.url}")
    }
    startActivity(Intent.createChooser(intent, "Share article"))
}

fun Context.openArticle(article: Article, @ColorInt toolbarColor: Int) {
    val uri = article.url.toUri()
    val customTab = CustomTabsIntent.Builder()
        .setShowTitle(true)
        .setDefaultColorSchemeParams(
            CustomTabColorSchemeParams.Builder().setToolbarColor(toolbarColor).build(),
        )
        .build()

    try {
        customTab.launchUrl(this, uri)
    } catch (_: ActivityNotFoundException) {
        runCatching { startActivity(Intent(Intent.ACTION_VIEW, uri)) }
    }
}
