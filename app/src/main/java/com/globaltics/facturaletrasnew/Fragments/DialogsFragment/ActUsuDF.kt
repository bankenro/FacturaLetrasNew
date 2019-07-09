package com.globaltics.facturaletrasnew.Fragments.DialogsFragment


import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
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
class ActUsuDF : DialogFragment(), View.OnClickListener, AdapterView.OnItemSelectedListener {

    private var textEmprClie1: TextView? = null
    private var activo: TextView? = null
    private var estadoActi: Spinner? = null
    private var actualizar: Button? = null
    private var estadoActiList: MutableList<Tipos>? = null
    private var IdActi: Int? = 0
    private var IdEmpr: Int? = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_act_usu_d, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textEmprClie1 = view.findViewById(R.id.textEmprClie1)
        activo = view.findViewById(R.id.activo)
        estadoActi = view.findViewById(R.id.estado)
        actualizar = view.findViewById(R.id.actualizar)

        estadoActiList = ArrayList()

        if (arguments != null) {
            IdEmpr = arguments?.getInt("id", 0)
            textEmprClie1?.text = arguments?.get("nombre").toString()
            activo?.text = arguments?.get("activo").toString()
        }

        LlenarTiposActivo()

        estadoActi?.onItemSelectedListener = this
        actualizar?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.actualizar -> Comprobar()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.id) {
            R.id.estado -> IdActi = estadoActiList!![position].id
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window!!.setLayout(width, height)
        }
    }

    private fun LlenarTiposActivo() {
        estadoActi?.adapter = null
        val stringRequest = object : StringRequest(
            Method.POST, EndPoints.URL_ROOT,
            Response.Listener<String> { response ->
                try {
                    val obj = JSONObject(response)
                    if (!obj.getBoolean("error")) {
                        //Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                        val array = obj.getJSONArray("tactivo")
                        estadoActiList?.clear()
                        for (i in 0 until array.length()) {
                            val objectArtist = array.getJSONObject(i)
                            val tipos = Tipos(
                                objectArtist.getInt("id"),
                                objectArtist.getString("nombre")
                            )
                            estadoActiList?.add(tipos)
                        }
                        val adapter = TiposAdaptador(
                            this.activity!!,
                            R.layout.item_spinner,
                            this.estadoActiList!!
                        )
                        estadoActi?.adapter = adapter
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

    private fun Comprobar() {
        if (IdActi != 0 && IdEmpr != 0) {
            Registrar(IdActi, IdEmpr)
        } else {
            Toast.makeText(activity, "Escoja un estado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun Registrar(idActi: Int?, idEmpr: Int?) {
        val dialogA: AlertDialog =
            SpotsDialog.Builder().setContext(activity).setMessage(R.string.app_name).setCancelable(false).build()
        dialogA.show()
        val request = object : StringRequest(
            Method.POST, EndPoints.URL_ROOT,
            Response.Listener { response ->
                //Log.e("Error", response)
                try {
                    val obj = JSONObject(response)
                    if (!obj.getBoolean("error")) {
                        dialogA.dismiss()
                        try {
                            (targetFragment as ActualizarRecyclerViews).ActuRecy()
                        } catch (e: ClassCastException) {
                            e.printStackTrace()
                        }
                        dialog.dismiss()
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
                params["accion"] = "ActuEstaActiUsu"
                params["idempr"] = idEmpr.toString()
                params["idest"] = idActi.toString()
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(request)
    }
}
