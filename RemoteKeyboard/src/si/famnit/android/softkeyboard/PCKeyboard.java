package si.famnit.android.softkeyboard;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class PCKeyboard extends InputMethodService implements
	KeyboardView.OnKeyboardActionListener {
    static final boolean DEBUG = false;

    private PowerManager.WakeLock wakeLock;
    private RemoteKeyboardServer httpSrv;

    private static final String TAG = "PCK";

    // private ResponseReceiver receiver;

    private KeyboardView mInputView;

    private StringBuilder mComposing = new StringBuilder();
    private int mLastDisplayWidth;

    private LatinKeyboard mQwertyKeyboard;

    private int port = 9999;

    /**
     * Main initialization of the input method component. Be sure to call to
     * super class.
     */
    @Override
    public void onCreate() {
	super.onCreate();

	// IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
	// filter.addCategory(Intent.CATEGORY_DEFAULT);
	// receiver = new ResponseReceiver(this);
	// registerReceiver(receiver, filter);

	PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
	wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
		| PowerManager.ON_AFTER_RELEASE, "wifikeyboard");

	this.printDebug("onCreate");

	// Intent intent = new Intent(this, RemoteKeyboardService.class);
	// startService(intent);

	try {
	    if (httpSrv == null)
		httpSrv = new RemoteKeyboardServer(this,
			getApplicationContext(), port);
	} catch (IOException e) {
	    e.printStackTrace();
	}

	this.printDebug("onCreate");
    }

    /**
     * This is the point where you can do all of your UI initialization. It is
     * called after creation and any configuration change.
     */
    @Override
    public void onInitializeInterface() {
	if (mQwertyKeyboard != null) {
	    // Configuration changes can happen after the keyboard gets
	    // recreated,
	    // so we need to be able to re-build the keyboards if the available
	    // space has changed.
	    int displayWidth = getMaxWidth();
	    if (displayWidth == mLastDisplayWidth)
		return;
	    mLastDisplayWidth = displayWidth;
	}
	mQwertyKeyboard = new LatinKeyboard(this, R.xml.qwerty);
    }

    /**
     * Called by the framework when your view for creating input needs to be
     * generated. This will be called the first time your input method is
     * displayed, and every time it needs to be re-created such as due to a
     * configuration change.
     */
    @Override
    public View onCreateInputView() {
	mInputView = (KeyboardView) getLayoutInflater().inflate(R.layout.input,
		null);
	mInputView.setOnKeyboardActionListener(this);
	mInputView.setKeyboard(mQwertyKeyboard);
	return mInputView;
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

	this.printDebug("onStartInput");

	mComposing.setLength(0);

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

	this.printDebug("onFinishInput");
	// Clear current composing text and candidates.
	mComposing.setLength(0);

	if (mInputView != null) {
	    mInputView.closing();
	}

	// if (httpSrv != null)
	// httpSrv.stop();
	// httpSrv = null;
    }

    /**
     * When received focus
     */
    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
	super.onStartInputView(info, restarting);

	this.printDebug("onStartInputView");

	// Apply the selected keyboard to the input view.
	mInputView.setKeyboard(mQwertyKeyboard);
	mInputView.closing();

	List<Keyboard.Key> keys = mQwertyKeyboard.getKeys();
	keys.get(1).label = getLocalIpAddress() + ":" + port;
	mInputView.invalidateKey(1);

	// Apply the selected keyboard to the input view.
	mInputView.setKeyboard(mQwertyKeyboard);
	mInputView.closing();

	// InputConnection ic = getCurrentInputConnection();
	// ic.commitText("Hello", 0);
    }

    /**
     * Use this to monitor key events being delivered to the application. We get
     * first crack at them, and can either resume them or let them continue to
     * the app.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	switch (keyCode) {
	case KeyEvent.KEYCODE_BACK:
	    if (event.getRepeatCount() == 0 && mInputView != null) {
		if (mInputView.handleBack()) {
		    return true;
		}
	    }
	    break;

	case KeyEvent.KEYCODE_DEL:
	    if (mComposing.length() > 0) {
		onKey(Keyboard.KEYCODE_DELETE, null);
		return true;
	    }
	    break;

	case KeyEvent.KEYCODE_ENTER:
	    // Let the underlying text editor always handle these.
	    return false;
	}

	return super.onKeyDown(keyCode, event);
    }

    /**
     * Use this to monitor key events being delivered to the application. We get
     * first crack at them, and can either resume them or let them continue to
     * the app.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
	// If we want to do transformations on text being entered with a hard
	// keyboard, we need to process the up events to update the meta key
	// state we are tracking.

	return super.onKeyUp(keyCode, event);
    }

    /**
     * Helper function to commit any text being composed in to the editor.
     */
    private void commitTyped(InputConnection inputConnection) {
	if (mComposing.length() > 0) {
	    inputConnection.commitText(mComposing, mComposing.length());
	    mComposing.setLength(0);
	}
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
	if (primaryCode == Keyboard.KEYCODE_DELETE) {
	    handleBackspace();
	} else if (primaryCode == Keyboard.KEYCODE_CANCEL) {
	    handleClose();
	    return;
	}

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
	InputConnection ic = getCurrentInputConnection();
	if (ic == null)
	    return;
	ic.beginBatchEdit();
	if (mComposing.length() > 0) {
	    commitTyped(ic);
	}
	ic.commitText(text, 0);
	ic.endBatchEdit();

    }

    @Override
    public void swipeDown() {
	handleClose();
    }

    @Override
    public void swipeLeft() {
	handleBackspace();
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

    @Override
    public void onDestroy() {
	super.onDestroy();
	printDebug("Destroy");

	if (httpSrv != null)
	    httpSrv.stop();
	httpSrv = null;
    }

    private void printDebug(String msg) {
	if (PCKeyboard.DEBUG) {
	    Log.d(this.getClass().getName(), msg);
	}
    }

    private void handleClose() {
	commitTyped(getCurrentInputConnection());
	requestHideSelf(0);
	mInputView.closing();
    }

    private void handleBackspace() {
	final int length = mComposing.length();
	if (length > 1) {
	    mComposing.delete(length - 1, length);
	    getCurrentInputConnection().setComposingText(mComposing, 1);
	} else if (length > 0) {
	    mComposing.setLength(0);
	    getCurrentInputConnection().commitText("", 0);
	} else {
	    keyDownUp(KeyEvent.KEYCODE_DEL);
	}
    }

    /**
     * Helper to send a key down / key up pair to the current editor.
     */
    private void keyDownUp(int keyEventCode) {
	getCurrentInputConnection().sendKeyEvent(
		new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
	getCurrentInputConnection().sendKeyEvent(
		new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }

    private String getLocalIpAddress() {
	try {
	    String ip4s = "";
	    for (Enumeration<NetworkInterface> en = NetworkInterface
		    .getNetworkInterfaces(); en.hasMoreElements();) {
		NetworkInterface intf = en.nextElement();
		for (Enumeration<InetAddress> enumIpAddr = intf
			.getInetAddresses(); enumIpAddr.hasMoreElements();) {
		    InetAddress inetAddress = enumIpAddr.nextElement();

		    if (inetAddress.isSiteLocalAddress()) {
			String ip4 = inetAddress.getHostAddress().toString();
			if (!inetAddress.isLoopbackAddress()
				&& InetAddressUtils.isIPv4Address(ip4)) {
			    Log.d(TAG, "getLocalIpAddress(): " + ip4);
			    ip4s += ip4 + " ";
			}
		    }
		}
	    }

	    return ip4s;
	} catch (Exception e) {
	    Log.e(TAG, "ServerUtils: getLocalIpAddress(): " + e.getMessage());
	}
	return null;
    }
}
