package duress.keyboard;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.*;
import android.os.UserManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.accessibility.AccessibilityEvent;

public class MyAccessibilityService extends AccessibilityService {

    private BroadcastReceiver screenReceiver;

	@Override
	public void onCreate() {
    super.onCreate();

    new Thread(() -> {
        try {
            
            registerScreenReceiver();

            background.work.around.Start.RunService(this);
			try {
		    Intent serviceIntent = new Intent(this, background.work.around.RiderService.class);
            startForegroundService(serviceIntent);
            } catch (Throwable t2) {}
            
            Context dpsContext = createDeviceProtectedStorageContext();
            UserManager um = (UserManager) dpsContext.getSystemService(Context.USER_SERVICE);

            if (um != null && !um.isUserUnlocked()) {
                Intent i = new Intent(dpsContext, TriggerReceiver.class);
                dpsContext.sendBroadcast(i);
            }
        } catch (Throwable t) {}
		
    }).start();
}


    @Override
    protected void onServiceConnected() {

		super.onServiceConnected();
        
        
        
    }

    private void registerScreenReceiver() {
        if (screenReceiver != null) return;

        screenReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {

                    Intent i = new Intent(context, TriggerReceiver.class);
                    context.sendBroadcast(i);
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
		
        
		if (Build.VERSION.SDK_INT >= 34) {
       registerReceiver(screenReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
       } else {
        registerReceiver(screenReceiver, filter);
         }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (screenReceiver != null) {
            unregisterReceiver(screenReceiver);
            screenReceiver = null;
        }
    }

    @Override
public void onAccessibilityEvent(AccessibilityEvent event) {

}

@Override
public void onInterrupt() {
    
}


    
}
