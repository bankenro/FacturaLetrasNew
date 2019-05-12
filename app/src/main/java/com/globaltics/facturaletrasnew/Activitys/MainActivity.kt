package com.globaltics.facturaletrasnew.Activitys

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.globaltics.facturaletrasnew.Fragments.LoginFragment
import com.globaltics.facturaletrasnew.Fragments.MenuFragment
import com.globaltics.facturaletrasnew.R

class MainActivity : AppCompatActivity() {

    var preferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preferences = getSharedPreferences("FactLetraGTs", Context.MODE_PRIVATE)

        val id = preferences?.getInt("id",0)
        val idt = preferences?.getString("idt","")

        if (savedInstanceState == null) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            if (id != 0 && idt!!.isNotEmpty()){
                val fragment = MenuFragment()
                fragmentTransaction.add(R.id.contenedor, fragment).commit()
            }else{
                val fragment = LoginFragment()
                fragmentTransaction.add(R.id.contenedor, fragment).commit()
            }
        }
    }
}
