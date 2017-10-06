package com.infullmobile.voicetempo

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.AlarmClock
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.github.glomadrian.grav.GravView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.regex.Pattern


open class FormActivity : AppCompatActivity(), RecognitionListener {

    private lateinit var datePickerButton: Button
    private lateinit var logButton: Button
    private lateinit var emailEdit: EditText
    private lateinit var passwordEdit: EditText
    private lateinit var taskCodeEdit: EditText
    private lateinit var authorEdit: EditText
    private lateinit var commentEdit: EditText
    private lateinit var hoursSpentEdit: EditText
    private lateinit var minutesSpentEdit: EditText
    private lateinit var secondsSpentEdit: EditText
    private lateinit var grav: GravView

    private lateinit var datePickerDialog: DatePickerDialog
    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0
    private var chosenYear: Int = 0
    private var chosenMonth: Int = 0
    private var chosenDay: Int = 0
    var dialog: Dialog? = null

    val re = Pattern.compile("(\\w+)")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
        datePickerButton = findViewById(R.id.datePickerButton) as Button
        logButton = findViewById(R.id.logButton) as Button
        emailEdit = findViewById(R.id.emailEdit) as EditText
        passwordEdit = findViewById(R.id.passwordEdit) as EditText
        taskCodeEdit = findViewById(R.id.taskCodeEdit) as EditText
        authorEdit = findViewById(R.id.authorEdit) as EditText
        commentEdit = findViewById(R.id.commentEdit) as EditText
        hoursSpentEdit = findViewById(R.id.hoursSpentEdit) as EditText
        minutesSpentEdit = findViewById(R.id.minutesSpentEdit) as EditText
        secondsSpentEdit = findViewById(R.id.secondsSpentEdit) as EditText
//        grav = findViewById(R.id.grav) as GravView
        findViewById(R.id.background).setOnClickListener {startRecognition()}

        val timeInt = intent.getIntExtra(AlarmClock.EXTRA_LENGTH, 0)
        timeInt?.let { setTimeSpent(it) }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            startRecognition()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.RECORD_AUDIO), 123)
        }

        val c = Calendar.getInstance()
        mYear = c.get(Calendar.YEAR)
        mMonth = c.get(Calendar.MONTH)
        mDay = c.get(Calendar.DAY_OF_MONTH)
        chosenYear = mYear
        chosenMonth = mMonth + 1
        chosenDay = mDay
        datePickerButton.text = chosenDay.toString() + "-" + (chosenMonth) + "-" + chosenYear

        datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            chosenYear = year
            chosenMonth = monthOfYear+1
            chosenDay = dayOfMonth
            datePickerButton.text = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
        }, mYear, mMonth, mDay)
        datePickerButton.setOnClickListener { datePickerDialog.show() }
        logButton.setOnClickListener { sendData() }
    }

    private fun setTimeSpent(timeSpent: Int) {
        hoursSpentEdit.setText((timeSpent / 3600).toString())
        minutesSpentEdit.setText(((timeSpent % 3600) / 60).toString())
        secondsSpentEdit.setText((timeSpent % 60).toString())
    }

    private fun getTimeSpent(): Long {
        return hoursSpentEdit.text.toString().toLong() * 3600 +
                minutesSpentEdit.text.toString().toLong() * 60 +
                secondsSpentEdit.text.toString().toLong()
    }

    private fun startRecognition() {
        taskCodeEdit.setText("")
        commentEdit.setText("")
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            Log.e("AAAA", " OK, recognition available")
            val recog = SpeechRecognizer.createSpeechRecognizer(this)
            recog.setRecognitionListener(this)
            val intent = RecognizerIntent.getVoiceDetailsIntent(applicationContext)
            recog.startListening(intent)
        } else {
            Log.e("AAAA", " no recognition available")
        }
        dialog = Dialog(this)
        dialog?.setContentView(R.layout.dialog)
        dialog?.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        if (123 == requestCode) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecognition()
            } else {
            }
        }
    }


    private fun sendData() {
        val request = createRequest()
        Utils.getTempoService().postWorklog(request, TokenGenerator.generateToken(emailEdit.text.toString(), passwordEdit.text.toString()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { showSuccessDialog() },
                        { showFailDialog() }
                )
    }

    private fun createRequest() = SendLogRequest(
            KeyRequest(taskCodeEdit.text.toString()),
            NameRequest(authorEdit.text.toString()),
            commentEdit.text.toString(),
            DateFormatter.convertToSendableDate(chosenYear, chosenMonth, chosenDay),
            getTimeSpent()
    )

    private fun showSuccessDialog() {
        val builder = AlertDialog.Builder(this).apply {
            setTitle("Success")
            setMessage("Timesheets added a worklog")
            setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun showFailDialog() {
        val builder = AlertDialog.Builder(this).apply {
            setTitle("Failed")
            setMessage("Something went wrong")
            setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
        }
        builder.show()
    }

    override fun onReadyForSpeech(p0: Bundle?) {
        Log.e("AAAAA", "ready")
    }

    override fun onRmsChanged(p0: Float) {
        Log.e("AAAAA", "rms")
    }

    override fun onBufferReceived(p0: ByteArray?) {
        Log.e("AAAAA", "buff")
    }

    override fun onPartialResults(p0: Bundle?) {
        Log.e("AAAAA", "partial")
    }

    override fun onEvent(p0: Int, p1: Bundle?) {
        Log.e("AAAAA", "event")
    }

    override fun onBeginningOfSpeech() {
        Log.e("AAAAA", "begin")
    }

    override fun onEndOfSpeech() {
        Log.e("AAAAA", "end")
    }

    override fun onError(p0: Int) {
        Log.e("AAAAA", p0.toString())
    }

    override fun onResults(p0: Bundle?) {
        dialog?.dismiss()
        var result = p0?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
        val reMatch = re.matcher(result)
        reMatch.find()
        taskCodeEdit.setText(reMatch.group(1))
        taskCodeEdit.append("-")

        reMatch.find()
        taskCodeEdit.append(reMatch.group(1))
        val newresult = reMatch.replaceFirst("")
        val newReMach = re.matcher(newresult)
        newReMach.find()
        val newnewResult = newReMach.replaceFirst("")
        commentEdit.setText(newnewResult.trim())
    }

    override fun onDestroy() {
        super.onDestroy()
        grav.start()
    }
}