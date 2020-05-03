package com.fyp.agrifarm.app.prices.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.fyp.agrifarm.R
import com.fyp.agrifarm.app.prices.model.LocationListItem
import com.fyp.agrifarm.utils.AssetUtils

/**
 * A fragment to display the locations for prices
 */
class LocationListFragment : Fragment() {

    private var mListener: OnLocationItemClickListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_location_list_list, container, false)

        val recyclerView = view as RecyclerView

        val locationListItems = AssetUtils.readLocationsFile(resources)

        recyclerView.adapter = LocationRvAdapter(locationListItems, mListener!!)
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = if (context is OnLocationItemClickListener) {
            context
        } else {
            throw RuntimeException(context.toString()
                    + " must implement OnLocationItemClickListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnLocationItemClickListener {
        fun onLocationSelected(item: LocationListItem)
    }
}