package com.globaltics.facturaletrasnew.Clases.Views

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.globaltics.facturaletrasnew.Clases.Modelos.Letras
import com.globaltics.facturaletrasnew.R
import com.squareup.picasso.Picasso
import java.util.*

class ReportLetraAdaptador(
    val letrasList: ArrayList<Letras>,
    val context: Context
) : androidx.recyclerview.widget.RecyclerView.Adapter<ReportLetraAdaptador.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_report_letra, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return letrasList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(letrasList[position])
        if (Objects.equals(letrasList[position].estado, "PAGADO")) {
            holder.estado.setTextColor(ContextCompat.getColor(context, R.color.verde))
        } else {
            holder.estado.setTextColor(ContextCompat.getColor(context, R.color.rojo))
        }
        holder.item.setOnClickListener {
            notifyItemChanged(position)
        }
    }

    class ViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {
        val letra = v.findViewById(R.id.letra) as TextView
        val factura = v.findViewById(R.id.factura) as TextView
        val empresa = v.findViewById(R.id.empresa) as TextView
        val fecha = v.findViewById(R.id.fecha) as TextView
        val monto = v.findViewById(R.id.monto) as TextView
        val estado = v.findViewById(R.id.estado) as TextView
        val moneda = v.findViewById(R.id.moneda) as TextView
        val imagen = v.findViewById(R.id.imagen) as ImageView
        val descripcion = v.findViewById(R.id.descripcion) as TextView
        val item = v.findViewById(R.id.item) as ConstraintLayout
        val sub_item = v.findViewById(R.id.sub_item) as ConstraintLayout
        fun bindItems(letras: Letras) {
            if (sub_item.visibility == View.GONE) {
                sub_item.visibility = View.VISIBLE
            } else  if (sub_item.visibility == View.VISIBLE){
                sub_item.visibility = View.GONE
            }
            letra.text = letras.letra
            factura.text = letras.factura
            empresa.text = letras.empresa
            fecha.text = letras.fecha
            monto.text = letras.monto
            estado.text = letras.estado
            moneda.text = letras.moneda
            if (!(Objects.equals(letras.imagen,"a") && Objects.equals(letras.descripcion,"a"))){
                Picasso.get().load(letras.imagen).resize(150, 200).into(imagen)
                descripcion.text = letras.descripcion
            }else{
                descripcion.text = "SIN DESCRIPCION"
            }
        }

    }
}