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
                val boreVal = bore.toDouble()
                val strokeVal = stroke.toDouble()
                val rodVal = rodLength.toDouble()
                val scrVal = staticCR.toDouble()
                val ivcDeg = ivcAngle.toDouble()
                val ivcRad = Math.toRadians(ivcDeg) // Convert to radians

                val crank = strokeVal / 2.0
                val sweptVol = Math.PI * (boreVal / 2).pow(2.0) * strokeVal
                val vc = sweptVol / (scrVal - 1.0)

                // Accurate piston position at IVC angle
                val x = crank * (1 - cos(ivcRad)) +
                        rodVal - sqrt(rodVal.pow(2) - (crank * sin(ivcRad)).pow(2))

                val effStroke = strokeVal - x
                val effSweptVol = Math.PI * (boreVal / 2).pow(2.0) * effStroke
                val dcr = (effSweptVol + vc) / vc

                // Optional: Debugging Output (logs to console/logcat)
                println("========== DEBUG INFO ==========")
                println("Crank Radius: $crank in")
                println("Rod Length: $rodVal in")
                println("Rod/Stroke Ratio: ${rodVal / crank}")
                println("IVC Angle (deg): $ivcDeg")
                println("IVC Angle (rad): $ivcRad")
                println("Piston Pos at IVC (x): $x in")
                println("Effective Stroke: $effStroke in")
                println("Eff Swept Vol: $effSweptVol in³")
                println("Clearance Vol (Vc): $vc in³")
                println("DCR: $dcr")
                println("================================")

                result = "Dynamic CR: %.2f".format(dcr)

            } catch (e: Exception) {
                result = "Invalid input!"
                e.printStackTrace()
            }
        }) {
            Text("Calculate DCR")
        }

        Text(text = result, style = MaterialTheme.typography.bodyLarge)
    }
}
