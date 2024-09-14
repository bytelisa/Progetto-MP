package it.VES.yahtzee

import it.VES.yahtzee.ui.theme.YahtzeeTheme


import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.tooling.preview.Preview


import it.VES.yahtzee.R

class HowToPlayActivity: ComponentActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YahtzeeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BackgroundPictureHTP()
                    Back()
                }
            }
        }
    }
}

@Composable
fun Back() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Spacer(modifier = Modifier.height(100.dp))
        Button(
            onClick = {/*Bottone Back*/ },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xD03F51B5) // Cambia il colore del pulsante qui
            ),
            modifier = Modifier
                .width(200.dp)
                .height(45.dp)
                .offset(y = -50.dp)
        ) {
            Text(text = "GoBack")
        }
    }
}


@Composable
fun BackgroundPictureHTP(){
    Box(
        modifier=Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.howtoplay),
            contentDescription = "How to play background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
    }
}
@Preview(showBackground = true)
@Composable
fun HTPreview() {
    YahtzeeTheme {
        BackgroundPictureHTP()
        Back()
    }
}