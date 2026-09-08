package com.berco.spaceflightnews.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.berco.spaceflightnews.core.ui.R
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.berco.spaceflightnews.core.ui.OrganicIcons
import com.berco.spaceflightnews.core.ui.preview.PreviewSurface
import com.berco.spaceflightnews.core.ui.theme.OrganicRadius

/**
 * The full-screen treatment shared by the error and empty artboards: a tinted
 * disc, a Caprasimo heading, one line of guidance and a single primary action.
 */
@Composable
fun StatusView(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    discColor: Color = MaterialTheme.colorScheme.primaryContainer,
    iconTint: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    actionLabel: String? = null,
    actionIcon: ImageVector? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(112.dp)
                .clip(CircleShape)
                .background(discColor),
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(48.dp))
        }

        Spacer(Modifier.height(26.dp))
        Text(title, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)

        Spacer(Modifier.height(10.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 300.dp),
        )

        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(26.dp))
            Button(onClick = onAction, shape = CircleShape) {
                if (actionIcon != null) {
                    Icon(actionIcon, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                }
                Text(actionLabel, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

/** Appended below the list while more pages load, so cached rows stay visible. */
@Composable
fun InlineErrorRow(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(OrganicRadius.Row),
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Row(
            Modifier.padding(start = 18.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Icon(
                OrganicIcons.Alert,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(22.dp),
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            TextButton(
                onClick = onRetry,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
            ) {
                Text(stringResource(R.string.action_retry), style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
fun EndOfListFooter(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            listOf(
                MaterialTheme.colorScheme.secondary,
                MaterialTheme.colorScheme.secondaryContainer,
                MaterialTheme.colorScheme.outlineVariant,
            ).forEach { color ->
                Box(Modifier.size(7.dp).clip(CircleShape).background(color))
            }
        }
        Spacer(Modifier.height(20.dp))
        Text(
            stringResource(R.string.end_of_list_title),
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(R.string.end_of_list_message),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(name = "Error", widthDp = 412, heightDp = 520, showBackground = true)
@Composable
private fun StatusViewErrorPreview() {
    PreviewSurface {
        StatusView(
            icon = OrganicIcons.Alert,
            title = "No connection",
            message = "We couldn't reach the newsroom. Check your connection and try again.",
            actionLabel = "Try again",
            actionIcon = OrganicIcons.Refresh,
            onAction = {},
        )
    }
}

@Preview(name = "Empty favorites", widthDp = 412, heightDp = 520, showBackground = true)
@Composable
private fun StatusViewEmptyPreview() {
    PreviewSurface {
        StatusView(
            icon = OrganicIcons.HeartOutline,
            title = "Nothing saved yet",
            message = "Tap the heart on any story and it will wait for you here — no connection needed.",
            discColor = MaterialTheme.colorScheme.secondaryContainer,
            iconTint = MaterialTheme.colorScheme.onSecondaryContainer,
            actionLabel = "Browse the feed",
            actionIcon = OrganicIcons.ChevronRight,
            onAction = {},
        )
    }
}

@Preview(name = "Inline error", widthDp = 412, showBackground = true)
@Composable
private fun InlineErrorPreview() {
    PreviewSurface {
        InlineErrorRow("Couldn't load more stories.", onRetry = {})
    }
}

@Preview(name = "End of list", widthDp = 412, showBackground = true)
@Composable
private fun EndOfListPreview() {
    PreviewSurface { EndOfListFooter() }
}
