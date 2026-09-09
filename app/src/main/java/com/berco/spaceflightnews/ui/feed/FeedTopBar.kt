package com.berco.spaceflightnews.ui.feed

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.berco.spaceflightnews.R
import androidx.compose.ui.unit.dp
import com.berco.spaceflightnews.core.ui.OrganicIcons

private val FEED_HEADER_HEIGHT = 92.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LargeTopAppBar(
        expandedHeight = FEED_HEADER_HEIGHT,
        title = { Text(stringResource(R.string.feed_title), style = MaterialTheme.typography.displaySmall) },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    OrganicIcons.Search,
                    contentDescription = stringResource(R.string.feed_search_open),
                    modifier = Modifier.size(24.dp),
                )
            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface,
        ),
        modifier = modifier,
    )
}
