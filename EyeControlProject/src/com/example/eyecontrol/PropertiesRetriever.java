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
	
	public String getMenuName(char c) {
		return prop.getProperty("menu_"+c);
	}
	
	public String getInstructions(char c) {
		return prop.getProperty("instructions_"+c);
	}

}
