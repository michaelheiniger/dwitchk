package ch.qscqlmpa.dwitch.ui.home.main

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultBlendMode
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.base.ActivityScreenContainer


@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun Preview() {
    ActivityScreenContainer {
        SmileyFaceCanvas(Modifier)
    }
}

enum class MyState {
    STATE_A, STATE_B
}

@Composable
fun SmileyFaceCanvas(
    modifier: Modifier
) {

    var currentState by remember { mutableStateOf(MyState.STATE_A) }
    val transition = updateTransition(currentState, label = "")


    val deltaXAnim = rememberInfiniteTransition()
//    val anim = rememberTransformableState(
//        onTransformation =
//    )

    val dx by deltaXAnim.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000)
        )
    )
//    val dx2 by deltaXAnim.animateFloat(
//        initialValue = 1f,
//        targetValue = 0f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(1000)
//        )
//    )

    val image = ImageBitmap.Companion.imageResource(R.drawable.spades_ace)

    val imageWidth = transition.animateFloat(label = "") {
        when (it) {
            MyState.STATE_A -> image.width.toFloat()
            MyState.STATE_B -> (image.width * 1.5).toFloat()
        }
    }

    val imageHeight = transition.animateFloat(label = "") {
        when (it) {
            MyState.STATE_A -> image.height.toFloat()
            MyState.STATE_B -> (image.height * 1.5).toFloat()
        }
    }
    Column {
        Button(onClick = {
            currentState = when (currentState) {
                MyState.STATE_A -> MyState.STATE_B
                MyState.STATE_B -> MyState.STATE_A
            }
        }) {
            Text("Salut")
        }
        Canvas(
            modifier = modifier.size(300.dp),
            onDraw = {
                drawImage(
                    image = image,
                    srcSize = IntSize(imageWidth.value.toInt(), imageHeight.value.toInt()),
//                topLeft = Offset(size.width / 2 - image.width/2, size.height / 2 - image.height / 2),
//                topLeft = Offset(dx*size.width / 2, dx*size.height / 2),
                    alpha = 1.0f,
                    style = Fill,
                    colorFilter = ColorFilter.tint(Color.Blue),
                    blendMode = DefaultBlendMode
                )

//            Transition(definition = definition, initState = MyState.STATE_A, toState = STATE_B) {
//                val currentFloatValue = it[myFloatKey]
//                println("Transition: $currentFloatValue")
//            }


                // Head
//            drawCircle(
//                Brush.linearGradient(colors = listOf(Color.Green, Color.Yellow)),
//                radius = size.width / 2,
//                center = center,
//                style = Stroke(width = size.width * 0.04f)
//            )

                // Smile
//            val smilePadding = size.width * 0.15f
//            drawArc(
//                color = Color.Red,
//                startAngle = 0f,
//                sweepAngle = dx * 360f,
//                useCenter = false,
//                topLeft = Offset(smilePadding, smilePadding),
//                size = Size(size.width - (smilePadding * 2f), size.height - (smilePadding * 2f))
//            )

//            // Left eye
//            drawRect(
//                color = Color.Black,
//                topLeft = Offset(size.width * 0.35f, size.height / 4),
//                size = Size(smilePadding, smilePadding)
//            )
//
//            // Right eye
//            drawRect(
//                color = Color.Black,
//                topLeft = Offset((size.width * 0.75f) - smilePadding, size.height * 0.3f),
//                size = Size(smilePadding, smilePadding)
//            )
            }
        )
    }
}