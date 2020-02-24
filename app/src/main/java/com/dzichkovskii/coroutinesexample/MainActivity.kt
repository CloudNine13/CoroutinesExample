package com.dzichkovskii.coroutinesexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {

    private val RESULT_1 = "Result #1"
    private var counter = 0
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

    private suspend fun fakeApiRequest(){
        val result1 = getResult1fromApi()
        println("debug: $result1")
    }

    private suspend fun getResult1fromApi():String {
        logThread("getResult1fromApi()")
        delay(1000)
        // DOES NOT WORK, I need to google TODO(android.view.ViewRootImpl$CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.)
        //tvCoroutines.text = "Job #" + counter
        counter++
        return RESULT_1
    }

    private fun logThread(methodName: String) {
        println("debug: ${methodName}: ${Thread.currentThread().name}")
    }
}
