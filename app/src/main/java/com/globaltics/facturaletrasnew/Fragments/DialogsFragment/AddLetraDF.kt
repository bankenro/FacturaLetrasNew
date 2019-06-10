package com.globaltics.facturaletrasnew.Fragments.DialogsFragment


import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.globaltics.facturaletrasnew.Clases.ActualizarRecyclerViews
import com.globaltics.facturaletrasnew.Clases.EndPoints
import com.globaltics.facturaletrasnew.Clases.Modelos.Tipos
import com.globaltics.facturaletrasnew.Clases.Views.Spinners.TiposAdaptador
import com.globaltics.facturaletrasnew.Clases.VolleySingleton

import com.globaltics.facturaletrasnew.R
import dmax.dialog.SpotsDialog
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AddLetraDF : DialogFragment(), View.OnClickListener, AdapterView.OnItemSelectedListener {

    private var calendar = Calendar.getInstance()
    private var sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
    private var letra: EditText? = null
    private var monto: EditText? = null
    private var fecha: TextView? = null
    private var registrar: Button? = null
    private var cancelar: Button? = null
    private var accion: String? = null
    private var factura: String? = null
    private var preferences: SharedPreferences? = null
    private var usuario: Int? = null
    private var ContNume: Int? = 0
    private var monedas: Spinner? = null
    private var moneda: TextView? = null
    private var textMoneda: TextView? = null
    private var monedasList: MutableList<Tipos>? = null
    private var mid: Int? = null
    private var textConteo: TextView? = null
    private var conteo: TextView? = null
    private var monto0: TextView? = null
    private var ContMont: Int? = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_letra_d, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        letra = view.findViewById(R.id.letra)
        monto = view.findViewById(R.id.monto)
        fecha = view.findViewById(R.id.fecha)
        registrar = view.findViewById(R.id.registrar)
        cancelar = view.findViewById(R.id.cancelar)
        monedas = view.findViewById(R.id.monedas)
        moneda = view.findViewById(R.id.moneda)
        textMoneda = view.findViewById(R.id.textMoneda)
        textConteo = view.findViewById(R.id.textConteo)
        conteo = view.findViewById(R.id.conteo)
        monto0 = view.findViewById(R.id.monto0)

        preferences = activity?.getSharedPreferences("FactLetraGTs", Context.MODE_PRIVATE)
        usuario = preferences?.getInt("id", 0)
        fecha?.text = sdf.format(calendar.time)

        monedasList = ArrayList()

        moneda?.visibility = View.GONE
        textMoneda?.visibility = View.GONE

        if (arguments != null) {
            factura = arguments?.get("id").toString()
            accion = arguments?.get("accion").toString()
            ContNume = arguments?.get("numero").toString().toInt()
            ContMont = arguments?.get("monto").toString().toInt()
            monto0?.text = ContMont.toString()
            conteo?.text = ContNume.toString()
            if (Objects.equals(accion, "act_letra")) {
                letra?.isFocusable = false
                letra?.isClickable = false
                letra?.setText(arguments?.get("letra").toString())
                monto?.setText(arguments?.get("monto").toString())
                moneda?.text = arguments?.get("moneda").toString()
                moneda?.visibility = View.VISIBLE
                textMoneda?.visibility = View.VISIBLE
                textConteo?.visibility = View.GONE
                conteo?.visibility = View.GONE
            }
        }

        LlenarMonedas()

        monedas?.onItemSelectedListener = this
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
        val nombreStr = letra?.text.toString()
        if (montoStr.isNotEmpty() && nombreStr.isNotEmpty() && mid != null) {
            if (ContNume!! <= 0) {
                if (ContMont!! <= montoStr.toInt() && ContMont == 0) {
                    Toast.makeText(activity, "El monto ingresado es mucho mayor al solicitado", Toast.LENGTH_SHORT).show()
                } else {
                    dialog.dismiss()
                }
            } else {
                Registrar(montoStr, nombreStr, mid!!)
            }
        } else {
            Toast.makeText(activity, "Rellene los campos", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.id) {
            R.id.monedas -> mid = monedasList!![position].id
        }
    }

    private fun LlenarMonedas() {
        monedas!!.adapter = null
        val stringRequest = object : StringRequest(
            Method.POST, EndPoints.URL_ROOT,
            Response.Listener<String> { response ->
                try {
                    val obj = JSONObject(response)
                    if (!obj.getBoolean("error")) {
                        //Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                        val array = obj.getJSONArray("monedas")
                        monedasList!!.clear()
                        for (i in 0 until array.length()) {
                            val objectArtist = array.getJSONObject(i)
                            val tipos = Tipos(
                                objectArtist.getInt("id"),
                                objectArtist.getString("nombre")
                            )
                            monedasList?.add(tipos)
                        }
                        val adapter = TiposAdaptador(
                            this.activity!!,
                            R.layout.item_spinner,
                            this.monedasList!!
                        )
                        monedas?.adapter = adapter
                    } else {
                        //Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error -> Toast.makeText(activity, error?.message, Toast.LENGTH_LONG).show() }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["accion"] = "monedas"
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }

    private fun Registrar(montoStr: String, nombreStr: String, mid: Int) {
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
                        ContNume = ContNume?.minus(1)
                        ContMont = ContMont?.minus(montoStr.toInt())
                        conteo?.text = ContNume.toString()
                        monto0?.text = ContMont.toString()
                        letra?.setText("")
                        monto?.setText("")
                        Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                        ComprobarCeros()
                        if (Objects.equals(accion, "act_letra")) {
                            try {
                                (targetFragment as ActualizarRecyclerViews).ActuDetalFact()
                            } catch (e: ClassCastException) {
                                e.printStackTrace()
                            }
                            dialog.dismiss()
                        }
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
                params["moneda"] = mid.toString()
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(request)
    }

    private fun ComprobarCeros() {
        if (ContMont==0 && ContNume==0)
            dialog.dismiss()
    }
}
