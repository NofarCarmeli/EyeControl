package com.example.eyecontrol;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.view.GestureDetectorCompat;
import android.text.method.ScrollingMovementMethod;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements OnInitListener{ 
    
	// to read gestures
	private GestureDetectorCompat mDetector;
	// to speak
	private int MY_DATA_CHECK_CODE = 0;
	private TextToSpeech myTTS;
	// for bluetooth
	private int REQUEST_ENABLE_BT = 1;
	private BluetoothAdapter mBluetoothAdapter;
	// objects with jobs
	private static TextEditor text_editor;
	private static GestureTranslator translator;
	private static Definitions def;
	private static TextView mode_view;
	private static TextView instructions_view;
	private static ImageView board_view;
	private static MediaPlayer alarm_player;
	private static AudioManager audio_manager;
	// mode variables
	private boolean verbose = true;
	private char lang;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// to read gestures
		mDetector = new GestureDetectorCompat(this, new MyGestureListener());
		// activate bluetooth // TODO BT
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
		    // Device does not support Bluetooth
			Toast.makeText(getApplicationContext(),"Device does not support Bluetooth" 
			         ,Toast.LENGTH_LONG).show();
			return;
		}
		if (!mBluetoothAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		// to speak
		Intent checkTTSIntent = new Intent();
		checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
		// more static initializations
		((TextView)findViewById (R.id.cumulated_text)).setMovementMethod(ScrollingMovementMethod.getInstance());
		mode_view = ((TextView)findViewById (R.id.mode_name));
		TextView last_gesture_view = ((TextView)findViewById (R.id.last_gesture));
		board_view = (ImageView) findViewById(R.id.boardImageView);
		def = new Definitions();
		translator = new GestureTranslator(def, last_gesture_view);
		text_editor = new TextEditor((TextView)findViewById (R.id.cumulated_text));
		alarm_player = MediaPlayer.create(getBaseContext(), R.raw.alarm);
		alarm_player.setLooping(true);
		audio_manager = (AudioManager)getSystemService(AUDIO_SERVICE);
		audio_manager.setMode(AudioManager.MODE_IN_CALL);
		instructions_view = ((TextView)findViewById (R.id.boardDisplayInstructions));
		// state initialization
		lang = def.first_language;
		changeModeDisplay(def.first_menu);
		instructions_view.setVisibility(View.GONE);
	}
	
	// to show menu:
	
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
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	// text to speech methods:
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MY_DATA_CHECK_CODE) {
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {      
				myTTS = new TextToSpeech(this, this);
			} else {
				Intent installTTSIntent = new Intent();
				installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installTTSIntent);
			}
		} else if (requestCode == REQUEST_ENABLE_BT)  { //TODO BT
			if (resultCode == RESULT_CANCELED) {
				Toast.makeText(getApplicationContext(),"Bluetooth must be activated" 
				         ,Toast.LENGTH_LONG).show();
			} /*else {
				BluetoothDevice mDevice; //TODO
				ConnectThread mConnectThread = new ConnectThread(mDevice);
				mConnectThread.start();

			}*/
		}
	}
	
	public void onInit(int initStatus) {
		if (initStatus == TextToSpeech.SUCCESS) {
			myTTS.setLanguage(Locale.US);
		}
	}
	
	private void speakWords(String speech) {
		myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null); // can use QUEUE_ADD
	}
	
	// gesture identification: (will be replaced with bluetooth receiver)
	
	@Override 
	public boolean onTouchEvent(MotionEvent event){ 
		this.mDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
    
	class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
	    
		private static final int SWIPE_DISTANCE_THRESHOLD = 100;
		private static final int SWIPE_VELOCITY_THRESHOLD = 100;
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			float distanceX = e2.getX() - e1.getX();
			float distanceY = e2.getY() - e1.getY();
			if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
				if (distanceX > 0) {
				   	getGesture('R');
				} else {
					getGesture('L');
				}
			} else if (Math.abs(distanceY) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
				if (distanceY > 0) {
					getGesture('D');
				} else {
					getGesture('U');
				}
			}
			return true;
		}
		 
		public boolean onDoubleTap(MotionEvent event) {
			getGesture('B');
			return true;
		}
		
	}
    
	// action helpers:
    
	public void onToggleClicked(View view) {
		toggleAlarm();
	}
    
	private void toggleAlarm() {
		Button but = (Button) findViewById(R.id.alarmToggleButton);
		if (alarm_player.isPlaying()) {
			alarm_player.stop();
			alarm_player.prepareAsync();
			but.setVisibility(View.GONE);
		} else {
			audio_manager.setSpeakerphoneOn(true);
			alarm_player.start();
			but.setVisibility(View.VISIBLE);
		}
	}
	
	private void speakRecordedSentence(char sentence_id) {
		int res_id = getResources().getIdentifier(String.valueOf(lang)+sentence_id,"raw",getPackageName());
		final MediaPlayer mediaPlayer = MediaPlayer.create(getBaseContext(), res_id);
		audio_manager.setSpeakerphoneOn(true);
		mediaPlayer.start();
		Handler sHandler = new Handler();
		sHandler.postDelayed(new Runnable() {
			public void run() {
				mediaPlayer.release();
			}
		}, 1000);
	}
	
	private void changeModeDisplay(char mode) {
		mode_view.setText(def.menu_map.get(mode)+" Mode:");
		int board_res_id = getResources().getIdentifier("menu"+mode,"drawable",getPackageName());
		board_view.setImageResource(board_res_id);
		instructions_view.setText(def.instructions.get(mode));
	}
	
	private void toggleBoardDisplay() {
		if (board_view.getVisibility() == View.VISIBLE) {
			board_view.setVisibility(View.GONE);
			instructions_view.setVisibility(View.VISIBLE);
		} else {
			instructions_view.setVisibility(View.GONE);
			board_view.setVisibility(View.VISIBLE);
		}
	}
	
	private void readTextFromEditor() {
		String to_read = text_editor.extractText();
		if (to_read.equals("") || to_read.equals(" ")) {
			to_read = lang=='e' ? def.eng_no_text : def.heb_no_text;
		}
		audio_manager.setSpeakerphoneOn(true);
		speakWords(to_read);
	}
	
	private void readActionDescription(Action a){
		audio_manager.setSpeakerphoneOn(false);
		if (lang=='e' && !a.eng_desc.equals("")) {
			speakWords(a.eng_desc);
		} else if (lang=='h' && !a.heb_desc.equals("")) {
			speakWords(a.heb_desc);
		}
	}
	
	private void powerOff() {
		// turn alarm off if it is working, and release the player
		if (alarm_player.isPlaying()) {
			alarm_player.stop();
		}
		alarm_player.release();
		// turn off
		Handler pHandler = new Handler();
		pHandler.postDelayed(new Runnable() {
			public void run() {
				finish();
			}
		}, 1000);
	}

    
	// handling gestures:
    
	public void getGesture(char c) {
		Action a = translator.getGesture(c);
		if (a ==null) {
			return;
		}
		if (verbose) {
			readActionDescription(a);
		}
		
		switch (a.action) {
		case POWER:
			powerOff();
			break;
		case MODE:
			changeModeDisplay(a.character);	
			break;
		case ALARM:
			toggleAlarm();
			break;
		case SPEAK:
			speakRecordedSentence(a.character);
			break;
		case LANGUAGE:
			lang = (lang=='e')? 'h' : 'e';
			break;
		case CHARACTER:
			if (a.character == ' ' && text_editor.isLastSpace()) {
				readTextFromEditor();
			} else {
				text_editor.addLetter(a.character);
			}
			break;
		case ERASE:
			text_editor.deleteLetter();
			break;
		case READ:
			readTextFromEditor();
			break;
		case CLEAR:
			text_editor.clear();
			break;
		case DISPLAY:
			toggleBoardDisplay();
			break;
		default:
			break;
		}
	}
	
	//TODO BT
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
