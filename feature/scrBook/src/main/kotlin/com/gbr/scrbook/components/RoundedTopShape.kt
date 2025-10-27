package com.gbr.scrbook.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * Custom shape that creates rounded top corners with 190dp radius,
 * matching the @drawable/rounded_corner_book_top XML drawable.
 */
class RoundedTopShape(
    private val topLeftRadius: Dp = 150.dp,
    private val topRightRadius: Dp = 150.dp
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: androidx.compose.ui.unit.Density
    ): Outline {
        val topLeftRadiusPx = with(density) { topLeftRadius.toPx() }
        val topRightRadiusPx = with(density) { topRightRadius.toPx() }

        val path = Path().apply {
            // Start from top-left corner
            moveTo(0f, topLeftRadiusPx)

            // Top-left rounded corner
            arcTo(
                rect = Rect(
                    left = 0f,
                    top = 0f,
                    right = topLeftRadiusPx * 2,
                    bottom = topLeftRadiusPx * 2
                ),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            // Top edge
            lineTo(size.width - topRightRadiusPx, 0f)

            // Top-right rounded corner
            arcTo(
                rect = Rect(
                    left = size.width - topRightRadiusPx * 2,
                    top = 0f,
                    right = size.width,
                    bottom = topRightRadiusPx * 2
                ),
                startAngleDegrees = 270f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            // Right edge
            lineTo(size.width, size.height)

            // Bottom edge
            lineTo(0f, size.height)

            // Left edge
            lineTo(0f, topLeftRadiusPx)

            close()
        }

        return Outline.Generic(path)
    }
}
