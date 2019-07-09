package com.globaltics.facturaletrasnew.Fragments.DialogsFragment


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.globaltics.facturaletrasnew.Clases.EndPoints
import com.globaltics.facturaletrasnew.Clases.VolleySingleton
import com.globaltics.facturaletrasnew.Fragments.MenuFragment

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
class AddCodeVerifDF : DialogFragment(), View.OnClickListener {

    private var titulo: TextView? = null
    private var codverant: EditText? = null
    private var codvernew0: EditText? = null
    private var codvernew1: EditText? = null
    private var registrar: Button? = null
    private var cancelar: Button? = null
    private var condicion: String? = ""
    private var code: Int? = 1234
    private var usuario: Int? = 0
    private var preferences: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_code_verif_d, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titulo = view.findViewById(R.id.titulo)
        codverant = view.findViewById(R.id.codverant)
        codvernew0 = view.findViewById(R.id.codvernew0)
        codvernew1 = view.findViewById(R.id.codvernew1)
        registrar = view.findViewById(R.id.registrar)
        cancelar = view.findViewById(R.id.cancelar)

        preferences = activity?.getSharedPreferences("FactLetraGTs", Context.MODE_PRIVATE)
        usuario = preferences?.getInt("id", 0)
        code = preferences?.getInt("code", 1234)

        condicion = arguments?.getString("condicion", "")
        if (Objects.equals(condicion, "regcod")) {
            titulo?.text = "Registro de codigo de verificacion"
            codverant?.visibility = View.GONE
        } else {
            titulo?.text = "Actualizar codigo de verificacion"
        }
        registrar?.setOnClickListener(this)
        cancelar?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.registrar -> Comprobar()
            R.id.cancelar -> dialog.dismiss()
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

    private fun Comprobar() {
        if (Objects.equals(condicion, "regcod")) {
            if (codvernew0?.text!!.isNotEmpty() && codvernew1?.text!!.isNotEmpty()) {
                val Strcodvernew0 = codvernew0?.text.toString().trim().toInt()
                val Strcodvernew1 = codvernew1?.text.toString().trim().toInt()
                if (Objects.equals(Strcodvernew0, Strcodvernew1) && !Objects.equals(Strcodvernew0, 1234)) {
                    Registrar(code!!, Strcodvernew0, usuario!!)
                } else {
                    Toast.makeText(
                        activity,
                        "Las contraseñas no coinciden o son igual a 1234, por favor cambie a otra",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(activity, "Rellene todos los campos", Toast.LENGTH_SHORT).show()
            }

        } else {
            val Strcodverant = codverant?.text.toString().trim().toInt()
            if (Objects.equals(code, Strcodverant)) {
                if (codvernew0?.text!!.isNotEmpty() && codvernew1?.text!!.isNotEmpty()) {
                    val Strcodvernew0 = codvernew0?.text.toString().trim().toInt()
                    val Strcodvernew1 = codvernew1?.text.toString().trim().toInt()
                    if (Objects.equals(Strcodvernew0, Strcodvernew1) && !Objects.equals(Strcodvernew0, 1234)) {
                        Registrar(Strcodverant, Strcodvernew0, usuario!!)
                    } else {
                        Toast.makeText(
                            activity,
                            "Las contraseñas no coinciden o son igual a 1234, por favor cambie a otra",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(activity, "Rellene todos los campos", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(
                    activity,
                    "Las contraseña anterior no coincide", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun Registrar(code: Int, codenew: Int, usuario: Int) {
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

                        val editor = preferences?.edit()
                        editor?.remove("code")?.apply()
                        editor?.putInt("code", codenew)
                        editor?.apply()
                        editor?.commit()

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
                params["accion"] = "actcod"
                params["code"] = code.toString()
                params["codenew"] = codenew.toString()
                params["usuario"] = usuario.toString()
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(request)
    }
}
