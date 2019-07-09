package com.globaltics.facturaletrasnew.Clases.Views

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.globaltics.facturaletrasnew.Clases.Modelos.EmpreClien
import com.globaltics.facturaletrasnew.Fragments.DialogsFragment.ActUsuDF
import com.globaltics.facturaletrasnew.R
import java.util.*

class EmprClieAdaptador(
    val EmpreClienList: ArrayList<EmpreClien>,
    val context: Context,
    val EmpresasClientesFragment: Fragment
) : RecyclerView.Adapter<EmprClieAdaptador.ViewHolder>() {
    private var preferences: SharedPreferences? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_empresas_clientes, parent, false)
        preferences = context.getSharedPreferences("FactLetraGTs", Context.MODE_PRIVATE)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return EmpreClienList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(EmpreClienList[position])
        val tipou = preferences?.getString("idt", "")
        val bundle = Bundle()
        holder.menu.visibility = View.GONE
        if (Objects.equals(tipou, "supersu")) {
            if (Objects.equals(EmpreClienList[position].activo,"INACTIVO")){
                holder.activo.setTextColor(ContextCompat.getColor(context, R.color.rojo))
            }else{
                holder.activo.setTextColor(ContextCompat.getColor(context, R.color.verde))
            }
            holder.menu.visibility = View.VISIBLE
            holder.menu.setOnClickListener { v ->
                val popupMenu = PopupMenu(context, v)
                popupMenu.menuInflater.inflate(R.menu.menu_editar, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.editar -> {
                            val dialog = ActUsuDF()
                            dialog.setTargetFragment(EmpresasClientesFragment, 1)
                            val ft = (context as FragmentActivity).supportFragmentManager.beginTransaction()
                            bundle.putInt("id", EmpreClienList[position].id)
                            bundle.putString("nombre", EmpreClienList[position].nombre)
                            bundle.putString("activo", EmpreClienList[position].activo)
                            dialog.arguments = bundle
                            dialog.isCancelable = false
                            dialog.show(ft, "Actualizar Estado")
                        }
                    }
                    false
                }
                popupMenu.show()
            }
        }
    }
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val textEmprClie1 = v.findViewById(R.id.textEmprClie1) as TextView
        val activo = v.findViewById(R.id.activo) as TextView
        val menu = v.findViewById(R.id.menu) as ImageButton
        fun bindItems(empreClien: EmpreClien) {
            textEmprClie1.text = empreClien.nombre
            activo.text = empreClien.activo
        }

    }
}