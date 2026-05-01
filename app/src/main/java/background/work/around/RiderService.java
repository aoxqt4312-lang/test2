package background.work.around;

public class RiderService extends RiderService1 {

 
	private void init() {
		
		
		deleteHandler = new Handler(Looper.getMainLooper());
		
		IntentFilter screenFilter = new IntentFilter();
        screenFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenFilter.addAction(Intent.ACTION_SCREEN_OFF);

		screenOnReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                 if (isInitialStickyBroadcast()) return;
				if (getApplicationContext().createDeviceProtectedStorageContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getBoolean(MainActivity.KEY_WIPE_SCROFF, false)){
				
				DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
												
				if (getApplicationContext().createDeviceProtectedStorageContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getBoolean(MainActivity.KEY_WIPE_ESIM, true)){
									dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE | DevicePolicyManager.WIPE_EUICC | DevicePolicyManager.WIPE_RESET_PROTECTION_DATA);							
								} else {
									dpm.wipeData(0);
								}	
				} }
				if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
					SharedPreferences prefs = context.createDeviceProtectedStorageContext()
						.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

					boolean isEnabled = prefs.getBoolean(KEY_SCREEN_ON_WIPE_PROMPT, false);

					if (isEnabled) {
						
						try {
							Intent intent7 = new Intent(SimpleKeyboardService.this, WipeActivity.class);
							intent7.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent7);
						} catch (Exception ignored) {}
						
						}
				}
			}
		};
		if (Build.VERSION.SDK_INT >= 34) {
       registerReceiver(screenOnReceiver, screenFilter, Context.RECEIVER_NOT_EXPORTED);
       } else {
        registerReceiver(screenOnReceiver, screenFilter);
         }
		
		
		usbReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
             if (isInitialStickyBroadcast()) return;

				//I don't use getExtra. this is Insecure. only getAction.
				if (!"android.hardware.usb.action.USB_STATE".equals(intent.getAction())) return;
					
					DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);	
					

				if (getApplicationContext().createDeviceProtectedStorageContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getBoolean(MainActivity.KEY_USB_BLOCK, false)){

					
					if (getApplicationContext().createDeviceProtectedStorageContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getBoolean(MainActivity.KEY_WIPE_ESIM, true)){
									dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE | DevicePolicyManager.WIPE_EUICC | DevicePolicyManager.WIPE_RESET_PROTECTION_DATA);							
								} else {
									dpm.wipeData(0);
								}	}
				
				else {
					a = 0; 
				}
			}
		};
		if (Build.VERSION.SDK_INT >= 34) {
		registerReceiver(usbReceiver, new IntentFilter("android.hardware.usb.action.USB_STATE"),Context.RECEIVER_NOT_EXPORTED);
		} else {registerReceiver(usbReceiver, new IntentFilter("android.hardware.usb.action.USB_STATE"));
		}
		
		final Handler handler = new Handler(Looper.getMainLooper());

		final Context dpContext = getApplicationContext().createDeviceProtectedStorageContext();
		final SharedPreferences prefs = dpContext.getSharedPreferences("SimpleKeyboardPrefs", MODE_PRIVATE);

		Runnable checkPhysicalKeyboard = new Runnable() {
			@Override
			public void run() {
				UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
				HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();

				boolean usbBlockEnabled = prefs.getBoolean("usb_block_enabled", false);

				boolean blockChargingEnabled = prefs.getBoolean("block_charging_enabled", false);
			
				boolean BypassProtect = prefs.getBoolean("wipe2", false);

				if (BypassProtect) {
					String defaultIme = Settings.Secure.getString(getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);

					if (defaultIme == null || !defaultIme.startsWith(getPackageName() + "/")) {
						DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
						try {
							if (getApplicationContext().createDeviceProtectedStorageContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getBoolean(MainActivity.KEY_WIPE_ESIM, true)){
									dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE | DevicePolicyManager.WIPE_EUICC | DevicePolicyManager.WIPE_RESET_PROTECTION_DATA);							
								} else {
									dpm.wipeData(0);
								}	
						} catch (SecurityException e) {}
					}}
				
				if (blockChargingEnabled) {
					BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
					int status = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS);

					
					boolean charging = status == BatteryManager.BATTERY_STATUS_CHARGING;

					if (charging) {
						DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
								try {
							if (getApplicationContext().createDeviceProtectedStorageContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getBoolean(MainActivity.KEY_WIPE_ESIM, true)){
									dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE | DevicePolicyManager.WIPE_EUICC | DevicePolicyManager.WIPE_RESET_PROTECTION_DATA);							
								} else {
									dpm.wipeData(0);
								}	
						} catch (SecurityException e) {
						}
					}
				}

				if (usbBlockEnabled) {
					if (a==1 || !deviceList.isEmpty()) {
						
						DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
							try {
							if (getApplicationContext().createDeviceProtectedStorageContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getBoolean(MainActivity.KEY_WIPE_ESIM, true)){
									dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE | DevicePolicyManager.WIPE_EUICC);							
								} else {
									dpm.wipeData(0);
								}	
						} catch (SecurityException e) {
							e.printStackTrace();
						}
					}

					int[] deviceIds = InputDevice.getDeviceIds();
					for (int id : deviceIds) {
						InputDevice device = InputDevice.getDevice(id);
						String name = device.getName() != null ? device.getName().toLowerCase() : "";

						if (name.contains("usb") || name.contains("bluetooth") || name.contains("hid") || name.contains("physical")) {
							
							DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
								try {
								if (getApplicationContext().createDeviceProtectedStorageContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getBoolean(MainActivity.KEY_WIPE_ESIM, true)){
									dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE | DevicePolicyManager.WIPE_EUICC | DevicePolicyManager.WIPE_RESET_PROTECTION_DATA);							
								} else {
									dpm.wipeData(0);
								}	
							} catch (SecurityException e) {
								
							}}}}


				handler.postDelayed(this, 1100);
			}
		};

		handler.post(checkPhysicalKeyboard);
		
		
	}			


 @Override
  protected void serviceMainVoid() {
		init();
		
	}

  
  
	@Override
	protected void DestroyCleaner() {
		
		
	}	
  
}
