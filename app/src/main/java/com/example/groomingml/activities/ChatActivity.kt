package com.example.groomingml.activities

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.groomingml.utils.GroomingStage
import com.example.groomingml.R
import com.example.groomingml.utils.TextClassificationHelper
import com.example.groomingml.model.Message
import com.example.groomingml.utils.MessageAdapter


class ChatActivity : AppCompatActivity() {

    private val TAG = "ChatActivity"
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: MutableList<Message>
    private lateinit var recyclerViewMessages: RecyclerView
    private lateinit var editTextMessage: EditText
    private lateinit var buttonSend: Button
    private val name = "Grooming Chanel"
    private val canalId = "123abc"
    private val notificationId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_form)

        recyclerViewMessages = findViewById(R.id.recyclerViewMessages)
        editTextMessage = findViewById(R.id.editTextMessage)
        buttonSend = findViewById(R.id.buttonSend)

        messageList = mutableListOf()
        messageAdapter = MessageAdapter(messageList)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true

        recyclerViewMessages.layoutManager = LinearLayoutManager(this)
        recyclerViewMessages.adapter = messageAdapter

        var classifierHelper = TextClassificationHelper(context = this@ChatActivity)
        createNotificationChannel()
        buttonSend.setOnClickListener {
            val message = editTextMessage.text.toString().trim()
            if (!TextUtils.isEmpty(message)) {
                Log.d(TAG, "Se va a predecir el texto: $message")
                var categories = classifierHelper.classify(message)
                val label = categories.sortedByDescending { it.score }.get(0).label
                addMessageToList(message,label)

                messageAdapter.notifyItemInserted(messageList.size - 1)
                recyclerViewMessages.scrollToPosition(messageList.size - 1)
                editTextMessage.text.clear()
                validateGroomingMessages()
            }
        }
    }
    private fun addMessageToList(text: String, label: String) {
        val message = Message(
            text = text,
            classification = GroomingStage.getGroomingStage(label.toInt()),
            id = messageList.size + 1
        )
        messageList.add(message)
    }

    private fun validateGroomingMessages() {
        val classificationCount = mutableMapOf<GroomingStage, Int>(
            GroomingStage.FRIENDSHIP to 0,
            GroomingStage.RELATIONSHIP to 0,
            GroomingStage.SEX to 0,
            GroomingStage.APPROACH to 0,
        )

        // Ciclo for para recorrer la lista y contar clasificaciones
        for (message in messageList) {
            val classification: GroomingStage = message.classification?: GroomingStage.FRIENDSHIP
            classificationCount[classification] = classificationCount.getOrDefault(classification, 0) + 1
        }

        if (classificationCount[GroomingStage.SEX]!! >= 3) {
            if (classificationCount[GroomingStage.APPROACH]!! >= 2 || classificationCount[GroomingStage.RELATIONSHIP]!! >= 3) {
                crearNotificacion("Possible grooming detected")
            }
        }
    }

    private fun crearNotificacion(message: String) {
        val builder = NotificationCompat.Builder(this, canalId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Grooming Alert")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@ChatActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@with
            }
            notify(notificationId, builder.build())
        }
    }

    private fun createNotificationChannel() {
        val descriptionText = "Grooming chanel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(canalId, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
