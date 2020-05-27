package com.example.daipanprot

import android.content.Context
import android.content.res.AssetManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.absoluteValue
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(), SensorEventListener {

    private var mAssetManager: AssetManager by Delegates.notNull()
    private var mSensorManager: SensorManager by Delegates.notNull()
    private var mSensor: Sensor by Delegates.notNull()
    private var mPlayedFlg = true

    private lateinit var mSoundPool: SoundPool

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAssetManager = this.resources.assets

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST)

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        mSoundPool = SoundPool.Builder()
            .setAudioAttributes(audioAttributes)
            .setMaxStreams(1)
            .build()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val x = event?.values?.get(0)
        val y = event?.values?.get(1)
        val z = event?.values?.get(2)

        if (x?.absoluteValue!! > 3 && y?.absoluteValue!! > 3 && z?.absoluteValue!! > 3) {
            if(mPlayedFlg) {
                mPlayedFlg = false
                val soundNum = (0 until 8).random()
                val fileName = "0$soundNum.mp3"
                val fd = mAssetManager.openFd(fileName)
                val s = mSoundPool.load(fd, 1)
                mSoundPool.setOnLoadCompleteListener { soundPool, sampleId, status ->
                    soundPool.play(s, 1.0f, 1.0f, 0, 0, 1.0f)
                    mPlayedFlg = true
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        mSoundPool.release()
    }
}
