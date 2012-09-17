package si.famnit.android.softkeyboard;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

public class RemoteKeyboardServer extends NanoHTTPD {
    private final String[] assetFiles = { "index.html",
	    "bootstrap-combined.min.css", "jquery.min.js", "88x31.png", "script.js", "style.css", "bootstrap.min.js" };
    private PowerManager.WakeLock wakeLock;
    private PCKeyboard pcK;
    private Context context;

    public RemoteKeyboardServer(PCKeyboard pcKeyboard, Context ctx, int port)
	    throws IOException {
	super(port, new File(".").getAbsoluteFile());

	pcK = pcKeyboard;

	PowerManager pm = (PowerManager) pcK
		.getSystemService(Context.POWER_SERVICE);
	wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
		| PowerManager.ON_AFTER_RELEASE, "wifikeyboard");

	context = ctx;
    }

    public Response serve(String uri, String method, Properties header,
	    Properties parms, Properties files) {

	InputStream input = null;

	if (method.equalsIgnoreCase("POST")) {
	    Log.d("WEB", parms.getProperty("text"));
	    pcK.sendMessage(parms.getProperty("text"));
	    
	    return new NanoHTTPD.Response(HTTP_OK, MIME_PLAINTEXT, "ok");
	}

	if (uri.contains("no-sleep")) {
	    // wake up
	    wakeLock.acquire();
	    wakeLock.release();

	    return new NanoHTTPD.Response(HTTP_OK, MIME_PLAINTEXT, "ok");
	}

	input = loadAsset(uri);

	if (input == null) {
	    return new NanoHTTPD.Response(HTTP_NOTFOUND, MIME_HTML,
		    "File not found");
	}

	String mime = MIME_HTML;

	if (uri.contains(".html")) {
	    mime = MIME_HTML;
	} else if (uri.contains(".js")) {
	    mime = "text/javascript";
	} else if (uri.contains(".css")) {
	    mime = "text/css";
	} else if (uri.contains(".png")) {
	    mime = "image/png";
	}
	

	return new NanoHTTPD.Response(HTTP_OK, mime, input);
    }

    private InputStream loadAsset(String asset) {

	InputStream input = null;

	if (asset.length() > 1) {
	    asset = asset.substring(1, asset.length());
	}

	if (!Arrays.asList(assetFiles).contains(asset)) {
	    asset = "index.html";
	}

	try {
	    input = context.getAssets().open(asset);
	} catch (IOException e) {
	    e.printStackTrace();
	}

	return input;
    }

    // private void displayFiles(AssetManager mgr, String path) {
    // try {
    // String list[] = mgr.list(path);
    // if (list != null)
    // for (int i = 0; i < list.length; ++i) {
    // Log.v("Assets:", path + "/" + list[i]);
    // displayFiles(mgr, path + "/" + list[i]);
    // }
    // } catch (IOException e) {
    // Log.v("List error:", "can't list" + path);
    // }
    //
    // }
}