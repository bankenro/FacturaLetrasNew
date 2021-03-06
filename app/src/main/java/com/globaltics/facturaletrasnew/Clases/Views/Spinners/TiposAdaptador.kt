package com.globaltics.facturaletrasnew.Clases.Views.Spinners

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.globaltics.facturaletrasnew.Clases.Modelos.Tipos
import com.globaltics.facturaletrasnew.R

class TiposAdaptador(private val c: Context, private val mResource: Int, private val tipousuList: MutableList<Tipos>) :
    ArrayAdapter<Tipos>(c, mResource, tipousuList) {

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

        val nombre = view.findViewById(R.id.nombre) as TextView

        nombre.text = tipousuList[position].nombre

        return view
    }

}