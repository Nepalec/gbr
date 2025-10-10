package com.gbr.designsystem.components.navigationbar.textandicon

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gbr.designsystem.R
import com.gbr.designsystem.theme.SemestaUIKitTheme
import com.gbr.designsystem.components.topappbar.small.SmallAppBarView
//
//@Composable
//fun NavigationBarWithTextAndIconScreen() {
//
//    SemestaUIKitTheme {
//        Scaffold(
//            topBar = { SmallAppBarView("Text & Icon") },
//            bottomBar = { NavigationBarWithTextAndIconView() },
//            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
//        ) { innerPadding ->
//
//            Box(
//                modifier = Modifier
//                    .padding(innerPadding)
//                    .fillMaxWidth()
//                    .fillMaxHeight(),
//                contentAlignment = Alignment.Center
//            ) {
//
//                Icon(
//                    imageVector = ImageVector.vectorResource(R.drawable.potted_plant_24px),
//                    contentDescription = "Plant",
//                    tint = MaterialTheme.colorScheme.outlineVariant,
//                    modifier = Modifier.size(160.dp)
//                )
//
//            }
//        }
//    }
//
//
//}

@Composable
fun NavigationBarWithTextAndIconView(
    items: List<String>,
    icons: List<Int>,
    selectedIcons: List<Int>,
    selectedIndex: Int = 0,
    onItemClick: (Int) -> Unit = {}
) {
    var selectedTabIndex by remember { mutableIntStateOf(selectedIndex) }

    NavigationBar {
        items.forEachIndexed { index, title ->
            val isSelected = selectedTabIndex == index
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    selectedTabIndex = index
                    onItemClick(index)
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) {
                            ImageVector.vectorResource(selectedIcons[index])
                        } else {
                            ImageVector.vectorResource(icons[index])
                        },
                        contentDescription = title,
                        tint = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        }
                    )

                },
                label = {
                    Text(text = title)
                }
            )
        }
    }
}


@Preview
@Composable
private fun NavigationBarWithTextAndIconPreview() {
    SemestaUIKitTheme {
        NavigationBarWithTextAndIconView(
            items = listOf("Tab 1", "Tab 2", "Tab 3"),
            icons = listOf(
                R.drawable.home_24px,
                R.drawable.news_24px,
                R.drawable.bookmark_24px,
            ),
            selectedIcons = listOf(
                R.drawable.home_filled_24px,
                R.drawable.news_filled_24px,
                R.drawable.bookmark_filled_24px,
            )
        )
    }
}
