package com.example.eyecontrol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class BluetoothService {
	private static final String TAG = "my log";

	private final BluetoothAdapter mAdapter;
	private final Handler mHandler;
	private final Context mContext;

	BluetoothService(Context context, Handler handler) {
		Log.d(TAG, "initialize variables");

		// initialize variables
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mHandler = handler;
		mContext = context;

		// find the device
		BluetoothDevice mDevice = null;
		Set<BluetoothDevice> pairedDevices = mAdapter.getBondedDevices();
		for(BluetoothDevice bt : pairedDevices) {
			if (bt.getName().equals("ELI-BOOK-L-0")) { //TODO change name
				//if (bt.getName().equals("Msbeny")) { //TODO change name
				mDevice = bt;
			}
		}
		if (mDevice == null) {
			Toast.makeText(context,"No Eye Control device found" 
					,Toast.LENGTH_LONG).show();
			// turn off?
			return;
		}
		// connect
		//Toast.makeText(mContext,"Debug: device found",Toast.LENGTH_LONG).show();
		ConnectThread mConnectThread = new ConnectThread(mDevice);
		//Toast.makeText(mContext,"still here 1",Toast.LENGTH_LONG).show();
		mConnectThread.start();
		//Toast.makeText(mContext,"still here 3",Toast.LENGTH_LONG).show();
	}


	private class ConnectThread extends Thread {
		private BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;

		private final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

		public ConnectThread(BluetoothDevice device) {
			Log.d(TAG, "ConnectThread creat start");
			// Use a temporary object that is later assigned to mmSocket,
			// because mmSocket is final
			BluetoothSocket tmp = null;
			mmDevice = device;

			// Get a BluetoothSocket to connect with the given BluetoothDevice
			try {
				// MY_UUID is the app's UUID string, also used by the server code
				Method m = mmDevice.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
				tmp = (BluetoothSocket) m.invoke(device, Integer.valueOf(1)); // 1==RFCOMM channel code 
				
				//tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
			} catch (Exception e) {	Log.d(TAG, "ConnectThread IOException"); }
			mmSocket = tmp;
			Log.d(TAG, "ConnectThread creat end");
		}

		public void run() {
			Log.d(TAG, "run ConnectThread start");

			// Cancel discovery because it will slow down the connection
			mAdapter.cancelDiscovery();

			try {
				// Connect the device through the socket. This will block
				// until it succeeds or throws an exception
				Log.d(TAG, "run ConnectThread connect");
				mmSocket.connect();
				Log.d(TAG, "run ConnectThread connected");
			} catch (IOException connectException) {
				// Unable to connect; close the socket and get out
				Log.e(TAG, "run ConnectThread ERROR");
				try {
					Log.d(TAG,"trying fallback...");

					mmSocket =(BluetoothSocket) mmDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(mmDevice,1);
					mmSocket.connect();

					Log.d(TAG,"Connected");
				}
				catch (Exception closeException) { 
					try {
						Log.e(TAG,"no...");
						mmSocket.close();
					}
					catch (IOException exception) { }
				} 
				return;
			}

			// Do work to manage the connection (in a separate thread)
			//  Toast.makeText(mContext,"Debug: connection made",Toast.LENGTH_LONG).show();
			manageConnectedSocket(mmSocket);
		}

		// Will cancel an in-progress connection, and close the socket 
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) { }
		}
	}


	private void manageConnectedSocket(BluetoothSocket mmSocket) {
		Log.d(TAG, "manageConnectedSocket start");
		InputStream tmpIn = null;
		OutputStream tmpOut = null;

		// Get the input and output streams, using temp objects because
		// member streams are final
		try {
			tmpIn = mmSocket.getInputStream();
			tmpOut = mmSocket.getOutputStream();
		} catch (IOException e) { }

		InputStream mmInStream = tmpIn;
		OutputStream mmOutStream = tmpOut;

		// run
		
		
//		try {
//			String s = "Hello";
//			mmOutStream.write(s.getBytes(),0,s.length());// send data check
//		} catch (IOException e1) {}

		int gesture = 0; // bytes returned from read()

		// Keep listening to the InputStream until an exception occurs
		Log.d(TAG, "manageConnectedSocket while");
		while (true) {
			try {
				// Read from the InputStream
				gesture = mmInStream.read();
				if (gesture == -1) {
					// the end of the stream has been reached
					//Toast.makeText(mContext,"Debug: gesture is -1",Toast.LENGTH_LONG).show();
					Log.e(TAG, "manageConnectedSocket end?");
					break;
				}
				Log.e(TAG, "got: "+ (char)gesture);
				// Send the obtained byte to the UI Activity
				mHandler.obtainMessage(gesture).sendToTarget();
				//Toast.makeText(mContext,"Debug: "+(char)gesture+" recieved",Toast.LENGTH_LONG).show();
			} catch (IOException e) {
				//Toast.makeText(mContext,"Debug: gesture IOException",Toast.LENGTH_LONG).show();
				// disconnected
				Log.d(TAG, "manageConnectedSocket IOException");
				break;
			}
		}
	}




}
