package com.wm.bleconnect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class ConnectionService extends Service {
	private static final String TAG = ResourceUtils.TAG;
	
	private static ConnectionService singleInstance;
	
	public static final String FILTER_ACTION_UI_CONNECT = "FILTER_ACTION_UI_CONNECT";
	
	public static final String WMSTATE_CONNECTED = "connected";
	public static final String WMSTATE_DISCONNECTED = "disconnected";
	
	private static final UUID UUID_COM = ResourceUtils.UUID_COM;
	
	private HandlerThread mHandlerThread;
	public H mHandler = null;
	
	BluetoothAdapter mBluetoothAdapter;
	BluetoothManager mBTManager;
//	BLEServerThread mServerThread;
	BluetoothReceiver mBTReceiver;
	IntentFilter mBTFilter;
	UIOperationReceiver mUIOptReceiver;
	IntentFilter mUIOptFilter;
//	HashMap<BluetoothDevice, String> mWMDevices = 
//			new HashMap<BluetoothDevice, String>();
	HashMap<BluetoothDevice, ConnectThread> mConnectedWMDevices = 
			new HashMap<BluetoothDevice, ConnectThread>();
	
	private Object locker0 = new Object();
	/** 
	 * For BLE use, it will be changed in ConnectActivity, and accessed in
	 * other activities. But it should not be changed except ConnectActivity.
	 */
	HashMap<BluetoothDevice, BluetoothGatt> mBLEDevices = 
			new HashMap<BluetoothDevice, BluetoothGatt>();
	
	FileWriteEngine mFileWriteEngine;
	

	@Override
	public void onCreate() {
		super.onCreate();
		//for test
		Intent mIntent = new Intent();
		mIntent.setAction("com.wm.bleconnect.ConnectionService.UI");
		String str = "ConnectionService onCreate()";
		mIntent.putExtra("test", str);
		sendBroadcast(mIntent);
		
		init();
		synchronized (this) {
			singleInstance = this;			
		}
		
	}
	
	public static ConnectionService getInstance(){
		return singleInstance;
	}
	
	private void init(){
		mHandlerThread = new HandlerThread("BLE_Service_Handler");
    	mHandlerThread.start();
		mHandler = new H(mHandlerThread.getLooper());
		
		if(mBluetoothAdapter == null)
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if(mBTManager == null)
			mBTManager = (BluetoothManager)getSystemService(
					Context.BLUETOOTH_SERVICE);
		
//		mServerThread = new BLEServerThread(mBluetoothAdapter,
//				"WMBLE_SERVER");
//		mServerThread.start();
		
		mBTFilter = new IntentFilter();
		addBluetoothAction(mBTFilter);
		mBTReceiver = new BluetoothReceiver();
		registerReceiver(mBTReceiver, mBTFilter);
		
		mUIOptFilter = new IntentFilter();
		addUIOperationAction(mUIOptFilter);
		mUIOptReceiver = new UIOperationReceiver();
		registerReceiver(mUIOptReceiver, mUIOptFilter);
		ResourceUtils.init();
		
		mFileWriteEngine = new FileWriteEngine(this);
	}
	
	private void addBluetoothAction(IntentFilter filter){
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
	}
	
	private void addUIOperationAction(IntentFilter filter) {
		filter.addAction(FILTER_ACTION_UI_CONNECT);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mBTReceiver);
		unregisterReceiver(mUIOptReceiver);
	}
	
	boolean addBLEDevice(BluetoothDevice device, BluetoothGatt gatt){
		if((device == null)||(gatt == null)){
			return false;
		}
		synchronized (locker0) {
			if(mBLEDevices.containsKey(device)){
				return true;
			}else{
				mBLEDevices.put(device, gatt);
				return true;
			}
		}
	}
	
	boolean removeBLEDevice(BluetoothDevice device){
		if(device == null){
			return false;
		}
		synchronized (locker0) {
			if(mBLEDevices.containsKey(device)){
				mBLEDevices.remove(device);
				return true;
			}else{
				return false;
			}
		}
		
	}
	
	boolean removeBLEDevice(BluetoothGatt gatt){
		BluetoothDevice d = gatt.getDevice();
		return removeBLEDevice(d);
	}
	
	BluetoothGatt getBluetoothGatt(BluetoothDevice device){
		if(device == null){
			return null;
		}
		synchronized (locker0) {
			return mBLEDevices.get(device);
		}
	}
	
	void closeBluetoothGatt(BluetoothDevice device){
		if(device == null){
			return;
		}
		synchronized (locker0) {
			BluetoothGatt gatt = mBLEDevices.get(device);
			gatt.close();
		}
		
		
	}
	
	private void connect(){
		//should not be bonded device
		Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
		for(BluetoothDevice d : devices){
			Log.d(TAG, "ConnectionService : connect() find device :"+d.getName());
			connectToDevice(d);
		}
		
		// TODO : to discover device
		
		
		
		
	}
	
	public void connectToDevice(BluetoothDevice device){
		boolean isContains = false;
		synchronized(this){
			//connect will consume too much time, just sync key place;
			isContains = mConnectedWMDevices.containsKey(device);
		}
		if(!isContains){
			Log.d(TAG, "ConnectionService : connect() connecting to " +
					"device :"+device.getName());
			//TODO check if it is WMDevice
			connectToWMDevice(device);
		}
	}
	
	private boolean connectToWMDevice(BluetoothDevice device){
		
/*		int state = device.getBondState();
		if(state == BluetoothDevice.BOND_NONE){
			Log.d(TAG, "ConnectionService : connect to WMDevice  "+device.getName()
					+"failed, state = BOND_NONE.");
			return false;
		}else if(state == BluetoothDevice.BOND_BONDING){
			Log.d(TAG, "ConnectionService : connect to WMDevice "+device.getName()
					+" failed, state = BOND_BONDING.");
			return false;
		}*/
		
		BluetoothSocket socket = null;
		ConnectThread thread = null;
		try {
			socket = device.createRfcommSocketToServiceRecord(UUID_COM);
			thread = new ConnectThread(device, socket, mHandler, this);
			
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "ConnectionService : connect to WMDevice "+device.getName()
					+" failed, IOException");
			thread = null;
			ResourceUtils.toastShort("连接至"+device.getName()+"失败");
			return false;
		}
		
		if((socket != null)&&(thread != null)){
			thread.start();
			synchronized(this){
				mConnectedWMDevices.put(device, thread);
			}
			broadcastDeviceConnected(device);
			Log.e(TAG, "ConnectionService : connect to WMDevice "+device.getName()
					+" successfully.");
			ResourceUtils.toastShort("连接至"+device.getName()+"成功");
			return true;
		}else{
			Log.e(TAG, "ConnectionService : connect to WMDevice "+device.getName()
					+" failed, thread error.");
			ResourceUtils.toastShort("连接至"+device.getName()+"失败");
			return false;
		}
	}
	
	public void disconnectAll(){
		Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
		Log.d(TAG, "ConnectionService : disconnectAll() ");
		for(BluetoothDevice d : devices){
			disconnect(d);
			//notify connection lost
			broadcastDeviceLost(d);
		}
	}
	
	public void disconnect(BluetoothDevice device){
		if(device == null){
			return;
		}
		ConnectThread t = mConnectedWMDevices.get(device);
		if(t!=null){
			t.stopConnect();
			mConnectedWMDevices.remove(device);
			broadcastDeviceLost(device);
			Log.d(TAG, "ConnectionService : disconnect() device "+device.getName());
		}else{
			Log.d(TAG, "ConnectionService : disconnect() device "+device.getName()
					+"failed, device is not in connected record");
		}
	} 
	
	public void sendCMDByDeviceAddress(int cmd, String address){
		sendCMDByDeviceAddress(cmd, null, address);
	}
	
	public void sendCMDByDeviceAddress(int cmd, int[] data, String address){
		if(!Protocol.isSendCMD(cmd)){
			Log.e(TAG, "ConnectionService : sendCMDByDeviceAddress failed, cmd "
					+cmd+" is not a send CMD");
			return;
		}
		//BluetoothDevice device = getConnectedDeviceByAddress(address);
		BluetoothDevice device = getConnectedDeviceByAddress(address);
		if( device == null){
			Log.e(TAG, "ConnectionService : sendCMDByDeviceAddress failed, " +
					"address is not a connected device address");
			return;
		}
		
		if(data == null){
			//TODO fill in Checksum
			data = new int[1];
		}
		
		Message msg = Message.obtain();
		msg.what = H.FLAG_SEND_CMD;
		Bundle b = new Bundle();
		b.putParcelable(ResourceUtils.DATA_BTDEVICE, device);
		b.putInt(ResourceUtils.DATA_SEND_CMD, cmd);
		b.putIntArray(ResourceUtils.DATA_SEND_DATA, data);
		msg.setData(b);
		mHandler.handleMessage(msg);
		
	}
	
	public void sendCMDRawData(byte[] data, BluetoothDevice d){
		BluetoothDevice device = getConnectedDeviceByAddress(d.getAddress());
		Message msg = Message.obtain();
		msg.what = H.FLAG_SEND_CMD_RAW_DATA;
		Bundle b = new Bundle();
		b.putParcelable(ResourceUtils.DATA_BTDEVICE, device);
		//b.putInt(ResourceUtils.DATA_SEND_CMD, cmd);
		b.putByteArray(ResourceUtils.DATA_SEND_RAW_DATA, data);
		msg.setData(b);
		mHandler.handleMessage(msg);
	
	}
	
	public boolean isBLEDeviceConnectedByAddress(String address){
		if(address == null)
			return false;
		synchronized(this){
			Set<BluetoothDevice> devices = mConnectedWMDevices.keySet();
			for(BluetoothDevice d : devices){
				if(d.getAddress().equals(address)){
					return true;
				}
			}
		}
		
		return false;
	}
	
	public Set<BluetoothDevice> getConnectedWMDevices(){
		synchronized (this) {
			return mConnectedWMDevices.keySet();
		}
	}
	
	public BluetoothDevice getConnectedDeviceByAddress(String address){
		if(address == null)
			return null;
		synchronized(this){
			//Set<BluetoothDevice> devices = mConnectedWMDevices.keySet();
			Set<BluetoothDevice> devices = mBLEDevices.keySet();
			for(BluetoothDevice d : devices){
				if(d.getAddress().equals(address)){
					return d;
				}
			}
		}
		
		return null;
	}
	
	public boolean isConnected(BluetoothDevice de){
		String address = de.getAddress();
		synchronized (this) {
			Set<BluetoothDevice> devices = mConnectedWMDevices.keySet();
			for(BluetoothDevice d : devices){
				if(d.getAddress().equals(address)){
					Log.d(TAG, "ConnectionService : isConnected() "+d.getName()
							+", set size = "+devices.size());
					return true;
				}
			}
		}
		return false;
	}
	
	public ConnectThread getConnectThreadByDevice(BluetoothDevice d){
		if(mConnectedWMDevices.containsKey(d)){
			return mConnectedWMDevices.get(d);
		}
		return null;
	}
	
	

	public void handleException(int flag){
		switch(flag){
		case ResourceUtils.EXCEPTION_CONNECTIONTHREAD_DEVICE_NOT_AVAILABLE:
			break;
		default:
			break;
		}
		
	}
	
	private void broadcastDeviceConnected(BluetoothDevice d){
		if(d ==null)
			return;
		Intent intent = new Intent();
		intent.setAction(ResourceUtils.ACTION_NEW_DEVICE_CONNECTED);
		intent.putExtra(ResourceUtils.DATA_BTDEVICE, d);
		sendBroadcast(intent);
	}
	
	private void broadcastDeviceLost(BluetoothDevice d){
		// device exist?
		if(d == null)
			return;
		Intent intent = new Intent();
		intent.setAction(ResourceUtils.ACTION_CONNECTED_DEVICE_LOST);
		intent.putExtra(ResourceUtils.DATA_BTDEVICE, d);
		sendBroadcast(intent);
		
	}
	
	
	private class BluetoothReceiver extends BroadcastReceiver{
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)){
				
			}else if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){
				
			}else if(action.equals(BluetoothDevice.ACTION_FOUND)){
				
			}else if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
				BluetoothDevice d = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
				int pState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, -1);
				if((state == BluetoothDevice.BOND_BONDED)
						&&((pState == BluetoothDevice.BOND_BONDING)
							||(pState == BluetoothDevice.BOND_NONE))){
					connectToDevice(d);
				}else if((state == BluetoothDevice.BOND_NONE)
						&&((pState==BluetoothDevice.BOND_BONDING)
							||(pState==BluetoothDevice.BOND_BONDED))){
					disconnect(d);
				}
			}
			
			
		}
		
	}
	
	private class UIOperationReceiver extends BroadcastReceiver{
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(FILTER_ACTION_UI_CONNECT)){
				Log.d(TAG, "ConnectionService : UIReceiver: FILTER_ACTION_UI_CONNECT");
				ConnectionService.this.mHandler.sendEmptyMessage(H.FLAG_UI_CONNECT);
		
			}
			
		}
		
	}
	
	static class H extends Handler{
		public static final int FLAG_CMD_RECEIVED = 0;
		public static final int FLAG_UI_CONNECT = 1;
		public static final int FLAG_SEND_CMD = 2;
		public static final int FLAG_SEND_CMD_RAW_DATA = 3;
		
		public H(Looper looper){
			super(looper);
		}
		
		@Override
		public void handleMessage(final Message msg) {
			super.handleMessage(msg);
			
			switch(msg.what){
			case FLAG_CMD_RECEIVED:
				Log.d(TAG, "ConnectionService : H: FLAG_CMD_RECEIVED");
				Intent intent = new Intent();
				intent.setAction(ResourceUtils.ACTION_CMD_RECEIVED);
				intent.putExtras(msg.getData());
				Bundle b0 = msg.getData();
				int cmdtmp = b0.getInt(ResourceUtils.DATA_RECEIVED_CMD);
				int[] rdtmp = b0.getIntArray(ResourceUtils.DATA_RECEIVED_DATA);
				Log.d(TAG, "ConnectionService : H: receive data: " +
						"CMD = 0x"+Integer.toHexString(cmdtmp)+" Content: "
						+Arrays.toString(rdtmp));
				ConnectionService.singleInstance.sendBroadcast(intent);
				
				if(cmdtmp == 0x45){
					BluetoothDevice d = intent.getParcelableExtra(ResourceUtils.DATA_BTDEVICE);
					//test speed problem_______xianzhe
//					
//					Log.d(TAG, "ConnectionService : H: ＝＝＝＝＝＝＝＝＝＝＝！！！！！＝＝＝＝＝＝＝＝＝＝＝"
//							+ "TEST SEND DATA AFTER RECEIVED ==========");
//					sendDataToBLE( d, 0x90, new int[]{111,0});
					
					writeToFile(d, cmdtmp, rdtmp);
					
				}
				break;
			case FLAG_UI_CONNECT:
				Log.d(TAG, "ConnectionService : H: FLAG_UI_CONNECT");
				Thread t0 = new Thread(){
					@Override
					public void run() {
						super.run();
						ConnectionService.singleInstance.connect();
					}
				};
				t0.start();
				break;
			case FLAG_SEND_CMD:
				Log.d(TAG, "ConnectionService : H: FLAG_SEND_CMD");
				Thread t1 = new Thread(){
					@Override
					public void run() {
						super.run();
						Bundle b = msg.getData();
						BluetoothDevice d = b.getParcelable(ResourceUtils
								.DATA_BTDEVICE);
						int cmd = b.getInt(ResourceUtils.DATA_SEND_CMD);
						int[] data = b.getIntArray(ResourceUtils.DATA_SEND_DATA);
						/*ConnectThread ct = ConnectionService.singleInstance
								.getConnectThreadByDevice(d);
						ct.sendCMD(cmd, data);*/
						if(sendDataToBLE(d, cmd, data)){
							Log.d(TAG, "ConnectionService : H: FLAG_SEND_CMD " +
									"sending ...");
						}else{
							Log.d(TAG, "ConnectionService : H: FLAG_SEND_CMD " +
									"send failed!");
						}
						
					}
				};
				t1.start();
				break;
			case FLAG_SEND_CMD_RAW_DATA:
				Log.d(TAG, "ConnectionService : H: FLAG_SEND_CMD_RAW_DATA");
				Thread t2 = new Thread(){
					@Override
					public void run() {
						super.run();
						Bundle b = msg.getData();
						BluetoothDevice d = b.getParcelable(ResourceUtils
								.DATA_BTDEVICE);
						
						byte[] data = b.getByteArray(ResourceUtils.DATA_SEND_RAW_DATA);
						if(sendRawDataToBLE(d, data)){
							Log.d(TAG, "ConnectionService : H: FLAG_SEND_CMD_RAW_DATA " +
									"sending ...");
						}else{
							Log.d(TAG, "ConnectionService : H: FLAG_SEND_CMD_RAW_DATA " +
									"send failed!");
						}
						
					}
				};
				t2.start();
				break;
			default:
				break;
			}
		}
		
		private void writeToFile(BluetoothDevice d, int cmd, int[] data){
			ConnectionService.getInstance().mFileWriteEngine
				.writeHistoryData(d, cmd, data);
			Log.d(TAG, "ConnectionService : H: writeToFile history data writing...");
		}
		
		private boolean sendDataToBLE(BluetoothDevice d, int cmd, int[] data){
			BluetoothGatt gatt = ConnectionService.singleInstance.getBluetoothGatt(d);
			if(gatt == null){
				Log.e(TAG, "ConnectionService : H: gatt not found!");
				return false;
			}
			
			BluetoothGattService mainService = 
					gatt.getService(ResourceUtils.UUID_MAIN_SERVICE);
			if (mainService == null) {
				Log.e(TAG, "ConnectionService : H: service not found!");
				return false;
			}
			BluetoothGattCharacteristic txCharac = 
					mainService.getCharacteristic(ResourceUtils.UUID_SEND_DATA_CHAR);
			if (txCharac == null) {
				Log.e(TAG, "ConnectionService : H: charateristic not found!");
				return false;
			}
			byte[] parsedData = Protocol.parseRealDataToSendData(cmd, data);
			byte[] sendData = new byte[parsedData.length+3];
			sendData[0] = (byte)cmd;
			sendData[1] = (byte)(parsedData.length+3);
			sendData[2] = (byte)3;
			
			
			for(int i = 3;i<sendData.length;i++){
//				Log.e(TAG, "ConnectionService : H: byte[] index = "
//						+i);
				sendData[i] = parsedData[i-3];
			}
			byte[] finalSendData = checkSum(sendData);
			
			//set characteristic to write
			txCharac.setValue(finalSendData);
			//txCharac.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
			Log.e(TAG, "ConnectionService : H: int[] data = "
					+Arrays.toString(data));
			Log.e(TAG, "ConnectionService : H: byte[] parsedData = "
					+Arrays.toString(parsedData));
			Log.e(TAG, "ConnectionService : H: write charateristic = "
					+"++++++++=================+++++++++"+Arrays.toString(finalSendData)
					+"   characteristic UUID = "+txCharac.getUuid().toString());
			gatt.writeCharacteristic(txCharac);
			
			int[] intDataToSend = new int[data.length+3];
			intDataToSend[0] = cmd;
			intDataToSend[1] = (parsedData.length+3);
			intDataToSend[2] = 3;
			for(int x = 3; x<intDataToSend.length;x++){
				intDataToSend[x] = data[x-3];
			}
			
			Intent intent = new Intent(ResourceUtils.ACTION_CMD_SEND_DATA);
			intent.putExtra(ResourceUtils.DATA_SEND_DATA, intDataToSend);
			intent.putExtra(ResourceUtils.DATA_BTDEVICE, d);
			ConnectionService.getInstance().sendBroadcast(intent);
			
			return true;
		}
		
		private boolean sendRawDataToBLE(BluetoothDevice d, byte[] data){
			
			BluetoothGatt gatt = ConnectionService.singleInstance.getBluetoothGatt(d);
			if(gatt == null){
				Log.e(TAG, "ConnectionService : H2: gatt not found!");
				return false;
			}
			
			BluetoothGattService mainService = 
					gatt.getService(ResourceUtils.UUID_MAIN_SERVICE);
			if (mainService == null) {
				Log.e(TAG, "ConnectionService : H2: service not found!");
				return false;
			}
			BluetoothGattCharacteristic txCharac = 
					mainService.getCharacteristic(ResourceUtils.UUID_SEND_DATA_CHAR);
			if (txCharac == null) {
				Log.e(TAG, "ConnectionService : H2: charateristic not found!");
				return false;
			}
			
			byte[] finalSendData = checkSum(data);
			txCharac.setValue(finalSendData);
			Log.e(TAG, "ConnectionService : H2: write charateristic raw data = "
					+Arrays.toString(finalSendData));
			gatt.writeCharacteristic(txCharac);
			
			Intent intent = new Intent(ResourceUtils.ACTION_CMD_SEND_RAW_DATA);
			intent.putExtra(ResourceUtils.DATA_SEND_RAW_DATA, finalSendData);
			intent.putExtra(ResourceUtils.DATA_BTDEVICE, d);
			ConnectionService.getInstance().sendBroadcast(intent);
			return true;
			
		}
		
		private byte[] checkSum(byte[] data){
			int sum = 0;
			for(int i = 0; i<data.length-1;i++){
				sum = (sum & 0xFF )+ (data[i] & 0xFF) ;
			}
			data[data.length-1] = (byte)(sum & 0xFF);
			return data;
			
		}

		
	}
	
/*	private class BLEServerThread extends Thread{
		BluetoothAdapter mAdapter;
		BluetoothServerSocket mServerSocket;
		//BluetoothSocket mSocket;
		
		@SuppressWarnings("unused")
		public BLEServerThread(BluetoothAdapter adapter){
			this.mAdapter = adapter;
		}
		
		public BLEServerThread(BluetoothAdapter adapter, String name){
			super(name);
			this.mAdapter = adapter;
		}

		@Override
		public void run() {
			super.run();
			Log.d(TAG, "ConnectionService : ServerThread is Running");
			ConnectThread thread = null;
			BluetoothDevice d = null;
			try {
				mServerSocket = mAdapter.listenUsingRfcommWithServiceRecord(
						"WMBLE", ResourceUtils.UUID_COM);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			while(true){
				try {
					BluetoothSocket s = mServerSocket.accept();
					Log.d(TAG, "ConnectionService : ServerThread get a connect request");
					d = s.getRemoteDevice();
					thread = new ConnectThread(d, s, s.getInputStream(),
							s.getOutputStream(), mHandler, ConnectionService.this);
				} catch (IOException e) {
					e.printStackTrace();
					Log.d(TAG, "ConnectionService : ServerThread connect to "
							+ d.getName()+" failed IOException...");
					thread = null;
				}
				if((thread != null)&&(d != null)){
					thread.start();
					synchronized(this){
						mConnectedWMDevices.put(d, thread);
						Log.d(TAG, "ConnectionService : ServerThread connect to "
								+ d.getName());
						//notify device found
						broadcastDeviceConnected(d);
					}
				}else{
					Log.d(TAG, "ConnectionService : ServerThread connect failed");
				}
			}
		
		}
			
		
	}*/
	
	
	
	/***************************************************
	 * 
	 *						BLE 
	 * 
	 ***************************************************/
	
	private boolean mScanning = false;
	private long SCAN_PERIOD = 20000L;
	
	void scanLeDevice(final boolean enable){
		if(enable){
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					Intent intent = new Intent(ResourceUtils.ACTION_BLE_DISCOVERY_FINISHED);
					sendBroadcast(intent);
				}
			}, SCAN_PERIOD);
			
			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
			Intent intent = new Intent(ResourceUtils.ACTION_BLE_DISCOVERY_START);
			sendBroadcast(intent);
			
		}else{
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
			Intent intent = new Intent(ResourceUtils.ACTION_BLE_DISCOVERY_FINISHED);
			sendBroadcast(intent);
		}
	}
	
	
	void connectToBLE(BluetoothDevice device){
		BluetoothGatt mGatt = null;
		try{
			mGatt = device.connectGatt(
					ConnectionService.this, false, mBluetoothGattCallback);
			Log.d(TAG, "ConnectionService : connecting to "+device.getName()+"......");
		}catch(Exception e){
			e.printStackTrace();
			Log.d(TAG, "......Gatt connect exception....................");
		}
/*		
		try{
			ResourceUtils.toastShort("BLE "+device.getName()+" connected");
			Toast.makeText(mService, "BLE "+device.getName()+" connected",
					Toast.LENGTH_LONG).show();
		}catch(Exception e){
			e.printStackTrace();
			Log.d(TAG, "......Toast exception....................");
		}
		
		int mState = mBTManager.getConnectionState(device,BluetoothProfile.GATT );
		Log.d(TAG, "ConnectionService :mLeScanCallback onLeScan()" +
				"device = "+device.getName()+", state = " +mState);
		if(mState == mGatt.STATE_CONNECTED){
			Log.d(TAG, "ConnectionService :mLeScanCallback onLeScan()" +
					"device = "+device.getName()+", state = STATE_CONNECTED");
		}else if(mState == mGatt.STATE_CONNECTING){
			Log.d(TAG, "ConnectionService :mLeScanCallback onLeScan()" +
					"device = "+device.getName()+", state = STATE_CONNECTING");
		}else if(mState == mGatt.STATE_DISCONNECTED){
			Log.d(TAG, "ConnectionService :mLeScanCallback onLeScan()" +
					"device = "+device.getName()+", state = STATE_DISCONNECTED");
		}else if(mState == mGatt.STATE_DISCONNECTING){
			Log.d(TAG, "ConnectionService :mLeScanCallback onLeScan()" +
					"device = "+device.getName()+", state = STATE_DISCONNECTING");
		}
		
		mGatt.discoverServices();
		addBLEDevice(device, mGatt);*/
	}
	
	void disconnectToBLE(BluetoothGatt mGatt){
		mGatt.disconnect();
	}
	
	void disconnectToBLE(BluetoothDevice device){
		if(!isBLEConnected(device)){
			return;
		}
		synchronized (locker0) {
			BluetoothGatt gatt = mBLEDevices.get(device);
			gatt.disconnect();
		}
		
/*		synchronized (locker0) {
			Set<BluetoothDevice> set = mBLEDevices.keySet();
			String address = device.getAddress();
			for(BluetoothDevice d : set){
				if(address.equals(d.getAddress())){
					BluetoothGatt gatt = mBLEDevices.get(d);
					gatt.disconnect();
				}
			}
		
		}*/
	}
	
	boolean isBLEConnected(BluetoothDevice device){
		
		int mState = mBTManager.getConnectionState(device, BluetoothProfile.GATT);
		if(mState == BluetoothGatt.STATE_CONNECTED){
			Log.d(TAG, "ConnectionService :isBLEConnected " +
					"device = "+device.getName()+", state = STATE_CONNECTED");
			return true;
		}else if(mState == BluetoothGatt.STATE_CONNECTING){
			Log.d(TAG, "ConnectionService :isBLEConnected " +
					"device = "+device.getName()+", state = STATE_CONNECTING");
			return false;
		}else if(mState == BluetoothGatt.STATE_DISCONNECTED){
			Log.d(TAG, "ConnectionService :isBLEConnected " +
					"device = "+device.getName()+", state = STATE_DISCONNECTED");
			return false;
		}else if(mState == BluetoothGatt.STATE_DISCONNECTING){
			Log.d(TAG, "ConnectionService :isBLEConnected " +
					"device = "+device.getName()+", state = STATE_DISCONNECTING");
			return false;
		}
		return false;
	}
	
	HashMap<BluetoothDevice, BluetoothGatt> getConnectedBLEMapWithGatt(){
		synchronized (locker0) {
			return mBLEDevices;
		}
	}
	
	Set<BluetoothDevice> getConnectedBLESet(){
		synchronized (locker0) {
			return mBLEDevices.keySet();
		}

	}
	
	BluetoothAdapter.LeScanCallback mLeScanCallback =
			new BluetoothAdapter.LeScanCallback(){

				@Override
				public void onLeScan(BluetoothDevice device, int rssi,
						byte[] scanRecord) {
					Log.d(TAG, "ConnectionService :mLeScanCallback onLeScan()"
						+", discovered a device called "+device.getName());
					Log.d(TAG, "ConnectionService : device mac address = "
						+device.getAddress());
/*					Log.d(TAG, "ConnectionService :mLeScanCallback onLeScan()"
							+" scanRecord = "+Arrays.toString(scanRecord));*/
					Intent intent = new Intent(ResourceUtils.ACTION_BLE_FOUND);
					intent.putExtra(ResourceUtils.DATA_BTDEVICE, device);
					sendBroadcast(intent);
					Log.d(TAG, "ConnectionService : broadcast BLE found.");
				}
		
	};
	
	
	BluetoothGattCallback mBluetoothGattCallback =
			new BluetoothGattCallback(){
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt,
				int status, int newState) {
			super.onConnectionStateChange(gatt, status, newState);
			Log.d(TAG, "ConnectionService :1. mBTGattCallback onConnectionStateChange() "
					+"Name = "+gatt.getDevice().getName()+", address = "
					+gatt.getDevice().getAddress());
			if(newState == BluetoothProfile.STATE_CONNECTED){
				Log.d(TAG, "ConnectionService :2. mBTGattCallback onConnectionStateChange() "
						+"Name = "+gatt.getDevice().getName()+", state = "
						+"BluetoothProfile.STATE_CONNECTED");
				gatt.discoverServices();
				addBLEDevice(gatt.getDevice(), gatt);
				
				Intent intent = new Intent(ResourceUtils.ACTION_BLE_CONNECTED);
				intent.putExtra(ResourceUtils.DATA_BTDEVICE, gatt.getDevice());
				sendBroadcast(intent);
			}else if(newState == BluetoothProfile.STATE_DISCONNECTED){
				Log.d(TAG, "ConnectionService :3. mBTGattCallback onConnectionStateChange() "
						+"Name = "+gatt.getDevice().getName()+", state = "
						+"BluetoothProfile.STATE_DISCONNECTED");
				removeBLEDevice(gatt);
				Intent intent = new Intent(ResourceUtils.ACTION_BLE_DISCONNECTED);
				intent.putExtra(ResourceUtils.DATA_BTDEVICE, gatt.getDevice());
				sendBroadcast(intent);
				ResourceUtils.toastLong("Device "+
						gatt.getDevice().getName()+" disconnected");
				
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			super.onServicesDiscovered(gatt, status);
			Log.d(TAG, "ConnectionService :mBTGattCallback onServicesDiscovered()"
					+gatt.getDevice().getName());
			
			List<BluetoothGattService> l = gatt.getServices();
			Log.d(TAG, "ConnectionService :mBluetoothGattCallback onServicesDiscovered() " +
					"services list size = "+l.size());
			for(BluetoothGattService s : l){
				Log.d(TAG, "ConnectionService :Service UUID = "+s.getUuid().toString());
				if(s.getUuid().toString().equals(ResourceUtils.UUID_MAIN_SERVICE.toString())){
					
					List<BluetoothGattCharacteristic> cl = s.getCharacteristics();
					for(BluetoothGattCharacteristic c : cl){
						Log.d(TAG, "ConnectionService :Characteristic UUID = "
								+c.getUuid().toString());
						if(c.getUuid().toString().equals(
								ResourceUtils.UUID_RECEIVE_DATA_CHAR.toString())){
							enableNotification(gatt, c);
							
						}
					}
					
				}
				
			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			super.onCharacteristicRead(gatt, characteristic, status);
			Log.d(TAG, "ConnectionService :mBTGattCallback onCharacteristicRead() "
					+gatt.getDevice().getName());
			
			
			//test______xianzhe
			Log.d(TAG, "ConnectionService :mBTGattCallback onCharacteristicRead() "
					+ "result ========= "+Arrays.toString(characteristic.getValue()));
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			super.onCharacteristicWrite(gatt, characteristic, status);
			Log.d(TAG, "ConnectionService :mBTGattCallback onCharacteristicWrite() "
					+gatt.getDevice().getName());
			
			if(status == BluetoothGatt.GATT_SUCCESS){
				Log.d(TAG, "ConnectionService :mBTGattCallback onCharacteristicWrite() "
						+gatt.getDevice().getName()+" sucessfully ＝＝＝＝＝＝＝＝＝＝＝＝＝"
								+ "＝＝＝＝＝＝＝＝＝＝＝＝＝＝！");
			}else{
				Log.d(TAG, "ConnectionService :mBTGattCallback onCharacteristicWrite() "
						+gatt.getDevice().getName()+" failed =====================!");
			}
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			super.onCharacteristicChanged(gatt, characteristic);
			Log.d(TAG, "ConnectionService :mBTGattCallback onCharacteristicChanged() "
					+gatt.getDevice().getName()+" Characteristic UUID = "+
					characteristic.getUuid().toString());
			
			
			byte[] data = characteristic.getValue();
			Log.d(TAG, "ConnectionService : Receive data = "+Arrays.toString(data));
			Log.d(TAG, "CMD = 0x"+Integer.toHexString((int)(data[0]&0x00FF )));
			int checksum = 0;
			for(int i = 0; i<data.length-1;i++){
				checksum = (data[i]&0xFF) + (checksum & 0xFF);
			}
			Log.d(TAG, "My checksum = "+ (byte)(checksum & 0xFF));
			
			//for test ______xianzhe
//			boolean t = gatt.readCharacteristic(characteristic);
//			Log.d(TAG, "ConnectionService :mBTGattCallback onCharacteristicChanged() "
//					+ "CALL READ +++++++++++++++++++ = "+t);
			
			int cmd = data[0]&0xFF;
			handleReceivedCommand(gatt.getDevice(), cmd, data);
			
			
		}

		@Override
		public void onDescriptorRead(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
			super.onDescriptorRead(gatt, descriptor, status);
			Log.d(TAG, "ConnectionService :mBTGattCallback onDescriptorRead() "
					+gatt.getDevice().getName());
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
			super.onDescriptorWrite(gatt, descriptor, status);
			Log.d(TAG, "ConnectionService :mBTGattCallback onDescriptorWrite() "
					+gatt.getDevice().getName());
			
		}

		@Override
		public void onReliableWriteCompleted(BluetoothGatt gatt,
				int status) {
			super.onReliableWriteCompleted(gatt, status);
			Log.d(TAG, "ConnectionService :mBTGattCallback onReliableWriteCompleted()"
					+gatt.getDevice().getName());
		}

		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi,
				int status) {
			super.onReadRemoteRssi(gatt, rssi, status);
			Log.d(TAG, "ConnectionService :mBTGattCallback onReadRemoteRssi()"
					+gatt.getDevice().getName());
		}
	};
	
	public boolean enableNotification(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
		Log.i(TAG, "ConnectionService : enableNotification = " 
				+ characteristic.getUuid());

		boolean result = gatt.setCharacteristicNotification(
				characteristic, true);
		Log.i(TAG, "ConnectionService : setCharacteristicNotification = " + result);

		BluetoothGattDescriptor clientConfig = characteristic
				.getDescriptor(ResourceUtils.UUID_DESCRIPTOR);

		if (clientConfig == null) {
			Log.e(TAG, "clientConfig is null");
			return false;
		}

		clientConfig
				.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

		return gatt.writeDescriptor(clientConfig);
	}
	
	private void handleReceivedCommand(BluetoothDevice device, int cmd, byte[] rawData) {
		byte[] dataBuf = Arrays.copyOfRange(rawData, 3, rawData.length);
		Log.d(TAG, "ConnectionService : Receive dataBuf = "+Arrays.toString(dataBuf));
		
		int[] realData = Protocol.parseReceivedDataToRealData( cmd, dataBuf);
		
		//String dataStr = new String(dataBuf);
		Log.d(TAG, "ConnectionService : receive data with cmd ================================ : [0x"
				+Integer.toHexString(cmd) +"] "+ Arrays.toString(realData));
		
		Message msg = Message.obtain();
		msg.what = ConnectionService.H.FLAG_CMD_RECEIVED;
		msg.arg1 = cmd;
		
		Bundle data = new Bundle();
		data.putParcelable(ResourceUtils.DATA_BTDEVICE, device);
		data.putInt(ResourceUtils.DATA_RECEIVED_CMD, cmd);
		data.putIntArray(ResourceUtils.DATA_RECEIVED_DATA, realData);
		data.putByteArray(ResourceUtils.DATA_RECEIVED_RAW_DATA, rawData);
		
		msg.setData(data);
		mHandler.handleMessage(msg);
		
		//XIANZHE : REPLY
		//TODO confirm history data
		if(cmd == 0x45){
			if(!ResourceUtils.HISTORY_DATA_CONFIRM_MANUALLY){
				confirmCMDreceived(0x90, new int[]{cmd,0}, device);		
				
				//for test----xianzhe
				//confirmCMDreceived(0x90, new int[]{199,0}, device);
			}
			//else confirm manually
		}
		/*****
		else if(cmd == 0x93){
			Log.d(TAG, "ConnectionService : confirmCMDsended was received......");
		}else{
			confirmCMDreceived(0x90, new int[]{cmd,0}, device);
		}
		*****/
		
		
	}
	
	private void confirmCMDreceived(int cmd, int[] data, BluetoothDevice d){
		ResourceUtils.sendCMDToDevice(0x90, new int[]{data[0],0}, d);
		
		Log.d(TAG, "ConnectionService : confirmCMDreceived ==== " +
				"0x90, response to 0x"+Integer.toHexString(data[0]));
	}
	
	

	

}
