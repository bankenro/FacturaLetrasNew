package com.globaltics.facturaletrasnew.Clases.Views

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import com.globaltics.facturaletrasnew.Clases.Modelos.Letras
import com.globaltics.facturaletrasnew.Fragments.DetallesFacturaFragment
import com.globaltics.facturaletrasnew.Fragments.DialogsFragment.AddLetraDF
import com.globaltics.facturaletrasnew.Fragments.DialogsFragment.PagoLetrasDF
import com.globaltics.facturaletrasnew.R
import java.util.*

class LetrasAdaptador(
    val letrasList: ArrayList<Letras>,
    val context: Context,
    val detallesFacturaFragment: DetallesFacturaFragment,
    val factura: String
) : RecyclerView.Adapter<LetrasAdaptador.ViewHolder>() {

    private var preferences: SharedPreferences? = null
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_letra, parent, false)
        preferences = context.getSharedPreferences("FactLetraGTs", Context.MODE_PRIVATE)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return letrasList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(letrasList[position])
        val tipou = preferences?.getString("idt", "")
        if (Objects.equals(letrasList[position].estado, 1)) {
            holder.estado.setTextColor(ContextCompat.getColor(context, R.color.rojo))
        } else {
            holder.estado.setTextColor(ContextCompat.getColor(context, R.color.verde))
        }

        val bundle = Bundle()
        if (Objects.equals(tipou, "supersu")) {
            holder.menu.visibility = View.VISIBLE
            holder.menu.setOnClickListener { v ->
                val popupMenu = PopupMenu(context, v)
                popupMenu.menuInflater.inflate(R.menu.menu_letras, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.editar -> {
                            val dialog = AddLetraDF()
                            dialog.setTargetFragment(detallesFacturaFragment, 1)
                            val ft = (context as FragmentActivity).supportFragmentManager.beginTransaction()
                            bundle.putString("accion", "act_letra")
                            bundle.putString("numero","1")
                            bundle.putString("id", factura)
                            bundle.putString("nombre", letrasList[position].letra)
                            bundle.putString("monto", letrasList[position].monto)
                            dialog.arguments = bundle
                            dialog.isCancelable = false
                            dialog.show(ft, "Actualizar Letra")
                        }
                        R.id.pagar -> {
                            val dialog = PagoLetrasDF()
                            dialog.setTargetFragment(detallesFacturaFragment, 1)
                            val ft = (context as FragmentActivity).supportFragmentManager.beginTransaction()
                            bundle.putString("id", factura)
                            bundle.putString("nombre", letrasList[position].letra)
                            bundle.putString("monto", letrasList[position].monto)
                            dialog.arguments = bundle
                            dialog.isCancelable = false
                            dialog.show(ft, "Pagar Letra")
                        }
                    }
                    false
                }
                popupMenu.show()
            }
        }
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val letra = v.findViewById(R.id.nombre) as TextView
        val factura = v.findViewById(R.id.factura) as TextView
        val empresa = v.findViewById(R.id.empresa) as TextView
        val fecha = v.findViewById(R.id.fecha) as TextView
        val monto = v.findViewById(R.id.monto) as TextView
        val estado = v.findViewById(R.id.estado) as TextView
        val menu = v.findViewById(R.id.menu) as ImageButton
        fun bindItems(letras: Letras) {
            letra.text = letras.letra
            factura.text = letras.factura
            empresa.text = letras.empresa
            fecha.text = letras.fecha
            monto.text = letras.monto
            estado.text = letras.estado
        }

    }
}