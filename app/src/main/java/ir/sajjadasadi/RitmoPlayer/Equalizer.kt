package ir.sajjadasadi.RitmoPlayer

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun Equalizer(isPlaying: Boolean) {
    val animationValues = remember { List(200) { mutableStateOf(0.5f) } }
    val animatedValues = animationValues.map { animateFloatAsState(it.value) }
    val alpha by animateFloatAsState(
        targetValue = if (isPlaying) 1f else 0f,
        animationSpec = tween(durationMillis = 250)
    )

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            animationValues.forEachIndexed { index, _ ->
                animationValues[index].value = Random.nextFloat().coerceIn(0.3f, 1.0f)
            }
            delay(5) // کاهش زمان تأخیر برای افزایش سرعت انیمیشن
        }
    }

    Canvas(modifier = Modifier
        .fillMaxSize()
        .graphicsLayer(alpha = alpha)) {
        val barWidth = size.width / 200
        animatedValues.forEachIndexed { index, animatedValue ->
            drawRect(
                color = Color.White.copy(alpha = 0.5f),
                size = androidx.compose.ui.geometry.Size(barWidth, size.height * animatedValue.value),
                topLeft = androidx.compose.ui.geometry.Offset(barWidth * index, (size.height - (size.height * animatedValue.value)) / 2),
                style = Stroke(width = barWidth)
            )
        }
    }
}