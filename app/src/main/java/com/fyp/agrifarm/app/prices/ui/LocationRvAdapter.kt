package com.fyp.agrifarm.app.prices.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fyp.agrifarm.R
import com.fyp.agrifarm.app.prices.ui.LocationListFragment.OnLocationItemClickListener
import com.fyp.agrifarm.app.prices.model.LocationListItem

class LocationRvAdapter(private val locationList: ArrayList<LocationListItem>?,
                        private val mListener: OnLocationItemClickListener) :
        RecyclerView.Adapter<LocationRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_location_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = locationList!!.get(position)
        holder.area.text = item.areaName
        holder.itemView.setOnClickListener {
            mListener.onLocationSelected(item)
        }
    }

    override fun getItemCount(): Int {
        return locationList?.size ?: 0
    }

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val area = mView.findViewById<View>(R.id.area_title) as TextView
    }

}