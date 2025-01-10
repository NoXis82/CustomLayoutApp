package ru.noxis.customlayoutapp.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

private val digits = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")


@Composable
fun OldPhone(
    modifier: Modifier,
) {

    var rotationAngle by remember { mutableFloatStateOf(0f) }
    val textMeasurer = rememberTextMeasurer()
    val typography = MaterialTheme.typography.headlineLarge

    Canvas(
        modifier = modifier.aspectRatio(1f)
    ) {
        rotate(rotationAngle) {
            drawCircle(
                color = Color.White,
            )

            //шаг расположения цифр
            val angleStep = -270f / digits.size
            //цент круга
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width / 2f - 20.dp.toPx()
            digits.forEachIndexed { index, digit ->
                //необходимо добавить смещение на -45 градусов.
                val angleInDegrees = index * angleStep - 45
                //перевести угол из градусов в радианы
                val angleInRadians = Math.toRadians(angleInDegrees.toDouble())
                //координаты размещения цифр на циферблате
                val x = center.x + radius * cos(angleInRadians).toFloat()
                val y = center.y + radius * sin(angleInRadians).toFloat()

                //рисуем циферблат
                val textLayoutResult: TextLayoutResult = textMeasurer.measure(
                    text = digit,
                    style = typography,
                )

                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = Offset(
                        x - textLayoutResult.size.width / 2f,
                        y - textLayoutResult.size.height / 2f,
                    ),
                )

            }

        }
    }

}


@Preview
@Composable
private fun OldPhonePreview() {
    OldPhone(modifier = Modifier)
}