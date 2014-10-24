package com.example.eyecontrol;

import android.widget.TextView;


public class GestureTranslator {    
	
	static private TextView last_gesture;
	static private Definitions def;
	
	private char mode;
	private String gestures = String.valueOf(mode);
	private long last_time;

	GestureTranslator(Definitions d, TextView debug_v) {
		last_gesture = debug_v;
		def = d;
		mode = def.first_menu;
		last_time = System.nanoTime();
		clearGestures();
	}
	
	private void clearGestures() {
		gestures=String.valueOf(mode);
	}
	
	private void addToGestures(char c) {
		gestures+=c;
		updateLastGestureDisplay(c);
	} 
	
	private void updateLastGestureDisplay(char c) {
		String text = "";
		switch (c) {
		case 'U':
			text = "Up";
			break;
		case 'D':
			text = "Down";
			break;
		case 'R':
			text = "Right";
			break;
		case 'L':
			text = "Left";
			break;
		case 'B':
			text = "Blink";
			break;
		}
		last_gesture.setText("Last gesture: "+text);
	}

	
	public Action getGesture(char c) {
	    
		// clear previous gestures if too long has passed
		long cur_time = System.nanoTime();
		long allowed_time_difference = 1000000000*def.time_between_gestures;
		if (cur_time-last_time>allowed_time_difference) {
			clearGestures();
		}
		last_time = cur_time;
		
		// add gesture and check for translation
		addToGestures(c);
		Action a = def.seq_map.get(gestures);
		if (a!=null) {
			if (a.action==Action.Type.MODE) {
				mode = a.character;
			}
			clearGestures(); // should be after changing mode
		} else if (gestures.contains(def.alarm_seq)) {
			a = new Action ("", "", Action.Type.ALARM);
			clearGestures();
		}
		return a;
	}
    
}
