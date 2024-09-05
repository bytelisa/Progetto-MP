package it.VES.yahtzee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.VES.yahtzee.bluebutton.BlueButton
import it.VES.yahtzee.ui.theme.YahtzeeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YahtzeeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Sofia",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column(){
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
        BlueButton(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            onClick = {
                //SimpleTextDisplay("ciaooo")
            }
        )

    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    YahtzeeTheme {
        Greeting("Android")
    }
}

@Composable
fun SimpleTextDisplay(message: String) {
    Text(text = message)
}
