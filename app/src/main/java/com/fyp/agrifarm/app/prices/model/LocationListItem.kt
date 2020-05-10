package com.fyp.agrifarm.app.prices.model

data class LocationListItem(val areaCode: String, val areaName: String  ) {
    constructor( lat : Double , long : Double) : this("", "")
}