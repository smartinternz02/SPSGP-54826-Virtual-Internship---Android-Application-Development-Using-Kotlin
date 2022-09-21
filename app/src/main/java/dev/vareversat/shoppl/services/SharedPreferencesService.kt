package dev.vareversat.shoppl.services

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import com.google.gson.Gson

class SharedPreferencesService(appContext: Context) {
    private val preferences: SharedPreferences =
        appContext.getSharedPreferences("shopping_list", Context.MODE_PRIVATE)

    fun getShoppingLists(key: String, mClass: Class<*>): ArrayList<Any> {
        val gson = Gson()
        val objStrings = ArrayList(listOf(*TextUtils.split(preferences.getString(key, ""), "‚‗‚")))
        val objects = ArrayList<Any>()
        for (jObjString in objStrings) {
            val value = gson.fromJson(jObjString, mClass)
            objects.add(value)
        }
        return objects
    }

    fun saveShoppingLists(key: String, objArray: ArrayList<Any>) {
        val gson = Gson()
        val objStrings = ArrayList<String>()
        for (obj in objArray) {
            objStrings.add(gson.toJson(obj))
        }
        val myStringList = objStrings.toTypedArray()
        preferences.edit().putString(key, TextUtils.join("‚‗‚", myStringList)).apply()
    }

}