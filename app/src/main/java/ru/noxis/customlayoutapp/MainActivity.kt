package ru.noxis.customlayoutapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.noxis.customlayoutapp.components.EqualHeightColumn
import ru.noxis.customlayoutapp.components.OldPhone
import ru.noxis.customlayoutapp.ui.theme.CustomLayoutAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CustomLayoutAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    EqualHeightColumn(modifier = Modifier
//                        .padding(innerPadding)
//                        .padding(8.dp)) {
//                        CardText("Test ad dsdfsbv asddsffasf")
//                        CardText(
//                            "Test ad dsdfsbv asddsffasf" +
//                                    "asdasdasdasdasd" +
//                                    "asdasdasdasdasd" +
//                                    "asdasdasdasdasd" +
//                                    "asdasdasdasdasda"
//                        )
//                        CardText("Test ad dsdfsbv asddsffasf, sdasdasasd asasdasd asdasdasd")
//                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        OldPhone(modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CardText(msg: String) {
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(width = 1.dp, color = Color.Black)
    ) {
        Text(modifier = Modifier.padding(4.dp), text = msg)
    }
}

@Preview
@Composable
fun CardTextPreview() {
    CardText("Test")
}