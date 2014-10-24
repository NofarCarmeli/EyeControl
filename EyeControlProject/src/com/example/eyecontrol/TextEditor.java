package com.example.eyecontrol;

import android.widget.TextView;


class TextEditor {
	
	private TextView text_view;
	
	TextEditor(TextView view) {
		text_view = view;
	}
	
	public void addLetter(char c) {
		String text = text_view.getText().toString();
		text_view.setText(text+c);
	}
	
	public void deleteLetter() {
		String text = text_view.getText().toString();
		if (text.length() > 0) {
			text = text.substring(0,text.length() - 1);
		}
		text_view.setText(text);
	}
	
	public void clear() {
		text_view.setText("");
	}
	
	public String getText() {
		return text_view.getText().toString();
	}
	
	public boolean isLastSpace() {
		String text = text_view.getText().toString();
		return text.length() > 0 && text.charAt(text.length()-1)==' ';
	}

}