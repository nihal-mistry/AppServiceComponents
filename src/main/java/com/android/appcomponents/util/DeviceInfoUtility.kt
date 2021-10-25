package com.android.appcomponents.util

import android.os.Build
import androidx.lifecycle.MutableLiveData
import com.android.appcomponents.model.DeviceInfo

object DeviceInfoUtility {

    public fun getDeviceInfo(): MutableLiveData<DeviceInfo> {
        val deviceInfo = MutableLiveData<DeviceInfo>()

        val deviceData = DeviceInfo(
            Build.VERSION.SDK, Build.BOARD, Build.FINGERPRINT, Build.HOST, Build.USER,
            Build.TYPE, Build.BRAND, Build.MANUFACTURER, Build.ID, Build.MODEL, Build.USER,
            Build.DEVICE,Build.PRODUCT,Build.BOOTLOADER,Build.DISPLAY,Build.HARDWARE
        )

        deviceInfo.postValue(deviceData)
        return deviceInfo
    }
}