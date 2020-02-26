package com.dzichkovskii.coroutinesexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class MainActivity : AppCompatActivity() {

    private val RESULT_1 = "Result #1"
    private var counter = 1
    private lateinit var tvCoroutines: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvCoroutines = findViewById(R.id.tv_coroutines_pause_example)

        button.setOnClickListener {

            // IO (Network, local db interactions),
            // Main (Main thread, interacting with UI),
            // Default (Heavy competition work)
            CoroutineScope(IO).launch {
                fakeApiRequest()
            }
        }
    }

    private fun setNewText(input: String){
        val newText = tv_coroutines_pause_example.text.toString() + "\n$input"
        tv_coroutines_pause_example.text = newText
    }

    private fun setNewText1(input: Int){
        tv_coroutines_pause_example2.text = "Job #" + input.toString()
    }

    private suspend fun setTextOnMainThread(input: String){
        withContext(Main){
            setNewText(input)
        }
    }

    private suspend fun setTextOnMainThread1(input: Int){
        withContext(Main){
            setNewText1(input)
        }
    }

    private suspend fun fakeApiRequest(){
        val result1 = getResult1fromApi()
        println("debug: $result1")
        setTextOnMainThread(result1)
    }

    private suspend fun getResult1fromApi():String {
        logThread("getResult1fromApi()")
        delay(1000)
        setTextOnMainThread1(counter)
        counter++

        return RESULT_1
    }

    private fun logThread(methodName: String) {
        println("debug: ${methodName}: ${Thread.currentThread().name}")
    }
}
