package com.gbr.designsystem.components.navigationbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gbr.designsystem.R
import com.gbr.designsystem.theme.SemestaUIKitTheme

@Composable
fun NavigationBarContent(onIconOnlyClick: () -> Unit = {}, onTextAndIconClick: () -> Unit = {}) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .padding(24.dp)
    ) {
        OutlinedButton(
            onClick = onIconOnlyClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.preview_icon_only))
        }

        OutlinedButton(
            onClick = onTextAndIconClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.preview_with_text_and_icon))
        }
    }
}

@Preview
@Composable
private fun NavigationBarContentPreview() {
    SemestaUIKitTheme {
        NavigationBarContent()
    }
}