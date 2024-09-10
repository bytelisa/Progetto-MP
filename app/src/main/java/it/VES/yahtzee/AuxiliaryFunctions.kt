package it.VES.yahtzee

import android.widget.TextView
import it.VES.yahtzee.ScoreCalculator

/*classe che contiene funzioni ausiliarie utili:
        -calcolo punteggio
        -randomizzazione dei dadi
        -blocco e sblocco delle caselle del punteggio

 */
 class AuxiliaryFunctions {

     //disable textview
    fun lockTextView(tvList : List<TextView>){
        for(tv in tvList){
            tv.isEnabled = false
        }
    }

    //enable textview
    fun unlockTextView(tvList : List<TextView>, playedDices : List<Boolean>){
        for(i in 0..12){
            if(!playedDices[i]){
                tvList[i].isEnabled = true
            }
        }
    }


    //display point preview after three dice rolls
    fun displayPointPreview(tvList: List<TextView>, pointPreviewList: List<Int>){

        //pointPreviewList contains a list of points as computed in previewScoreCalculator

        for (i in 0 ..12){
            if (!tvList[i].isEnabled){
                tvList[i].setText(pointPreviewList[i])
            }
        }
    }

    /*
    //compute preview of all possible scores with given set of dice
    fun previewScoreCalculator(dice: List<Int>): List<Int> {

        var pointPreviewList: List<Int>

        for (i in 0..12){
            pointPreviewList + ScoreCalculator().point(i, dice) //compute score based on slot and dice
        }
        return pointPreviewList
    }
    */

 }
