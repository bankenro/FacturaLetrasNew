package com.globaltics.facturaletrasnew.Clases.Servicios

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.util.Log
import com.globaltics.facturaletrasnew.Activitys.MainActivity
import com.globaltics.facturaletrasnew.Clases.EndPoints.NOTIFICACION
import com.globaltics.facturaletrasnew.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*
import android.app.NotificationChannel
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi

@SuppressLint("Registered")
class FireBaseServiceNotificacion : FirebaseMessagingService() {

    private var notificationManager: NotificationManager? = null
    private val ADMIN_CHANNEL_ID = "admin_channel"
    //factura--token--letra--fecha--moneda

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
       /* Log.d("remoteMessage", "From: " + remoteMessage?.from)
        if (remoteMessage?.data!!.isNotEmpty()) {*/
            Log.d("remoteMessage", "Message data payload: " + remoteMessage!!.data)
            val factura = remoteMessage.data["factura"]
            val letra = remoteMessage.data["letra"]
            val fecha = remoteMessage.data["fecha"]
            val moneda = remoteMessage.data["moneda"]
            val empresa = remoteMessage.data["empresa"]
            val monto = remoteMessage.data["monto"]
            Notificacion(letra)
            ShowNotificacion(
                factura, letra, fecha, moneda,empresa,monto
            )
       // }
    }

    private fun ShowNotificacion(
        factura: String?,
        letra: String?,
        fecha: String?,
        moneda: String?,
        empresa: String?,
        monto: String?
    ) {

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("id", factura)
        intent.putExtra("letra", letra)
        intent.putExtra("fecha", fecha)
        intent.putExtra("moneda", moneda)
        intent.putExtra("monto", monto)
        intent.putExtra("fragment", "pagoLetra")
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels()
        }

        val notificationId = Random().nextInt(60000)
        val builder = NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
        builder.setAutoCancel(true)
        builder.setContentTitle("$empresa PAGO DE LETRA")
        builder.setContentText("MONTO: $monto $moneda")
        builder.setSubText("ULTIMO DIA $fecha")
        builder.setSound(uri)
        //builder.setTicker("ticker");
        builder.setSmallIcon(R.mipmap.logo)
        builder.setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(notificationId, builder.build())

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupChannels() {
        val adminChannelName = getString(R.string.notifications_admin_channel_name)
        val adminChannelDescription = getString(R.string.notifications_admin_channel_description)
        val adminChannel: NotificationChannel
        adminChannel = NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_LOW)
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        if (notificationManager != null) {
            notificationManager?.createNotificationChannel(adminChannel)
        }
    }

    private fun Notificacion(letra: String?) {
        val intent = Intent(NOTIFICACION)
        intent.putExtra("letra", letra)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }
}