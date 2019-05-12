package com.globaltics.facturaletrasnew.Clases.Views

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.globaltics.facturaletrasnew.Clases.Modelos.Letras
import com.globaltics.facturaletrasnew.R
import java.util.*

class ReportLetraAdaptador(
    val letrasList: ArrayList<Letras>,
    val context: Context
) : RecyclerView.Adapter<ReportLetraAdaptador.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_report_letra, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return letrasList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(letrasList[position])
        if (Objects.equals(letrasList[position].estado, 1)) {
            holder.estado.setTextColor(ContextCompat.getColor(context, R.color.rojo))
        } else {
            holder.estado.setTextColor(ContextCompat.getColor(context, R.color.verde))
        }
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val letra = v.findViewById(R.id.letra) as TextView
        val factura = v.findViewById(R.id.factura) as TextView
        val empresa = v.findViewById(R.id.empresa) as TextView
        val fecha = v.findViewById(R.id.fecha) as TextView
        val monto = v.findViewById(R.id.monto) as TextView
        val estado = v.findViewById(R.id.estado) as TextView
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