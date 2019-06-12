package com.globaltics.facturaletrasnew.Fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.globaltics.facturaletrasnew.Clases.ActualizarRecyclerViews
import com.globaltics.facturaletrasnew.Clases.EndPoints
import com.globaltics.facturaletrasnew.Clases.Modelos.Letras
import com.globaltics.facturaletrasnew.Clases.Views.LetrasAdaptador
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
class DetallesFacturaFragment : Fragment(),ActualizarRecyclerViews {
    override fun ActuRecy() {
        LlenarLetras()
    }

    private var imagen: ImageView? = null
    private var factura: TextView? = null
    private var nombre: TextView? = null
    private var pagados: TextView? = null
    private var debidos: TextView? = null
    private var letras: RecyclerView? = null
    private var letrasList: MutableList<Letras>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_detalles_factura, container, false)

        imagen = view.findViewById(R.id.imagen)
        factura = view.findViewById(R.id.factura)
        nombre = view.findViewById(R.id.nombre)
        pagados = view.findViewById(R.id.pagados)
        debidos = view.findViewById(R.id.debidos)
        letras = view.findViewById(R.id.letras)
        letrasList = ArrayList()
        letras?.setHasFixedSize(true)
        letras?.itemAnimator = null
        letras?.layoutManager = LinearLayoutManager(activity)
        letras?.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

        if (arguments != null) {
            factura?.text = arguments?.get("factura").toString()
            nombre?.text = arguments?.get("empresa").toString()
            pagados?.text = arguments?.get("pagados").toString()
            debidos?.text = arguments?.get("debidos").toString()
            LlenarLetras()
        }

        return view
    }

    private fun LlenarLetras() {
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
                                    this.activity!!, this, factura?.text.toString()
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
                params["accion"] = "letras"
                params["factura"] = factura?.text.toString()
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }
}
