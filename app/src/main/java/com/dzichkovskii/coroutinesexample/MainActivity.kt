package com.dzichkovskii.coroutinesexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PROGRESS_MAX = 100
        private const val PROGRESS_START = 0
        private const val JOB_TIME = 4000 //ms
    }

    private lateinit var job: CompletableJob
    private var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        job_button.setOnClickListener {
            if(!::job.isInitialized){
                initJob()
            }
            job_progress_bar.startJobOrCancel(job)
        }
    }

    private fun initJob(){
        job_button.text = getString(R.string.start, counter + 1)
        counter++
        updateJobCompleteTextView("" )
        job = Job()
        job.invokeOnCompletion { it ->
            it?.message.let{
                var msg = it
                if(msg.isNullOrBlank()) {
                    msg = "Unknown cancellation error"
                }
                println("$job was cancelled. Reason: $msg")
                showToast(msg)
            }
        }
        job_progress_bar.max = PROGRESS_MAX
        job_progress_bar.progress = PROGRESS_START
    }

    private fun ProgressBar.startJobOrCancel(job: Job) {
        if(this.progress > 0){
            println("$job is already active. Cancelling...")
            resetJob()
        }
        else {
            job_button.text = getString(R.string.cancel, counter)
            CoroutineScope(IO + job).launch {
                println("coroutine $this is activated with job $job")

                for (i in PROGRESS_START .. PROGRESS_MAX){
                    delay((JOB_TIME/PROGRESS_MAX).toLong())
                    this@startJobOrCancel.progress = i
                }
                updateJobCompleteTextView("Job#$counter is complete")
            }
        }
    }

    private fun updateJobCompleteTextView(text: String){
        GlobalScope.launch(Main) {
            job_complete_text.text = text
        }
    }

    private fun resetJob() {
        if(job.isCompleted){
            job.cancel(CancellationException("Resetting Job"))
            initJob()
        } else if (job.isActive) {
            job.cancel(CancellationException("Resetting Job"))
            initJob()
            updateJobCompleteTextView("Job#${counter-1} was cancelled")
        }
    }

    private fun showToast(text: String) {
        GlobalScope.launch(Main) {
            Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()
        }
    }
}
