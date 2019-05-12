package com.globaltics.facturaletrasnew.Fragments.DialogsFragment


import android.app.AlertDialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.globaltics.facturaletrasnew.Clases.EndPoints
import com.globaltics.facturaletrasnew.Clases.VolleySingleton

import com.globaltics.facturaletrasnew.R
import dmax.dialog.SpotsDialog
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class PagoLetrasDF : DialogFragment(), View.OnClickListener {

    private var imagen: ImageView? = null
    private var nombre: TextView? = null
    private var monto: TextView? = null
    private var confirmar: EditText? = null
    private var registrar: Button? = null
    private var cancelar: Button? = null
    private var factura: String?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pago_letras_d, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imagen = view.findViewById(R.id.imagen)
        nombre = view.findViewById(R.id.nombre)
        monto = view.findViewById(R.id.monto)
        confirmar = view.findViewById(R.id.confirmar)
        registrar = view.findViewById(R.id.registrar)
        cancelar = view.findViewById(R.id.cancelar)
        if (arguments!=null){
            factura = arguments?.get("id").toString()
            nombre?.text = arguments?.get("nombre").toString()
            monto?.text = arguments?.get("monto").toString()
            registrar?.setOnClickListener(this)
        }
        cancelar?.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.registrar -> Comprobar()
            R.id.cancelar -> dialog.dismiss()
        }
    }

    private fun Comprobar() {
        val confirmarStr = confirmar?.text.toString().trim()
        if (Objects.equals(confirmarStr, "CONFIRMAR")) {
            Registrar()
        } else {
            Toast.makeText(activity,"Porfavor escriba CONFIRMAR",Toast.LENGTH_SHORT).show()
        }
    }

    private fun Registrar() {
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
                params["accion"] = "pago"
                params["factura"] = factura!!
                params["letra"] = nombre.toString()
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(request)
    }
}
