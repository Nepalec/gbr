package components.buttons.icon

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gbr.designsystem.R
import com.gbr.designsystem.theme.SemestaUIKitTheme

@Composable
fun OutlinedIconButtonView() {
    OutlinedIconButton(
        onClick = { }
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.call_24px),
            contentDescription = "Call",
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview
@Composable
private fun OutlinedIconButtonPreview() {
    SemestaUIKitTheme {
        Surface {
            OutlinedIconButtonView()
        }
    }
}