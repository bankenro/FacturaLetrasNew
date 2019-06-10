package com.globaltics.facturaletrasnew.Clases.Servicios

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import android.support.v4.content.LocalBroadcastManager
import com.globaltics.facturaletrasnew.Activitys.MainActivity
import com.globaltics.facturaletrasnew.Clases.EndPoints.NOTIFICACION
import com.globaltics.facturaletrasnew.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception
import java.util.*

@SuppressLint("Registered")
class FireBaseServiceNotificacion : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        val empresa = remoteMessage!!.data["empresa"]
        val fecha = remoteMessage.data["fecha"]
        val letra = remoteMessage.data["letra"]
        val JsonObject = remoteMessage.data["data"]
        Notificacion(letra)
        ShowNotificacion(empresa, fecha, letra, JsonObject)
    }

    private fun ShowNotificacion(
        titulo: String?,
        fecha: String?,
        mensaje: String?,
        jsonObject: String?
    ) {
        try {
            var jo: JSONObject
            val ja = JSONArray(jsonObject)
            var factura: String? = ""
            var empresa: String? = ""
            /*var pagados: String? = ""
            var debidos: String? = ""*/
            for (i in 0 until ja.length()) {
                jo = ja.getJSONObject(i)
                factura = jo.getString("factura")
                empresa = jo.getString("empresa")
              /*  pagados = jo.getString("pagados")
                debidos = jo.getString("debidos")*/
            }
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("factura", factura)
            intent.putExtra("empresa", empresa)
            /*intent.putExtra("pagados", pagados)
            intent.putExtra("debidos", debidos)*/
            intent.putExtra("fragment", "facturaDetalles")
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val canalid = "canal01"
            val builder = NotificationCompat.Builder(this, canalid)
            builder.setAutoCancel(true)
            builder.setContentTitle(titulo)
            builder.setContentText(mensaje)
            builder.setSubText(fecha)
            builder.setSound(uri)
            //builder.setTicker("ticker");
            builder.setSmallIcon(R.mipmap.ic_launcher_round)
            builder.setContentIntent(pendingIntent)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val random = Random()

            notificationManager.notify(random.nextInt(), builder.build())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun Notificacion(letra: String?) {
        val intent = Intent(NOTIFICACION)
        intent.putExtra("letra", letra)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }
}