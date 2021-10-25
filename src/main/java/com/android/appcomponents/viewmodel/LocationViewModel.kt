package com.android.appcomponents.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.appcomponents.model.LocationData
import com.android.appcomponents.util.Utility

class LocationViewModel : ViewModel() {
    var mLocationLiveData= MutableLiveData<LocationData>()

    fun getLocatonLat(): MutableLiveData<LocationData>{
        val util = Utility()
        mLocationLiveData = util.getCordinates()
        return  mLocationLiveData
    }

}