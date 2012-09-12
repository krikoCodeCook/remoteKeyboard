package com.example.android.softkeyboard;


import java.io.File;
import java.io.IOException;
import java.util.Properties;

import com.example.android.softkeyboard.service.ResponseReceiver;

import android.content.Intent;
import android.util.Log;



public class RemoteKeyboardServer extends NanoHTTPD
{
	String msg = "File not loaded!";
	PCKeyboard pcK;
	public RemoteKeyboardServer(PCKeyboard pcKeyboard) throws IOException
	{
		super(9998, new File(".").getAbsoluteFile());
		pcK = pcKeyboard;
		
		msg = "<form action=\"\" method=\"POST\">";
		msg += "<div class=\"hero-unit\">";
        msg += "<h1>Remote Keyboard</h1>";
        msg += "<p>Write down and click the send button in order to send the text to your device.</p>";
        msg += "<div class=\"controls\"><textarea name=\"text\" rows=\"5\" autofocus></textarea></div>";
        msg += "<p><input type=\"submit\" class=\"btn btn-primary btn-large\" value=\"Send\" /></p></div></form>";
		
	}

	public Response serve( String uri, String method, Properties header, Properties parms, Properties files )
	{			
		Log.i("WEB", "Method: "+method);
		if (method.equalsIgnoreCase("POST"))
		{
			Log.i("WEB", "POST: "+parms.getProperty("text"));

			pcK.sendMessage(parms.getProperty("text"));
		}
		
		return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, msg);
	}

	
	/*public static void main( String[] args )
	{
		try
		{
			new RemoteKeyboardServer();
		}
		catch( IOException ioe )
		{
			System.err.println( "Couldn't start server:\n" + ioe );
			System.exit( -1 );
		}
		System.out.println( "Listening on port 9999. Hit Enter to stop.\n" );
		try { System.in.read(); } catch( Throwable t ) {};
	}*/

}