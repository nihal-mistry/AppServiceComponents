package com.android.appcomponents.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.appcomponents.model.DeviceInfo
import com.android.appcomponents.util.DeviceInfoUtility

class DeviceInfoViewModel : ViewModel() {
    val deviceInfoData = MutableLiveData<DeviceInfo>()

    fun getDeviceData():LiveData<DeviceInfo>{
        val deviceInfoData = DeviceInfoUtility
        return deviceInfoData.getDeviceInfo()
    }
}