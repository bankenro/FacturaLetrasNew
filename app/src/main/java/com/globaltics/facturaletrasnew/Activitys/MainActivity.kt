package com.globaltics.facturaletrasnew.Activitys

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.widget.Toast
import com.globaltics.facturaletrasnew.Clases.EndPoints.NOTIFICACION
import com.globaltics.facturaletrasnew.Fragments.DetallesFacturaFragment
import com.globaltics.facturaletrasnew.Fragments.DialogsFragment.PagoLetrasDF
import com.globaltics.facturaletrasnew.Fragments.LetrasFragment
import com.globaltics.facturaletrasnew.Fragments.LoginFragment
import com.globaltics.facturaletrasnew.Fragments.MenuFragment
import com.globaltics.facturaletrasnew.R

class MainActivity : AppCompatActivity() {

    private var preferences: SharedPreferences? = null
    private var broadcastReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preferences = getSharedPreferences("FactLetraGTs", Context.MODE_PRIVATE)

        val id = preferences?.getInt("id", 0)
        val idt = preferences?.getString("idt", "")

        if (savedInstanceState == null) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            if (id != 0 && idt!!.isNotEmpty()) {
                val fragment = MenuFragment()
                fragmentTransaction.add(R.id.contenedor, fragment).commit()
            } else {
                val fragment = LoginFragment()
                fragmentTransaction.add(R.id.contenedor, fragment).commit()
            }
        }

        if (intent.extras != null) {
            if (intent.getStringExtra("fragment") == "pagoLetra") {
                val bundle = Bundle()
                val fragment: Fragment? = PagoLetrasDF()
                bundle.putString("id", intent.getStringExtra("id"))
                bundle.putString("letra", intent.getStringExtra("letra"))
                bundle.putString("fecha", intent.getStringExtra("fecha"))
                bundle.putString("moneda", intent.getStringExtra("moneda"))
                bundle.putString("monto", intent.getStringExtra("monto"))
                fragment!!.arguments = bundle
                supportFragmentManager.beginTransaction()
                .replace(R.id.contenedor, LetrasFragment()).commit()
            }
        }

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val letra = intent.getStringExtra("letra")
                Toast.makeText(this@MainActivity, "Notificacion $letra", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver!!, IntentFilter(NOTIFICACION))
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver!!)
    }

}
