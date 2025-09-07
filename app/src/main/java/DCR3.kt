
package com.spartasoap.dcr3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DCRCalculatorUI()
        }
    }
}

@Composable
fun DCRCalculatorUI() {
    var bore by remember { mutableStateOf("") }
    var stroke by remember { mutableStateOf("") }
    var rodLength by remember { mutableStateOf("") }
    var staticCR by remember { mutableStateOf("") }
    var ivcAngle by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("Enter values and calculate") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(value = bore, onValueChange = { bore = it }, label = { Text("Bore (in)") })
        OutlinedTextField(value = stroke, onValueChange = { stroke = it }, label = { Text("Stroke (in)") })
        OutlinedTextField(value = rodLength, onValueChange = { rodLength = it }, label = { Text("Rod Length (in)") })
        OutlinedTextField(value = staticCR, onValueChange = { staticCR = it }, label = { Text("Static CR") })
        OutlinedTextField(value = ivcAngle, onValueChange = { ivcAngle = it }, label = { Text("IVC Angle (° ABDC)") })

        Button(onClick = {
            try {
                val boreVal = bore.toDouble()           // inches
                val strokeVal = stroke.toDouble()       // inches
                val scrVal = staticCR.toDouble()
                val ivcRad = Math.toRadians(ivcAngle.toDouble())

                // Cylinder swept volume (in³)
                val sweptVol = Math.PI * (boreVal / 2).pow(2.0) * strokeVal

                // Clearance volume (Vc)
                val vc = sweptVol / (scrVal - 1.0)

                // Effective stroke using simple cosine method
                val effStroke = strokeVal * cos(ivcRad)

                // Effective swept volume
                val effSweptVol = Math.PI * (boreVal / 2).pow(2.0) * effStroke

                // Dynamic CR
                val dcr = (effSweptVol + vc) / vc

                result = "Dynamic CR: %.2f".format(dcr)

            } catch (e: Exception) {
                result = "Invalid input!"
            }
        }) {
            Text("Calculate DCR")
        }

        Text(text = result, style = MaterialTheme.typography.bodyLarge)
    }
}