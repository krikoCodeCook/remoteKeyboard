package com.example.android.softkeyboard;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.KeyboardView;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Toast;

import com.example.android.softkeyboard.service.RemoteKeyboardService;
import com.example.android.softkeyboard.service.ResponseReceiver;

public class PCKeyboard extends InputMethodService implements
	KeyboardView.OnKeyboardActionListener {
    static final boolean DEBUG = false;

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

	this.showDebug("onCreate");
	Intent intent = new Intent(this, RemoteKeyboardService.class);
	startService(intent);
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

    private void showDebug(String name) {
	if (PCKeyboard.DEBUG) {
	    Context context = getApplicationContext();
	    CharSequence text = this.getClass().getName() + name;
	    int duration = Toast.LENGTH_SHORT;

	    Toast toast = Toast.makeText(context, text, duration);
	    toast.show();
	}
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
	InputConnection ic = this.getCurrentInputConnection();
	if (ic != null)
	    ic.commitText(message, 0);
    }
}
