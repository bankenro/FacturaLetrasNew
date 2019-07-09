package com.globaltics.facturaletrasnew.Fragments


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.globaltics.facturaletrasnew.Clases.ActualizarRecyclerViews
import com.globaltics.facturaletrasnew.Clases.EndPoints
import com.globaltics.facturaletrasnew.Clases.Modelos.EmpreClien
import com.globaltics.facturaletrasnew.Clases.Modelos.Tipos
import com.globaltics.facturaletrasnew.Clases.Views.EmprClieAdaptador
import com.globaltics.facturaletrasnew.Clases.Views.Spinners.TiposAdaptador
import com.globaltics.facturaletrasnew.Clases.VolleySingleton

import com.globaltics.facturaletrasnew.R
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class EmpresasClientesFragment : Fragment(), AdapterView.OnItemSelectedListener, ActualizarRecyclerViews {
    override fun ActuRecy() {
        LlenarEmpreClien()
    }

    private var tipousu: Spinner? = null
    private var tipoActivo: Spinner? = null
    private var EmpreClien: RecyclerView? = null
    private var tipousuList: MutableList<Tipos>? = null
    private var tiposActivoList: MutableList<Tipos>? = null
    private var EmpreClienList: MutableList<EmpreClien>? = null
    private var IdTipoUsu: Int? = 0
    private var IdAct: Int? = 0
    private var usuario: Int? = 0
    private var preferences: SharedPreferences? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_empresas_clientes, container, false)

        tipousu = view.findViewById(R.id.tipousu)
        tipoActivo = view.findViewById(R.id.activo)
        EmpreClien = view.findViewById(R.id.EmpreClien)

        preferences = activity?.getSharedPreferences("FactLetraGTs", Context.MODE_PRIVATE)
        usuario = preferences?.getInt("id", 0)

        tiposActivoList = ArrayList()
        tipousuList = ArrayList()
        EmpreClienList = ArrayList()

        EmpreClien?.setHasFixedSize(true)
        EmpreClien?.itemAnimator = null
        EmpreClien?.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        EmpreClien?.addItemDecoration(
            androidx.recyclerview.widget.DividerItemDecoration(
                activity,
                androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
            )
        )
        LlenarTipousu()
        LlenarTiposActivo()

        tipousu?.onItemSelectedListener = this
        tipoActivo?.onItemSelectedListener = this


        return view
    }

    private fun LlenarTiposActivo() {
        tipoActivo?.adapter = null
        val stringRequest = object : StringRequest(
            Method.POST, EndPoints.URL_ROOT,
            Response.Listener<String> { response ->
                try {
                    val obj = JSONObject(response)
                    if (!obj.getBoolean("error")) {
                        //Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                        val array = obj.getJSONArray("tactivo")
                        tiposActivoList?.clear()
                        for (i in 0 until array.length()) {
                            val objectArtist = array.getJSONObject(i)
                            val tipos = Tipos(
                                objectArtist.getInt("id"),
                                objectArtist.getString("nombre")
                            )
                            tiposActivoList?.add(tipos)
                        }
                        val adapter = TiposAdaptador(
                            this.activity!!,
                            R.layout.item_spinner,
                            this.tiposActivoList!!
                        )
                        tipoActivo?.adapter = adapter
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
                params["accion"] = "tactivo"
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }

    private fun LlenarTipousu() {
        tipousu!!.adapter = null
        val stringRequest = object : StringRequest(
            Method.POST, EndPoints.URL_ROOT,
            Response.Listener<String> { response ->
                try {
                    val obj = JSONObject(response)
                    if (!obj.getBoolean("error")) {
                        //Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                        val array = obj.getJSONArray("tusu")
                        tipousuList!!.clear()
                        for (i in 0 until array.length()) {
                            val objectArtist = array.getJSONObject(i)
                            val tipos = Tipos(
                                objectArtist.getInt("id"),
                                objectArtist.getString("nombre")
                            )
                            tipousuList?.add(tipos)
                        }
                        val adapter = TiposAdaptador(
                            this.activity!!,
                            R.layout.item_spinner,
                            this.tipousuList!!
                        )
                        tipousu?.adapter = adapter
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
                params["accion"] = "tusu"
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }

    private fun LlenarEmpreClien() {
        if (IdAct != 0 && IdTipoUsu != 0) {
            EmpreClien?.adapter = null
            val stringRequest = object : StringRequest(
                Method.POST, EndPoints.URL_ROOT,
                Response.Listener<String> { response ->
                    try {
                        val obj = JSONObject(response)
                        if (!obj.getBoolean("error")) {
                            Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                            val array = obj.getJSONArray("EmpreClien")
                            EmpreClienList?.clear()
                            for (i in 0 until array.length()) {
                                val objectArtist = array.getJSONObject(i)
                                val empreClien = EmpreClien(
                                    objectArtist.getInt("id"),
                                    objectArtist.getString("nombre"),
                                    objectArtist.getString("activo")
                                )
                                EmpreClienList!!.add(empreClien)
                            }
                            val adapter =
                                EmprClieAdaptador(
                                    (EmpreClienList as java.util.ArrayList<EmpreClien>?)!!,
                                    this.activity!!, this
                                )
                            EmpreClien?.adapter = adapter
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
                    params["accion"] = "EmpreClien"
                    params["IdAct"] = IdAct.toString()
                    params["IdTipoUsu"] = IdTipoUsu.toString()
                    params["usu"] = usuario.toString()
                    return params
                }
            }
            VolleySingleton.instance?.addToRequestQueue(stringRequest)
        } else {
            Toast.makeText(activity, "Seleccione tipos o estados", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.id) {
            R.id.tipousu -> {
                IdTipoUsu = tipousuList!![position].id
                LlenarEmpreClien()
            }
            R.id.activo -> {
                IdAct = tiposActivoList!![position].id
                LlenarEmpreClien()
            }
        }
    }
}
