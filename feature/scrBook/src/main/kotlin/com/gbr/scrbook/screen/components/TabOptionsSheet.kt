package com.gbr.scrbook.screen.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.activity.compose.BackHandler
import com.gbr.model.book.BookContentsTabOptions
import com.gbr.model.book.BookImagesTabOptions
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabOptionsSheet(
    showSheet: Boolean,
    onDismissRequest: () -> Unit,
    currentTabIndex: Int,
    onTabColumnsChanged: (Int) -> Unit,
    onGroupByChaptersChange: (Boolean) -> Unit,
    onTextSizeChange: (Int) -> Unit,
    initialContentsOptions: BookContentsTabOptions,
    initialImagesOptions: BookImagesTabOptions?,
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Determine slider configuration based on currentTabIndex
    // Use 4 dots for 4 column options to ensure all values (1,2,3,4) are accessible
    val minColumns: Int
    val maxColumns: Int
    val steps: Int
    val validValues: List<Int>

    if (currentTabIndex > 0) {
        // 4 column options (1,2,3,4) with 4 dots (steps=3 creates 4 positions)
        // Direct 1:1 mapping: position 1->columns 1, 2->2, 3->3, 4->4
        minColumns = 1
        maxColumns = 4
        steps = 3
        validValues = listOf(1, 2, 3, 4)
    } else {
        // 2 column options (1,2) with 2 dots (steps=1 creates 2 positions)
        minColumns = 1
        maxColumns = 2
        steps = 1
        validValues = listOf(1, 2)
    }

    // Initialize slider value - use initialContentsOptions.columns for currentTabIndex == 0,
    // use initialImagesOptions.columns for currentTabIndex > 0
    // Material3 Slider with steps parameter handles discrete snapping automatically
    // Note: Slider API requires Float, so we convert from Int
    var sliderValue by remember(showSheet, currentTabIndex, initialContentsOptions, initialImagesOptions) {
        mutableFloatStateOf(
            if (currentTabIndex == 0) initialContentsOptions.columns.toFloat() else (initialImagesOptions?.columns ?: 2).toFloat()
        )
    }

    // Update slider value when sheet opens or initial options change
    LaunchedEffect(showSheet, initialContentsOptions, initialImagesOptions, currentTabIndex) {
        if (showSheet) {
            sliderValue = if (currentTabIndex == 0) {
                initialContentsOptions.columns.toFloat()
            } else {
                (initialImagesOptions?.columns ?: 2).toFloat()
            }
        }
    }

    // State for "Group by chapters" switch
    var groupedByChapters by remember(showSheet, initialImagesOptions) {
        mutableStateOf(initialImagesOptions?.groupByChapter ?: false)
    }

    // Update switch state when sheet opens or initialImagesOptions changes
    LaunchedEffect(showSheet, initialImagesOptions) {
        if (showSheet && currentTabIndex > 0) {
            groupedByChapters = initialImagesOptions?.groupByChapter ?: false
        }
    }

    // State for text size slider (for currentTabIndex == 0)
    // Range: -2, -1, 0, 1, 2 (5 values), default is initialContentsOptions.textSize
    var textSizeValue by remember(showSheet, currentTabIndex, initialContentsOptions) {
        mutableFloatStateOf(initialContentsOptions.textSize.toFloat())
    }

    // Update slider value when sheet opens or initialContentsOptions changes
    LaunchedEffect(showSheet, initialContentsOptions) {
        if (showSheet && currentTabIndex == 0) {
            textSizeValue = initialContentsOptions.textSize.toFloat()
        }
    }

    if (showSheet) {
        BackHandler {
            scope.launch { sheetState.hide() }.invokeOnCompletion { onDismissRequest() }
        }
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Columns row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Columns",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = sliderValue.roundToInt().toString(),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Slider(
                        value = sliderValue,
                        onValueChange = { newValue ->
                            // Material3 Slider with steps already snaps to discrete positions
                            sliderValue = newValue
                            // Direct mapping: slider value IS the column number
                            val columns = newValue.roundToInt()
                            // Ensure it's a valid column value before calling callback
                            // Only save columns preference when currentTabIndex == 0
                            if (columns in validValues) {
                                onTabColumnsChanged(columns)
                            }
                        },
                        // Slider API requires Float range, convert from Int
                        valueRange = minColumns.toFloat()..maxColumns.toFloat(),
                        steps = steps-1,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                    )
                }
                if(currentTabIndex>0)
                {
                    // Group by chapters row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Group by chapters",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Switch(
                            checked = groupedByChapters,
                            onCheckedChange = { checked ->
                                groupedByChapters = checked
                                onGroupByChaptersChange(checked)
                            }
                        )
                    }
                }

                if (currentTabIndex == 0) {
                    // Text size row (only for contents tab)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Text size:",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Slider(
                            value = textSizeValue,
                            onValueChange = { newValue ->
                                // Material3 Slider with steps already snaps to discrete positions
                                textSizeValue = newValue
                                // Convert to integer text size value (-2, -1, 0, 1, 2)
                                val textSize = newValue.roundToInt()
                                if (textSize in -2..2) {
                                    onTextSizeChange(textSize)
                                }
                            },
                            // Range: -2 to 2, steps = 4 creates 5 positions (-2, -1, 0, 1, 2)
                            valueRange = -2f..2f,
                            steps = 3,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp)
                        )
                    }
                }

            }
        }
    }
}

