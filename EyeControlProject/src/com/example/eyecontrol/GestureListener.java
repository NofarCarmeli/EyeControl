package com.example.eyecontrol;

import android.view.GestureDetector;
import android.view.MotionEvent;

class GestureListener extends GestureDetector.SimpleOnGestureListener {
    
	private static final int SWIPE_DISTANCE_THRESHOLD = 100;
	private static final int SWIPE_VELOCITY_THRESHOLD = 100;
	MainActivity activity;
	
	GestureListener(MainActivity a) {
		activity = a;
	}
	
	// detects swipes
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
	 
	// detects double tap
	public boolean onDoubleTap(MotionEvent event) {
		getGesture('B');
		return true;
	}
	
	// this function determines what happens when gesture c is received
	private void getGesture(char c) {
		activity.onGestureReceived(c);
	}
	
}