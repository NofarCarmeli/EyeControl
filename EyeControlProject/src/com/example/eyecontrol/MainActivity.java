package com.example.eyecontrol;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.GestureDetectorCompat;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity  {
    
	// for TTS initialization
	private int MY_DATA_CHECK_CODE = 0;
	// for Bluetooth
	private int REQUEST_ENABLE_BT = 1;
	private BluetoothAdapter bluetooth_adapter;
	// objects with responsibilities
	private static Definitions def;
	private static PropertiesRetriever properties;
	private GestureDetectorCompat gesture_detector;
	private static TextEditor text_editor;
	private static GestureTranslator translator;
	private static AudioController audio;
	private static DisplayManipulator display;
	// state variables
	private char lang;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// initialize view
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// activate bluetooth
		bluetooth_adapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetooth_adapter == null) {
		    // Device does not support Bluetooth
			Toast.makeText(getApplicationContext(),"Device does not support Bluetooth" 
			         ,Toast.LENGTH_LONG).show();
			return;
		}
		if (!bluetooth_adapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		// initialize TTS
		Intent checkTTSIntent = new Intent();
		checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
		// initialize objects
		((TextView)findViewById (R.id.cumulated_text)).setMovementMethod(ScrollingMovementMethod.getInstance());
		def = new Definitions();
		properties = new PropertiesRetriever(getBaseContext());
		gesture_detector = new GestureDetectorCompat(this, new GestureListener(this));
		text_editor = new TextEditor((TextView)findViewById (R.id.cumulated_text));
		display = new DisplayManipulator(getBaseContext(), properties, findViewById(android.R.id.content));
		translator = new GestureTranslator(def, properties, display);
		audio = new AudioController(getBaseContext(), properties);
		// initialize language state
		lang = properties.get("first_language").charAt(0);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// initialize TTS
		if (requestCode == MY_DATA_CHECK_CODE) {
			audio.initTTS(resultCode);
		} else
		// initialize Bluetooth
		if (requestCode == REQUEST_ENABLE_BT)  {
			if (resultCode == RESULT_CANCELED) {
				Toast.makeText(getApplicationContext(),"Please turn Bluetooth on.\nThis app does not work without Bluetooth." 
				         ,Toast.LENGTH_LONG).show();
			} else {
				startBluetoothConnection();
			}
		}
	}
	
	// gesture identification initialization
	@Override 
	public boolean onTouchEvent(MotionEvent event){ 
		this.gesture_detector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
	
	// Action bar overflow menu functions
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.user_guide:
	        	Intent intent = new Intent(this, UserGuide.class);
	        	startActivity(intent);
	            return true;
	        case R.id.heb_user_guide:
	        	Intent heb_intent = new Intent(this, HebUserGuide.class);
	        	startActivity(heb_intent);
	            return true;
	        case R.id.toggle_screen:
	        	display.toggleScreenLock();
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

    
	// Called when alarm button is clicked
	public void onToggleClicked(View view) {
		audio.toggleAlarm(lang);
		display.toggleAlarmButton();
	}

	
	// Called when gesture is received
	public void onGestureReceived(char c) {
		Action a = translator.getGesture(c);
		if (a ==null) {
			return;
		}
		audio.readActionDescription(a, lang);
		switch (a.action) {
		case POWER:
			Handler pHandler = new Handler();
			pHandler.postDelayed(new Runnable() {
				public void run() {
					audio.release();
					finish();
				}
			}, 3000);
			break;
		case MODE:
			display.setMode(a.character);
			break;
		case ALARM:
			audio.toggleAlarm(lang);
			display.toggleAlarmButton();
			break;
		case SPEAK:
			audio.speakRecordedSentence(a.character, lang);
			break;
		case LANGUAGE:
			lang = (lang=='e')? 'h' : 'e';
			break;
		case CHARACTER:
			if (a.character == ' ' && text_editor.isLastSpace()) {
				audio.readAloud(text_editor.extractText(), lang);
			} else {
				text_editor.addLetter(a.character);
			}
			break;
		case ERASE:
			text_editor.deleteLetter();
			break;
		case READ:
			audio.readAloud(text_editor.extractText(), lang);
			break;
		case CLEAR:
			text_editor.clear();
			break;
		case DISPLAY:
			display.toggleBoard();
			break;
		default:
			break;
		}
	}
	
	
	// Bluetooth functions
	
	private void startBluetoothConnection() {
		// find the device
		BluetoothDevice mDevice = null;
		Set<BluetoothDevice> pairedDevices = bluetooth_adapter.getBondedDevices();
		for(BluetoothDevice bt : pairedDevices) {
	         if (bt.getName().equals("Eye Control")) {
	        	 mDevice = bt;
	         }
		}
		if (mDevice == null) {
			Toast.makeText(getApplicationContext(),"No Eye Control device found" 
			         ,Toast.LENGTH_LONG).show();
			// turn off?
			return;
		}
		// connect
		/*ConnectThread mConnectThread = new ConnectThread(mDevice);
		mConnectThread.start();*/
	}
	
	//TODO
	/*
	private class ConnectThread extends Thread {
	    private final BluetoothSocket mmSocket;
	    private final BluetoothDevice mmDevice;
	 
	    public ConnectThread(BluetoothDevice device) {
	        // Use a temporary object that is later assigned to mmSocket,
	        // because mmSocket is final
	        BluetoothSocket tmp = null;
	        mmDevice = device;
	 
	        // Get a BluetoothSocket to connect with the given BluetoothDevice
	        try {
	            // MY_UUID is the app's UUID string, also used by the server code
	            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
	        } catch (IOException e) { }
	        mmSocket = tmp;
	    }
	 
	    public void run() {
	        // Cancel discovery because it will slow down the connection
	        mBluetoothAdapter.cancelDiscovery();
	 
	        try {
	            // Connect the device through the socket. This will block
	            // until it succeeds or throws an exception
	            mmSocket.connect();
	        } catch (IOException connectException) {
	            // Unable to connect; close the socket and get out
	            try {
	                mmSocket.close();
	            } catch (IOException closeException) { }
	            return;
	        }
	    }
	 
	    // Will cancel an in-progress connection, and close the socket
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) { }
	    }
	}
	
	private class ConnectedThread extends Thread {
	    private final BluetoothSocket mmSocket;
	    private final InputStream mmInStream;
	 
	    public ConnectedThread(BluetoothSocket socket) {
	        mmSocket = socket;
	        InputStream tmpIn = null;
	 
	        // Get the input and output streams, using temp objects because
	        // member streams are final
	        try {
	            tmpIn = socket.getInputStream();
	        } catch (IOException e) { }
	 
	        mmInStream = tmpIn;
	    }
	 
	    public void run() {
	        byte[] buffer = new byte[1024];  // buffer store for the stream
	        int begin = 0;
	        int bytes = 0; // bytes returned from read()
	 
	        Handler mHandler;
			// Keep listening to the InputStream until an exception occurs
	        while (true) {
	            try {
	            	bytes += mmInStream.read(buffer, bytes, buffer.length - bytes);
	     	        for(int i = begin; i < bytes; i++) {
	     		        if(buffer[i] == "#".getBytes()[0]) {
	     		        	mHandler.obtainMessage(1, begin, i, buffer).sendToTarget();
	     		        	begin = i + 1;
	     		        	if(i == bytes - 1) {
	     		        		bytes = 0;
	     		        		begin = 0;
	     		        	}
	     		        } 
	     	        }
	            } catch (IOException e) {
	                break;
	            }
	        }

	    }
	 
	    // Call this from the main activity to shutdown the connection 
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) { }
	    }
	}
	*/
	
 
}
