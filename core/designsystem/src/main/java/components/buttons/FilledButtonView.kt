package components.buttons

import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.gbr.designsystem.R
import com.gbr.designsystem.theme.SemestaUIKitTheme


@Composable
fun FilledButtonView() {

    Button(
        onClick = {},
    ) {
        Text(
            text = stringResource(R.string.preview_save),
            style = MaterialTheme.typography.labelLarge
        )
    }

}

@Composable
fun FilledButtonView(
    text: String,
    onClick: () -> Unit,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}


@Preview
@Composable
private fun FilledButtonPreview() {
    SemestaUIKitTheme {
        Surface {
            FilledButtonView()
        }
    }
}
