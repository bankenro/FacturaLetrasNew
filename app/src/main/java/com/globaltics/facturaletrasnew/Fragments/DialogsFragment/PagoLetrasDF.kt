package com.globaltics.facturaletrasnew.Fragments.DialogsFragment


import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
import java.io.ByteArrayOutputStream
import java.util.*

class PagoLetrasDF : DialogFragment(), View.OnClickListener {

    private var imagen: ImageView? = null
    private var letra: TextView? = null
    private var fecha: TextView? = null
    private var confirmar: EditText? = null
    private var registrar: Button? = null
    private var cancelar: Button? = null
    private var factura: String? = null
    private var moneda: TextView? = null
    private var foto: ImageView?=null
    private var descripcion: EditText?=null
    private var GALLERY_PICTURE = 0
    private val REQUEST_IMAGE_CAPTURE = 1
    private var bitmap: Bitmap? = null
    private var foto1 = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pago_letras_d, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imagen = view.findViewById(R.id.imagen)
        letra = view.findViewById(R.id.letra)
        fecha = view.findViewById(R.id.fecha)
        moneda = view.findViewById(R.id.moneda)
        confirmar = view.findViewById(R.id.confirmar)
        registrar = view.findViewById(R.id.registrar)
        cancelar = view.findViewById(R.id.cancelar)
        foto = view.findViewById(R.id.foto)
        descripcion = view.findViewById(R.id.descripcion)

        if (arguments != null) {
            factura = arguments?.get("id").toString()
            letra?.text = arguments?.get("letra").toString()
            fecha?.text = arguments?.get("fecha").toString()
            moneda?.text = arguments?.get("moneda").toString()
            registrar?.setOnClickListener(this)
        }
        cancelar?.setOnClickListener(this)
        foto?.setOnClickListener(this)
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
        when (v?.id) {
            R.id.registrar -> Comprobar()
            R.id.cancelar -> dialog.dismiss()
            R.id.foto -> {
                val builder = AlertDialog.Builder(context, R.style.MyDialogTheme)
                builder.setTitle("ELEGIR FOTO O TOMAR FOTO")
                    .setMessage(("ELIJA UNA OPCIÓN"))
                    .setPositiveButton(
                        "ELEGIR FOTO"
                    ) { _, _ ->
                        val permisos = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        ActivityCompat.requestPermissions(activity!!, permisos, GALLERY_PICTURE)
                        if (ActivityCompat.checkSelfPermission(
                                activity!!,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                            == PackageManager.PERMISSION_GRANTED
                        ) {
                            val intent = Intent(Intent.ACTION_PICK)
                            intent.type = "image/*"
                            startActivityForResult(Intent.createChooser(intent, "seleccionar Imagen"), GALLERY_PICTURE)
                        } else {
                            Toast.makeText(activity!!, "Active los permisos de almacenamiento", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    .setNegativeButton(
                        "TOMAR FOTO"
                    ) { _, _ ->
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                        if (intent.resolveActivity(activity!!.packageManager) != null) {
                            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
                        }
                    }
                builder.show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == GALLERY_PICTURE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(Intent.createChooser(intent, "seleccionar Imagen"), GALLERY_PICTURE)
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val bundle = data!!.extras!!
            bitmap = bundle.get("data") as Bitmap
            foto!!.setImageBitmap(bitmap)
        }
        if (requestCode == GALLERY_PICTURE && resultCode == Activity.RESULT_OK && data != null) {
            val filepath = data.data
            val inpuStream = activity!!.contentResolver.openInputStream(filepath!!)
            bitmap = BitmapFactory.decodeStream(inpuStream)
            foto!!.setImageBitmap(bitmap)
        }
    }

    private fun Comprobar() {
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(foto!!.width, foto!!.height, Bitmap.Config.RGB_565)
            /*val canvas = Canvas(bitmap!!)
            foto!!.draw(canvas)*/
        }
        foto1 = convertirfoto(bitmap!!)
        if (descripcion!!.text.trim().isNotEmpty() && foto1.isNotEmpty()){
            val confirmarStr = confirmar?.text.toString().trim()
            if (Objects.equals(confirmarStr, "CONFIRMAR")) {
                Registrar(foto1,descripcion!!.text.toString().trim())
            } else {
                Toast.makeText(activity, "Porfavor escriba CONFIRMAR", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(activity, "Conplete los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun convertirfoto(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream)
        val byteFormat = stream.toByteArray()
        return Base64.encodeToString(byteFormat, Base64.NO_WRAP)
    }

    private fun Registrar(foto1: String, descripcion: String) {
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
                        dialog.dismiss()
                        Toast.makeText(activity, obj.getString("mensaje"), Toast.LENGTH_LONG).show()
                        try {
                            (targetFragment as ActualizarRecyclerViews).ActuRecy()
                        } catch (e: ClassCastException) {
                            e.printStackTrace()
                        }
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
                params["accion"] = "pago"
                params["factura"] = factura!!
                params["letra"] = letra?.text.toString()
                params["foto"] = foto1
                params["descripcion"] = descripcion
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(request)
    }
}
