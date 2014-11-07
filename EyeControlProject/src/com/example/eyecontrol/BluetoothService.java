package com.example.eyecontrol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
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
			Log.d("Testing", bt.getName());
			if (bt.getName().equals("xubu1")) { //TODO change name
				Log.d("Testing","Found you!!!");
				mDevice = bt;
				break;
			}
		}
		/*Log.d("Testing", "found the device, continuing");
		if (mDevice == null) {
			Toast.makeText(context,"No Eye Control device found" 
					,Toast.LENGTH_LONG).show();
			// turn off?
			return;
		}*/
		// connect
		//Log.d("Testing", "creating toast");
		//Toast.makeText(mContext,"Debug: device found" + mDevice.getName(),Toast.LENGTH_LONG).show();
		//Log.d("Testing", "done creating toast");
		ConnectThread mConnectThread = new ConnectThread(mDevice, handler);
		//Log.d("Testing", "Done creating thread");
		//Toast.makeText(mContext,"still here 1",Toast.LENGTH_LONG).show();
		mConnectThread.start();
		//Log.d("Testing", "done start");
		//Toast.makeText(mContext,"still here 3",Toast.LENGTH_LONG).show();
	}


	private class ConnectThread extends Thread {
		private BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;
		private Handler mHandler;
		private final UUID MY_UUID = UUID.fromString("11111111-1111-1111-1111-111111111111");
		//private final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

		public ConnectThread(BluetoothDevice device, Handler handler) {
			Log.d(TAG, "ConnectThread creat start");
			// Use a temporary object that is later assigned to mmSocket,
			// because mmSocket is final
			//BluetoothSocket tmp = null;
			mmDevice = device;
			mHandler = handler;
		}

		public void run() {
			Log.d(TAG, "run ConnectThread start");

			// Cancel discovery because it will slow down the connection
			try {

				mmSocket = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);

	        } catch (IOException e) {
	            Log.e(TAG, "create() failed", e);
	        }
			mAdapter.cancelDiscovery();
			 try {

		            /* This is a blocking call and will only return on a
		               successful connection or an exception*/
				 mmSocket.connect();
		        } catch (IOException e) {
		        	Log.e(TAG, "connect() failed", e);
		        }
			manageConnectedSocket(mmSocket, mHandler);
		}

		// Will cancel an in-progress connection, and close the socket 
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) { }
		}
	}


	private void manageConnectedSocket(BluetoothSocket mmSocket, Handler handler) {
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
				Log.e(TAG, "got: "+ (int)gesture);
				// Send the obtained byte to the UI Activity
				//mHandler.obta  inMessage(gesture).sendToTarget();
				if((char) gesture=='L' ||(char)gesture=='R' ||(char)gesture=='U' ||(char)gesture=='D' ||(char)gesture=='B' ){ 
					Log.d("MAJD", "Sending: " + gesture);
					mHandler.obtainMessage(gesture).sendToTarget();
				}
				//Toast.makeText(mContext,"Debug: "+(char)gesture+" recieved",Toast.LENGTH_LONG).show();
				//handler.obtainMessage(gesture).sendToTarget();
			} catch (IOException e) {
				//Toast.makeText(mContext,"Debug: gesture IOException",Toast.LENGTH_LONG).show();
				// disconnected
				Log.d(TAG, "manageConnectedSocket IOException");
				break;
			}
		}
	}




}

