package it.VES.yahtzee

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class PlayUtils {

    @SuppressLint("MutableCollectionMutableState")
    @Composable
    fun imageSequence(
        rolledDice: List<Int>,  // Lista dei dadi
        rotationValues: List<Float>,
        context: Context
    ): MutableList<Boolean> {
        var clickedStates by rememberSaveable { mutableStateOf(List(5) { false }) }
        val oldImageIds = remember { mutableStateListOf(*List(5) { 0 }.toTypedArray()) }
        val imageIds by remember(rolledDice, clickedStates) {
            mutableStateOf(PlayUtils().getImageResourceIds(rolledDice, context, clickedStates, oldImageIds))
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val imageSize = 80.dp  // Dimensione fissa per le immagini dei dadi

            for (i in imageIds.indices) {
                if (!clickedStates[i]) {
                    oldImageIds[i] = imageIds[i] // oldImageIds viene aggiornato solo per i dadi che non sono stati cliccati
                }

                val isClicked = clickedStates[i]
                Image(
                    painter = painterResource(id = imageIds[i]),
                    contentDescription = null,
                    modifier = Modifier
                        .size(imageSize)
                        .scale(0.85f)
                        .rotate(rotationValues[i])
                        .padding(1.dp)
                        .graphicsLayer {
                            // Cambia la luminosità in base al click
                            alpha = if (isClicked) 0.3f else 1.0f
                        }
                        .clickable {
                            clickedStates = clickedStates // cambiamo lo stato dell'immagine
                                .toMutableList()
                                .also {
                                    it[i] = !isClicked
                                }
                        }
                        .offset(x = (i * 100).dp, y = 0.dp),  // Posizione fissa per ogni dado
                    contentScale = ContentScale.Crop,
                )
            }
        }

        return clickedStates.toMutableList()
    }

    fun getImageResourceIds(diceValues: List<Int>, context: Context, clickedStates: List<Boolean>, oldImageIds: List<Int>): List<Int> {
        return diceValues.mapIndexed { index, currentValue ->
            if (!clickedStates[index]) {
                val resourceName = "dice$currentValue" // Costruisce il nome dell'immagine (es. dice1, dice2...)
                getDrawableResourceByName(resourceName, context)
            } else {
                oldImageIds[index]
            }
        }
    }

    private fun getDrawableResourceByName(name: String, context: Context): Int {
        val resourceId = context.resources.getIdentifier(name, "drawable", context.packageName)
        if (resourceId == 0) {
            throw IllegalArgumentException("Risorsa drawable non trovata per nome: $name")
        }
        return resourceId
    }

    fun getScorePreview(rolledDice: List<Int>): List<Int> {
        return List(14) { index -> ScoreCalculator().point(index + 1, ArrayList(rolledDice)) }
    }

    @Composable
    fun RoundsLeft(rounds: Int) {
        Box(
            modifier = Modifier
        ) {
            Text(
                text = "Rounds left: ${13 - rounds}",
                fontSize = 25.sp, // Big
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 15.dp, top = 15.dp)
            )
        }
    }
}