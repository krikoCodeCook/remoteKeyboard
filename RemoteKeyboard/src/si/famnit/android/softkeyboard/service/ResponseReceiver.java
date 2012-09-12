package si.famnit.android.softkeyboard.service;

import si.famnit.android.softkeyboard.PCKeyboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.inputmethod.InputConnection;

public class ResponseReceiver extends BroadcastReceiver {
    private PCKeyboard remoteKeyboard;
    public static final String ACTION_RESP = "MESSAGE_PROCESSED";
    public static final String PARAM_MSG = "MSG";

    public ResponseReceiver(PCKeyboard pcKeyboard) {
	this.remoteKeyboard = pcKeyboard;
	InputConnection ic = this.remoteKeyboard.getCurrentInputConnection();

	if (ic != null)
	    ic.commitText("Test from receiver", 0);
	// TODO Auto-generated constructor stub
    }

    @Override
    public void onReceive(Context context, Intent intent) {
	// TODO Auto-generated method stub
	this.remoteKeyboard.sendMessage("Hello from receiver!");
    }

}
