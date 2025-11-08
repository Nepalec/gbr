package components.buttons

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.gbr.designsystem.R
import com.gbr.designsystem.theme.SemestaUIKitTheme


@Composable
fun OutlinedButtonView() {

    OutlinedButton(
        onClick = { }
    ) {
        Text(
            text = stringResource(R.string.preview_back_to_home),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun OutlinedButtonView(
    text: String,
    onClick: () -> Unit,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    OutlinedButton(
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
private fun OutlinedButtonPreview() {
    SemestaUIKitTheme {
        Surface {
            OutlinedButtonView()
        }
    }
}
