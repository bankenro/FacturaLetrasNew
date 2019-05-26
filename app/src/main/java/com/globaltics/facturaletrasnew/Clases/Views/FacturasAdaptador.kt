package com.globaltics.facturaletrasnew.Clases.Views

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import com.globaltics.facturaletrasnew.Clases.Modelos.Facturas
import com.globaltics.facturaletrasnew.Clases.Modelos.Letras
import com.globaltics.facturaletrasnew.Fragments.DetallesFacturaFragment
import com.globaltics.facturaletrasnew.Fragments.DialogsFragment.AddFacturaDF
import com.globaltics.facturaletrasnew.Fragments.DialogsFragment.AddLetraDF
import com.globaltics.facturaletrasnew.Fragments.DialogsFragment.PagoLetrasDF
import com.globaltics.facturaletrasnew.Fragments.FacturasFragment
import com.globaltics.facturaletrasnew.R
import java.util.*

class FacturasAdaptador(
    val facturasList: ArrayList<Facturas>,
    val context: Context,
    val facturasFragment: FacturasFragment
) : RecyclerView.Adapter<FacturasAdaptador.ViewHolder>() {
    private var preferences: SharedPreferences? = null
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_factura, parent, false)
        preferences = context.getSharedPreferences("FactLetraGTs", Context.MODE_PRIVATE)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return facturasList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(facturasList[position])
        val tipou = preferences?.getString("idt", "")
        val bundle = Bundle()
        holder.menu.visibility = View.GONE
        if (Objects.equals(tipou, "supersu")) {
            holder.cardFactura.setOnClickListener {
                val fragment = DetallesFacturaFragment()
                val fragmentManager = (context as FragmentActivity).supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.contenedor, fragment).addToBackStack(null)
                bundle.putString("factura", facturasList[position].factura)
                bundle.putString("empresa", facturasList[position].empresa)
                bundle.putString("pagados", facturasList[position].pagados.toString())
                bundle.putString("debidos", facturasList[position].debidos.toString())
                fragment.arguments = bundle
                fragmentTransaction.commit()
            }
            holder.menu.visibility = View.VISIBLE
            holder.menu.setOnClickListener { v ->
                val popupMenu = PopupMenu(context, v)
                popupMenu.menuInflater.inflate(R.menu.menu_facturas, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.editar -> {
                            val dialog = AddFacturaDF()
                            dialog.setTargetFragment(facturasFragment, 1)
                            val ft = (context as FragmentActivity).supportFragmentManager.beginTransaction()
                            bundle.putString("accion", "act_fact")
                            bundle.putString("id",facturasList[position].factura )
                            bundle.putString("empresa", facturasList[position].empresa)
                            bundle.putString("monto", facturasList[position].monto)
                            bundle.putString("banco", facturasList[position].banco)
                            bundle.putString("moneda", facturasList[position].moneda)
                            bundle.putInt("numero", facturasList[position].numero)
                            dialog.arguments = bundle
                            dialog.isCancelable = false
                            dialog.show(ft, "Actualizar Factura")
                        }
                    }
                    false
                }
                popupMenu.show()
            }
        }
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val cardFactura = v.findViewById(R.id.cardFactura) as CardView
        val factura = v.findViewById(R.id.factura) as TextView
        val empresa = v.findViewById(R.id.empresa) as TextView
        val nletras = v.findViewById(R.id.nletras) as TextView
        val monto = v.findViewById(R.id.monto) as TextView
        val banco = v.findViewById(R.id.banco) as TextView
        val moneda = v.findViewById(R.id.moneda) as TextView
        val pagados = v.findViewById(R.id.pagados) as TextView
        val debidos = v.findViewById(R.id.debidos) as TextView
        val pagado = v.findViewById(R.id.pagado) as TextView
        val debido = v.findViewById(R.id.debido) as TextView
        val menu = v.findViewById(R.id.menu) as ImageButton
        fun bindItems(facturas: Facturas) {
            factura.text = facturas.factura
            empresa.text = facturas.empresa
            nletras.text = facturas.numero.toString()
            monto.text = facturas.monto
            banco.text = facturas.banco
            moneda.text = facturas.moneda
            pagados.text = facturas.pagados.toString()
            debidos.text = facturas.debidos.toString()
            pagado.text = facturas.pagado
            debido.text = facturas.debido
        }

    }
}