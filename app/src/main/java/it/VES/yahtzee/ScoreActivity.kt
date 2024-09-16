package it.VES.yahtzee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.VES.yahtzee.ui.theme.YahtzeeTheme
import java.text.SimpleDateFormat
import java.util.*

data class Score(val username: String, val gameMode: String, val score: Int, val datePlayed: Date,val opponentUsername: String="",val opponentScore:Int=0)

class ScoreActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YahtzeeTheme {
                val sampleScores = listOf(
                    Score("Player1", "Singleplayer", 1500, Date()),
                    Score("Player2", "Multiplayer", 2000, Date()),
                    Score("Player3", "Singleplayer", 1800, Date())
                )
                ScoreScreen(scores = sampleScores)
            }
        }
    }
}

@Composable
fun SingleplayerScoreCard(score:Score){
    val dateFormat=SimpleDateFormat("dd/MM/yyyy",Locale.getDefault())
    val formattedDate=dateFormat.format(score.datePlayed)
    Column{
        Text(
            text="Username:${score.username}",
            fontSize=18.sp,
            color=MaterialTheme.colorScheme.onPrimary,
            textAlign=TextAlign.Left,
            style=TextStyle(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Score: ${score.score}",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Left,
            style = TextStyle(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Date Played: $formattedDate",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Left,
            style = TextStyle(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )
        )
    }
}
@Composable
fun MultiplayerScoreCard(score:Score){
    val dateFormat=SimpleDateFormat("dd/MM/yyyy",Locale.getDefault())
    val formattedDate=dateFormat.format(score.datePlayed)
    Column{
        Text(
            text="Player 1:${score.username}",
            fontSize=18.sp,
            color=MaterialTheme.colorScheme.onPrimary,
            textAlign=TextAlign.Left,
            style = TextStyle(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Player 2: ${score.opponentUsername}",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Left,
            style = TextStyle(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Score: ${score.score} - ${score.opponentScore}",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Left,
            style = TextStyle(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Winner: ${if (score.score > score.opponentScore) score.username else score.opponentUsername}",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Left,
            style = TextStyle(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Date Played: $formattedDate",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Left,
            style = TextStyle(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )
        )
    }
}


@Composable
fun ScoreCard(score: Score, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            if (score.gameMode == "Singleplayer") {
                SingleplayerScoreCard(score)
            } else {
                MultiplayerScoreCard(score)
            }
            IconButton(
                onClick=onDelete,
                modifier=Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
fun ScoreScreen(scores: List<Score>) {
    var scoreList by remember{mutableStateOf(scores)}
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.score),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        LazyColumn(
            modifier = Modifier.padding(top =70.dp) //
        ) {
            items(scores) { score ->
                ScoreCard(score = score, onDelete = {
                    scoreList = scoreList.toMutableList().apply { remove(score) }
                })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScoreScreenPreview() {
    YahtzeeTheme {
        val sampleScores = listOf(
            Score("Player1", "Singleplayer", 1500, Date()),
            Score("Player2", "Multiplayer", 2000, Date()),
            Score("Player3", "Singleplayer", 1800, Date())
        )
        ScoreScreen(scores = sampleScores)
    }
}