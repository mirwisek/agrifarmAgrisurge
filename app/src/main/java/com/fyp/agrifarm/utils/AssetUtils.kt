package com.fyp.agrifarm.utils

import android.content.res.Resources
import android.util.Log
import com.fyp.agrifarm.R
import com.fyp.agrifarm.app.prices.model.LocationListItem
import java.io.BufferedReader
import java.io.IOException
import java.io.StringReader
import java.util.*
import kotlin.collections.ArrayList

object AssetUtils {

    private const val FILE_NAME = R.raw.areacode

    fun readLocationsFile(resources: Resources): ArrayList<LocationListItem>? {
        var list: ArrayList<LocationListItem>? = null
        val inputStream = resources.openRawResource(FILE_NAME)
        var areas: String? = null
        try {
            val buffer = ByteArray(inputStream.available())
            while (inputStream.read(buffer) != -1)
                areas = String(buffer)

            BufferedReader(StringReader(areas!!)).use { br ->
                var line: String?
                list = arrayListOf()
                while (br.readLine().also { line = it } != null) {
                    val split = line!!.split(", ").toTypedArray()
                    val code = split[0]
                    val area = split[1]
                    list!!.add(LocationListItem(code, area))
                    Log.d("LocationListFragment", "run: $code$area")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return list
    }

    fun findAddress(resources: Resources, areaToFind: String): LocationListItem? {
        val area = areaToFind.toLowerCase(Locale.ENGLISH)
        readLocationsFile(resources)?.let { list ->
            list.forEach { loc ->
                loc.areaName.toLowerCase(Locale.ENGLISH).contains(area)
                return loc
            }
        }
        return null
    }

}