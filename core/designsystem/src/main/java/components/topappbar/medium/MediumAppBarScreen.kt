package com.gbr.designsystem.components.topappbar.medium

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gbr.designsystem.R
import com.gbr.designsystem.theme.SemestaUIKitTheme


@Composable
fun MediumAppBarScreen() {

    SemestaUIKitTheme {
        Scaffold(
            topBar = { MediumAppBarView() },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ) { innerPadding ->

            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.potted_plant_24px),
                    contentDescription = "Plant",
                    tint = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.size(160.dp)
                )

            }
        }
    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediumAppBarView(title: String = "Headline Medium") {
    MediumTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        navigationIcon = {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.arrow_back_24px),
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.attach_file_24px),
                    contentDescription = "More"
                )
            }

            IconButton(onClick = { }) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.calendar_today_24px),
                    contentDescription = "Event"
                )
            }

            IconButton(onClick = { }) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.more_vert_24px),
                    contentDescription = "More"
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


@Preview
@Composable
private fun MediumAppBarPreview() {
    SemestaUIKitTheme {
        MediumAppBarView()
    }
}