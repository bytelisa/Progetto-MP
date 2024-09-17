package it.VES.yahtzee
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import android.media.MediaPlayer
import it.VES.yahtzee.ui.theme.YahtzeeTheme


class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YahtzeeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BackgroundPictureSettings()
                    //Setting()
                    AppNavigation()
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Setting(navController: NavController) {
    var switch1State by remember { mutableStateOf(false) }
    var switch2State by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("") }

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()



    LaunchedEffect(Unit) {
        userName = sharedPreferences.getString("userName", "") ?: ""
        switch1State = sharedPreferences.getBoolean("soundEnabled", false)
        switch2State = sharedPreferences.getBoolean("clickMode", false)
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        BackgroundPictureSettings()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(290.dp))

            // Casella di testo per il nome dell'utente
            TextField(
                value = userName,
                onValueChange = { userName = it },
                label = { Text("Your username") },
                modifier = Modifier
                    .width(250.dp)
                    .clip(RoundedCornerShape(16.dp)),

                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFF7B88D1),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.White
                )
            )


            // Primo switch con parole
            Spacer(modifier = Modifier.height(50.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Sound")

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth(), // Allinea al centro
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Off")
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(
                            checked = switch1State,
                            onCheckedChange = {
                                switch1State = it
                                if(it){
                                    playDiceSound(context)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "On")
                    }
                }
            }




            Spacer(modifier = Modifier.height(20.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Dice Roll mode")

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {


                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Click")
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(
                            checked = switch2State,
                            onCheckedChange = { switch2State = it }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Shake")
                    }
                }
            }



            Spacer(modifier = Modifier.height(100.dp))
            Button(
                onClick = {

                    editor.putString("userName", userName)
                    editor.putBoolean("soundEnabled", switch1State)
                    editor.putBoolean("clickMode", switch2State)
                    editor.apply()



                    navController.popBackStack()

                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xD03F51B5)
                ),
                modifier = Modifier
                    .size(200.dp, 60.dp)
                    .padding(bottom = 16.dp)
            ) {
                Text(text = "Save")
            }
        }
    }
}



@Composable
fun BackgroundPictureSettings(){
    Box(
        modifier=Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.settings),
            contentDescription = "settings",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
    }
}
@Preview(showBackground = true)
@Composable
fun SettingsActivityPreview() {
    YahtzeeTheme {
        val navController = rememberNavController()
        Setting(navController = navController)
    }
}









