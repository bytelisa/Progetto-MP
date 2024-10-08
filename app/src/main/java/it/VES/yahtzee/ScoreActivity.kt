package it.VES.yahtzee


import androidx.compose.foundation.Image

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import it.VES.yahtzee.db.Scores
import it.VES.yahtzee.db.UserViewModel
import it.VES.yahtzee.ui.theme.YahtzeeTheme
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date

@Composable
fun SingleplayerScoreCard(scores: Scores){
    val dateFormat=SimpleDateFormat("dd/MM/yyyy HH:mm:ss",Locale.getDefault())
    val formattedDate=dateFormat.format(scores.datePlayed)

    Column{
        Text(
            text="Username:${scores.username}",
            fontSize=18.sp,
            color=MaterialTheme.colorScheme.onPrimary,
            textAlign=TextAlign.Left,
            style=TextStyle(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(8.dp))


        // Aggiungi la modalità di gioco
        Text(
            text = "Game Mode: ${scores.gameMode}",
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
            text = "Score: ${scores.score}",
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
fun MultiplayerScoreCard(scores:Scores){
    val dateFormat=SimpleDateFormat("dd/MM/yyyy HH:mm:ss",Locale.getDefault())
    val formattedDate=dateFormat.format(scores.datePlayed)
    Column{
        Text(
            text="Player 1:${scores.username}",
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
            text = "Player 2: ${scores.opponentUsername}",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Left,
            style = TextStyle(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Aggiungi la modalità di gioco
        Text(
            text = "Game Mode: ${scores.gameMode}",
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
            text = "Score: ${scores.score} - ${scores.opponentScore}",
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
            text = "Winner: ${if (scores.score > scores.opponentScore.toString()) scores.username else scores.opponentUsername}",
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
fun ScoreCard(scores: Scores, onDelete: (Scores) -> Unit) {
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
            if (scores.gameMode == "Singleplayer") {
                SingleplayerScoreCard(scores)
            } else {
                MultiplayerScoreCard(scores)
            }
            IconButton(
                onClick = { onDelete(scores) },
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
fun ScoreScreen(navController: NavController) {
    val userViewModel: UserViewModel = viewModel()
    val userList by userViewModel.allUsers.observeAsState(emptyList())

    var selectedScore by remember { mutableStateOf<Scores?>(null) }
    var showDialog by remember { mutableStateOf(false) }



    val multiplayerGames = userList
        .filter { it.mod == "Multiplayer" }
        .groupBy { it.date }


    val uniqueDates = mutableSetOf<String>()


    val scoreList = remember(userList) {
        userList.mapNotNull { user ->
            val datePlayed = try {
                SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).parse(user.date)?.time
            } catch (e: Exception) {
                null
            }

            if (user.mod == "Singleplayer") {

                Scores(
                    username = user.player,
                    gameMode = user.mod,
                    score = user.score,
                    datePlayed = datePlayed?.let { Date(it) } ?: Date(),
                    opponentUsername = "",
                    opponentScore = 0
                )
            } else if (user.mod == "Multiplayer" && datePlayed != null) {

                val dateKey = datePlayed.toString()

                if (!uniqueDates.contains(dateKey)) {
                    uniqueDates.add(dateKey)


                    val opponentUsername: String
                    val opponentScore: Int

                    val gameDetails = multiplayerGames[user.date]
                    if (gameDetails != null && gameDetails.size > 1) {
                        // Find the opponent in the same game
                        val opponent = gameDetails.find { it.player != user.player }
                        opponentUsername = opponent?.player ?: ""
                        opponentScore = opponent?.score?.toIntOrNull() ?: 0
                    } else {
                        opponentUsername = ""
                        opponentScore = 0
                    }

                    Scores(
                        username = user.player,
                        gameMode = user.mod,
                        score = user.score,
                        datePlayed = Date(datePlayed),
                        opponentUsername = opponentUsername,
                        opponentScore = opponentScore
                    )
                } else {
                    null
                }
            } else {
                null
            }
        }
    }



    YahtzeeTheme {

        ScoreScreenContent(
            scores = scoreList,
            onDelete = { score ->
                selectedScore = score
                showDialog = true
            }
        )


    }



    if (showDialog && selectedScore != null) {
        ConfirmationDialog(
            onConfirm = {
                selectedScore?.let { score ->
                    val userToDelete = userList.find { it.player == score.username && it.score == score.score }
                    if (userToDelete != null) {
                        userViewModel.delete(userToDelete)
                    }
                }
                showDialog = false
                selectedScore = null
            },
            onDismiss = {
                showDialog = false
                selectedScore = null
            }
        )
    }

}


@Composable
fun ConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(

        onDismissRequest = onDismiss,
        title = { Text(text = "Confirm Deletion") },
        text = { Text("Are you sure you want to delete this score?") },
        confirmButton = {


            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xBE673AB7)) // Light blue color
            ) {
                Text("Delete")
            }
        },
        dismissButton = {

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xBE673AB7)) // Light blue color
            ) {
                Text("Cancel")
            }
        }
    )
}



@Composable
fun ScoreScreenContent(scores: List<Scores>, onDelete: (Scores) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.score),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        LazyColumn(modifier = Modifier.padding(top = 70.dp)) {
            items(scores) { score ->
                ScoreCard(scores = score, onDelete = onDelete)
            }
        }
    }
}

