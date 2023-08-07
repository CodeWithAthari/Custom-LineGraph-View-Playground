package com.devatrii.customgraphview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.devatrii.customgraphview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val activity = this
    lateinit var binding: ActivityMainBinding
    private val tempDataForLineGraph = floatArrayOf(4f, 6f, 7f, 9f, 5f, 4f, 9f, 10f, 6f, 8f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            val mDataForLineGraph = ArrayList<Float>()
            tempDataForLineGraph.forEach {
                mDataForLineGraph.add(it)
            }
            mLineGraph.setDataPoints(mDataForLineGraph);
//            mLineGraph.postInvalidate();
        }

    }
}