package background.work.around;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class MainActivity extends Activity {

    private static boolean main=true;
	boolean accessibilityEnabled = false;
    private static final String PREFS_NAME = "SimpleKeyboardPrefs";
    private static final String KEY_CUSTOM_COMMAND = "custom_wipe_command";	
	private static final String KEY_WIPE_ON_REBOOT = "wipe_on_reboot";
	private static final String KEY_AUTORUN = "auto_run";
	private static final String KEY_WIPE2 = "wipe2";
	static final String KEY_WIPE_ESIM = "WIPE_ESIM";
	static final String KEY_WIPE_SCROFF = "WIPE_SCROFF";
	private static final String KEY_SCREEN_ON_WIPE_PROMPT = "screen_on_wipe_prompt";	
	private static final String KEY_FAKE_HOME = "fake_home_enabled";		
	private static final String KEY_WIPE_ON_NO_NETWORK = "wipe_on_no_network";
	static final String KEY_USB_BLOCK = "usb_block_enabled";
    private static final String KEY_BLOCK_CHARGING = "block_charging_enabled";
    private static final String KEY_LAYOUT_RU = "layout_ru";
    private static final String KEY_LAYOUT_EN = "layout_en";
    private static final String KEY_LAYOUT_SYM = "layout_sym";
    private static final String KEY_LAYOUT_EMOJI = "layout_emoji";
    private static final String KEY_LAYOUT_ES = "layout_es";
	private static boolean RESULT = false;	
    private static final String KEY_LANG_RU = "lang_ru";
    private static final String KEY_LANG_EN = "lang_en";
    private static final String KEY_LANG_SYM = "lang_sym";
    private static final String KEY_LANG_EMOJI = "lang_emoji";
    private static final String KEY_LANG_ES = "lang_es";
	private static int e= 0;

    @Override
    protected void onResume() {
        super.onResume();
        try {
        Context appContext = getApplicationContext();
        Intent serviceIntent = new Intent(appContext, RiderService.class);
        appContext.startForegroundService(serviceIntent);             
        } catch (Throwable t) {}
        finish();
    }
}
