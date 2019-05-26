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
import android.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.globaltics.facturaletrasnew.Clases.EndPoints
import com.globaltics.facturaletrasnew.Clases.Modelos.Tipos
import com.globaltics.facturaletrasnew.Clases.Views.Spinners.TiposAdaptador
import com.globaltics.facturaletrasnew.Clases.VolleySingleton

import com.globaltics.facturaletrasnew.R
import dmax.dialog.SpotsDialog
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class AddFacturaDF : DialogFragment(), View.OnClickListener, AdapterView.OnItemSelectedListener {

    private var factura: EditText? = null
    private var monto: EditText? = null
    private var numero: EditText? = null
    private var registrar: Button? = null
    private var cancelar: Button? = null
    private var accion: String? = null
    private var preferences: SharedPreferences? = null
    private var usuario: Int? = null
    private var monedas: Spinner? = null
    private var textMoneda: TextView? = null
    private var moneda: TextView? = null
    private var monedasList: MutableList<Tipos>? = null
    private var mid: Int? = null
    private var bancos: Spinner? = null
    private var textBanco: TextView? = null
    private var banco: TextView? = null
    private var bancosList: MutableList<Tipos>? = null
    private var bid: Int? = null
    private var proveedores: Spinner? = null
    private var textProveedor: TextView? = null
    private var proveedor: TextView? = null
    private var proveedoresList: MutableList<Tipos>? = null
    private var pid: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_factura_d, container, false)

        proveedor = view.findViewById(R.id.proveedor)
        factura = view.findViewById(R.id.factura)
        monto = view.findViewById(R.id.monto)
        numero = view.findViewById(R.id.numero)
        monedas = view.findViewById(R.id.monedas)
        textMoneda = view.findViewById(R.id.textMoneda)
        moneda = view.findViewById(R.id.moneda)
        bancos = view.findViewById(R.id.bancos)
        textBanco = view.findViewById(R.id.textBanco)
        banco = view.findViewById(R.id.banco)
        proveedores = view.findViewById(R.id.proveedores)
        textProveedor = view.findViewById(R.id.textProveedor)
        registrar = view.findViewById(R.id.registrar)
        cancelar = view.findViewById(R.id.cancelar)

        preferences = activity?.getSharedPreferences("FactLetraGTs", Context.MODE_PRIVATE)
        usuario = preferences?.getInt("id", 0)

        monedasList = ArrayList()
        bancosList = ArrayList()
        proveedoresList = ArrayList()

        textMoneda?.visibility = View.GONE
        moneda?.visibility = View.GONE
        textBanco?.visibility = View.GONE
        banco?.visibility = View.GONE
        textProveedor?.visibility = View.GONE
        proveedor?.visibility = View.GONE

        if (arguments != null) {
            accion = arguments?.get("accion").toString()
            if (Objects.equals(accion, "act_fact")) {
                factura?.isFocusable = false
                factura?.isClickable = false
                textMoneda?.visibility = View.VISIBLE
                moneda?.visibility = View.VISIBLE
                textBanco?.visibility = View.VISIBLE
                banco?.visibility = View.VISIBLE
                textProveedor?.visibility = View.VISIBLE
                proveedor?.visibility = View.VISIBLE
                factura?.setText(arguments?.get("id").toString())
                proveedor?.text = arguments?.get("empresa").toString()
                monto?.setText(arguments?.get("monto").toString())
                numero?.setText(arguments?.get("numero").toString())
                moneda?.text = arguments?.get("moneda").toString()
                banco?.text = arguments?.get("banco").toString()
            }
        }
        LlenarMonedas()
        LlenarBancos()
        LlenarProveedores()

        bancos?.onItemSelectedListener = this
        proveedores?.onItemSelectedListener = this
        monedas?.onItemSelectedListener = this
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
        when (v?.id) {
            R.id.registrar -> Comprobar()
            R.id.cancelar -> dialog.dismiss()
        }
    }

    private fun Comprobar() {
        val facturaStr = factura?.text.toString()
        val montoStr = monto?.text.toString()
        val numeroStr = numero?.text.toString()
        if (facturaStr.isNotEmpty() && montoStr.isNotEmpty()
            && numeroStr.isNotEmpty() && mid != null && bid != null && pid != null
        ) {
            Registrar(facturaStr, montoStr, numeroStr, mid!!, bid!!, pid!!)
        } else {
            Toast.makeText(activity, "Rellene todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.id) {
            R.id.monedas -> mid = monedasList!![position].id
            R.id.bancos -> bid = bancosList!![position].id
            R.id.proveedores -> pid = proveedoresList!![position].id
        }
    }

    private fun LlenarBancos() {
        bancos!!.adapter = null
        val stringRequest = object : StringRequest(
            Method.POST, EndPoints.URL_ROOT,
            Response.Listener<String> { response ->
                try {
                    val obj = JSONObject(response)
                    if (!obj.getBoolean("error")) {
                        //Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                        val array = obj.getJSONArray("bancos")
                        bancosList!!.clear()
                        for (i in 0 until array.length()) {
                            val objectArtist = array.getJSONObject(i)
                            val tipos = Tipos(
                                objectArtist.getInt("id"),
                                objectArtist.getString("nombre")
                            )
                            bancosList?.add(tipos)
                        }
                        val adapter = TiposAdaptador(
                            this.activity!!,
                            R.layout.item_spinner,
                            this.bancosList!!
                        )
                        bancos?.adapter = adapter
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
                params["accion"] = "bancos"
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }

    private fun LlenarProveedores() {
        proveedores!!.adapter = null
        val stringRequest = object : StringRequest(
            Method.POST, EndPoints.URL_ROOT,
            Response.Listener<String> { response ->
                try {
                    val obj = JSONObject(response)
                    if (!obj.getBoolean("error")) {
                        //Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                        val array = obj.getJSONArray("proveedores")
                        proveedoresList!!.clear()
                        for (i in 0 until array.length()) {
                            val objectArtist = array.getJSONObject(i)
                            val tipos = Tipos(
                                objectArtist.getInt("id"),
                                objectArtist.getString("nombre")
                            )
                            proveedoresList?.add(tipos)
                        }
                        val adapter = TiposAdaptador(
                            this.activity!!,
                            R.layout.item_spinner,
                            this.proveedoresList!!
                        )
                        proveedores?.adapter = adapter
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
                params["accion"] = "proveedores"
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
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

    private fun Registrar(
        facturaStr: String,
        montoStr: String,
        numeroStr: String,
        mid: Int,
        bid: Int,
        pid: Int
    ) {
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

                        proveedor?.text = ""
                        monto?.setText("")
                        factura?.setText("")
                        numero?.setText("")

                        val bundle = Bundle()
                        val dialogLe = AddLetraDF()
                        dialogLe.setTargetFragment(this, 1)
                        val ft = activity?.supportFragmentManager?.beginTransaction()
                        bundle.putString("accion", "add_letra")
                        bundle.putString("id", facturaStr)
                        bundle.putString("numero", numeroStr)
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
                params["monto"] = montoStr
                params["numero"] = numeroStr
                params["usu"] = usuario.toString()
                params["mid"] = mid.toString()
                params["bid"] = bid.toString()
                params["pid"] = pid.toString()
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(request)
    }
}
