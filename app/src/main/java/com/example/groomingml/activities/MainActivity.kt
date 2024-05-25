package com.example.groomingml.activities

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
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
import com.example.groomingml.utils.GroomingStage
import com.example.groomingml.R
import com.example.groomingml.utils.TextClassificationHelper
import com.example.groomingml.model.Message


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private val messages = ArrayList<Message>()
    private lateinit var adapter: ArrayAdapter<Message>
    private val name = "Grooming Chanel"
    private val canalId = "123abc"
    private val notificationId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editText = findViewById<EditText>(R.id.editText)
        val buttonPredict = findViewById<Button>(R.id.buttonPredict)
        val mListView = findViewById<ListView>(R.id.listView)
        // Inicializar el adaptador para la lista
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, messages)
        mListView.adapter = adapter
        createNotificationChannel()
        try {
            // Create the classification helper that will do the heavy lifting
            var classifierHelper = TextClassificationHelper(context = this@MainActivity)

            buttonPredict.setOnClickListener {
                val inputText = editText.text.toString()
                Log.d(TAG, "Se va a predecir el texto: $inputText")
                if (inputText.isNotEmpty()) {
                    var categories = classifierHelper.classify(inputText)
                    val label = categories.sortedByDescending { it.score }.get(0).label
                    addMessageToList(inputText,label)
                    editText.setText("")
                    validateGroomingMessages()
                    //Toast.makeText(this, "Etiqueta: $label", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Por favor, ingresa un texto", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al cargar el modelo TensorFlow Lite", Toast.LENGTH_SHORT).show()
        }
    }

    // Método para agregar un mensaje a la lista
    private fun addMessageToList(text: String, label: String) {
        val message = Message(
            text = text,
            classification = GroomingStage.getGroomingStage(label.toInt()),
            id = messages.size + 1
        )
        messages.add(message)
        adapter.notifyDataSetChanged() // Notificar al adaptador que los datos han cambiado
    }

    private fun validateGroomingMessages() {
        val classificationCount = mutableMapOf<GroomingStage, Int>(
            GroomingStage.FRIENDSHIP to 0,
            GroomingStage.RELATIONSHIP to 0,
            GroomingStage.SEX to 0,
            GroomingStage.APPROACH to 0,
        )

        // Ciclo for para recorrer la lista y contar clasificaciones
        for (message in messages) {
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
                    this@MainActivity,
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
