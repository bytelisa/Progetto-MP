package it.VES.yahtzee

// SecondActivity.kt
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface

class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(color = MaterialTheme.colorScheme.background) {

                Text("Welcome to Settings!")
                Settings()
                BackgroundPicture()
            }
        }
    }
}

@Preview
@Composable
fun SettingsPreview(){
    BackgroundPicture()
    Settings()
}

@Composable
fun Settings(){

}