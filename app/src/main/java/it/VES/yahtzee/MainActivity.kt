package it.VES.yahtzee

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.AnnotatedString
import it.VES.yahtzee.ui.theme.YahtzeeTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YahtzeeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BackgroundPicture()
                    Home()
                }
            }
        }
    }
}


@Composable
fun Home() {
    val context = LocalContext.current
    var showDialog  by remember {mutableStateOf(false)}


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ){

        Spacer(modifier = Modifier.height(100.dp))

        Text(
            text = "Yahtzee!",
            fontSize = 50.sp, // Big
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color(0xFF5221AA),
            modifier = Modifier.padding(top = 24.dp)
        )

        Spacer(modifier = Modifier.height(150.dp))

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {

                // Avvia la nuova Activity per il lancio dei dadi
                val intent = Intent(context, PlayModeActivity::class.java)
                context.startActivity(intent)

            },
            modifier = Modifier
                .width(200.dp) // Set width for all buttons
                .height(45.dp), // Set height for all buttons)
        ) {
            Text(text = "Play")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Second Button
        Button(

            onClick = {
                // Avvia la nuova Activity per le regole di gioco
                val intent = Intent(context, HowToPlayActivity::class.java)
                context.startActivity(intent)
            },

            modifier = Modifier
                .width(200.dp)
                .height(45.dp),
            ) {
            Text(text = "How to Play")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Third Button
        Button(
            onClick = {
                val intent = Intent(context, SettingsActivity::class.java)
                context.startActivity(intent) },
            modifier = Modifier
                .width(200.dp)
                .height(45.dp),
        ) {
            Text(text = "Settings")
        }
        Spacer(modifier = Modifier.height(24.dp))
        //fourth button
        Button(
            onClick={showDialog=true},
            modifier=Modifier
                .width(200.dp)
                .height(45.dp),
        ){
            Text(text="About Us")
        }
        if(showDialog){
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "About Us") },
                text = {
                    Column {
                        Text("Name 1: Elisa Marzioli")
                        Text("Name 2: Sofia Tosti")
                        Text("Name 3: Valentina Jin")
                        Spacer(modifier = Modifier.height(16.dp))
                        ClickableText(
                            text = AnnotatedString("Learn more on Wikipedia"),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://en.wikipedia.org/wiki/Yahtzee"))
                                context.startActivity(intent)
                            },
                            style = TextStyle(color = Color.Blue, textDecoration = TextDecoration.Underline)
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("OK")
                    }
                },
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomePreview() {
    YahtzeeTheme {
        BackgroundPicture()
        Home()
    }
}





@Composable
fun BackgroundPicture(){
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Image(
            painter = painterResource(id = R.drawable.home),
            contentDescription = "Home background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
            )
    }
}