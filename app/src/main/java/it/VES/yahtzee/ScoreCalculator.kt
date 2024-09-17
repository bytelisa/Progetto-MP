package it.VES.yahtzee

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList

class ScoreCalculator {

    fun totalScore(scoreList: List<Int>): Int{
        var score: Int = 0
        for (i in 0..13){
            score += scoreList[i]
        }
        return score
    }


    fun point(category: Int, myDice: ArrayList<Int>): Int {

        if (myDice[0] == 0){
            return 0
        }

        if (category in 1..6) {
            return number(category,myDice)
        } else {
            when(category){
                8 -> return threeX(myDice)
                9 -> return fourX(myDice)
                10 -> return full(myDice)
                11 -> return lowScale(myDice)
                12 -> return bigScale(myDice)
                13 -> return yahtzee(myDice)
                14 -> return chance(myDice)
            }
            return 0
        }
    }

    //Return the score of the box in the first column
    private fun number(dice: Int, myDice: ArrayList<Int>): Int {
        var counter = 0
        for (i in myDice) {
            if (dice == i) {
                counter += dice
            }
        }
        return counter
    }

    //Return 50 if a Yahtzee was obtained
    private fun yahtzee(myDice: ArrayList<Int>) : Int {
        for(i in 0..3){
            if(myDice[i] != myDice[i+1]){
                return 0
            }
        }
        return 50
    }

    //Return the score obtained whit chance
    private fun chance(myDice: ArrayList<Int>) : Int {
        var count = 0
        for(i in myDice){
            count += i
        }
        return count
    }

    //Return 40 if a bigScale was obtained
    private fun bigScale(myDice: ArrayList<Int>): Int {
        val foundDice = arrayListOf(0,0,0,0,0,0)
        for(i in myDice){
            foundDice[i-1] += 1
        }

        if(foundDice[0] == 0){ //2-3-4-5-6
            for(i in 1..5){
                if(foundDice[i] != 1){
                    return 0
                }
            }
            return 40
        } else if(foundDice[5] == 0){ //1-2-3-4-5
            for(i in 0..4){
                if(foundDice[i] != 1){
                    return 0
                }
            }
            return 40
        }
        return 0
    }

    //Return 30 if a lowScale was obtained
    private fun lowScale(myDice: ArrayList<Int>): Int  {
        val foundDice = arrayListOf(0,0,0,0,0,0)
        var count1 = 0
        var count2 = 0
        var count3 = 0

        if(bigScale(myDice) == 40){
            return 30
        }
        for(i in myDice){
            foundDice[i-1] += 1
        }

        for(i in 0..3){ //1-2-3-4
            if(foundDice[i] >= 1){
                count1 += 1
            }
        }
        for(i in 1..4){ //2-3-4-5
            if(foundDice[i] >= 1){
                count2 += 1
            }
        }
        for(i in 2..5){ //3-4-5-6
            if(foundDice[i] >= 1){
                count3 += 1
            }
        }

        if(count1 == 4 || count2 == 4 || count3 == 4){
            return 30
        }

        return 0
    }

    //Return 25 if a full was obtained
    private fun full(myDice: ArrayList<Int>): Int  {
        val foundDice = arrayListOf(0,0,0,0,0,0)
        val full = mutableListOf(0,0)

        if (myDice[0] == 0){
            //primo lancio a vuoto per la funzione diceRollStates
            return 0
        }

        for(i in myDice){
            foundDice[i-1] += 1
        }

        for(i in foundDice){
            if(i == 3){
                full[0] = 1
            } else if(i == 2){
                full[1] = 1
            }
        }

        if(full[0] == 1 && full[1] == 1) {
            return 25
        }
        return 0
    }

    //Return the score obtained whit 4x
    private fun fourX(myDice: ArrayList<Int>): Int  {
        val foundDice = arrayListOf(0,0,0,0,0,0)
        var point = 0

        for(i in myDice){
            foundDice[i-1] += 1
            point += i
        }
        for(i in foundDice){
            if(i >= 4){
                return point
            }
        }
        return 0
    }

    //Return the score obtained whit 3x
    private fun threeX(myDice: ArrayList<Int>): Int {
        val foundDice = arrayListOf(0,0,0,0,0,0)
        var point = 0

        for(i in myDice){
            foundDice[i-1] += 1
            point += i
        }

        for(i in foundDice){
            if(i >= 3){
                return point
            }
        }
        return 0
    }

    fun bonusCheck(myDice: SnapshotStateList<Int>): Int{

        var sum = 0
        for (i in 0..5){    //somma dei primi 6 dadi
            sum+=myDice[i]

        }
        Log.d("ScoreCalculator", "Current sum: $sum")

        return when{
            sum>62 -> 35    //se la somma è >=63 restituiamo il punteggio
            else -> 0
        }

    }
}