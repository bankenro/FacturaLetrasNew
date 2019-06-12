package com.globaltics.facturaletrasnew.Fragments


import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.globaltics.facturaletras.Clases.Views.ReportFacturaAdaptador
import com.globaltics.facturaletrasnew.Clases.EndPoints
import com.globaltics.facturaletrasnew.Clases.Modelos.Empresas
import com.globaltics.facturaletrasnew.Clases.Modelos.Facturas
import com.globaltics.facturaletrasnew.Clases.Modelos.Filtros
import com.globaltics.facturaletrasnew.Clases.Modelos.Letras
import com.globaltics.facturaletrasnew.Clases.Views.ReportEmpresaAdaptador
import com.globaltics.facturaletrasnew.Clases.Views.ReportLetraAdaptador
import com.globaltics.facturaletrasnew.Clases.Views.Spinners.FiltrosAdaptador
import com.globaltics.facturaletrasnew.Clases.VolleySingleton

import com.globaltics.facturaletrasnew.R
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ReportesFragment : Fragment(), View.OnClickListener, AdapterView.OnItemSelectedListener {

    private var calendar = Calendar.getInstance()
    private var sdf = SimpleDateFormat("yyyy-MM", Locale.ROOT)
    private var filtro: Spinner? = null
    private var fecha: TextView? = null
    private var descargar: Button? = null
    private var reporte: RecyclerView? = null
    private var idr: Int? = 0
    private val filtroArray = arrayOf("FACTURAS", "LETRAS", "EMPRESAS")
    private var filtrosList: MutableList<Filtros>? = null
    private var reportesLetraList: MutableList<Letras>? = null
    private var reportesEmpreList: MutableList<Empresas>? = null
    private var reportesFactuList: MutableList<Facturas>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_reportes, container, false)

        filtro = view.findViewById(R.id.filtro)
        fecha = view.findViewById(R.id.fecha)
        descargar = view.findViewById(R.id.descargar)
        reporte = view.findViewById(R.id.reporte)

        fecha?.text = sdf.format(calendar.time)
        filtrosList = ArrayList()
        reportesLetraList = ArrayList()
        reportesEmpreList = ArrayList()
        reportesFactuList = ArrayList()
        LlenarFiltros()

        reporte?.setHasFixedSize(true)
        reporte?.itemAnimator = null
        reporte?.layoutManager = LinearLayoutManager(activity)
        reporte?.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

        fecha?.setOnClickListener(this)
        descargar?.setOnClickListener(this)
        filtro?.onItemSelectedListener = this

        return view
    }

    private fun LlenarFiltros() {
        filtrosList!!.clear()
        for (i in filtroArray.indices) {
            val filtros = Filtros(
                filtroArray[i]
            )
            filtrosList?.add(filtros)
        }
        try {
            val adapter = FiltrosAdaptador(
                this.activity!!,
                R.layout.item_spinner,
                this.filtrosList!!
            )
            filtro?.adapter = adapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fecha -> date()
            R.id.descargar -> descargarPDF()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.id) {
            R.id.filtro -> {
                idr = position
                when (position) {
                    0 -> {
                        LlenarReporteFacturas(fecha?.text.toString(), position)
                    }
                    1 -> {
                        LlenarReporteLetras(fecha?.text.toString(), position)
                    }
                    2 -> {
                        LlenarReporteEmpresas(fecha?.text.toString(), position)
                    }
                }
            }
        }
    }

    private fun descargarPDF() {

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
        when (idr) {
            0 -> {
                LlenarReporteFacturas(fecha?.text.toString(), idr!!)
            }
            1 -> {
                LlenarReporteLetras(fecha?.text.toString(), idr!!)
            }
            2 -> {
                LlenarReporteEmpresas(fecha?.text.toString(), idr!!)
            }
        }
    }

    private fun LlenarReporteLetras(fecha: String, idr: Int) {
        reporte!!.adapter = null
        val stringRequest = object : StringRequest(
            Method.POST, EndPoints.URL_ROOT,
            Response.Listener<String> { response ->
                try {
                    val obj = JSONObject(response)
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                        val array = obj.getJSONArray("reportes")
                        reportesLetraList!!.clear()
                        for (i in 0 until array.length()) {
                            val objectArtist = array.getJSONObject(i)
                            val letras = Letras(
                                objectArtist.getString("nombre"),
                                objectArtist.getString("factura"),
                                objectArtist.getString("empresa"),
                                objectArtist.getString("monto"),
                                objectArtist.getString("fecha"),
                                objectArtist.getString("estado"),
                                objectArtist.getString("moneda"),
                                objectArtist.getString("descripcion"),
                                objectArtist.getString("imagen")

                            )
                            reportesLetraList!!.add(letras)
                        }
                        try {
                            val adapter =
                                ReportLetraAdaptador(
                                    (reportesLetraList as java.util.ArrayList<Letras>?)!!,
                                    this.activity!!
                                )
                            reporte!!.adapter = adapter
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error -> Toast.makeText(activity, error?.message, Toast.LENGTH_LONG).show() }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["accion"] = "reportes"
                params["fecha"] = fecha
                params["idr"] = idr.toString()
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }

    private fun LlenarReporteEmpresas(fecha: String, idr: Int) {
        reporte!!.adapter = null
        val stringRequest = object : StringRequest(
            Method.POST, EndPoints.URL_ROOT,
            Response.Listener<String> { response ->
                try {
                    val obj = JSONObject(response)
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                        val array = obj.getJSONArray("reportes")
                        reportesEmpreList!!.clear()
                        for (i in 0 until array.length()) {
                            val objectArtist = array.getJSONObject(i)
                            val reportes = Empresas(
                                objectArtist.getString("empresa"),
                                objectArtist.getInt("nfactura"),
                                objectArtist.getString("monto"),
                                objectArtist.getString("pagado"),
                                objectArtist.getString("debido")
                            )
                            reportesEmpreList!!.add(reportes)
                        }
                        try {
                            val adapter =
                                ReportEmpresaAdaptador(
                                    (reportesEmpreList as java.util.ArrayList<Empresas>?)!!,
                                    this.activity!!
                                )
                            reporte!!.adapter = adapter
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error -> Toast.makeText(activity, error?.message, Toast.LENGTH_LONG).show() }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["accion"] = "reportes"
                params["fecha"] = fecha
                params["idr"] = idr.toString()
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }

    private fun LlenarReporteFacturas(fecha: String, idr: Int) {
        reporte!!.adapter = null
        val stringRequest = object : StringRequest(
            Method.POST, EndPoints.URL_ROOT,
            Response.Listener<String> { response ->
                try {
                    val obj = JSONObject(response)
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                        val array = obj.getJSONArray("reportes")
                        reportesFactuList!!.clear()
                        for (i in 0 until array.length()) {
                            val objectArtist = array.getJSONObject(i)
                            val reportes = Facturas(
                                objectArtist.getString("factura"),
                                objectArtist.getString("empresa"),
                                objectArtist.getString("monto"),
                                objectArtist.getInt("pagados"),
                                objectArtist.getInt("debidos"),
                                objectArtist.getString("pagado"),
                                objectArtist.getString("debido"),
                                objectArtist.getInt("numero"),
                                objectArtist.getString("banco"),
                                objectArtist.getString("moneda")
                            )
                            reportesFactuList!!.add(reportes)
                        }
                        try {
                            val adapter =
                                ReportFacturaAdaptador(
                                    (reportesFactuList as java.util.ArrayList<Facturas>?)!!,
                                    this.activity!!
                                )
                            reporte!!.adapter = adapter
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error -> Toast.makeText(activity, error?.message, Toast.LENGTH_LONG).show() }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["accion"] = "reportes"
                params["fecha"] = fecha
                params["idr"] = idr.toString()
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }

}
