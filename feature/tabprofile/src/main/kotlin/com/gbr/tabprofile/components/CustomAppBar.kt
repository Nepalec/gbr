package com.gbr.tabprofile.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.gbr.designsystem.R
import com.gbr.tabprofile.R as ProfileR

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CustomAppBar(
    title: String,
    onNavigationClick: () -> Unit = {},
    onActionClick: () -> Unit = {},
    navigationIcon: Int = R.drawable.menu_24px,
    actionIcon: Int = R.drawable.settings_24px
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = { }, // Remove hamburger icon for Profile tab
        actions = {
            IconButton(onClick = onActionClick) {
                Icon(
                    imageVector = ImageVector.vectorResource(actionIcon),
                    contentDescription = stringResource(ProfileR.string.cd_settings)
                )
            }
        },
        colors = TopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            scrolledContainerColor = MaterialTheme.colorScheme.primary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
    )
}




