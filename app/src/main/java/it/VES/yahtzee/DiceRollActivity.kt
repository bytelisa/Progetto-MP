package it.VES.yahtzee


import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog



import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign

import kotlin.math.sqrt

class DiceRollActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var shakeStartTime: Long = 0
    private val shakeDuration = 1400 // 1.4 secondi di shake
    private val shakeThreshold = 1
    private var lastX = 0.0f
    private var lastY = 0.0f
    private var lastZ = 0.0f
    var overThreshold = mutableStateOf(false)


    private var diceResults by mutableStateOf(List(5) { 1 })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configura il sensore di accelerazione
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        setContent {
            BackgroundShake()
            DiceScreen()
        }
    }


    @Composable
    fun DiceScreen() {
        // UI per visualizzare i valori dei dadi e il bottone per lanciare manualmente
        var done by rememberSaveable { mutableStateOf(true) }

        AlertDialog(
            onDismissRequest = { done = false },
            title = { Text(text = "Shake!", textAlign = TextAlign.Center) },
            confirmButton = {
            }
        )
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

            // Se il movimento di shake supera la soglia
            if (shakeMagnitude > shakeThreshold) {
                if (shakeStartTime == 0L) {
                    shakeStartTime = currentTime
                }

                if ((currentTime - shakeStartTime) > shakeDuration) {
                    // Lancia i dadi dopo 3 secondi di shake
                    overThreshold.value = true
                    diceResults = rollDice()
                    shakeStartTime = 0L // Reset
                }
            } else {
                // Reset il tempo di inizio se non si supera la soglia
                shakeStartTime = 0L

                if (overThreshold.value){
                    val resultIntent = intent
                    resultIntent.putExtra("diceResults", diceResults.toIntArray())
                    setResult(RESULT_OK, resultIntent)
                    finish() // Chiude l'activity e restituisce i dati
                }
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

    fun rollDice(): List<Int> {
        val results = List(5) { (1..6).random() }
        //Log.d("DiceRollActivity", "New Dice Results: $results")
        return results
    }

    fun rollDiceStates(rolledDice: List<Int>, clickedStates: List<Boolean>): List<Int> {

        val newDice = MutableList(5) { 0 }

        for (i in 0..4) {
            if (!clickedStates[i]) {
                newDice[i] = (1..6).random()
            } else {
                newDice[i] = rolledDice[i]
            }
        }
        //Log.d("DiceRollActivity", "New Dice Results: $rolledDice")
        return newDice
    }

    @Composable
    fun BackgroundShake() {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.img),
                contentDescription = "Single player background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        }
    }
}