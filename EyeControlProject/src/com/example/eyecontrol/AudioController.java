package com.example.eyecontrol;

import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;



public class AudioController implements OnInitListener {
	
	private static Context context;
	private static Definitions def;
	private static MediaPlayer alarm_player;
	private static AudioManager audio_manager;
	private TextToSpeech myTTS;
	
	AudioController(Context con, Definitions defs) {
		def = defs;
		context = con;
		alarm_player = MediaPlayer.create(context, R.raw.alarm);
		alarm_player.setLooping(true);
		audio_manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		audio_manager.setMode(AudioManager.MODE_IN_CALL);
	}
	
	// inits TTS
	// should be called after TextToSpeech.Engine.ACTION_CHECK_TTS_DATA returns
	protected void initTTS(int resultCode) {
		if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {      
			myTTS = new TextToSpeech(context, this);
		} else {
			Intent installTTSIntent = new Intent();
			installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
			context.startActivity(installTTSIntent);
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
	
	private void cancelSpeaking() {
		myTTS.stop();
	}
	
	
	// reads the given sentence. replaces with predetermined sentences if empty.
	public void readAloud(String to_read, char lang) {
		if (to_read.equals("") || to_read.equals(" ")) {
			to_read = lang=='e' ? def.eng_no_text : def.heb_no_text;
		}
		audio_manager.setSpeakerphoneOn(true);
		speakWords(to_read);
	}
	
	
	// gets an action, and reads its description according to the language
	public void readActionDescription(Action a, char lang){
		audio_manager.setSpeakerphoneOn(false);
		if (lang=='e' && !a.eng_desc.equals("")) {
			speakWords(a.eng_desc);
		} else if (lang=='h' && !a.heb_desc.equals("")) {
			speakWords(a.heb_desc);
		}
	}
	
	
	// toggles the alarm sound
	public void toggleAlarm() {
		if (alarm_player.isPlaying()) {
			alarm_player.stop();
			alarm_player.prepareAsync();
		} else {
			cancelSpeaking();
			audio_manager.setSpeakerphoneOn(true);
			alarm_player.start();
		}
	}
	
	
	// reads the recorded sentence according to its id in the given language
	public void speakRecordedSentence(char sentence_id, char lang) {
		// create a player for the sentence
		int res_id = context.getResources().getIdentifier(String.valueOf(lang)+sentence_id,"raw",context.getPackageName());
		final MediaPlayer mediaPlayer = MediaPlayer.create(context, res_id);
		// order release of the player when it stops
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
	        @Override
	        public void onCompletion(MediaPlayer mediaPlayer) {
	            mediaPlayer.stop();
	            if (mediaPlayer != null) {
	            	mediaPlayer.release();
	            }
	        }
	    });
		// play the sentence
		cancelSpeaking();
		audio_manager.setSpeakerphoneOn(true);
		mediaPlayer.start();
	}
	
	
	// releases audio resources and returns audio settings to normal
	public void release() {
		// release TTS engine
		myTTS.shutdown();
		// release alarm player
		if (alarm_player.isPlaying()) {
			alarm_player.stop();
		}
		if (alarm_player != null) {
			alarm_player.release();
        }
		// go back to normal audio mode
		audio_manager.setMode(AudioManager.MODE_NORMAL);
	}
	
}
