package com.globaltics.facturaletrasnew.Fragments


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.globaltics.facturaletrasnew.Clases.ActualizarRecyclerViews
import com.globaltics.facturaletrasnew.Clases.EndPoints
import com.globaltics.facturaletrasnew.Clases.Modelos.Letras
import com.globaltics.facturaletrasnew.Clases.Modelos.Tipos
import com.globaltics.facturaletrasnew.Clases.Views.LetrasAdaptador
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
class LetrasFragment : Fragment(), AdapterView.OnItemSelectedListener, ActualizarRecyclerViews {
    override fun ActuRecy() {
        LlenarLetras(1)
    }

    private var preferences: SharedPreferences? = null
    private var usuario: Int? = null
    private var estados: Spinner? = null
    private var letras: RecyclerView? = null
    private var letrasList: MutableList<Letras>? = null
    private var estadosList: MutableList<Tipos>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_letras, container, false)

        estados = view.findViewById(R.id.estados)
        letras = view.findViewById(R.id.letras)

        preferences = activity?.getSharedPreferences("FactLetraGTs", Context.MODE_PRIVATE)
        usuario = preferences?.getInt("id", 0)

        letrasList = ArrayList()
        estadosList = ArrayList()
        letras?.setHasFixedSize(true)
        letras?.itemAnimator = null
        letras?.layoutManager = LinearLayoutManager(activity)
        letras?.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

        LlenarEstados()

        estados?.onItemSelectedListener = this

        return view
    }

    private fun LlenarEstados() {
        estados?.adapter = null
        val stringRequest = object : StringRequest(
            Method.POST, EndPoints.URL_ROOT,
            Response.Listener<String> { response ->
                try {
                    val obj = JSONObject(response)
                    if (!obj.getBoolean("error")) {
                        //Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                        val array = obj.getJSONArray("estados")
                        estadosList?.clear()
                        for (i in 0 until array.length()) {
                            val objectArtist = array.getJSONObject(i)
                            val tipos = Tipos(
                                objectArtist.getInt("id"),
                                objectArtist.getString("nombre")
                            )
                            estadosList?.add(tipos)
                        }
                        val adapter = TiposAdaptador(
                            this.activity!!,
                            R.layout.item_spinner,
                            this.estadosList!!
                        )
                        estados?.adapter = adapter
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
                params["accion"] = "estados"
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.id) {
            R.id.estados -> LlenarLetras(estadosList!![position].id)
        }
    }

    private fun LlenarLetras(id: Int) {
        letras?.adapter = null
        val stringRequest = object : StringRequest(
            Method.POST, EndPoints.URL_ROOT,
            Response.Listener<String> { response ->
                try {
                    val obj = JSONObject(response)
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                        val array = obj.getJSONArray("letras")
                        letrasList?.clear()
                        for (i in 0 until array.length()) {
                            val objectArtist = array.getJSONObject(i)
                            val letras = Letras(
                                objectArtist.getString("letra"),
                                objectArtist.getString("factura"),
                                objectArtist.getString("empresa"),
                                objectArtist.getString("monto"),
                                objectArtist.getString("fecha"),
                                objectArtist.getString("estado"),
                                objectArtist.getString("moneda"),
                                objectArtist.getString("descripcion"),
                                objectArtist.getString("imagen")
                            )
                            letrasList?.add(letras)
                        }
                        //try {
                        val adapter =
                            LetrasAdaptador(
                                (letrasList as java.util.ArrayList<Letras>?)!!,
                                this.activity!!, this
                            )
                        letras?.adapter = adapter
                        /*} catch (e: Exception) {
                            e.printStackTrace()
                        }*/
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
                params["accion"] = "letrasAll"
                params["estado"] = id.toString()
                params["usu"] = usuario.toString()
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }
}
