package com.stackphotonk.alertenchentes.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.stackphotonk.alertenchentes.R
import com.stackphotonk.alertenchentes.data.DBHelper
import com.stackphotonk.alertenchentes.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var db: DBHelper
    private var notificado = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        db = DBHelper()

        val i = intent
        val local = i.getStringExtra("local")!!

        db.getLevel(local) { level ->
            processLevel(level,local)
        }
    }

    private fun processLevel(nivel:Int, local: String) {
        binding.porcentagem.setText("Nível de água: ${nivel}%")

        when {
            nivel <= 50 -> {
                binding.preocupante.setText("Não preocupante")
                notificado = false
            }

            nivel in 51..69 -> {
                binding.preocupante.setText("Fique em alerta")
                notificado = false
            }

            nivel >= 70 -> {
                binding.preocupante.setText("Enchente na área!")
                if (!notificado) {
                    showNotification(local)
                    notificado = true
                }
            }
        }
    }

    private fun showNotification(local:String) {
        val channelId = "alertenchentes_channel"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Criar o canal (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alerta de Enchente",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificação de enchente em $local"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.outline_circle_notifications_24) // ou ícone personalizado
            .setContentTitle("⚠️ Alerta de Enchente")
            .setContentText("O nível da água ultrapassou 70% em $local.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}