package it.VES.yahtzee

import android.content.Context
import androidx.compose.foundation.Image
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


//questa classe contiene funzioni extra condivise dalle due modalità di gioco (singleplayer e multiplayer), ad es blocco dadi
//TODO: spostare qui le funzioni del lancio dadi presenti in singleplayer

class PlayUtils {

    fun DiceBlock(){
        //funzione che blocca un dado quando viene selezionato
    }


    @Composable
    fun ImageSequence(
        imageIds: List<Int>,  // Lista di ID delle immagini da visualizzare
        rotationValues: List<Float>
    ) {
        Column(
            modifier= Modifier
                .fillMaxSize()
        ){
            Spacer(modifier = Modifier.height(720.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                ,

                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                val imageSize = calculateImageSize(imageIds.size)

                for (i in imageIds.indices) {

                    Image(
                        painter = painterResource(id = imageIds[i]),  // Carica l'immagine con il suo ID
                        contentDescription = null,
                        modifier = Modifier
                            .size(imageSize)
                            .scale(0.7f)
                            .rotate(rotationValues[i])
                            .padding(1.dp),
                        contentScale = ContentScale.Crop
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


    fun getImageResourceIds(diceValues: List<Int>, context: Context): List<Int> {
        return diceValues.map { diceValue ->
            val resourceName = "dice$diceValue" // Costruisce il nome dell'immagine (es. dice1, dice2...)
            val resourceId = getDrawableResourceByName(resourceName, context)
            resourceId
        }
    }

    fun getDrawableResourceByName(name: String, context: Context): Int {
        val resourceId = context.resources.getIdentifier(name, "drawable", context.packageName)
        if (resourceId == 0) {
            throw IllegalArgumentException("Risorsa drawable non trovata per nome: $name")
        }
        return resourceId
    }

}