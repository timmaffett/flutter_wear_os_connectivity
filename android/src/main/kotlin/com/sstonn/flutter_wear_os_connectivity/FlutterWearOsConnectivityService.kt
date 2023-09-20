package com.sstonn.flutter_wear_os_connectivity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.wifi.WifiManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log

import io.flutter.plugin.common.EventChannel

import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer


class FlutterWearOsConnectivityService : Service(), CapabilityClient.OnCapabilityChangedListener, MessageClient.OnMessageReceivedListener, DataClient.OnDataChangedListener {

    override fun onBind(intent: Intent): IBinder {
        return LocalBinder()
    }

    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): FlutterWearOsConnectivityService = this@FlutterWearOsConnectivityService
    }

    override fun onCreate() {
        super.onCreate()
        // Initialize your WearOS API clients here like you do in FlutterWearOsConnectivityPlugin
    }

    override fun onCapabilityChanged(capabilityInfo: CapabilityInfo) {
        // Handle capability change
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        // Handle message received
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        // Handle data changed
    }
}
