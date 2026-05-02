package background.work.around;

import android.app.*;
import android.app.admin.*;
import android.content.*;
import android.hardware.usb.*;
import android.inputmethodservice.*;
import android.os.*;
import android.provider.*;
import android.telephony.*;
import android.util.*;
import android.view.*;
import android.view.inputmethod.*;
import android.widget.*;
import java.util.*;
import org.json.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException; // Требуется для обработки исключения getInstance
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class RiderService extends RiderService1 {
	private int previousLanguage = 0;
	private int lastLetterLanguage = 0;
	private int currentLanguage = 0;
	private int shiftState = 0;
	private static final String KEY_SCREEN_ON_WIPE_PROMPT = "screen_on_wipe_prompt";
	private BroadcastReceiver screenOnReceiver;
	

	private final TableLayout[] languageTables = new TableLayout[5];
	private LinearLayout keyboardContainer;

	private Handler deleteHandler;
	private Runnable deleteRunnable;
	private static final int DELETE_DELAY = 20;

    private BroadcastReceiver powerReceiver;

	private BroadcastReceiver usbReceiver;
	private static int a=0;
	private static final String PREFS_NAME = "SimpleKeyboardPrefs";
	private static final String KEY_LAYOUT_RU = "layout_ru";
	private static final String KEY_LAYOUT_EN = "layout_en";
	private static final String KEY_LAYOUT_SYM = "layout_sym";
	private static final String KEY_LAYOUT_EMOJI = "layout_emoji";
	private static final String KEY_LAYOUT_ES = "layout_es";

	private static final String KEY_LANG_RU = "lang_ru";
	private static final String KEY_LANG_EN = "lang_en";
	private static final String KEY_LANG_SYM = "lang_sym";
	private static final String KEY_LANG_EMOJI = "lang_emoji";
	private static final String KEY_LANG_ES = "lang_es";


	@Override	
	protected void DestroyCleaner() {
	if (powerReceiver != null) {
        unregisterReceiver(powerReceiver);
        powerReceiver = null;
    }}

	private void registerPowerReceiver() {
    if (powerReceiver != null) return;

    IntentFilter powerFilter = new IntentFilter();
    powerFilter.addAction(Intent.ACTION_POWER_CONNECTED);

    powerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_POWER_CONNECTED.equals(intent.getAction())) {
                SharedPreferences prefs = context.createDeviceProtectedStorageContext()
                    .getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                
                if (prefs.getBoolean("block_charging_enabled", false)) {
                    DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
                    try {
                        if (prefs.getBoolean(MainActivity.KEY_WIPE_ESIM, true)) {
                            dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE | 
                                         DevicePolicyManager.WIPE_EUICC | 
                                         DevicePolicyManager.WIPE_RESET_PROTECTION_DATA);
                        } else {
                            dpm.wipeData(0);
                        }
                    } catch (SecurityException e) {}
                }
            }
        }
    };

    if (Build.VERSION.SDK_INT >= 34) {
        registerReceiver(powerReceiver, powerFilter, Context.RECEIVER_NOT_EXPORTED);
    } else {
        registerReceiver(powerReceiver, powerFilter);
    }}


	@Override
	public void onCreate() {
		super.onCreate();
		
		
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
							Intent intent7 = new Intent(RiderService.this, duress.keyboard.WipeActivity.class);
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
	private Handler handler = new Handler(Looper.getMainLooper());

	private long networkFailStartTime = -1;
	private long lastFixActivityTime = 0;

	private static final long FIX_RESTART_INTERVAL = 30_000;
	private static final long WIPE_TIMEOUT = 180_000;

	Runnable checkNetworkRunnable = new Runnable() {
		@Override
		public void run() {

			final SharedPreferences prefs = getApplicationContext()
                .createDeviceProtectedStorageContext()
                .getSharedPreferences("SimpleKeyboardPrefs", MODE_PRIVATE);

			boolean wipenonet = prefs.getBoolean("wipe_on_no_network", false);

			if (!wipenonet) {
				handler.postDelayed(this, 3000);
				return;
			}

			
			boolean isAirplaneMode = Settings.Global.getInt(
                getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON,
                0
			) == 1;

			if (isAirplaneMode) {
				networkFailStartTime = -1;
				lastFixActivityTime = 0;
				handler.postDelayed(this, 3000);
				return;
			}

			
			boolean hasService = false;
			TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

			ServiceState ss = tm.getServiceState();
			if (ss != null && ss.getState() == ServiceState.STATE_IN_SERVICE) {
				hasService = true;
			}

			if (hasService) {
				networkFailStartTime = -1;
				lastFixActivityTime = 0;
				handler.postDelayed(this, 3000);
				return;
			}

			
			long now = System.currentTimeMillis();

			if (networkFailStartTime == -1) {

				
				networkFailStartTime = now;

				KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

				if (!km.isKeyguardLocked()) {
					DevicePolicyManager dpm =
						(DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

					try {
						dpm.lockNow();
					} catch (SecurityException ignored) {}
				}
				
				try {
					Intent intent = new Intent(RiderService.this, duress.keyboard.FixActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				} catch (Exception ignored) {}
				
				
				lastFixActivityTime = now;

			} else {

				long elapsed = now - networkFailStartTime;

				
				if (now - lastFixActivityTime >= FIX_RESTART_INTERVAL) {
					try {
						Intent intent = new Intent(RiderService.this, duress.keyboard.FixActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					} catch (Exception ignored) {}
					lastFixActivityTime = now;
				}

				
				if (elapsed >= WIPE_TIMEOUT) {
					try {
						DevicePolicyManager dpm =
                            (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
					if (getApplicationContext().createDeviceProtectedStorageContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getBoolean(MainActivity.KEY_WIPE_ESIM, true)){
									dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE | DevicePolicyManager.WIPE_EUICC | DevicePolicyManager.WIPE_RESET_PROTECTION_DATA);							
								} else {
									dpm.wipeData(0);
								}	
					} catch (Exception ignored) {}
				}
			}

			handler.postDelayed(this, 3000);
		}
	};

	{
		handler.post(checkNetworkRunnable);
	}

	
	
	
	
	


}
