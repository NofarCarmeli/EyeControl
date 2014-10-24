package com.example.eyecontrol;

import android.widget.TextView;


class TextEditor {
	
	private TextView text_view;
	private String saved_text;
	
	TextEditor(TextView view) {
		text_view = view;
		saved_text = "";
	}
	
	private boolean isEmpty() {
		String text = text_view.getText().toString();
		return text.equals("") || text.equals(" ");
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
	
	// returns the text from the editor, saves it, and clears the editor
	// if the editor is empty, it returns the last saved texts
	public String extractText() {
		String text =  text_view.getText().toString();
		if (!isEmpty()) {
			saved_text = text;
			clear();
		} else {
			text = saved_text;
		}
		return text;
	}
	
	public boolean isLastSpace() {
		String text = text_view.getText().toString();
		return text.length() > 0 && text.charAt(text.length()-1)==' ';
	}

}