package com.example.eyecontrol;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.content.Context;
import android.content.res.AssetManager;
import android.widget.Toast;

public class PropertiesRetriever {
	
	private static Properties prop;
	Context context;
	
	PropertiesRetriever(Context context) {
		this.context = context;
		prop = new Properties();
		String propFileName = "config.properties";
		AssetManager assetManager = context.getAssets();
        InputStream inputStream;
		try {
			inputStream = assetManager.open(propFileName);
			prop.load(inputStream);
		} catch (IOException e) {
			Toast.makeText(context,"no properties file found" ,Toast.LENGTH_LONG).show();
		}
	}
	
	public String get(String key) {
		return prop.getProperty(key);
	}
	
	public int getNumber(String key) {
		return Integer.valueOf(prop.getProperty(key));
	}
	
	public String getMenuName(char c) {
		return prop.getProperty("menu_"+c);
	}
	
	public String getInstructions(char c) {
		return prop.getProperty("instructions_"+c);
	}
	
	public Action getAction(String seq) {
		Action a = null;
		String res = prop.getProperty(seq, "");
		// if action is defined
		if (!res.equals("")) {
			String[] params = res.split("\\s?~\\s?");
			// figure out action type
			Action.Type type = null;
			if (params[0].equals("POWER")) {
				type = Action.Type.POWER;
			} else if (params[0].equals("MODE")) {
				type = Action.Type.MODE;
			} else if (params[0].equals("ALARM")) {
				type = Action.Type.ALARM;
			} else if (params[0].equals("SPEAK")) {
				type = Action.Type.SPEAK;
			} else if (params[0].equals("LANGUAGE")) {
				type = Action.Type.LANGUAGE;
			} else if (params[0].equals("DISPLAY")) {
				type = Action.Type.DISPLAY;
			} else if (params[0].equals("CHARACTER")) {
				type = Action.Type.CHARACTER;
			} else if (params[0].equals("ERASE")) {
				type = Action.Type.ERASE;
			} else if (params[0].equals("READ")) {
				type = Action.Type.READ;
			} else if (params[0].equals("CLEAR")) {
				type = Action.Type.CLEAR;
			}
			// create action object
			if (params.length == 4) {
				char c = params[1].length()==0 ? ' ' : (params[1]).charAt(0);
				a = new Action(params[2],params[3],type,c);
			} else if (params.length == 3) {
				a = new Action(params[1],params[2],type);
			} else if (params.length == 2) {
				char c = params[1].length()==0 ? ' ' : (params[1]).charAt(0);
				a = new Action("", "", type,c);
			} else if (params.length == 1) {
				a = new Action("", "",type);
			}
		}
		return a;
	}

}
