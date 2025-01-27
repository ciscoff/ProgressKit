package dev.barabu.pkit.ui.live_pictures

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AquariumFish(
    waterLevel: Float = 0.8f,
    numberBubbles: Int = 8,
    modifier: Modifier
) {

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {

        Aquarium(
            waterLevel = waterLevel,
            numberBubbles = numberBubbles,
            modifier = modifier
        ) {

            Fish(
                boxHeight = 200.dp,
                modifier = modifier
            )
        }
    }
}