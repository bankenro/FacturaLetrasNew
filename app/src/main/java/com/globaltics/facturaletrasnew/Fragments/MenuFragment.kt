package com.globaltics.facturaletrasnew.Fragments


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.FragmentActivity
import com.globaltics.facturaletrasnew.Activitys.MainActivity
import com.globaltics.facturaletrasnew.Fragments.DialogsFragment.AddCodeVerifDF
import com.globaltics.facturaletrasnew.Fragments.DialogsFragment.AddFacturaDF
import com.globaltics.facturaletrasnew.Fragments.DialogsFragment.ElimFactLetrDF

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
    private var empreclie: ImageButton? = null
    private var actcod: ImageButton? = null
    private var letras: ImageButton? = null
    private var salir: ImageButton? = null
    private var fragment: Fragment? = null
    private var preferences: SharedPreferences? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        preferences = activity?.getSharedPreferences("FactLetraGTs", Context.MODE_PRIVATE)

        addfactura = view.findViewById(R.id.addfactura)
        facturas = view.findViewById(R.id.facturas)
        letras = view.findViewById(R.id.letras)
        reportes = view.findViewById(R.id.reportes)
        registro = view.findViewById(R.id.registro)
        empreclie = view.findViewById(R.id.empreclie)
        actcod = view.findViewById(R.id.actcod)
        salir = view.findViewById(R.id.salir)

        val idt = preferences?.getString("idt", "")
        val code = preferences?.getInt("code",1234)

        if (Objects.equals(code,1234)){
            val bundle = Bundle()
            val dialog = AddCodeVerifDF()
            dialog.setTargetFragment(this, 1)
            val ft = (context as FragmentActivity).supportFragmentManager.beginTransaction()
            bundle.putString("condicion","regcod")
            dialog.arguments = bundle
            dialog.isCancelable = false
            dialog.show(ft, "Crear Codigo Verificacion")
        }

        if (Objects.equals(idt, "supersu")) {
            addfactura?.visibility = View.VISIBLE
            reportes?.visibility = View.VISIBLE
            registro?.visibility = View.VISIBLE
            empreclie?.visibility = View.VISIBLE
            actcod?.visibility = View.VISIBLE
        } else {
            addfactura?.visibility = View.GONE
            reportes?.visibility = View.GONE
            registro?.visibility = View.GONE
            empreclie?.visibility = View.GONE
            actcod?.visibility = View.GONE
        }

        addfactura?.setOnClickListener(this)
        facturas?.setOnClickListener(this)
        reportes?.setOnClickListener(this)
        registro?.setOnClickListener(this)
        salir?.setOnClickListener(this)
        letras?.setOnClickListener(this)
        empreclie?.setOnClickListener(this)
        actcod?.setOnClickListener(this)
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
            R.id.letras -> {
                fragment = LetrasFragment()
                CambiarFragment(fragment as LetrasFragment)
            }
            R.id.reportes -> {
                fragment = ReportesFragment()
                CambiarFragment(fragment as ReportesFragment)
            }
            R.id.registro -> {
                fragment = AddUsuarioFragment()
                CambiarFragment(fragment as AddUsuarioFragment)
            }
            R.id.empreclie -> {
                fragment = EmpresasClientesFragment()
                CambiarFragment(fragment as EmpresasClientesFragment)
            }
            R.id.actcod->{
                val bundle = Bundle()
                val dialog = AddCodeVerifDF()
                dialog.setTargetFragment(this, 1)
                val ft = (context as FragmentActivity).supportFragmentManager.beginTransaction()
                bundle.putString("condicion","actcod")
                dialog.arguments = bundle
                dialog.isCancelable = false
                dialog.show(ft, "Actualizar Codigo Verificacion")
            }
            R.id.salir -> CerrarSesion()
        }
    }

    private fun CambiarFragment(fragment: Fragment) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.contenedor, fragment)?.addToBackStack(null)?.commit()
    }

    private fun CerrarSesion() {
        val edit = preferences?.edit()
        edit?.clear()?.apply()
        activity?.finishAffinity()
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
    }
}
