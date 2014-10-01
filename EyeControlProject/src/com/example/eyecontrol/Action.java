package com.example.eyecontrol;

public class Action {

	public enum Type {
		POWER, MODE, ALARM, SPEAK, LANGUAGE, DISPLAY, CHARACTER, ERASE, READ, CLEAR
	}
	
	public Type action;
	public char character;
	public String eng_desc;
	public String heb_desc;
	
	public Action(String es, String hs, Type t, char c) {
		action = t;
		character = c;
		eng_desc = es;
		heb_desc = hs;
	}
	
	public Action(String es, String hs, Type t) {
		action = t;
		eng_desc = es;
		heb_desc = hs;
	}

}
