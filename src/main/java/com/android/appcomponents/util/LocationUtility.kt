package com.android.appcomponents.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.android.appcomponents.model.LocationData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlin.collections.HashMap

val MY_PERMISSIONS_REQUEST_LOCATION = 99
private lateinit var fusedLocationClient: FusedLocationProviderClient
lateinit var sharedPreferences: SharedPreferences

class Utility(var context: Context? = null) {

    var ctx = context


    public fun isNetworkConnected(): Boolean {
        val connectivityManager = ctx?.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
       // sharedPreferences = ctx?.getSharedPreferences("cordinates", MODE_PRIVATE)!!
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

    }

    public fun getCordinates():MutableLiveData<LocationData> {
        ctx?.let { checkLocationPermission(it) }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx)
        var cordinatesList = getLastLocation()
        return cordinatesList
    }

    private fun getLastLocation(): MutableLiveData<LocationData> {
        var locationLatLonDetails = MutableLiveData<LocationData>()
        var cordinates = HashMap<String, Double>()
        if (ctx?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                ctx!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                ctx!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {

            fusedLocationClient!!.lastLocation
                .addOnCompleteListener(ctx as Activity) { task ->
                    if (task.isSuccessful && task.result != null) {
                        task.result.latitude;
                        task.result.longitude
                        Log.d("shrishailkumar", "lat = $task.result.latitude")
                        Log.d("shrishailkumar", "lon = ${task.result.longitude}")
                        val locationData = LocationData(task.result.latitude,task.result.longitude)
                        locationLatLonDetails.postValue(locationData)

                       /* sharedPreferences = ctx.getSharedPreferences("cordinates", MODE_PRIVATE)
                        sharedPreferences.edit().putString("lat", task.result.latitude.toString())
                            ?.apply()
                        sharedPreferences.edit().putString("lon", task.result.longitude.toString())
                            ?.apply()*/
                    } else {
                        Log.w("shrishailkumar", "getLastLocation:exception", task.exception)
                    }
                }
        }
        return locationLatLonDetails
    }
}


private fun checkLocationPermission(ctx: Context) {

    if (ActivityCompat.checkSelfPermission(
            ctx,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                ctx as Activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            AlertDialog.Builder(ctx)
                .setTitle("Location Permission Needed")
                .setMessage("This app needs the Location permission, please accept to use location functionality")
                .setPositiveButton(
                    "OK"
                ) { _, _ ->
                    //Prompt the user once explanation has been shown
                    requestLocationPermission(ctx)
                }
                .create()
                .show()
        } else {
            // No explanation needed, we can request the permission.
            requestLocationPermission(ctx)
        }
    }
}

private fun requestLocationPermission(ctx: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ActivityCompat.requestPermissions(
            ctx as Activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ),
            MY_PERMISSIONS_REQUEST_LOCATION
        )
    } else {
        ActivityCompat.requestPermissions(
            ctx as Activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            MY_PERMISSIONS_REQUEST_LOCATION
        )
    }

}


// fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
