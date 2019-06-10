package com.globaltics.facturaletrasnew.Fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.globaltics.facturaletrasnew.Clases.EndPoints
import com.globaltics.facturaletrasnew.Clases.VolleySingleton
import com.globaltics.facturaletrasnew.R
import com.google.firebase.iid.FirebaseInstanceId
import dmax.dialog.SpotsDialog
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class LoginFragment : Fragment(), View.OnClickListener {

    private var preferences: SharedPreferences? = null
    private var usuario: EditText? = null
    private var password: EditText? = null
    private var ingresar: Button? = null
    private var TOKEN: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        preferences = activity?.getSharedPreferences("FactLetraGTs", Context.MODE_PRIVATE)
        usuario = view.findViewById(R.id.usuario)
        password = view.findViewById(R.id.password)
        ingresar = view.findViewById(R.id.ingresar)

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(
            (
                    activity!!
                    )
        ) { instanceIdResult ->
            TOKEN = instanceIdResult.token
            //Log.e("Token", TOKEN)
        }

        ingresar?.setOnClickListener(this)

        return view
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ingresar -> ComprobarDatos()
        }
    }

    private fun ComprobarDatos() {
        val usuarioStr = usuario?.text.toString().trim()
        val passwordStr = password?.text.toString().trim()
        var nuevotoken: String? = ""
        if (TOKEN!!.contains("{")) {
            try {
                val jo = JSONObject(TOKEN)
                nuevotoken = jo.getString("token")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }else{
            nuevotoken = TOKEN
        }
        if (usuarioStr.isNotEmpty() && passwordStr.isNotEmpty()) {
            Login(usuarioStr, passwordStr, nuevotoken!!)
        } else {
            Toast.makeText(activity, "Rellene los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun Login(usuarioStr: String, passwordStr: String, nuevotoken: String) {
        val dialog: AlertDialog =
            SpotsDialog.Builder().setContext(activity).setMessage(R.string.app_name).setCancelable(false).build()
        dialog.show()
        val request = object : StringRequest(
            Method.POST, EndPoints.URL_ROOT,
            Response.Listener { response ->
                try {
                    val obj = JSONObject(response)
                    if (!obj.getBoolean("error")) {
                        dialog.dismiss()
                        Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()

                        val array = obj.getJSONArray("usu")
                        val objectsUsuario = array.getJSONObject(0)

                        val tipou = objectsUsuario.getString("usuariot")

                        val editor = preferences?.edit()
                        editor?.clear()?.apply()
                        editor?.putInt("id", objectsUsuario.getInt("usuario"))
                        editor?.putString("idt", tipou)
                        editor?.apply()
                        editor?.commit()

                        val fragment = MenuFragment()
                        val transaction = activity?.supportFragmentManager?.beginTransaction()
                        transaction?.replace(R.id.contenedor, fragment)?.commit()

                    } else {
                        Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    dialog.dismiss()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(activity, error?.message, Toast.LENGTH_LONG).show()
                dialog.dismiss()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["accion"] = "login"
                params["usuario"] = usuarioStr
                params["password"] = passwordStr
                params["token"] = nuevotoken
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(request)
    }
}
