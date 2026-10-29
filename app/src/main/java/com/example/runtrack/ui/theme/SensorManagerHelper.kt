package com.example.runtrack.ui.theme

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class SensorManagerHelper(context: Context) : SensorEventListener {
    private val umbral = 0.05f
    private val intervaloMinimo = 100_000_000L
    private var ultimaActualizacionAcelerometro = 0L
    private var ultimaActualizacionGiroscopio = 0L
    private var ultimaLecturaAcelerometro =
        floatArrayOf(0f, 0f, 0f)
    private var ultimaLecturaGiroscopio =
        floatArrayOf(0f, 0f, 0f)
    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val acelerometro: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    val tieneAcelerometro: Boolean
        get() = acelerometro != null
    val giroscopio: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    val tieneGiroscopio: Boolean
        get() = giroscopio != null
    var alRecibirLectura: ((String, Float, Float, Float) -> Unit)? = null
    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val tiempoActual = event.timestamp

        when (event.sensor.type) {

            Sensor.TYPE_ACCELEROMETER -> {

                val cambio =
                    kotlin.math.abs(x - ultimaLecturaAcelerometro[0]) +
                            kotlin.math.abs(y - ultimaLecturaAcelerometro[1]) +
                            kotlin.math.abs(z - ultimaLecturaAcelerometro[2])

                val tiempoTranscurrido =
                    tiempoActual - ultimaActualizacionAcelerometro

                if (cambio > umbral &&
                    tiempoTranscurrido >= intervaloMinimo
                ) {
                    ultimaLecturaAcelerometro =
                        floatArrayOf(x, y, z)

                    ultimaActualizacionAcelerometro = tiempoActual

                    alRecibirLectura?.invoke(
                        "Acelerómetro", x, y, z
                    )
                }
            }

            Sensor.TYPE_GYROSCOPE -> {

                val cambio =
                    kotlin.math.abs(x - ultimaLecturaGiroscopio[0]) +
                            kotlin.math.abs(y - ultimaLecturaGiroscopio[1]) +
                            kotlin.math.abs(z - ultimaLecturaGiroscopio[2])

                val tiempoTranscurrido =
                    tiempoActual - ultimaActualizacionGiroscopio

                if (cambio > umbral &&
                    tiempoTranscurrido >= intervaloMinimo
                ) {
                    ultimaLecturaGiroscopio =
                        floatArrayOf(x, y, z)

                    ultimaActualizacionGiroscopio = tiempoActual

                    alRecibirLectura?.invoke(
                        "Giroscopio", x, y, z
                    )
                }
            }
        }
    }



    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Aquí podremos gestionar cambios en la precisión.
    }

    fun iniciarSensores() {
        acelerometro?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_UI
            )
        }

        giroscopio?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    fun detenerSensores() {
        sensorManager.unregisterListener(this)
    }
}

