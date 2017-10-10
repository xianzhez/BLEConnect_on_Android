package com.wm.bleconnect;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

@SuppressLint("SdCardPath")
public class ResourceUtils {
	public static final String TAG = "xianzheBLE";
	
	
	public static final UUID UUID_COM = UUID
			.fromString("00002902-0000-1000-8000-00805f9b34fb");
	
	public static final UUID UUID_MAIN_SERVICE = UUID
			.fromString("0000fff0-494c-4f47-4943-544543480000");
	public static final UUID UUID_SEND_DATA_CHAR = UUID
			.fromString("0000fff2-494c-4f47-4943-544543480000");
	public static final UUID UUID_RECEIVE_DATA_CHAR = UUID
			.fromString("0000fff1-494c-4f47-4943-544543480000");
	
	public static final UUID UUID_DESCRIPTOR = UUID
			.fromString("00002902-0000-1000-8000-00805f9b34fb");
	
	public static final String ACTION_NEW_DEVICE_CONNECTED = "ACTION_NEW_DEVICE_CONNECTED";
	public static final String ACTION_CONNECTED_DEVICE_LOST = "ACTION_CONNECTED_DEVICE_LOST";
	public static final String ACTION_CMD_RECEIVED = "ACTION_CMD_RECEIVED";
	public static final String ACTION_APP_INITIALIZED = "ACTION_APP_INITIALIZED";
	public static final String ACTION_CMD_SEND_DATA = "ACTION_CMD_SEND_DATA";
	public static final String ACTION_CMD_SEND_RAW_DATA = "ACTION_CMD_SEND_RAW_DATA";
	
	public static final String ACTION_BLE_FOUND = "ACTION_BLE_FOUND";
	public static final String ACTION_BLE_CONNECTED = "ACTION_BLE_CONNECTED";
	public static final String ACTION_BLE_DISCONNECTED = "ACTION_BLE_DISCONNECTED";
	public static final String ACTION_BLE_DISCOVERY_START = "ACTION_BLE_DISCOVERY_START";
	public static final String ACTION_BLE_DISCOVERY_FINISHED = "ACTION_BLE_DISCOVERY_FINISHED";
	
	public static final String DATA_BTDEVICE = "data_btdevice";
	public static final String DATA_RECEIVED_CMD = "DATA_RECEIVED_CMD";
	public static final String DATA_RECEIVED_DATA = "DATA_RECEIVED_DATA";
	public static final String DATA_RECEIVED_RAW_DATA = "DATA_RECEIVED_RAW_DATA";
	public static final String DATA_GATT = "DATA_GATT";
	public static final String DATA_HISTORY_FILE_NAME = "DATA_HISTORY_FILE_NAME";
	
	public static final String DATA_SEND_CMD = "DATA_SEND_CMD";
	public static final String DATA_SEND_DATA = "DATA_SEND_DATA";
	public static final String DATA_SEND_RAW_DATA = "DATA_SEND_RAW_DATA";
	
	public static final int ACTIVITY_HISTORY_MANAGER = 20;
	public static final int ACTIVITY_SERIAL_DEBUG = 21;
	
	
	public static final int EXCEPTION_CONNECTIONTHREAD_DEVICE_NOT_AVAILABLE = 1;
	
	
	public static final String EXTRA_INT_SHOW_CHOSEN = "EXTRA_INT_SHOW_CHOSEN";
	
	public static boolean HISTORY_DATA_CONFIRM_MANUALLY = false;
	
	@SuppressLint("SdCardPath")
	public static String MY_DIR = "/data/data/com.wm.bleconnect/WMBLE/";
	private static final String SDCARD_DIR = Environment.getExternalStorageDirectory().getPath()+"/WMBLE/";
	private static final String NOSDCARD_DIR = "/data/data/com.wm.bleconnect/WMBLE/";
	

	
	private static Object locker = new Object();
	private static BluetoothDevice mCurrentDevice = null;
	private static HandlerThread workerThread ;
	private static ConnectionService mService ;
	private static Context appInitContext;
	
	
	private static SimpleDateFormat mDataFormatter = null;
	
	public static void init(){
		mService = ConnectionService.getInstance();
		
	}
	
	public static void appinit(Context context){
		appInitContext = context;
		workerThread = new HandlerThread("worker_thread");
    	WorkerHandler.getInstance().sendEmptyMessage(
				WorkerHandler.WHAT_TEST);
		WorkerHandler.getInstance().sendEmptyMessage(
				WorkerHandler.WHAT_INIT_CREATE_DIRECTORY);
		WorkerHandler.getInstance().sendEmptyMessage(
				WorkerHandler.WHAT_TEST);
		getService();
	}
	
	public static ConnectionService getService(){
		if(mService == null){
			mService = ConnectionService.getInstance();
			return mService;
		}else{
			return mService;
		}
	}
	
	public static String getSystemTime(){
		if(mDataFormatter == null){
			mDataFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");     
		}
		Date curDate = new Date(System.currentTimeMillis());     
		String time = mDataFormatter.format(curDate);
		return time;
	}
	
	public static String getSystemTimeDay(){
		if(mDataFormatter == null){
			mDataFormatter = new SimpleDateFormat("yyyy-MM-dd");     
		}
		Date curDate = new Date(System.currentTimeMillis());     
		String time = mDataFormatter.format(curDate);
		return time;
	}
	
	public static String getSystemTimeClock(){
		if(mDataFormatter == null){
			mDataFormatter = new SimpleDateFormat("HH:mm:ss");     
		}
		Date curDate = new Date(System.currentTimeMillis());     
		String time = mDataFormatter.format(curDate);
		return time;
	}
	
	public static boolean isConnected(BluetoothDevice d){
		return getService().isConnected(d);
	}
	
	public static boolean isBLEConnected(BluetoothDevice d){
		return getService().isBLEConnected(d);
	}
	
	public static BluetoothDevice getCurrentDevice(){
		synchronized (locker) {
			Log.d(TAG, "ResouceUitls : mCurrentDevice == null? "
					+(mCurrentDevice == null));
			return mCurrentDevice;			
		}
	}
	
	public static void setCurrentDevice(BluetoothDevice d){
		synchronized (locker) {
			Log.d(TAG, "ResouceUitls : mCurrentDevice was set to "+d.getName()
					+", and address :"+d.getAddress());
			mCurrentDevice = d;
		}
	}
	
	
	public static void sendCMDToCurrentDevice(int cmd, int[] data){
		sendCMDToDevice(cmd, data, getCurrentDevice());
	}
	
	public static void sendCMDToDevice(int cmd, int[] data, BluetoothDevice d){
		String address = d.getAddress();
		if((address == null)||(getService() == null)){
			Log.d(TAG, "address = null "+(d == null)+" or mService = null "
					+(mService == null));
		}
		getService().sendCMDByDeviceAddress(cmd, data, address);
	}
	
	public static void sendCMDRawDataToCurrentDevice(byte[] data){
		sendCMDRawDataToDevice(data, getCurrentDevice());
	}
	
	public static void sendCMDRawDataToDevice(byte[] data, BluetoothDevice d){
		String address = d.getAddress();
		if((address == null)||(getService() == null)){
			Log.d(TAG, "address = null "+(d == null)+" or mService = null "
					+(mService == null));
		}
		getService().sendCMDRawData(data, d);
	}
	
	
	public static class UIHandler extends Handler{
		private static UIHandler instance = null;
		private static Object locker = new Object();
		
		private UIHandler(){
			super();
		}
		
		public static UIHandler getInstance(){
			synchronized(locker){
				if(instance == null){
					instance = new UIHandler();
				}
				return instance;
			}
		}
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
		
	}
	
	public static void toastShort(final String hint){
		if(mService == null){
			mService = ConnectionService.getInstance();
		}

		UIHandler.getInstance().post(new Runnable() {
			
			@Override
			public void run() {
				final Toast toast = Toast.makeText(mService.getApplicationContext(), hint, Toast.LENGTH_SHORT);
				toast.show();
				
				UIHandler.getInstance().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						toast.cancel();
					}
				}, 700);
				
			}
		});
		
	}
	

	
	public static void toastLong(final String hint){
		if(mService == null){
			mService = ConnectionService.getInstance();
		}

		UIHandler.getInstance().post(new Runnable() {
			
			@Override
			public void run() {
				final Toast toast = Toast.makeText(mService.getApplicationContext(), hint, Toast.LENGTH_SHORT);
				toast.show();
				
				UIHandler.getInstance().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						toast.cancel();
					}
				}, 3000);
				
			}
		});
	}
	
	public static ArrayList<String> getHistoryFiles(){
		ArrayList<String> list = new ArrayList<String>();
		File path = new File(MY_DIR);
		File[] fileList = path.listFiles();
		for(File f : fileList){
			String name = f.getPath();
			if(name.contains(FileWriteEngine.HistoryAppending)){
				list.add(name);
				Log.d(TAG, "ResouceUitls : find history file "+name);
			}
		}
		return list;
	}
	
	public static String arrayToHexString(byte[] array){
        if (array == null) {
            return "null";
        }
        if (array.length == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder(array.length * 6);
        sb.append('[');
        sb.append("0x"+Integer.toHexString(array[0] & 0xff)
        		.toUpperCase(Locale.getDefault()) );
        for (int i = 1; i < array.length; i++) {
            sb.append(", ");
            sb.append("0x"+Integer.toHexString(array[i] & 0xff)
            		.toUpperCase(Locale.getDefault()) );
        }
        sb.append(']');
        return sb.toString();
	}
	
	public static String arrayToHexString(int[] array){
        if (array == null) {
            return "null";
        }
        if (array.length == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder(array.length * 6);
        sb.append('[');
        sb.append("0x"+Integer.toHexString(array[0] & 0xff)
        		.toUpperCase(Locale.getDefault()) );
        for (int i = 1; i < array.length; i++) {
            sb.append(", ");
            sb.append("0x"+Integer.toHexString(array[i] & 0xff)
            		.toUpperCase(Locale.getDefault()) );
        }
        sb.append(']');
        return sb.toString();
	}
	
	public static String arrayToDecString(byte[] array){
		return Arrays.toString(array);
	}
	
	public static String arrayToDecString(int[] array){
		return Arrays.toString(array);
	}
	
	public static void changeHistoryDataConfirmState(boolean isChecked){
		HISTORY_DATA_CONFIRM_MANUALLY = isChecked;
	}
	
	
	
	
	public static class WorkerHandler extends Handler{
		public static final int WHAT_TEST = 0x0;
		public static final int WHAT_INIT_CREATE_DIRECTORY = 0x01;
		private static WorkerHandler instance = null;
		
		private WorkerHandler(Looper looper){
			super(looper);
		}

		public static WorkerHandler getInstance(){
			if(instance == null){
				workerThread.start();
				instance = new WorkerHandler(workerThread.getLooper());
				return instance;
			}else{
				return instance;
			}
		}
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case WHAT_TEST:
				Log.d(TAG, "ResouceUitls : WorkHandler TEST");
				break;
			case WHAT_INIT_CREATE_DIRECTORY:
				Log.d(TAG, "ResouceUitls : WorkHandler WHAT_INIT_CREATE_DIRECTORY");
				createDirectory();
				break;
			}
		}
		
		private void createDirectory(){
			if(ifSDCardAvailable()){
				MY_DIR = SDCARD_DIR;
			}else{
				MY_DIR = NOSDCARD_DIR;
			}
			  File destDir = new File(MY_DIR);
			  if (!destDir.exists()) {
				  if(destDir.mkdirs()){
					  Log.d(TAG, "ResouceUitls : create dir in "+MY_DIR);
					  toastShort("Initialized successfully!");
				  }
			  }
			  
			  Intent intent = new Intent();
			  intent.setAction(ACTION_APP_INITIALIZED);
			  appInitContext.sendBroadcast(intent);
			
		}
		
		private boolean ifSDCardAvailable(){
			String status = Environment.getExternalStorageState();
			  if (status.equals(Environment.MEDIA_MOUNTED)) {
			   return true;
			  } else {
			   return false;
			  }
		}
		
		
		
	}
	

	
	
}
