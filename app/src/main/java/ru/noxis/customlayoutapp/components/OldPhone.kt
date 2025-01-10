package ru.noxis.customlayoutapp.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
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
    //
    var accumulatedDelta by remember { mutableFloatStateOf(0f) }
    var activeDigitIndex by remember { mutableIntStateOf(-1) }

    //список DigitBounds равный количеству цифр
    val digitBoundsList = remember { List(digits.size) { DigitBounds() } }


    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    //определение нажатой цифры внутри обработчика касаний
                    val rotatedPoint = down.position.rotate(
                        center = size.center,
                        degrees = -rotationAngle,
                    )

                    val digitHit = digitBoundsList.indexOfFirst { it.contains(rotatedPoint) }

                    //сохраняем индекс цифры, если пользователь попал по ней
                    activeDigitIndex = digitHit

                    if (digitHit != -1) {

                        var currentAngle = calculateAngle(down.position, size.center)
                        do {
                            val event = awaitPointerEvent()
                            val newAngle =
                                calculateAngle(event.changes.first().position, size.center)
//                        /**
//                         * Когда палец двигается, мы вычисляем разницу между новым углом и предыдущим
//                         * deltaAngle - изменение угла
//                         */
//                        val deltaAngle = newAngle - currentAngle

                            //необходимо нормализовать deltaAngle
                            val deltaAngle = (newAngle - currentAngle).let { delta ->
                                when {
                                    delta > 180f -> delta - 360f
                                    delta < -180f -> delta + 360f
                                    else -> delta
                                }
                            }
//                        rotationAngle = (rotationAngle + deltaAngle)
                            val targetRotation = rotationAngle + deltaAngle

                            //движения диска только в одну сторону, а именно от 0 до 270 градусов
                            val canRotate = accumulatedDelta in 0f..270f
                            if (canRotate) {
                                rotationAngle = targetRotation.coerceIn(0f, 270f)
                                accumulatedDelta =
                                    (accumulatedDelta + deltaAngle).coerceIn(0f, 270f)
                            }

                            currentAngle = newAngle
                            event.changes
                                .first()
                                .consume()
                        } while (event.changes.any { it.pressed })

                        activeDigitIndex = -1
                    }

                }
            }

    ) {
        //шаг расположения цифр
        val angleStep = -270f / digits.size
        val radius = size.width / 2f - 20.dp.toPx()
        //цент круга
        val center = Offset(size.width / 2, size.height / 2)
        val digitSize = 64.dp.toPx()

        //заполняем список digitBoundsList реальными данными уже в момент отрисоки
        digits.forEachIndexed { index, _ ->
            val angleInDegrees = index * angleStep - 45
            val angleInRadians = Math.toRadians(angleInDegrees.toDouble())

            val x = center.x + radius * cos(angleInRadians).toFloat()
            val y = center.y + radius * sin(angleInRadians).toFloat()

            digitBoundsList[index].update(x, y, digitSize)
        }

        rotate(rotationAngle) {
            drawCircle(
                color = Color.White,
            )

            digits.forEachIndexed { index, digit ->
                //необходимо добавить смещение на -45 градусов.
//                val angleInDegrees = index * angleStep - 45
                //перевести угол из градусов в радианы
//                val angleInRadians = Math.toRadians(angleInDegrees.toDouble())
                //координаты размещения цифр на циферблате
//                val x = center.x + radius * cos(angleInRadians).toFloat()
//                val y = center.y + radius * sin(angleInRadians).toFloat()

                val bounds = digitBoundsList[index]
                val x = (bounds.left + bounds.right) / 2
                val y = (bounds.top + bounds.bottom) / 2

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

                //Индекс нажатой цифры был найден, осталось нарисовать индикатор
                val isActive = index == activeDigitIndex

                if (isActive) {
                    drawCircle(
                        color = Color.Blue.copy(alpha = 0.3f),
                        radius = digitSize / 1.8f,
                        center = Offset(x, y)
                    )
                }
            }

        }
    }

}

//
private fun Offset.rotate(
    center: IntOffset,
    degrees: Float,
): Offset {
    val angle = Math.toRadians(degrees.toDouble())
    val cos = cos(angle).toFloat()
    val sin = sin(angle).toFloat()

    val x = this.x - center.x
    val y = this.y - center.y

    return Offset(
        x = center.x + (x * cos - y * sin),
        y = center.y + (x * sin + y * cos)
    )
}

//вычисляется угол поворота телефонного диска
private fun calculateAngle(position: Offset, center: IntOffset): Float {
    //При каждом движении пальца мы определяем разницу по осям X и Y:
    val dx = position.x - center.x
    val dy = position.y - center.y
    //Эти значения описывают вектор от центра круга к точке касания.
    //С этим вектором мы можем вычислить угол с помощью функции atan2
    val angleInRadians = atan2(dy.toDouble(), dx.toDouble())
//Теперь у нас есть угол в диапазоне от −180 до 180 градусов.
    return Math.toDegrees(angleInRadians).toFloat()
}

@Preview
@Composable
private fun OldPhonePreview() {
    OldPhone(modifier = Modifier)
}