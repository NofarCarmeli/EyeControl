package com.example.eyecontrol;

import java.util.Locale;

import android.app.Activity;
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


public class MainActivity extends Activity implements OnInitListener{ 
    
	// to read gestures
	private GestureDetectorCompat mDetector;
	// to speak
	private int MY_DATA_CHECK_CODE = 0;
	private TextToSpeech myTTS;
	// objects with jobs
	private static TextEditor text_editor;
	private static GestureTranslator translator;
	private static Definitions def;
	private static TextView mode_view;
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
		// to speak
		Intent checkTTSIntent = new Intent();
		checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
		// more static initializations
		((TextView)findViewById (R.id.cumulated_text)).setMovementMethod(ScrollingMovementMethod.getInstance());
		mode_view = ((TextView)findViewById (R.id.mode_name));
		TextView debug_view = ((TextView)findViewById (R.id.debug_string));
		board_view = (ImageView) findViewById(R.id.boardImageView);
		def = new Definitions();
		translator = new GestureTranslator(def, debug_view);
		text_editor = new TextEditor((TextView)findViewById (R.id.cumulated_text));
		alarm_player = MediaPlayer.create(getBaseContext(), R.raw.alarm);
		alarm_player.setLooping(true);
		audio_manager = (AudioManager)getSystemService(AUDIO_SERVICE);
		audio_manager.setMode(AudioManager.MODE_IN_CALL);
		// state initialization
		lang = def.first_language;
		changeModeDisplay(def.first_menu);
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
		}
	}
	
	public void onInit(int initStatus) {
		if (initStatus == TextToSpeech.SUCCESS) {
			myTTS.setLanguage(Locale.US);
		}
	}
	
	private void speakWords(String speech) {
		myTTS.speak(speech, TextToSpeech.QUEUE_ADD, null);
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
		mode_view.setText(def.menu_map.get(mode)+" mode:");
		int board_res_id = getResources().getIdentifier("menu"+mode,"drawable",getPackageName());
		board_view.setImageResource(board_res_id);
	}
	
	private void toggleBoardDisplay() {
		if (board_view.getVisibility() == View.VISIBLE) {
			board_view.setVisibility(View.GONE);
		} else {
			board_view.setVisibility(View.VISIBLE);
		}
	}
	
	private void readTextFromEditor() {
		String to_read = text_editor.getText();
		if (to_read.equals("")) {
			to_read = def.no_text;
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
			text_editor.addLetter(a.character);
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
 
}
