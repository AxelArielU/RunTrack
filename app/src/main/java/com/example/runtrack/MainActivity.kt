package com.example.runtrack


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.runtrack.ui.theme.RunTrackTheme
import com.example.runtrack.ui.theme.SensorManagerHelper
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var sensorHelper: SensorManagerHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorHelper = SensorManagerHelper(this)

        setContent {
            RunTrackTheme {
                MonitorSensores(sensorHelper)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::sensorHelper.isInitialized) {
            sensorHelper.iniciarSensores()
        }
    }

    override fun onPause() {
        if (::sensorHelper.isInitialized) {
            sensorHelper.detenerSensores()
        }
        super.onPause()
    }
}

@Composable
fun MonitorSensores(sensorHelper: SensorManagerHelper) {

    var acelerometro by remember {
        mutableStateOf(listOf(0f, 0f, 0f))
    }

    var giroscopio by remember {
        mutableStateOf(listOf(0f, 0f, 0f))
    }

    DisposableEffect(sensorHelper) {

        sensorHelper.alRecibirLectura = { nombre, x, y, z ->

            if (nombre == "Acelerómetro") {
                acelerometro = listOf(x, y, z)
            }

            if (nombre == "Giroscopio") {
                giroscopio = listOf(x, y, z)
            }
        }

        onDispose {
            sensorHelper.alRecibirLectura = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "RunTrack",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Monitor de movimiento deportivo"
        )

        Text(
            text = "Acelerómetro: ${
                if (sensorHelper.tieneAcelerometro) "Disponible" else "No disponible"
            }"
        )

        Text(
            text = "Giroscopio: ${
                if (sensorHelper.tieneGiroscopio) "Disponible" else "No disponible"
            }"
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Acelerómetro",
                    style = MaterialTheme.typography.titleLarge
                )

                Text("X: ${formatear(acelerometro[0])} m/s²")
                Text("Y: ${formatear(acelerometro[1])} m/s²")
                Text("Z: ${formatear(acelerometro[2])} m/s²")
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Giroscopio",
                    style = MaterialTheme.typography.titleLarge
                )

                Text("X: ${formatear(giroscopio[0])} rad/s")
                Text("Y: ${formatear(giroscopio[1])} rad/s")
                Text("Z: ${formatear(giroscopio[2])} rad/s")
            }
        }

        Text(
            text = "Mueve tu teléfono para observar los cambios."
        )
    }
}

fun formatear(valor: Float): String {
    return String.format(
        Locale.getDefault(),
        "%.2f",
        valor
    )
}



