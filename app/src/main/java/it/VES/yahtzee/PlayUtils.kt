package it.VES.yahtzee

import android.annotation.SuppressLint
import android.content.Context
import android.util.MutableBoolean
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


//questa classe contiene funzioni extra condivise dalle due modalità di gioco (singleplayer e multiplayer), ad es blocco dadi

class PlayUtils {

    @SuppressLint("MutableCollectionMutableState")
    @Composable
    fun imageSequence(
        rolledDice: List<Int>,  // Lista dei dadi
        rotationValues: List<Float>,
        context: Context,
    ): List<Boolean>
    {
        var clickedStates by rememberSaveable { mutableStateOf(List(5) { false }) }
        val oldImageIds = remember { mutableStateListOf(*List(5) { 0 }.toTypedArray()) }
        val imageIds by remember(rolledDice, clickedStates) {
            mutableStateOf(PlayUtils().getImageResourceIds(rolledDice, context, clickedStates, oldImageIds))
        }


        Column(
            modifier = Modifier
                .fillMaxSize() ,
            verticalArrangement = Arrangement.Bottom,  // Aligns children at the bottom
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),

                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {

                val imageSize = calculateImageSize(5)

                for (i in imageIds.indices) {

                    if (!clickedStates[i]){
                        oldImageIds[i]= imageIds[i] //oldImageIds viene aggiornato solo per i dadi che non sono stati cliccati

                    }

                    val isClicked = clickedStates[i]
                    Image(

                        painter = painterResource( id = imageIds[i]),
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

                                clickedStates = clickedStates //cambiamo lo stato dell'immagine
                                    .toMutableList()
                                    .also {
                                        it[i] = !isClicked
                                    }
                            },
                        contentScale = ContentScale.Crop,
                    )
                }
            }
            Spacer(modifier = Modifier.height(65.dp))

        }

        return clickedStates
    }


    // Funzione per calcolare la dimensione massima delle immagini
    @Composable
    fun calculateImageSize(imageCount: Int): Dp {
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        return (screenWidth / imageCount) - 8.dp
    }


    fun getImageResourceIds(diceValues: List<Int>, context: Context, clickedStates: List<Boolean>, oldImageIds: List<Int>): List<Int> {
        return diceValues.mapIndexed { index, currentValue ->
            if (!clickedStates[index]) {
                val resourceName = "dice$currentValue" // Costruisce il nome dell'immagine (es. dice1, dice2...)
                getDrawableResourceByName(resourceName, context)
                // Se non è cliccata, aggiorna con il nuovo ID
            } else {
                oldImageIds[index]
                // se immagine è bloccata dobbiamo mantenere il vecchio id della sua immagine
                //posso fare una funzione che salva gli id correnti non appena l'immagine viene cliccata, e poi qui lo accedo

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
        //questa funzione sfrutta la classe ScoreCalculator per calcolare la preview di tutti i punteggi che poi verrà usata da ScoreTable
        return List(14) {
                index -> ScoreCalculator().point(index+1, ArrayList(rolledDice))
        }
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