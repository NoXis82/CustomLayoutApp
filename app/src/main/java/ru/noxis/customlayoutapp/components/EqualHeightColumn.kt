package ru.noxis.customlayoutapp.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.unit.dp

@Composable
fun EqualHeightColumn(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    // Кеширование MeasurePolicy чтобы не создавать его заново во время рекомпозиции
    val measurePolicy = rememberEqualHeightColumnMeasurePolicy()

    Layout(
        content = content,
        modifier = modifier,
        measurePolicy = measurePolicy
    )

}

@Composable
private fun rememberEqualHeightColumnMeasurePolicy(): MeasurePolicy {
    return remember {
        MeasurePolicy { measurables, constraints ->
            //нам необходимо найти максимальную высоту дочерних элементов
            val maxHeight = measurables.maxOf { measurable ->
                measurable.minIntrinsicHeight(constraints.maxWidth)
            }
            //необходимо обновить Constraints , чтобы все дочерние объекты имели одинаковую высоту
            val updatedConstraints = constraints.copy(
                minHeight = maxHeight,
                maxHeight = maxHeight,
            )
//            val placeables = measurables.map { measurable ->
//                measurable.measure(constraints)
//            }

            //измеряем все имеющиеся Measurable , одновременно
            // с этим считаем финальные размеры Layout
            var width = constraints.minWidth
            var height = 0
            val placeables = measurables.mapIndexed { index, measurable ->
                val placeable = measurable.measure(updatedConstraints)
                width = maxOf(width, placeable.width)
                val padding = if (index == measurables.size - 1) 0 else 8.dp.roundToPx()
                height += placeable.height + padding
                placeable
            }

            //Ширина определяется максимальным значением ширины,
            // а высота суммой высот дочерних элементов плюс небольшой отступ между ними.
            var y = 0
            layout(width, height) {
            placeables.forEach {
                it.placeRelative(0, y)

                y += it.height + 8.dp.roundToPx()
            }
        }
//            layout(constraints.maxWidth, constraints.maxHeight) {
//                placeables.forEach { placeable ->
//                    placeable.placeRelative(0, y)
////                    y += placeable.height
//                    // добавляю дополнительные 10.dp между объектами
//                    y += placeable.height + 10.dp.roundToPx()
//                }
//            }
        }
    }
}


