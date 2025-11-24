package com.gbr.designsystem.components.radiobuttonwithtext

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gbr.designsystem.R
import com.gbr.designsystem.theme.SemestaUIKitTheme

@Composable
fun RadioButtonWithTextView() {
    var isChecked by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(24.dp)
            .clickable {
                isChecked = !isChecked
            }
    ) {
        RadioButton(
            selected = isChecked,
            onClick = { isChecked = !isChecked }
        )

        Text(text = stringResource(R.string.preview_allow_notifications))
    }
}

@Composable
fun RadioButtonWithTextView(text: String, isSelected: Boolean, onSelected: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(24.dp)
            .clickable { onSelected() }
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelected
        )
        Text(text = text)
    }
}

@Preview
@Composable
private fun RadioButtonWithTextPreview() {
    SemestaUIKitTheme {
        Surface {
            RadioButtonWithTextView()
        }
    }
}