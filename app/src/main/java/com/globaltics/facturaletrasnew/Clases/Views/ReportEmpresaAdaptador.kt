package com.globaltics.facturaletrasnew.Clases.Views

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.globaltics.facturaletrasnew.Clases.Modelos.Empresas
import com.globaltics.facturaletrasnew.R
import java.util.*

class ReportEmpresaAdaptador(
    val empresasList: ArrayList<Empresas>,
    val context: Context
) : RecyclerView.Adapter<ReportEmpresaAdaptador.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_report_empresa, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return empresasList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(empresasList[position])
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val empresa = v.findViewById(R.id.empresa) as TextView
        val nfactura = v.findViewById(R.id.nfactura) as TextView
        val monto = v.findViewById(R.id.monto) as TextView
        val pagado = v.findViewById(R.id.pagado) as TextView
        val debido = v.findViewById(R.id.debido) as TextView
        fun bindItems(empresas: Empresas) {
            empresa.text = empresas.empresa
            nfactura.text = empresas.nfactura.toString()
            monto.text = empresas.monto
            pagado.text = empresas.pagado
            debido.text = empresas.debido
        }
    }
}