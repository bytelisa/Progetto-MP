package it.VES.yahtzee

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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


//questa classe contiene funzioni extra condivise dalle due modalità di gioco (singleplayer e multiplayer), ad es blocco dadi

class PlayUtils {


    @Composable
    fun ImageSequence(
        rolledDice: List<Int>,  // Lista dei dadi
        rotationValues: List<Float>,
        context: Context
    ) {
        var clickedStates by rememberSaveable { mutableStateOf(List(5) { false }) }
        var oldImageIds = rememberSaveable { mutableStateListOf(*List(5) { 0 }.toTypedArray()) }
        var imageIds: List<Int> = PlayUtils().getImageResourceIds(rolledDice, context, clickedStates)


        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(720.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),

                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                val imageSize = calculateImageSize(imageIds.size)

                for (i in imageIds.indices) {
                    oldImageIds[i]= imageIds[i]

                    val isClicked = clickedStates[i]
                    Image(

                        painter = painterResource(
                            id = if (imageIds[i] == 999) {
                                oldImageIds[i]
                            } else{
                            imageIds[i]
                        }
                        ),  // Carica l'immagine con il suo ID
                        contentDescription = null,
                        modifier = Modifier
                            .size(imageSize)
                            .scale(0.7f)
                            .rotate(rotationValues[i])
                            .padding(1.dp)
                            .graphicsLayer {
                                // Cambia la luminosità in base al click
                                alpha = if (isClicked) 0.3f else 1.0f
                            }
                            .clickable {
                                // Cambia lo stato dell'immagine quando viene cliccata
                                clickedStates = clickedStates
                                    .toMutableList()
                                    .also {
                                        it[i] = !isClicked
                                    }
                                imageIds = getImageResourceIds(rolledDice, context, clickedStates)
                            },
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        }
    }


    // Funzione per calcolare la dimensione massima delle immagini
    @Composable
    fun calculateImageSize(imageCount: Int): Dp {
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        return (screenWidth / imageCount) - 8.dp // Sottrai un padding di 4.dp su ogni lato
    }


    fun getImageResourceIds(diceValues: List<Int>, context: Context, clickedStates: List<Boolean>): List<Int> {
        return diceValues.mapIndexed { index, currentValue ->
            if (!clickedStates[index]) {
                val resourceName = "dice$currentValue" // Costruisce il nome dell'immagine (es. dice1, dice2...)
                getDrawableResourceByName(resourceName, context)
                // Se non è cliccata, aggiorna con il nuovo ID
            } else {
                getDrawableResourceByName("default", context)
                // Mantieni l'ID corrente se l'immagine è cliccata

            }
        }

    }

    private fun getDrawableResourceByName(name: String, context: Context): Int {

        if (name == "default"){
            return 999
        }

        val resourceId = context.resources.getIdentifier(name, "drawable", context.packageName)
        if (resourceId == 0) {
            throw IllegalArgumentException("Risorsa drawable non trovata per nome: $name")
        }
        return resourceId
    }

}