package com.sstonn.flutter_wear_os_connectivity

import android.annotation.SuppressLint

class FlutterWearOsConnectivityService : Service() {
    private val WAKELOCK_TAG = "FlutterWearOsConnectivityService:Wakelock"
    private val WIFILOCK_TAG = "FlutterWearOsConnectivityService:WifiLock"
    private val binder: LocalBinder = LocalBinder(this)

    // Service is foreground
    private var isForeground = false
    private var connectedEngines = 0
    private var listenerCount = 0

    @Nullable
    private var activity: Activity? = null

//    @Nullable
//    private var geolocationManager: GeolocationManager? = null

//    @Nullable
//    private var locationClient: LocationClient? = null

    @Nullable
    private var wakeLock: WakeLock? = null

    @Nullable
    private var wifiLock: WifiLock? = null

    @Nullable
    private var backgroundNotification: BackgroundNotification? = null
    @Override
    fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Creating wearos service.")
        // TODO
//        geolocationManager = GeolocationManager()
    }

    @Override
    fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    @Nullable
    @Override
    fun onBind(intent: Intent?): IBinder {
        Log.d(TAG, "Binding to wearos service.")
        return binder
    }

    @Override
    fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "Unbinding from wearos service.")
        return super.onUnbind(intent)
    }

    @Override
    fun onDestroy() {
        Log.d(TAG, "Destroying wearos service.")
        stopLocationService()
        disableBackgroundMode()
//        geolocationManager = null
        backgroundNotification = null
        Log.d(TAG, "Destroyed wearos service.")
        super.onDestroy()
    }

    fun canStopLocationService(cancellationRequested: Boolean): Boolean {
        return if (cancellationRequested) {
            listenerCount == 1
        } else connectedEngines == 0
    }

    fun flutterEngineConnected() {
        connectedEngines++
        Log.d(TAG, "Flutter engine connected. Connected engine count $connectedEngines")
    }

    fun flutterEngineDisconnected() {
        connectedEngines--
        Log.d(TAG, "Flutter engine disconnected. Connected engine count $connectedEngines")
    }

    fun startLocationService(
        forceLocationManager: Boolean,
        locationOptions: LocationOptions?,
        events: EventChannel.EventSink
    ) {
        listenerCount++
        // TODO
//        if (geolocationManager != null) {
//            locationClient = geolocationManager.createLocationClient(
//                this.getApplicationContext(),
//                Boolean.TRUE.equals(forceLocationManager),
//                locationOptions
//            )
//            geolocationManager.startPositionUpdates(
//                locationClient,
//                activity,
//                { location: Location? -> events.success(LocationMapper.toHashMap(location)) }
//            ) { errorCodes: ErrorCodes ->
//                events.error(
//                    errorCodes.toString(),
//                    errorCodes.toDescription(),
//                    null
//                )
//            }
//        }
    }

    fun stopLocationService() {
        listenerCount--
        Log.d(TAG, "Stopping location service.")
        // TODO
//        if (locationClient != null && geolocationManager != null) {
//            geolocationManager.stopPositionUpdates(locationClient)
//        }
    }

    fun enableBackgroundMode(options: ForegroundNotificationOptions) {
        if (backgroundNotification != null) {
            Log.d(TAG, "Service already in foreground mode.")
            changeNotificationOptions(options)
        } else {
            Log.d(TAG, "Start service in foreground mode.")
            backgroundNotification = BackgroundNotification(
                this.getApplicationContext(), CHANNEL_ID, ONGOING_NOTIFICATION_ID, options
            )
            backgroundNotification.updateChannel("Background Location")
            val notification: Notification = backgroundNotification.build()
            startForeground(ONGOING_NOTIFICATION_ID, notification)
            isForeground = true
        }
        obtainWakeLocks(options)
    }

    @SuppressWarnings("deprecation")
    fun disableBackgroundMode() {
        if (isForeground) {
            Log.d(TAG, "Stop service in foreground.")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(Service.STOP_FOREGROUND_REMOVE)
            } else {
                stopForeground(true)
            }
            releaseWakeLocks()
            isForeground = false
            backgroundNotification = null
        }
    }

    fun changeNotificationOptions(options: ForegroundNotificationOptions?) {
        if (backgroundNotification != null) {
            backgroundNotification.updateOptions(options, isForeground)
            obtainWakeLocks(options)
        }
    }

    fun setActivity(@Nullable activity: Activity?) {
        this.activity = activity
    }

    private fun releaseWakeLocks() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release()
            wakeLock = null
        }
        if (wifiLock != null && wifiLock.isHeld()) {
            wifiLock.release()
            wifiLock = null
        }
    }

    @SuppressLint("WakelockTimeout")
    private fun obtainWakeLocks(options: ForegroundNotificationOptions) {
        releaseWakeLocks()
        if (options.isEnableWakeLock()) {
            val powerManager: PowerManager =
                getApplicationContext().getSystemService(Context.POWER_SERVICE) as PowerManager
            if (powerManager != null) {
                wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKELOCK_TAG)
                wakeLock.setReferenceCounted(false)
                wakeLock.acquire()
            }
        }
        if (options.isEnableWifiLock()) {
            val wifiManager: WifiManager =
                getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (wifiManager != null) {
                wifiLock =
                    wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, WIFILOCK_TAG)
                wifiLock.setReferenceCounted(false)
                wifiLock.acquire()
            }
        }
    }

    internal inner class LocalBinder(val wearOsConnectivityService: FlutterWearOsConnectivityService) : Binder()
    companion object {
        private const val TAG = "FlutterWearOsConnectivity"
        private const val ONGOING_NOTIFICATION_ID = 75415
        private const val CHANNEL_ID = "wearos_channel_01"
    }
}