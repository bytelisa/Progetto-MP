package it.VES.yahtzee

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.VES.yahtzee.ui.theme.YahtzeeTheme


class PlayModeActivity: ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YahtzeeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BackgroundPicturepm()
                    Choose()
                }
            }
        }
    }
}

@Composable
fun Choose() {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    val intent = Intent(context, SingleplayerActivity::class.java)
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Blue
                ),
                modifier = Modifier
                    .padding(end = 16.dp) // Aumenta la distanza tra i bottoni
                    .width(150.dp)
                    .height(45.dp),
            ) {
                Text(text = "Singleplayer",
                    fontSize = 17.sp
                )
            }
            Button(
                onClick = {
                    val intent = Intent(context, NewMultiPlayerActivity::class.java)
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Red
                ),
                modifier = Modifier
                    .width(150.dp)
                    .height(45.dp),
            ) {
                Text(text = "Multiplayer",
                fontSize = 18.sp
                )
            }
        }
    }
}


@Composable
fun BackgroundPicturepm(){
    Box(
        modifier=Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.playmode),
            contentDescription = "play mode",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
    }
}


