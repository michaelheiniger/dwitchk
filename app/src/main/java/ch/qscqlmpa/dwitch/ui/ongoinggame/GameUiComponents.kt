package ch.qscqlmpa.dwitch.ui.ongoinggame

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.ResourceMapper
import ch.qscqlmpa.dwitch.ui.common.InfoDialog
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.info.CardItem

@Composable
fun GameOverDialog(onGameOverAcknowledge: () -> Unit) {
    InfoDialog(
        title = R.string.info_dialog_title,
        text = R.string.game_canceled_by_host,
        onOkClick = onGameOverAcknowledge
    )
}

@Composable
fun LoadingSpinner() {
    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colors.secondary
        )
    }
}

@Composable
fun CardItemDisplay(
    cardItem: CardItem,
    onCardClick: (Card) -> Unit
) {
    val surfaceColor = if (cardItem.selectable) Color.Transparent else Color(
        red = 0.3f,
        green = 0.3f,
        blue = 0.3f,
        alpha = 0.2f
    )
    Image(
        painter = painterResource(ResourceMapper.getResource(cardItem.card)),
        contentDescription = cardItem.toString(),
        colorFilter = ColorFilter.tint(surfaceColor, BlendMode.Overlay),
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .clickable(
                enabled = cardItem.selectable,
                onClick = { onCardClick(cardItem.card) }
            )
            .testTag(cardItem.card.toString())
//            .border(
//                width = Dp.Hairline,
//                brush = Brush.linearGradient(listOf(Color.Black, Color.Black)),
//                shape = RoundedCornerShape(10.dp, 10.dp, 10.dp, 10.dp)
//            )
    )
}