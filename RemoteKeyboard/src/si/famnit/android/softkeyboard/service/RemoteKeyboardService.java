package si.famnit.android.softkeyboard.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

/**
 * This is a subclass of Service that uses a worker thread to handle all start
 * requests, one at a time. This is the best option if you don't require that
 * your service handle multiple requests simultaneously. All you need to do is
 * implement onHandleIntent(), which receives the intent for each start request
 * so you can do the background work.
 */
public class RemoteKeyboardService extends IntentService {

    /**
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a name for the worker thread.
     */
    public RemoteKeyboardService() {
	super("RemoteKeyboardService");
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns,
     * IntentService stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
	// do the work

    }
    
}


