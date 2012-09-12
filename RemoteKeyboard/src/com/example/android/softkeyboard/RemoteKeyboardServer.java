package com.example.android.softkeyboard;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.example.android.softkeyboard.service.ResponseReceiver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;



public class RemoteKeyboardServer extends NanoHTTPD
{	
	String msg;
	PCKeyboard pcK;
	Context context;
	
	public RemoteKeyboardServer(PCKeyboard pcKeyboard, Context ctx) throws IOException
	{
		super(9998, new File(".").getAbsoluteFile());
		pcK = pcKeyboard;
		
		context = ctx;
		
		InputStream input;
		String text = "File not loaded!";
		try
		{
			input = context.getAssets().open("index.html");
			int size = input.available();
			byte[] buffer = new byte[size];
	        input.read(buffer);
	        input.close();
	        text = new String(buffer);
		}
		catch (IOException e) {
			
		}

		msg = text;
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

}