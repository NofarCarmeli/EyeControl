package com.example.eyecontrol;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class BluetoothService {
	
	private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private final Context mContext;
	
	BluetoothService(Context context, Handler handler) {
		
		// initialize variables
		mAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler = handler;
        mContext = context;
		
		// find the device
		BluetoothDevice mDevice = null;
		Set<BluetoothDevice> pairedDevices = mAdapter.getBondedDevices();
		for(BluetoothDevice bt : pairedDevices) {
	         if (bt.getName().equals("Eye Control")) { //TODO change name
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
		Toast.makeText(mContext,"Debug: device found",Toast.LENGTH_LONG).show();
		ConnectThread mConnectThread = new ConnectThread(mDevice);
		mConnectThread.start();
	}
	

	private class ConnectThread extends Thread {
	    private final BluetoothSocket mmSocket;
	    private final BluetoothDevice mmDevice;
	    
	    private final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
	 
	    public ConnectThread(BluetoothDevice device) {
	        // Use a temporary object that is later assigned to mmSocket,
	        // because mmSocket is final
	        BluetoothSocket tmp = null;
	        mmDevice = device;
	 
	        // Get a BluetoothSocket to connect with the given BluetoothDevice
	        try {
	            // MY_UUID is the app's UUID string, also used by the server code
	            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
	        } catch (IOException e) { }
	        mmSocket = tmp;
	    }
	 
	    public void run() {
	    	
	        // Cancel discovery because it will slow down the connection
	    	mAdapter.cancelDiscovery();
	 
	        try {
	            // Connect the device through the socket. This will block
	            // until it succeeds or throws an exception
	            mmSocket.connect();
	        } catch (IOException connectException) {
	            // Unable to connect; close the socket and get out
	            try {
	                mmSocket.close();
	            } catch (IOException closeException) { }
	            return;
	        }
	 
	        // Do work to manage the connection (in a separate thread)
	        Toast.makeText(mContext,"Debug: connection made",Toast.LENGTH_LONG).show();
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
		InputStream tmpIn = null;
		 
        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = mmSocket.getInputStream();
        } catch (IOException e) { }
 
        InputStream mmInStream = tmpIn;
        
        // run
        
        int gesture = 0; // bytes returned from read()
 
		// Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                gesture = mmInStream.read();
                if (gesture == -1) {
                	// the end of the stream has been reached
                	Toast.makeText(mContext,"Debug: gesture is -1",Toast.LENGTH_LONG).show();
                	break;
                }
                // Send the obtained byte to the UI Activity
                mHandler.obtainMessage(gesture).sendToTarget();
                Toast.makeText(mContext,"Debug: "+(char)gesture+" recieved",Toast.LENGTH_LONG).show();
            } catch (IOException e) {
            	Toast.makeText(mContext,"Debug: gesture IOException",Toast.LENGTH_LONG).show();
                // disconnected
                break;
            }
        }
	}

	
	

}
