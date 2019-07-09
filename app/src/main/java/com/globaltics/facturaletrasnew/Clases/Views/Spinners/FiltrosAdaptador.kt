package com.globaltics.facturaletrasnew.Clases.Views.Spinners

import android.content.Context
import androidx.fragment.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.globaltics.facturaletrasnew.Clases.Modelos.Filtros
import com.globaltics.facturaletrasnew.R

class FiltrosAdaptador(
    val c: Context,
    val mResource: Int,
    val tipousuList: MutableList<Filtros>
) : ArrayAdapter<Filtros>(c, mResource, tipousuList) {

    private val mInflater: LayoutInflater = LayoutInflater.from(c)

    override fun getDropDownView(
        position: Int, convertView: View?,
        parent: ViewGroup
    ): View {
        return createItemView(position, convertView, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    private fun createItemView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = mInflater.inflate(mResource, parent, false)

        val nombres = view.findViewById(R.id.nombre) as TextView
        nombres.text = tipousuList[position].nombre

        return view
    }
}