package com.android.appcomponents.util

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.lifecycle.*
import java.lang.NullPointerException
import java.lang.RuntimeException

class CompassUtility : SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private var magneticFieldSensor: Sensor? = null

    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private val compassAngleLiveData = MutableLiveData<Float>()
    private val compassAccuracyLiveData = MutableLiveData<Int>()

    private val RADIAN_TO_DEGREE_MULTIPLIER = 180 / 3.14159265f

    private constructor() {}
    private constructor(sensorManager: SensorManager) {
        this.sensorManager = sensorManager
        this.accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        this.magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }


    /*
    *  Start listening to updates from SensorManager
    * */
    fun startListening() {
        sensorManager?.let {
            it.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
            it.registerListener(this, magneticFieldSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    /*
    *  Stop listening to updates from SensorManager
    * */
    fun stopListening() {
        sensorManager?.let {
            sensorManager?.unregisterListener(this, accelerometer)
            sensorManager?.unregisterListener(this, magneticFieldSensor)
        }
    }

    /*
    *  Callback function for receiving new Sensor values
    * */
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor == accelerometer) {
                System.arraycopy(
                    event.values,
                    0,
                    accelerometerReading,
                    0,
                    accelerometerReading.size
                )
            } else if (it.sensor == magneticFieldSensor) {
                System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
            }
            SensorManager.getRotationMatrix(
                rotationMatrix,
                null,
                accelerometerReading,
                magnetometerReading
            )
            SensorManager.getOrientation(rotationMatrix, orientationAngles)
            compassAngleLiveData.postValue(pushNewAndGetMean(-orientationAngles[0] * RADIAN_TO_DEGREE_MULTIPLIER))
        }
    }

    /*
    *  Callback invoked by the SensorManager when accuracy of the sensor changes.
    * */
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        compassAccuracyLiveData.postValue(accuracy)
    }

    /*
    *  This queue is used to store last 8 values from sensor.
    *  These values will be used to remove noise and provide smoother direction values
    * */
    val queue = FloatArray(10)
    /*
    *  Position/Pointer to front of the queue
    * */
    var queueFront = queue.size - 1
    /*
    *  Mean of previous 8 values
    * */
    var meanValue = 0f
    /*
    * Adds new value to the queue and returns the new mean value
    * */
    private fun pushNewAndGetMean(newValue: Float): Float {
        // recalculate mean value
        meanValue += (newValue - queue[queueFront]) / queue.size;
        // overwrite value in front pointer position
        queue[queueFront] = newValue;
        // shift front pointer 1 step right or to '0' if end of array reached
        queueFront = (queueFront + 1) % queue.size
        return meanValue
    }

    /*
    *  Returns LiveData object containing latest compass angle value in float
    * */
    fun getCompassAngleLiveData(): LiveData<Float> = compassAngleLiveData

    /*
    *  Returns LiveData object containing accuracy of the sensors
    * */
    fun getCompassAccuracyLiveData(): LiveData<Int> = compassAccuracyLiveData

    companion object {
        @Volatile
        private var INSTANCE: CompassUtility? = null

        fun getInstance(context: Context): CompassUtility =
            INSTANCE ?: synchronized(this) {
                val sensorManager: SensorManager =
                    context.getSystemService(SENSOR_SERVICE) as SensorManager
                INSTANCE ?: CompassUtility(sensorManager).also { INSTANCE = it }

            }
    }
}