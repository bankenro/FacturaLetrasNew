package com.globaltics.facturaletrasnew.Fragments.DialogsFragment


import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.nfc.Tag
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.util.Log
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
import java.text.SimpleDateFormat
import java.util.*


class AddLetraDF : DialogFragment(), View.OnClickListener {

    private var calendar = Calendar.getInstance()
    private var sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
    private var nombre: EditText? = null
    private var monto: EditText? = null
    private var fecha: TextView? = null
    private var registrar: Button? = null
    private var cancelar: Button? = null
    private var accion: String? = null
    private var factura: String? = null
    private var preferences: SharedPreferences? = null
    private var usuario: Int? = null
    private var numero: Int? = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_letra_d, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nombre = view.findViewById(R.id.nombre)
        monto = view.findViewById(R.id.monto)
        fecha = view.findViewById(R.id.fecha)
        registrar = view.findViewById(R.id.registrar)
        cancelar = view.findViewById(R.id.cancelar)

        preferences = activity?.getSharedPreferences("FactLetraGTs", Context.MODE_PRIVATE)
        usuario = preferences?.getInt("id", 0)
        fecha?.text = sdf.format(calendar.time)

        if (arguments != null) {
            factura = arguments?.get("id").toString()
            accion = arguments?.get("accion").toString()
            numero = arguments?.get("numero").toString().toInt()
            if (Objects.equals(accion, "act_letra")) {
                nombre?.isFocusable = false
                nombre?.isClickable = false
                nombre?.setText(arguments?.get("nombre").toString())
                monto?.setText(arguments?.get("monto").toString())
            }
        }

        registrar?.setOnClickListener(this)
        fecha?.setOnClickListener(this)
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
            R.id.fecha -> date()
        }
    }

    private fun date() {
        DatePickerDialog(
            activity!!, d, calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private var d: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updatedate()
        }

    private fun updatedate() {
        fecha?.text = sdf.format(calendar.time)
    }

    private fun Comprobar() {
        val montoStr = monto?.text.toString()
        val nombreStr = nombre?.text.toString()
        if (montoStr.isNotEmpty() && nombreStr.isNotEmpty()) {
            if (Objects.equals(numero,0)){
                dialog.dismiss()
            }else{
                Registrar(montoStr, nombreStr)
            }
        } else {
            Toast.makeText(activity, "Rellene los campos", Toast.LENGTH_SHORT).show()
        }
    }


    private fun Registrar(montoStr: String, nombreStr: String) {
        val dialogA: AlertDialog =
            SpotsDialog.Builder().setContext(activity).setMessage(R.string.app_name).setCancelable(false).build()
        dialogA.show()
        val request = object : StringRequest(
            Method.POST, EndPoints.URL_ROOT,
            Response.Listener { response ->
                try {
                    val obj = JSONObject(response)
                    if (!obj.getBoolean("error")) {
                        dialogA.dismiss()
                        numero = numero?.minus(1)
                        nombre?.setText("")
                        monto?.setText("")
                        Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                        dialogA.dismiss()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    dialogA.dismiss()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(activity, error?.message, Toast.LENGTH_LONG).show()
                dialogA.dismiss()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["accion"] = accion!!
                params["factura"] = factura!!
                params["letra"] = nombreStr
                params["monto"] = montoStr
                params["usu"] = usuario.toString()
                params["fecha"] = fecha?.text.toString()
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(request)
    }
}
