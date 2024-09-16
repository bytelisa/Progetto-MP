package it.VES.yahtzee

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*


import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sqrt

class DiceRollActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var shakeStartTime: Long = 0
    private val shakeDuration = 2000 // 2 secondi di shake
    private val shakeThreshold = 1
    private var lastX = 0.0f
    private var lastY = 0.0f
    private var lastZ = 0.0f


    // Variabile di stato per memorizzare i risultati dei dadi
    private var diceResults by mutableStateOf(List(5) { 1 })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configura il sensore di accelerazione
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        setContent {
            DiceScreen(diceResults = diceResults)
        }
    }


    @Composable
    fun DiceScreen(diceResults: List<Int>) {
        // UI per visualizzare i valori dei dadi e il bottone per lanciare manualmente
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Lancia i dadi scuotendo il dispositivo!",
                fontSize = 24.sp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            )



            // Posiziona l'animazione dei dadi in un punto specifico della schermata
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 100.dp)
            ) {
                Text(
                    text = diceResults.joinToString(" "), // Visualizza i risultati dei dadi
                    fontSize = 32.sp,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
            }



            /*Button(
                onClick = {
                    // Forza un lancio manuale (opzionale)
                    diceResults = rollDice()
                },
                modifier = Modifier
                    .width(200.dp)
                    .height(50.dp)
            ) {
                Text(text = "Lancia manualmente")
            }

             */
        }
    }




    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val currentTime = System.currentTimeMillis()

            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val deltaX = x - lastX
            val deltaY = y - lastY
            val deltaZ = z - lastZ

            val shakeMagnitude = sqrt((deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ).toDouble()).toFloat()

            //Log.d("DiceRollActivity", "Shake Magnitude: $shakeMagnitude") // Log per il debug


            // Se il movimento di shake supera la soglia
            if (shakeMagnitude > shakeThreshold) {
                if (shakeStartTime == 0L) {
                    shakeStartTime = currentTime
                }

                if ((currentTime - shakeStartTime) > shakeDuration) {
                    // Lancia i dadi dopo 3 secondi di shake
                    diceResults = rollDice() // Aggiorna i risultati dei dadi
                    shakeStartTime = 0L // Reset
                }
            } else {
                // Reset il tempo di inizio se non si supera la soglia
                shakeStartTime = 0L
            }

            lastX = x
            lastY = y
            lastZ = z
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onResume() {
        super.onResume()
        accelerometer?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    public fun rollDice(): List<Int> {
        val results = List(5) { (1..6).random() }
        Log.d("DiceRollActivity", "New Dice Results: $results")
        return results
    }

    public fun rollDiceStates(clickedStates: List<Boolean>): List<Int> {
        val rolledDice = MutableList(5) { 0 } // Inizializza come una MutableList

        for (i in 0..4) {
            if (!clickedStates[i]) {
                rolledDice[i] =
                    (1..6).random() // Assegna un nuovo valore solo se il dado non è bloccato
            }
        }
        Log.d("DiceRollActivity", "New Dice Results: $rolledDice")
        return rolledDice
    }

}