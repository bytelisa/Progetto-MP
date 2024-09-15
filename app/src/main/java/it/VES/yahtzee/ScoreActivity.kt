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

data class Score(val username: String, val gameMode: String, val score: Int, val datePlayed: Date)

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
fun ScoreScreen(scores: List<Score>) {
    var scoreList by remember { mutableStateOf(scores) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.score),
            contentDescription = "Score background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            items(scoreList) { score ->
                ScoreCard(score) {
                    scoreList = scoreList.toMutableList().apply { remove(score) }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ScoreCard(score: Score, onDelete: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val formattedDate = dateFormat.format(score.datePlayed)

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
            Column(
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text(
                    text = "Username: ${score.username}",
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
                    text = "Game Mode: ${score.gameMode}",
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
            IconButton(
                onClick = onDelete,
                modifier = Modifier.align(Alignment.TopEnd)
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