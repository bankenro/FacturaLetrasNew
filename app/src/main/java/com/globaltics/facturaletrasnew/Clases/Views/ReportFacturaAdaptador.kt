package com.globaltics.facturaletras.Clases.Views

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.globaltics.facturaletrasnew.Clases.Modelos.Facturas
import com.globaltics.facturaletrasnew.R
import java.util.*

class ReportFacturaAdaptador(
    val facturasList: ArrayList<Facturas>,
    val context: Context
) : RecyclerView.Adapter<ReportFacturaAdaptador.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_report_factura, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return facturasList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(facturasList[position])
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val factura = v.findViewById(R.id.factura) as TextView
        val empresa = v.findViewById(R.id.empresa) as TextView
        val nletras = v.findViewById(R.id.nletras) as TextView
        val monto = v.findViewById(R.id.monto) as TextView
        val pagados = v.findViewById(R.id.pagados) as TextView
        val debidos = v.findViewById(R.id.debidos) as TextView
        val pagado = v.findViewById(R.id.pagado) as TextView
        val debido = v.findViewById(R.id.debido) as TextView
        fun bindItems(facturas: Facturas) {
            factura.text = facturas.factura
            empresa.text = facturas.empresa
            nletras.text = facturas.numero.toString()
            monto.text = facturas.monto
            pagados.text = facturas.pagados.toString()
            debidos.text = facturas.debidos.toString()
            pagado.text = facturas.pagado
            debido.text = facturas.debido
        }
    }
}