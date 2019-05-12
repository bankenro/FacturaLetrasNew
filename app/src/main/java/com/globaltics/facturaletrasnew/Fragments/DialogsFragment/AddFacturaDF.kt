package com.globaltics.facturaletrasnew.Fragments.DialogsFragment


import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
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
import dmax.dialog.SpotsDialog
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class AddFacturaDF : DialogFragment(), View.OnClickListener {

    private var nombre: EditText? = null
    private var factura: EditText? = null
    private var monto: EditText? = null
    private var numero: EditText? = null
    private var registrar: Button? = null
    private var cancelar: Button? = null
    private var accion: String? = null
    private var preferences: SharedPreferences? = null
    private var usuario: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_factura_d, container, false)

        nombre = view.findViewById(R.id.nombre)
        factura = view.findViewById(R.id.factura)
        monto = view.findViewById(R.id.monto)
        numero = view.findViewById(R.id.numero)
        registrar = view.findViewById(R.id.registrar)
        cancelar = view.findViewById(R.id.cancelar)

        preferences = activity?.getSharedPreferences("FactLetraGTs", Context.MODE_PRIVATE)
        usuario = preferences?.getInt("id",0)

        if (arguments!=null){
            accion = arguments?.get("accion").toString()
            if (Objects.equals(accion,"act_fact")){
                factura?.isFocusable = false
                factura?.isClickable = false
                factura?.setText(arguments?.get("id").toString())
                nombre?.setText(arguments?.get("nombre").toString())
                monto?.setText(arguments?.get("monto").toString())
                numero?.setText(arguments?.get("numero").toString())
            }
        }
        cancelar?.setOnClickListener(this)
        registrar?.setOnClickListener(this)
        return view
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
        when(v?.id){
            R.id.registrar -> Comprobar()
            R.id.cancelar -> dialog.dismiss()
        }
    }

    private fun Comprobar() {
        val nombreStr = nombre?.text.toString()
        val facturaStr = factura?.text.toString()
        val montoStr = monto?.text.toString()
        val numeroStr = numero?.text.toString()
        if (nombreStr.isNotEmpty() && facturaStr.isNotEmpty() && montoStr.isNotEmpty()
            && numeroStr.isNotEmpty()){
            Registrar(nombreStr,facturaStr,montoStr,numeroStr)
        }else{
            Toast.makeText(activity,"Rellene todos los campos",Toast.LENGTH_SHORT).show()
        }
    }

    private fun Registrar(nombreStr: String, facturaStr: String, montoStr: String, numeroStr: String) {
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
                        Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()

                        nombre?.setText("")
                        monto?.setText("")
                        factura?.setText("")
                        numero?.setText("")

                        val bundle = Bundle()
                        val dialogLe = AddLetraDF()
                        dialogLe.setTargetFragment(this, 1)
                        val ft = activity?.supportFragmentManager?.beginTransaction()
                        bundle.putString("accion", "add_letra")
                        bundle.putString("id",facturaStr)
                        bundle.putString("numero",numeroStr)
                        dialogLe.arguments = bundle
                        dialogLe.isCancelable = false
                        dialogLe.show(ft, "Registrar Letra")
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
                dialog.dismiss()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["accion"] = accion!!
                params["factura"] = facturaStr
                params["nombre"] = nombreStr
                params["monto"] = montoStr
                params["numero"] = numeroStr
                params["usu"] = usuario.toString()
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(request)
    }
}
