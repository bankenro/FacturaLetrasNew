package com.globaltics.facturaletrasnew.Fragments


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.globaltics.facturaletrasnew.Clases.ActualizarRecyclerViews
import com.globaltics.facturaletrasnew.Clases.EndPoints
import com.globaltics.facturaletrasnew.Clases.Modelos.Facturas
import com.globaltics.facturaletrasnew.Clases.Views.FacturasAdaptador
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
class FacturasFragment : Fragment(), ActualizarRecyclerViews {
    override fun ActuRecy() {
        LlenarFacturas()
    }

    private var preferences: SharedPreferences? = null
    private var usuario: Int? = null
    private var facturas: RecyclerView? = null
    private var facturasList: MutableList<Facturas>? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_facturas, container, false)

        facturas = view.findViewById(R.id.facturas)

        preferences = activity?.getSharedPreferences("FactLetraGTs", Context.MODE_PRIVATE)
        usuario = preferences?.getInt("id", 0)

        facturasList = ArrayList()
        facturas?.setHasFixedSize(true)
        facturas?.itemAnimator = null
        facturas?.layoutManager = LinearLayoutManager(activity)
        facturas?.addItemDecoration(
            DividerItemDecoration(
                activity,
                DividerItemDecoration.VERTICAL
            )
        )

        LlenarFacturas()

        return view
    }

    private fun LlenarFacturas() {
        facturas?.adapter = null
        val stringRequest = object : StringRequest(
            Method.POST, EndPoints.URL_ROOT,
            Response.Listener<String> { response ->
                try {
                    val obj = JSONObject(response)
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                        val array = obj.getJSONArray("facturas")
                        facturasList?.clear()
                        for (i in 0 until array.length()) {
                            val objectArtist = array.getJSONObject(i)
                            val facturas = Facturas(
                                objectArtist.getString("factura"),
                                objectArtist.getString("empresa"),
                                objectArtist.getString("cliente"),
                                objectArtist.getString("monto"),
                                objectArtist.getInt("pagados"),
                                objectArtist.getInt("debidos"),
                                objectArtist.getString("pagado"),
                                objectArtist.getString("debido"),
                                objectArtist.getInt("numero"),
                                objectArtist.getString("banco"),
                                objectArtist.getString("moneda")
                            )
                            facturasList!!.add(facturas)
                        }
                        val adapter =
                            FacturasAdaptador(
                                (facturasList as java.util.ArrayList<Facturas>?)!!,
                                this.activity!!, this
                            )
                        facturas?.adapter = adapter
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
                params["accion"] = "facturas"
                params["usu"] = usuario.toString()
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }

}
