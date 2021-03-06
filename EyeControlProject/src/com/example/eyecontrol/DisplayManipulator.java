package com.example.eyecontrol;

import android.content.Context;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayManipulator {
	
	private static Context context;
	private PropertiesRetriever properties;
	private static TextView mode_view;
	private static TextView instructions_view;
	private static TextView gesture_view;
	private static ImageView board_view;
	private static Button alarm_button;
	
	DisplayManipulator(Context con, PropertiesRetriever properties, View view) {
		this.properties = properties;
		context = con;
		alarm_button = (Button) view.findViewById(R.id.alarmToggleButton);
		mode_view = (TextView) view.findViewById(R.id.mode_name);
		instructions_view = (TextView) view.findViewById(R.id.boardDisplayInstructions);
		board_view = (ImageView) view.findViewById(R.id.boardImageView);
		gesture_view = ((TextView)view.findViewById (R.id.last_gesture));
		initDisplay();
	}
	
	private void initDisplay() {
		instructions_view.setVisibility(View.GONE);
		setMode(properties.get("first_menu").charAt(0));
	}
	
	public void setLastGesture(char gesture) {
		String text = "";
		switch (gesture) {
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
		gesture_view.setText("Last gesture: "+text);
		Animation anim = new AlphaAnimation(0.0f, 1.0f);
		anim.setDuration(50);
		gesture_view.startAnimation(anim);
	}
	
	public void setMode(char mode) {
		mode_view.setText(properties.getMenuName(mode)+" Mode");
		int board_res_id = context.getResources().getIdentifier(
				"menu"+mode,"drawable",context.getPackageName());
		board_view.setImageResource(board_res_id);
		instructions_view.setText(properties.getInstructions(mode));
	}
	
	public void toggleBoard() {
		if (board_view.getVisibility() == View.VISIBLE) {
			board_view.setVisibility(View.GONE);
			instructions_view.setVisibility(View.VISIBLE);
		} else {
			instructions_view.setVisibility(View.GONE);
			board_view.setVisibility(View.VISIBLE);
		}
	}
	
	public void toggleAlarmButton() {
		if (alarm_button.getVisibility() == View.VISIBLE) {
			alarm_button.setVisibility(View.GONE);
		} else {
			alarm_button.setVisibility(View.VISIBLE);
		}
	}
	
	public void toggleScreenLock() {
		if (mode_view.getKeepScreenOn()) {
    		mode_view.setKeepScreenOn(false);
    		Toast.makeText(context,
    				"Screen will turn off as usual" 
			         ,Toast.LENGTH_LONG).show();
    	} else {
    		mode_view.setKeepScreenOn(true);
    		Toast.makeText(context,
    				"Screen will stay on" 
			         ,Toast.LENGTH_LONG).show();
    	}
	}
}
