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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


//questa classe contiene funzioni extra condivise dalle due modalità di gioco (singleplayer e multiplayer), ad es blocco dadi

class PlayUtils {

    fun DiceBlock(){
        //funzione che blocca un dado quando viene selezionato, ci applichiamo un filtro grigio scuro e impediamo che venga rilanciato
    }


    @Composable
    fun ImageSequence(
        imageIds: List<Int>,  // Lista di ID delle immagini da visualizzare
        rotationValues: List<Float>
    ) {
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

                var clickedStates by rememberSaveable { mutableStateOf(List(imageIds.size) { false }) }

                for (i in imageIds.indices) {
                    val isClicked = clickedStates[i]
                    Image(
                        painter = painterResource(id = imageIds[i]),  // Carica l'immagine con il suo ID
                        contentDescription = null,
                        modifier = Modifier
                            .size(imageSize)
                            .scale(0.7f)
                            .rotate(rotationValues[i])
                            .padding(1.dp)
                            .graphicsLayer {
                                // Cambia la luminosità in base al click
                                //alpha = if (isClicked) 0.3f else 1.0f
                            }
                            .clickable {
                                // Cambia lo stato dell'immagine quando viene cliccata
                                clickedStates = clickedStates.toMutableList().also {
                                    it[i] = !isClicked
                                }
                            },
                        contentScale = ContentScale.Crop,
                        colorFilter = if (isClicked) {
                            ColorFilter.tint(
                                color = Color.Black.copy(alpha = 0.5f),  // Applica un'ombra nera semi-trasparente
                                blendMode = BlendMode.Multiply
                            )
                        } else {
                            null  // Nessun filtro quando non è cliccata
                        }
                    )
                }
            }
        }
    }
    /*
    @Composable
    fun blockDice(i: Int): () -> Unit {
        //blocca il dado cambiandone il colore e modificandone lo stato
        return
    }



     */
    // Funzione per calcolare la dimensione massima delle immagini
    @Composable
    fun calculateImageSize(imageCount: Int): Dp {
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        return (screenWidth / imageCount) - 8.dp // Sottrai un padding di 4.dp su ogni lato
    }


    fun getImageResourceIds(diceValues: List<Int>, context: Context): List<Int> {
        return diceValues.map { diceValue ->
            val resourceName = "dice$diceValue" // Costruisce il nome dell'immagine (es. dice1, dice2...)
            val resourceId = getDrawableResourceByName(resourceName, context)
            resourceId
        }
    }

    private fun getDrawableResourceByName(name: String, context: Context): Int {
        val resourceId = context.resources.getIdentifier(name, "drawable", context.packageName)
        if (resourceId == 0) {
            throw IllegalArgumentException("Risorsa drawable non trovata per nome: $name")
        }
        return resourceId
    }

}