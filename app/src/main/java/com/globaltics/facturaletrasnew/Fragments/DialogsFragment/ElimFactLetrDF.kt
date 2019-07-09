package com.globaltics.facturaletrasnew.Fragments.DialogsFragment


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
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
import com.globaltics.facturaletrasnew.Clases.VolleySingleton

import com.globaltics.facturaletrasnew.R
import dmax.dialog.SpotsDialog
import org.json.JSONException
import org.json.JSONObject
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ElimFactLetrDF : DialogFragment(), View.OnClickListener {

    private var preferences: SharedPreferences? = null
    private var usuario: Int? = 0
    private var titulo: TextView? = null
    private var factletr: TextView? = null
    private var nombre: TextView? = null
    private var codigo: EditText? = null
    private var eliminar: Button? = null
    private var cancelar: Button? = null
    private var condicion: String? = ""
    private var id: String? = ""
    private var code: Int? = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_elim_fact_letr_d, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferences = activity?.getSharedPreferences("FactLetraGTs", Context.MODE_PRIVATE)
        usuario = preferences?.getInt("id", 0)
        code = preferences?.getInt("code",1234)

        titulo = view.findViewById(R.id.titulo)
        factletr = view.findViewById(R.id.factletr)
        nombre = view.findViewById(R.id.nombre)
        codigo = view.findViewById(R.id.codigo)
        eliminar = view.findViewById(R.id.eliminar)
        cancelar = view.findViewById(R.id.cancelar)

        if (arguments != null) {
            condicion = arguments?.getString("condicion", "")
            if (Objects.equals(condicion, "elim_letr")) {
                titulo?.text = "Eliminar Letra"
            } else {
                titulo?.text = "Eliminar Factura"
            }
            id = arguments?.getString("id","")
            nombre?.text = arguments?.getString("factletr", "")
        }
        eliminar?.setOnClickListener(this)
        cancelar?.setOnClickListener(this)
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

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.eliminar->Comprobar()
            R.id.cancelar->dialog.dismiss()
        }
    }

    private fun Comprobar() {
        val Intcode = codigo?.text.toString().trim().toInt()
        if (Objects.equals(Intcode,code)){
            Eliminar()
        }else{
            Toast.makeText(activity,"Codigo de verificacion incorrecto",Toast.LENGTH_SHORT).show()
        }
    }

    private fun Eliminar() {
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
                params["accion"] = "elim_factlet"
                params["id"] = id!!
                params["usuario"] = usuario.toString()
                params["code"] = code.toString()
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(request)
    }

}
