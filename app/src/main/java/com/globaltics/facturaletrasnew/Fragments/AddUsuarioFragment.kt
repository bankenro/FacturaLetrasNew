package com.globaltics.facturaletrasnew.Fragments


import android.app.AlertDialog
import android.os.Bundle
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
import java.util.HashMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class AddUsuarioFragment : Fragment(), View.OnClickListener, AdapterView.OnItemSelectedListener {

    private var id: EditText? = null
    private var nombre: EditText? = null
    private var password: EditText? = null
    private var tipousu: Spinner? = null
    private var registrar: Button? = null
    private var tipousuList: MutableList<Tipos>? = null
    private var tid: Int? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_usuario, container, false)

        id = view.findViewById(R.id.id)
        nombre = view.findViewById(R.id.nombre)
        password = view.findViewById(R.id.password)
        tipousu = view.findViewById(R.id.tipousu)
        registrar = view.findViewById(R.id.registrar)

        tipousuList = ArrayList()

        registrar?.setOnClickListener(this)
        tipousu?.onItemSelectedListener = this

        LlenarTipousu()

        return view
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

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.id) {
            R.id.tipousu -> tid = tipousuList!![position].id
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.registrar -> ComprobarDatos()
        }
    }

    private fun ComprobarDatos() {
        val nombreStr = nombre?.text.toString().trim()
        val idStr = id?.text.toString().trim()
        val passwordStr = password?.text.toString().trim()
        if (nombreStr.isNotEmpty() && idStr.isNotEmpty() && passwordStr.isNotEmpty() && tid != null){
            RegistrarUsuario(nombreStr,idStr,passwordStr, tid!!)
        }else{
            Toast.makeText(activity,"Rellene todos los campos",Toast.LENGTH_SHORT).show()
        }
    }

    private fun RegistrarUsuario(nombreStr: String, idStr: String, passwordStr: String, tid: Int) {
        val dialog: AlertDialog =
            SpotsDialog.Builder().setContext(activity).setMessage(R.string.app_name).setCancelable(false).build()
        dialog.show()
        val request = object : StringRequest(
            Method.POST, EndPoints.URL_ROOT,
            Response.Listener { response ->
                try {
                    val obj = JSONObject(response)
                    if (!obj.getBoolean("error")) {
                        dialog.dismiss()
                        Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    dialog.dismiss()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(activity, error?.message, Toast.LENGTH_LONG).show()
                dialog.dismiss()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["accion"] = "registrar"
                params["id"] = idStr
                params["password"] = passwordStr
                params["nombre"] = nombreStr
                params["tid"] = tid.toString()
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(request)
    }

}
