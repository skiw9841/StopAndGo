package com.sunntree.stopandgo

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.sunntree.stopandgo.databinding.ActivityMainBinding
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {

    private var lastTime: Long = 0
    private var speed = 0f
    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f
    private var x = 0f
    private var y = 0f
    private var z = 0f

    private val DATA_X = 0
    private val DATA_Y = 1
    private val DATA_Z = 2

    private var sensorManager: SensorManager? = null
    private var accelerormeterSensor: Sensor? = null

    private var valueLiveData = MutableLiveData<Float>()
    private var value2LiveData = MutableLiveData<Int>()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerormeterSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

        binding.tvText2.text = "!!!!"
        var cnt = 0
        var freq = 0
        valueLiveData.observe(this, Observer {
            binding.tvText1.text = it.toString()
            if (cnt >= 20) {
                cnt = 0

                value2LiveData.value = freq
                freq = 0
            }
            cnt++
            if (it > 1.0) {
                freq += 1
            }

        })

        value2LiveData.observe(this, Observer {
            if (it < 18) {
                binding.tvText2.text = "Visible"
            } else {
                binding.tvText2.text = "Invisible"
            }
        })
//        kotlin.concurrent.timer(period = 1000) {
//            speed = sqrt( (lastX-x).pow(2) + (lastY-y).pow(2) + (lastZ-z).pow(2))
//            valueLiveData.postValue(speed)
//            lastX = x
//            lastY = y
//            lastZ = z
//        }
    }

    override fun onStart() {
        super.onStart()
        if (accelerormeterSensor != null) sensorManager!!.registerListener(
            this, accelerormeterSensor,
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    override fun onStop() {
        super.onStop()
        if (sensorManager != null) sensorManager!!.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            val currentTime = System.currentTimeMillis()
            val gabOfTime = currentTime - lastTime
            if (gabOfTime > 100) {
                lastTime = currentTime
                x = event.values[DATA_X]
                y = event.values[DATA_Y]
                z = event.values[DATA_Z]


                speed = sqrt((lastX - x).pow(2) + (lastY - y).pow(2) + (lastZ - z).pow(2))
                //speed = Math.abs(x + y + z - lastX - lastY - lastZ) / gabOfTime * 10000
                if (speed > 0) {
                    Log.i("TEST", "speed:" + speed)
                    valueLiveData.value = speed
                }
                lastX = event.values[DATA_X]
                lastY = event.values[DATA_Y]
                lastZ = event.values[DATA_Z]

            }
        }
    }


}
