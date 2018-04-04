package com.pathfinder;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

//unsued accessibility service, might use it in the following implementations.
public class CustomAccessibilityService extends AccessibilityService {
    public CustomAccessibilityService() {
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d("Evenr","hasdagag");
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED) {
//            event.setAction(Accessibili);
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
//        Toast.makeText(getApplication(), "onServiceConnected", Toast.LENGTH_SHORT).show();
        super.onServiceConnected();
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        Log.d("Key Event","Service");
        return super.onKeyEvent(event);
    }

}
