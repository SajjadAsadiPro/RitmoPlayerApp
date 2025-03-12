package ir.sajjadasadi.RitmoPlayer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color.Black, // اینجا اصلاح شد
    secondary = Color.DarkGray,
    onPrimary = Color.White,
    onSecondary = Color.LightGray,
    background = Color.Black, // اینجا هم اصلاح شد
    surface = Color.DarkGray,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
