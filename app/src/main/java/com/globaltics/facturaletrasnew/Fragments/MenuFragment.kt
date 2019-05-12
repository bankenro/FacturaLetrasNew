package com.globaltics.facturaletrasnew.Fragments


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.opengl.Visibility
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.globaltics.facturaletrasnew.Activitys.MainActivity
import com.globaltics.facturaletrasnew.Fragments.DialogsFragment.AddFacturaDF
import com.globaltics.facturaletrasnew.Fragments.DialogsFragment.AddLetraDF

import com.globaltics.facturaletrasnew.R
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class MenuFragment : Fragment(), View.OnClickListener {

    private var addfactura: ImageButton? = null
    private var facturas: ImageButton? = null
    private var reportes: ImageButton? = null
    private var registro: ImageButton? = null
    private var salir: ImageButton? = null
    private var fragment: Fragment? = null
    private var preferences: SharedPreferences? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        preferences = activity?.getSharedPreferences("FactLetraGTs",Context.MODE_PRIVATE)

        addfactura = view.findViewById(R.id.addfactura)
        facturas = view.findViewById(R.id.facturas)
        reportes = view.findViewById(R.id.reportes)
        registro = view.findViewById(R.id.registro)
        salir = view.findViewById(R.id.salir)

        val idt = preferences?.getString("idt","")
        if (Objects.equals(idt,"supersu")){
            addfactura?.visibility = View.VISIBLE
            reportes?.visibility = View.VISIBLE
            registro?.visibility = View.VISIBLE
        }

        addfactura?.setOnClickListener(this)
        facturas?.setOnClickListener(this)
        reportes?.setOnClickListener(this)
        registro?.setOnClickListener(this)
        salir?.setOnClickListener(this)
        return view
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.addfactura -> {
                val bundle = Bundle()
                val dialog = AddFacturaDF()
                dialog.setTargetFragment(this, 1)
                val ft = activity?.supportFragmentManager?.beginTransaction()
                bundle.putString("accion", "add_fact")
                dialog.arguments = bundle
                dialog.isCancelable = false
                dialog.show(ft, "Registrar Factura")
            }
            R.id.facturas -> {
                fragment = FacturasFragment()
                CambiarFragment(fragment as FacturasFragment)
            }
            R.id.reportes -> {
                fragment = ReportesFragment()
                CambiarFragment(fragment as ReportesFragment)
            }
            R.id.registro -> {
                fragment = AddUsuarioFragment()
                CambiarFragment(fragment as AddUsuarioFragment)
            }
            R.id.salir -> CerrarSesion()
        }
    }

    private fun CambiarFragment(fragment: Fragment) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.contenedor,fragment)?.commit()
    }

    private fun CerrarSesion() {
        val edit = preferences?.edit()
        edit?.clear()?.apply()
        activity?.finishAffinity()
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
    }
}
