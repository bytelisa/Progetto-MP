package it.VES.yahtzee

import android.content.Context
import android.media.MediaPlayer

fun playDiceSound(context: Context) {
    val mediaPlayer = MediaPlayer.create(context, R.raw.dice_roll)
    mediaPlayer.setOnCompletionListener { it.release() }
    mediaPlayer.start()
}