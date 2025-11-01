package components.slider

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gbr.designsystem.theme.SemestaUIKitTheme


@Composable
fun DiscreteSliderView() {

    var sliderPositionWithStep by remember { mutableFloatStateOf(0f) }

    Slider(
        value = sliderPositionWithStep,
        onValueChange = {sliderPositionWithStep = it},
        steps = 5,
        valueRange = 0f..50f,
        modifier = Modifier.fillMaxWidth()
    )

}



@Preview
@Composable
private fun DiscreteSliderPreview() {
    SemestaUIKitTheme {
        Surface {
            DiscreteSliderView()
        }
    }
}