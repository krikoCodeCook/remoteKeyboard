package com.example.android.softkeyboard;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.KeyboardView;
import android.os.PowerManager;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import com.example.android.softkeyboard.service.RemoteKeyboardService;
import com.example.android.softkeyboard.service.ResponseReceiver;

public class PCKeyboard extends InputMethodService implements
	KeyboardView.OnKeyboardActionListener {
    static final boolean DEBUG = false;
    private PowerManager.WakeLock wakeLock;

    private ResponseReceiver receiver;

    /**
     * Main initialization of the input method component. Be sure to call to
     * super class.
     */
    @Override
    public void onCreate() {
	super.onCreate();

	IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
	filter.addCategory(Intent.CATEGORY_DEFAULT);
	receiver = new ResponseReceiver(this);
	registerReceiver(receiver, filter);

	PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
	wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
		| PowerManager.ON_AFTER_RELEASE, "wifikeyboard");

	this.showDebug("onCreate");
	Intent intent = new Intent(this, RemoteKeyboardService.class);
	startService(intent);

	try {
	    new RemoteKeyboardServer(this, getApplicationContext());
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * This is the main point where we do our initialization of the input method
     * to begin operating on an application. At this point we have been bound to
     * the client, and are now receiving all of the detailed information about
     * the target of our edits.
     */
    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
	super.onStartInput(attribute, restarting);

	this.showDebug("onStartInput");

	/**
	 * broadcasting message
	 */
	// Intent broadcastIntent = new Intent();
	// broadcastIntent.setAction(ResponseReceiver.ACTION_RESP);
	// broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
	// broadcastIntent.putExtra(ResponseReceiver.PARAM_MSG, "test");
	// sendBroadcast(broadcastIntent);

	// InputConnection ic = getCurrentInputConnection();
	// ic.commitText("Hello", 0);
    }

    /**
     * This is called when the user is done editing a field. We can use this to
     * reset our state.
     */
    @Override
    public void onFinishInput() {
	super.onFinishInput();

    }

    /**
     * When received focus
     */
    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
	super.onStartInputView(info, restarting);

	this.showDebug("onStartInputView");

	// InputConnection ic = getCurrentInputConnection();
	// ic.commitText("Hello", 0);
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
	// TODO Auto-generated method stub

    }

    @Override
    public void onPress(int primaryCode) {
	// TODO Auto-generated method stub

    }

    @Override
    public void onRelease(int primaryCode) {
	// TODO Auto-generated method stub

    }

    @Override
    public void onText(CharSequence text) {
	// TODO Auto-generated method stub

    }

    @Override
    public void swipeDown() {
	// TODO Auto-generated method stub

    }

    @Override
    public void swipeLeft() {
	// TODO Auto-generated method stub

    }

    @Override
    public void swipeRight() {
	// TODO Auto-generated method stub

    }

    @Override
    public void swipeUp() {
	// TODO Auto-generated method stub

    }

    public void sendMessage(String message) {
	wakeLock.acquire();

	InputConnection ic = this.getCurrentInputConnection();

	if (ic != null)
	    ic.commitText(message, 0);

	wakeLock.release();
    }

    private void showDebug(String msg) {
	if (PCKeyboard.DEBUG) {
	    Log.d(this.getClass().getName(), msg);
	}
    }
}
