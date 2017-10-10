package com.wm.bleconnect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wm.bleconnect.ResourceUtils.UIHandler;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ConnectActivity extends Activity implements OnClickListener{
	private static final String TAG = ResourceUtils.TAG;
	
	Button btn_connect;
	private Object locker = new Object();
	ArrayList<Map<String,Object>> mDevices = new ArrayList<Map<String,Object>>();
	
	ListView lst_devices;
	MySimpleAdapter lstAdapter;
	String[] from = new String[]{"name","address","state"};
	int[] to = new int[]{R.id.txt_de_lst_itm_0, R.id.txt_de_lst_itm_1,
			R.id.txt_de_lst_itm_2};
	
	HandlerThread mHandlerThread;
	MyHandler mHandler;
	BluetoothReceiver mReceiver;
	ServiceReceiver sReceiver;
	IntentFilter mFilter;
	IntentFilter sFilter;
	UIHandler mUIHandler;
	BluetoothAdapter mBTAdapter;
	BluetoothManager mBTManager;
	ProgressBar probar;
	
	private ConnectionService mService = null;
	private boolean mScanning = false;
	private long SCAN_PERIOD = 10000L;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connect);
		btn_connect = (Button)findViewById(R.id.btn_conn_0);
		btn_connect.setOnClickListener(this);
		
		
		lst_devices = (ListView)findViewById(R.id.lstv_conn_0);
		lstAdapter = new MySimpleAdapter(getApplicationContext(), getData(),
				R.layout.device_list_item, from, to);
		lst_devices.setAdapter(lstAdapter);
		probar = (ProgressBar)findViewById(R.id.probar_conn_0);
		probar.setVisibility(ProgressBar.INVISIBLE);
	
		mHandlerThread = new HandlerThread("BLE_connect_thread");
		mHandlerThread.start();
		mHandler = new MyHandler(mHandlerThread.getLooper());
		mUIHandler = UIHandler.getInstance();
		initReceiver();
		mService = ConnectionService.getInstance();
		
		mBTAdapter = BluetoothAdapter.getDefaultAdapter();
		discoveryDevice();
		mBTManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
		
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_conn_0:
			discoveryDevice();
			break;
		}
	}
	
	

	@Override
	protected void onStop() {
		super.onStop();
		
		mService.scanLeDevice(false);
	}

	private void discoveryDevice(){
		Log.d(TAG, "discoverying device");
		if(mBTAdapter == null){
			mBTAdapter = BluetoothAdapter.getDefaultAdapter();
			if(mBTAdapter == null)
				return;
		}
		if(!mBTAdapter.isEnabled()){
			//TODO Alert
			if(mBTAdapter.enable()){
				Toast.makeText(this, "Bluetooth turned on!", Toast.LENGTH_SHORT).show();
			}
		}
		
		if(mBTAdapter.isDiscovering()){
			mBTAdapter.cancelDiscovery();
		}
		synchronized (locker) {
			mDevices.clear();
			addConnectedDevices();
		}
		lstAdapter.notifyDataSetChanged();
		//tmp test
		//mBTAdapter.startDiscovery(); 
		Log.d(TAG, "ConnectActivity : discoverying BT device start");
		
		//BLE
		mService.scanLeDevice(true);
		synchronized (locker) {
			mDevices.clear();
			addConnectedBLE();
		}
		
		
		Log.d(TAG, "ConnectActivity : discoverying BLE device start");
	}
	
	private void addConnectedDevices(){
		HashMap<String, Object> map ;
		Set<BluetoothDevice> cntDev = ConnectionService.getInstance().getConnectedWMDevices();
		for(BluetoothDevice d : cntDev){
			map = new HashMap<String, Object>();
			map.put("name", d.getName());
			map.put("address", d.getAddress());
			//TODO to get connected state
			String state = "UNKNOWN"; 
			if(ResourceUtils.isConnected(d)){
				state = "CONNECTED";
			}else{
				state = "NOT CONNECTED";
			}
			Log.d(TAG, "ConnectActivity : device found "+d.getName()+" connected "
					+ResourceUtils.isConnected(d));
			map.put("state", state);
			map.put("device", d);
			mDevices.add(map);
		}
		lstAdapter.notifyDataSetChanged();
	}
	
	private void addConnectedBLE(){
		HashMap<String, Object> map ;
		Set<BluetoothDevice> cntBLE = 
				ConnectionService.getInstance().getConnectedBLESet();
		for(BluetoothDevice d : cntBLE){
			map = new HashMap<String, Object>();
			map.put("name", d.getName());
			map.put("address", d.getAddress());
			//TODO to get connected state
			String state = "UNKNOWN"; 
			if(ResourceUtils.isBLEConnected(d)){
				state = "CONNECTED";
			}else{
				state = "NOT CONNECTED";
			}
			Log.d(TAG, "ConnectActivity : device found "+d.getName()+" connected "
					+ResourceUtils.isBLEConnected(d));
			map.put("state", state);
			map.put("device", d);
			mDevices.add(map);
		}
		lstAdapter.notifyDataSetChanged();
	}
	
/*	private void scanLeDevice(final boolean enable){
		if(enable){
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					mBTAdapter.stopLeScan(mLeScanCallback);
				}
			}, SCAN_PERIOD);
			
			mScanning = true;
			mBTAdapter.startLeScan(mLeScanCallback);
			Log.d(TAG, "ConnectActivity : start scanning.............");
		}else{
			mScanning = false;
			mBTAdapter.stopLeScan(mLeScanCallback);
		}
	}
	
	private BluetoothAdapter.LeScanCallback mLeScanCallback =
			new BluetoothAdapter.LeScanCallback(){

				@Override
				public void onLeScan(BluetoothDevice device, int rssi,
						byte[] scanRecord) {
					Log.d(TAG, "..........................");
					Log.d(TAG, "ConnectActivity :mLeScanCallback onLeScan()"
						+", discovered a device called "+device.getName());
					Log.d(TAG, "ConnectActivity : device mac address = "
						+device.getAddress());
					BluetoothGatt mGatt = null;
					try{
						mGatt = device.connectGatt(
								ConnectActivity.this, false, mBluetoothGattCallback);
					}catch(Exception e){
						e.printStackTrace();
						Log.d(TAG, "......Gatt connect exception....................");
					}
					
					Log.d(TAG, "ConnectActivity :mLeScanCallback onLeScan() " +
							"ready to toast, mService == nulll ?"+(mService == null));
					try{
						ResourceUtils.toastShort("BLE "+device.getName()+" connected");
						Toast.makeText(mService, "BLE "+device.getName()+" connected",
								Toast.LENGTH_LONG).show();
					}catch(Exception e){
						e.printStackTrace();
						Log.d(TAG, "......Toast exception....................");
					}
					
					int mState = mBTManager.getConnectionState(device,BluetoothProfile.GATT );
					Log.d(TAG, "ConnectActivity :mLeScanCallback onLeScan()" +
							"device = "+device.getName()+", state = " +mState);
					if(mState == mGatt.STATE_CONNECTED){
						Log.d(TAG, "ConnectActivity :mLeScanCallback onLeScan()" +
								"device = "+device.getName()+", state = STATE_CONNECTED");
					}else if(mState == mGatt.STATE_CONNECTING){
						Log.d(TAG, "ConnectActivity :mLeScanCallback onLeScan()" +
								"device = "+device.getName()+", state = STATE_CONNECTING");
					}else if(mState == mGatt.STATE_DISCONNECTED){
						Log.d(TAG, "ConnectActivity :mLeScanCallback onLeScan()" +
								"device = "+device.getName()+", state = STATE_DISCONNECTED");
					}else if(mState == mGatt.STATE_DISCONNECTING){
						Log.d(TAG, "ConnectActivity :mLeScanCallback onLeScan()" +
								"device = "+device.getName()+", state = STATE_DISCONNECTING");
					}
					
					mGatt.discoverServices();
					
					
					mService.addBLEDevice(device, mGatt);
					Log.d(TAG, "ConnectActivity :mLeScanCallback onLeScan()"
							+" scanRecord = "+Arrays.toString(scanRecord));
					addBLEToMap(device);
					Log.d(TAG, "..........................");
				}
		
	};
	
	
	
	private BluetoothGattCallback mBluetoothGattCallback =
			new BluetoothGattCallback(){
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt,
				int status, int newState) {
			super.onConnectionStateChange(gatt, status, newState);
			Log.d(TAG, "ConnectActivity :1. mBTGattCallback onConnectionStateChange() "
					+"Name = "+gatt.getDevice().getName()+", address = "
					+gatt.getDevice().getAddress());
			if(status == BluetoothProfile.STATE_CONNECTED){
				Log.d(TAG, "ConnectActivity :2. mBTGattCallback onConnectionStateChange() "
						+"Name = "+gatt.getDevice().getName()+", state = "
						+"BluetoothProfile.STATE_CONNECTED");
			}else if(status == BluetoothProfile.STATE_DISCONNECTED){
				Log.d(TAG, "ConnectActivity :3. mBTGattCallback onConnectionStateChange() "
						+"Name = "+gatt.getDevice().getName()+", state = "
						+"BluetoothProfile.STATE_DISCONNECTED");
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			super.onServicesDiscovered(gatt, status);
			Log.d(TAG, "ConnectActivity :mBTGattCallback onServicesDiscovered()"
					+gatt.getDevice().getName());
			
			List<BluetoothGattService> l = gatt.getServices();
			Log.d(TAG, "ConnectActivity :mBluetoothGattCallback onServicesDiscovered() " +
					"services list size = "+l.size());
			for(BluetoothGattService s : l){
				Log.d(TAG, "ConnectActivity :Service UUID = "+s.getUuid().toString());
			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			super.onCharacteristicRead(gatt, characteristic, status);
			Log.d(TAG, "ConnectActivity :mBTGattCallback onCharacteristicRead()"
					+gatt.getDevice().getName());
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			super.onCharacteristicWrite(gatt, characteristic, status);
			Log.d(TAG, "ConnectActivity :mBTGattCallback onCharacteristicWrite()"
					+gatt.getDevice().getName());
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			super.onCharacteristicChanged(gatt, characteristic);
			Log.d(TAG, "ConnectActivity :mBTGattCallback onCharacteristicChanged()"
					+gatt.getDevice().getName());
		}

		@Override
		public void onDescriptorRead(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
			super.onDescriptorRead(gatt, descriptor, status);
			Log.d(TAG, "ConnectActivity :mBTGattCallback onDescriptorRead()"
					+gatt.getDevice().getName());
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
			super.onDescriptorWrite(gatt, descriptor, status);
			Log.d(TAG, "ConnectActivity :mBTGattCallback onDescriptorWrite()"
					+gatt.getDevice().getName());
		}

		@Override
		public void onReliableWriteCompleted(BluetoothGatt gatt,
				int status) {
			super.onReliableWriteCompleted(gatt, status);
			Log.d(TAG, "ConnectActivity :mBTGattCallback onReliableWriteCompleted()"
					+gatt.getDevice().getName());
		}

		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi,
				int status) {
			super.onReadRemoteRssi(gatt, rssi, status);
			Log.d(TAG, "ConnectActivity :mBTGattCallback onReadRemoteRssi()"
					+gatt.getDevice().getName());
		}
	};*/
	
	private void addBLEToMap(BluetoothDevice d){
		synchronized (locker) {
			for(Map<String, Object> m : mDevices){
				if(((BluetoothDevice)m.get("device")).getAddress().equals(d.getAddress())){
					Log.d(TAG, "ConnectActivity : device existed "+d.getName());
					return;
				}
			}
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("name", d.getName());
			map.put("address", d.getAddress());
			//TODO to get connected state
			String state = "UNKNOWN"; 
			if(ResourceUtils.isBLEConnected(d)){
				state = "CONNECTED";
			}else{
				state = "NOT CONNECTED";
			}
			Log.d(TAG, "ConnectActivity : device found "+d.getName()+" connected "
					+ResourceUtils.isConnected(d));
			map.put("state", state);
			map.put("device", d);
			mDevices.add(map);
		}
		mUIHandler.post(new Runnable() {
			@Override
			public void run() {
				lstAdapter.notifyDataSetChanged();
			}
		});
	}

	
	private List<Map<String,Object>> getData(){
		synchronized (locker) {
			return mDevices;
		}
	}
	
	private void initReceiver(){
		mReceiver = new BluetoothReceiver();
		mFilter = new IntentFilter();
		mFilter.addAction(BluetoothDevice.ACTION_FOUND);
		mFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		mFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
		mFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		mFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		mFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		mFilter.addAction(ResourceUtils.ACTION_BLE_DISCOVERY_START);
		mFilter.addAction(ResourceUtils.ACTION_BLE_DISCOVERY_FINISHED);
		
		sReceiver = new ServiceReceiver();
		sFilter = new IntentFilter();
		sFilter.addAction(ResourceUtils.ACTION_NEW_DEVICE_CONNECTED);
		sFilter.addAction(ResourceUtils.ACTION_CONNECTED_DEVICE_LOST);
		sFilter.addAction(ResourceUtils.ACTION_BLE_FOUND);
		sFilter.addAction(ResourceUtils.ACTION_BLE_CONNECTED);
		sFilter.addAction(ResourceUtils.ACTION_BLE_DISCONNECTED);
		registerReceiver(mReceiver, mFilter);
		registerReceiver(sReceiver, sFilter);
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
		unregisterReceiver(sReceiver);
	}
	
	private class MySimpleAdapter extends SimpleAdapter {

		public MySimpleAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final int mPosition = position;
			convertView = super.getView(position, convertView, parent);
			Button btn_conn = (Button) convertView
					.findViewById(R.id.btn_de_lst_itm_0);
			btn_conn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d(TAG, "ConnectActivity : Button connect pressed");
					Map<String,Object> m = mDevices.get(mPosition);
					final BluetoothDevice d = (BluetoothDevice)m.get("device");
					new Thread(){
						@Override
						public void run() {
							super.run();
							//xianzhe
							//ConnectionService.getInstance().connectToDevice(d);
							ConnectionService.getInstance().connectToBLE(d);
						}
						
					}.start();
					Log.d(TAG, "ConnectActivity :Connecting to "+d.getName());
				}
			});
			Button btn_discon = (Button) convertView
					.findViewById(R.id.btn_de_lst_itm_1);
			btn_discon.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d(TAG, "ConnectActivity : Button disconnect pressed");
					Map<String,Object> m = mDevices.get(mPosition);
					final BluetoothDevice d = (BluetoothDevice)m.get("device");
					new Thread(){
						@Override
						public void run() {
							super.run();
							//xianzhe
							//ConnectionService.getInstance().disconnect(d);
							ConnectionService.getInstance().disconnectToBLE(d);
						}
						
					}.start();
					Log.d(TAG, "ConnectActivity :Disonnecting to "+d.getName());
				}
			});
			return convertView;
		}

		private Handler mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				
				}
			}

		};

	}
	
	
	private class MyHandler extends Handler{
		
		public MyHandler (Looper looper){
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
		
	}
	
	private class BluetoothReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(BluetoothDevice.ACTION_FOUND)){
				BluetoothDevice d = 
						intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				Log.d(TAG, "ConnectActivity : device found "+d.getName());
				synchronized (locker) {
					for(Map<String, Object> m : mDevices){
						if(((BluetoothDevice)m.get("device")).getAddress().equals(d.getAddress())){
							Log.d(TAG, "ConnectActivity : device existed "+d.getName());
							return;
						}
					}
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("name", d.getName());
					map.put("address", d.getAddress());
					//TODO to get connected state
					String state = "UNKNOWN"; 
					if(ResourceUtils.isConnected(d)){
						state = "CONNECTED";
					}else{
						state = "NOT CONNECTED";
					}
					Log.d(TAG, "ConnectActivity : device found "+d.getName()+" connected "
							+ResourceUtils.isConnected(d));
					map.put("state", state);
					map.put("device", d);
					mDevices.add(map);
				}
				mUIHandler.post(new Runnable() {
					@Override
					public void run() {
						lstAdapter.notifyDataSetChanged();
					}
				});
			}else if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
				
			}else if(action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)){
				
				
			}else if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
				
			}else if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)){
				probar.setVisibility(ProgressBar.VISIBLE);
			}else if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){
				probar.setVisibility(ProgressBar.INVISIBLE);
			}else if(action.equals(ResourceUtils.ACTION_BLE_DISCOVERY_START)){
				probar.setVisibility(ProgressBar.VISIBLE);
			}else if(action.equals(ResourceUtils.ACTION_BLE_DISCOVERY_FINISHED)){
				probar.setVisibility(ProgressBar.INVISIBLE);
			}
			
			
			
			
			
		}
		
	}
	
	private class ServiceReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			
			synchronized (locker) {
				
				
				if(action.equals(ResourceUtils.ACTION_NEW_DEVICE_CONNECTED)){
					BluetoothDevice d = intent.getParcelableExtra(ResourceUtils.DATA_BTDEVICE);
					Log.d(TAG, "ConnectActivity : device connected "+d.getName());
					for(Map<String, Object> m : mDevices){
						if(((BluetoothDevice)m.get("device")).getAddress().equals(d.getAddress())){
							Log.d(TAG, "ConnectActivity : device existed "+d.getName());
							String state = "UNKNOWN";
							if(ResourceUtils.isConnected(d)){
								state = "CONNECTED";
							}else{
								state = "NOT CONNECTED";
							}
							m.put("state", state);
							lstAdapter.notifyDataSetChanged();
							return;
						}
					}
				}else if(action.equals(ResourceUtils.ACTION_CONNECTED_DEVICE_LOST)){
					BluetoothDevice d = intent.getParcelableExtra(ResourceUtils.DATA_BTDEVICE);
					Log.d(TAG, "ConnectActivity : device lost "+d.getName());
					for(Map<String, Object> m : mDevices){
						if(((BluetoothDevice)m.get("device")).getAddress().equals(d.getAddress())){
							Log.d(TAG, "ConnectActivity : device existed "+d.getName());
							String state = "UNKNOWN";
							if(ResourceUtils.isConnected(d)){
								state = "CONNECTED";
							}else{
								state = "NOT CONNECTED";
							}
							m.put("state", state);
							lstAdapter.notifyDataSetChanged();
							return;
						}
					}
				
				
				}else if(action.equals(ResourceUtils.ACTION_BLE_FOUND)){
					BluetoothDevice d = intent.getParcelableExtra(ResourceUtils.DATA_BTDEVICE);
					Log.d(TAG, "ConnectActivity : ACTION_BLE_FOUND "+d.getName());
					addBLEToMap(d);
				}else if(action.equals(ResourceUtils.ACTION_BLE_CONNECTED)){
					BluetoothDevice d = intent.getParcelableExtra(ResourceUtils.DATA_BTDEVICE);
					Log.d(TAG, "ConnectActivity : BLE connected: "+d.getName());
					for(Map<String, Object> m : mDevices){
						if(((BluetoothDevice)m.get("device")).getAddress().equals(d.getAddress())){
							Log.d(TAG, "ConnectActivity : device existed "+d.getName());
							String state = "UNKNOWN";
							if(ResourceUtils.isBLEConnected(d)){
								state = "CONNECTED";
							}else{
								state = "NOT CONNECTED";
							}
							m.put("state", state);
							lstAdapter.notifyDataSetChanged();
							return;
						}
					}
					
				}else if(action.equals(ResourceUtils.ACTION_BLE_DISCONNECTED)){
					BluetoothDevice d = intent.getParcelableExtra(ResourceUtils.DATA_BTDEVICE);
					Log.d(TAG, "ConnectActivity : BLE disconnected: "+d.getName());
					for(Map<String, Object> m : mDevices){
						if(((BluetoothDevice)m.get("device")).getAddress().equals(d.getAddress())){
							Log.d(TAG, "ConnectActivity : device existed "+d.getName());
							String state = "UNKNOWN";
							if(ResourceUtils.isBLEConnected(d)){
								state = "CONNECTED";
							}else{
								state = "NOT CONNECTED";
							}
							m.put("state", state);
							lstAdapter.notifyDataSetChanged();
							return;
						}
					}
				} 
				
			}
			
			
		}
		
	}


	
	
	
	
	
	

}
