package com.wm.bleconnect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wm.bleconnect.ResourceUtils.UIHandler;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class RealtimeDataActivity extends Activity {
	private static final String TAG = ResourceUtils.TAG;
	private static final int WHAT_GET_REALTIME_DATA = 0x001;
	private static final int WHAT_AUTO_REALTIME_DATA = 0x002;
	private static final int WHAT_STOP_ALL_AUTO_THREAD = 0x003;
	private static final int WHAT_STOP_AUTO_THREAD = 0X004;
	
	private boolean isAutoPressed = false;
	IntentFilter mIntentFilter;
	CMDReceiver mReceiver;
	
	UIHandler mUIHandler;
	
	ListView listView;
	MySimpleAdapter listAdapter;
	List<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();
	List<HashMap<String, Object>>  mDevices = new ArrayList<HashMap<String, Object>>();
	Map<String, Drawable> iconMap = new HashMap<String, Drawable>();
	
	HandlerThread mHandlerThread;
	RealtimeHandler mRTHandler;

	String[] from = new String[]{"name",
			"data0","data1","data2","data3","data4","data5","data6","data7"};
	int[] to = new int[]{R.id.txt_rt_item_name,
			R.id.txt_rt_item_PM,R.id.txt_rt_item_EM,
			R.id.txt_rt_item_VOC,R.id.txt_rt_item_UV,
			R.id.txt_rt_item_HUM,R.id.txt_rt_item_TEM,
			R.id.txt_rt_item_V,R.id.txt_rt_item_LVL};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "RealtimeDataActivity : RealtimeDataActivity ========================="
				+ "=====================START========================================");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_realtime_data);
		
		listView = (ListView)findViewById(R.id.listView_realtime);
		listAdapter = new MySimpleAdapter(getApplicationContext(), getData(), 
			R.layout.realtime_list_item, from, to);
		listView.setAdapter(listAdapter);
		
		mHandlerThread = new HandlerThread("Realtime ble handler");
		mHandlerThread.start();
		mRTHandler = new RealtimeHandler(mHandlerThread.getLooper());
		
		mUIHandler = UIHandler.getInstance();
		initReceiver();
		
		//sendReceiveRealtimeDataCMDToAll();
	}
	
	private void initReceiver(){
        mIntentFilter = new IntentFilter();
        //mIntentFilter.addAction(ResourceUtils.ACTION_NEW_DEVICE_CONNECTED);
        //mIntentFilter.addAction(ResourceUtils.ACTION_CONNECTED_DEVICE_LOST);
        mIntentFilter.addAction(ResourceUtils.ACTION_CMD_RECEIVED);
        mReceiver = new CMDReceiver();
        registerReceiver(mReceiver, mIntentFilter);
	}
	
	private List<HashMap<String, Object>> getData(){
		Set<BluetoothDevice> devices = ConnectionService.getInstance()
				.getConnectedBLESet();
		//List<HashMap<String, Object>>
		HashMap<String, Object> map ;
		mDeviceList.clear();
		mDevices.clear();
		for(BluetoothDevice d : devices){
			mDeviceList.add(d);
			map = new HashMap<String, Object>();
			map.put("device", d);
			map.put("name", d.getName());
			Log.d(TAG, "RealtimeDataActivity : update UI "+d.getName());
			map.put("data0", "0");
			map.put("data1", "0");
			map.put("data2", "0");
			map.put("data3", "0");
			map.put("data4", "0");
			map.put("data5", "0");
			map.put("data6", "0");
			map.put("data7", "0");
			mDevices.add(map);
		}
		return mDevices;
	}
	
	

	@Override
	protected void onStop() {
		super.onStop();
		Message msg = Message.obtain();
		msg.what = WHAT_STOP_ALL_AUTO_THREAD;
		mRTHandler.handleMessage(msg);
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}
	
	private void sendReceiveRealtimeDataCMDToAll(){
		Set<BluetoothDevice> devices = ConnectionService.getInstance()
				.getConnectedWMDevices();
		for(BluetoothDevice d : devices){
			ResourceUtils.sendCMDToDevice(0x43, new int[]{0}, d);
		}
	}
	
	
	private class MySimpleAdapter extends SimpleAdapter {

		public MySimpleAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.d(TAG, "RealtimeDataActivity : getView, init +++++++++==============++++++++");
			final int mPosition = position;
			convertView = super.getView(position, convertView, parent);
			
			Map<String,Object> m = mDevices.get(mPosition);
			final BluetoothDevice d = (BluetoothDevice)m.get("device");
			final EditText edt_seconds = (EditText) convertView
					.findViewById(R.id.edt_rt_0_0);
			int intervalTime = mRTHandler.getAutoIntervalTime(d);
			if(intervalTime != -1){
				Log.d(TAG, "RealtimeDataActivity : getView, init intervalTime = "+intervalTime+" "
						+ "(not -1)");
				edt_seconds.setText(""+intervalTime);
			}else{
				Log.d(TAG, "RealtimeDataActivity : getView, init intervalTime = "+intervalTime+" "
						+ "(shoud be -1)");
				edt_seconds.setText("");
			}
			//xz150907
			//edt_seconds.setEnabled(true);
			Log.d(TAG, "RealtimeDataActivity : getView, init edt_seconds.setEnabled(true)");
			
			final Button btn_auto = (Button) convertView
					.findViewById(R.id.btn_rt_rt_auto);
			if(!mRTHandler.isAutoThreadAlived(d)){
				Log.d(TAG, "RealtimeDataActivity : getView, init view isAutoThreadAlived = false");
				edt_seconds.setEnabled(true);
				btn_auto.setBackgroundColor(Color.rgb(0x55, 0x55, 0x55));//cc white, 55 black
			}else{
				Log.d(TAG, "RealtimeDataActivity : getView, init view isAutoThreadAlived = true"
						+" intervalTime = "+mRTHandler.getAutoIntervalTime(d));
				edt_seconds.setEnabled(false);
				btn_auto.setBackgroundColor(Color.rgb(0xcc, 0xcc, 0xcc));//55 black, cc white
			}
			btn_auto.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//xz150829
					Log.d(TAG, "RealtimeDataActivity : Button Auto pressed");
					

					//if(edt_seconds.isEnabled()){
					if(!mRTHandler.isAutoThreadAlived(d)){
						Log.d(TAG, "RealtimeDataActivity : Button Auto pressed to start thread");
						new Thread(){
							@Override
							public void run() {
								super.run();
								Log.d(TAG, "RealtimeDataActivity : 1===== device "
										+d.getName()+" picked");
								
								int seconds = 0;
								try{
									seconds = Integer.parseInt(edt_seconds.getText().toString());
								}catch(Exception e){
									e.printStackTrace();
									ResourceUtils.toastShort("Please input correct parameter");
									return;
								}
								
								if(seconds == 0){
									return;
								}
								
								Log.d(TAG, "RealtimeDataActivity : auto seconds = "+seconds);
								Message msg = Message.obtain();
								msg.what = WHAT_AUTO_REALTIME_DATA;
								msg.arg1 = seconds;
								Bundle b = new Bundle();
								b.putParcelable(ResourceUtils.DATA_BTDEVICE, d);
								msg.setData(b);
								mRTHandler.handleMessage(msg);
								
								
							}
							
						}.start();
						edt_seconds.setEnabled(false);
						Log.d(TAG, "RealtimeDataActivity : getView, edt_seconds was set to false");
						btn_auto.setBackgroundColor(Color.rgb(0x55, 0x55, 0x55));
					}else{
						Log.d(TAG, "RealtimeDataActivity : Button Auto pressed to interrupt thread");
						Message msg = Message.obtain();
						msg.what = WHAT_STOP_AUTO_THREAD;
						Bundle b = new Bundle();
						b.putParcelable(ResourceUtils.DATA_BTDEVICE, d);
						msg.setData(b);
						mRTHandler.handleMessage(msg);
						edt_seconds.setEnabled(true);
						Log.d(TAG, "RealtimeDataActivity : getView, edt_seconds was set to true");
						btn_auto.setBackgroundColor(Color.rgb(0xcc, 0xcc, 0xcc));
						
					}
					
					
					


				}
			});
			
			Button btn_refresh = (Button) convertView
					.findViewById(R.id.btn_rt_refresh);
			btn_refresh.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d(TAG, "RealtimeDataActivity : Button Refresh pressed");
					Map<String,Object> m = mDevices.get(mPosition);
					final BluetoothDevice d = (BluetoothDevice)m.get("device");
					new Thread(){
						@Override
						public void run() {
							super.run();
							Log.d(TAG, "RealtimeDataActivity : 2===== device "
									+d.getName()+" picked");
							Message msg = Message.obtain();
							msg.what = WHAT_GET_REALTIME_DATA;
							Bundle b = new Bundle();
							b.putParcelable(ResourceUtils.DATA_BTDEVICE, d);
							msg.setData(b);
							mRTHandler.handleMessage(msg);
						}
						
					}.start();

				}
			});
			return convertView;
		}


	}
	
	
	
	
	
	
	
	
	//for tmp use
	private HashMap<String, Object> item = null;
	private class CMDReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "RealtimeDataActivity : Receive a command");
			BluetoothDevice d = intent.getParcelableExtra(ResourceUtils.DATA_BTDEVICE);
			
			for(HashMap<String, Object> m : mDevices){
				if(((BluetoothDevice)(m.get("device"))).getAddress().equals(
						d.getAddress())){
					item = m;
					break;
				}
			}
			if(item == null){
				return;
			}
			int cmd = intent.getIntExtra(ResourceUtils.DATA_RECEIVED_CMD, -1);
			Log.d(TAG, "RealtimeDataActivity : Receive a command, cmd = 0x"
					+Integer.toHexString(cmd));
			final int[] data = intent.getIntArrayExtra(
					ResourceUtils.DATA_RECEIVED_DATA);
			final byte[] rawData = intent.getByteArrayExtra(
					ResourceUtils.DATA_RECEIVED_RAW_DATA);
			switch(cmd){
			case 0x43:
				mUIHandler.post(new Runnable() {
					@Override
					public void run() {
						item.put("data0", data[0]);
						item.put("data1", data[1]);
						item.put("data2", data[2]);
						item.put("data3", data[3]);
						item.put("data4", data[4]);
						item.put("data5", data[5]);
						item.put("data6", data[6]);
						item.put("data7", "0B"+Integer.toBinaryString(data[7]));
						
						listAdapter.notifyDataSetChanged();
						//xianzheTest
						ResourceUtils.toastShort("RawData :"+Arrays.toString(rawData));
						
						
						
					}
				});
				//!!!red alert, reply to device
				//ResourceUtils.sendCMDToDevice(0x90, new int[]{67,0}, d);
				break;
			
			}
			//ResourceUtils.sendCMDToCurrentDevice(0x90, new int[]{cmd,0});
		}
		
	}
	
	private class RealtimeHandler extends Handler{
		HashMap<BluetoothDevice, HashMap<String, Object>> threadMap = 
				new HashMap<BluetoothDevice, HashMap<String, Object>>();
		
		public RealtimeHandler(Looper looper){
			super(looper);
		}
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case WHAT_GET_REALTIME_DATA:
				Bundle b1 = msg.getData();
				BluetoothDevice d1 = b1.getParcelable(ResourceUtils.DATA_BTDEVICE);
				ResourceUtils.sendCMDToDevice(0x43, new int[]{0}, d1);
				//xz150828: not interrupt auto thread
				//interruptAutoThread(d1);
				
				break;
			case WHAT_AUTO_REALTIME_DATA:
				Bundle b2 = msg.getData();
				final BluetoothDevice d2 = b2.getParcelable(
						ResourceUtils.DATA_BTDEVICE);
				final int seconds = msg.arg1;
				HashMap<String, Object> tmp2 = new HashMap<String, Object>();
				if(threadMap.containsKey(d2)){
					tmp2 = threadMap.get(d2);
					int oldSec = (Integer)tmp2.get("seconds");
					if(oldSec == seconds){
						return;
					}
					Thread oldThr = (Thread) tmp2.get("thread");
					oldThr.interrupt();
				}
				Thread newThr = new Thread(){

					@Override
					public void run() {
						Log.d(TAG, "RealtimeDataActivity : " +
								"Auto Thread started ====================== !!!!");
						super.run();
						while(true){
							ResourceUtils.sendCMDToDevice(0x43, new int[]{0}, d2);
							try {
								Thread.sleep(seconds*1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
								Log.d(TAG, "RealtimeDataActivity : " +
										"Thread interrupted ====================== !!!");
								return;
							}
						}
					}
				};
				newThr.start();
				tmp2.put("thread", newThr);
				tmp2.put("seconds", seconds);
				threadMap.put(d2, tmp2);
				Log.d(TAG, "RealtimeDataActivity :  auto thread started");
				break;
				
			case WHAT_STOP_ALL_AUTO_THREAD:
				Set<BluetoothDevice> set = threadMap.keySet();
				for(BluetoothDevice device : set){
					interruptAutoThread(device);
				}
				
				break;
			
			case WHAT_STOP_AUTO_THREAD:
				Bundle b3 = msg.getData();
				BluetoothDevice d3 = b3.getParcelable(ResourceUtils.DATA_BTDEVICE);
				interruptAutoThread(d3);
				break;

			default:
				break;
			}
		}
		
		
		private void interruptAutoThread(BluetoothDevice d){
			
			Set<BluetoothDevice> set = threadMap.keySet();
			for(BluetoothDevice device : set){
				if(d.getAddress().equals(device.getAddress())){
					HashMap<String, Object> map = threadMap.get(device);
					Thread t = (Thread)map.get("thread");
					t.interrupt();
					threadMap.remove(d);
					Log.d(TAG, "RealtimeDataActivity : "+d.getName()
							+" auto thread interrupted =======================");
					return;
				}
				
			}
			Log.d(TAG, "RealtimeDataActivity : "+d.getName()+
					" auto thread not found ===========================");
			return;
			
		}
		
		public boolean isAutoThreadAlived(BluetoothDevice d){
			Set<BluetoothDevice> set = threadMap.keySet();
			for(BluetoothDevice device : set){
				if(d.getAddress().equals(device.getAddress())){
					Log.d(TAG, "RealtimeDataActivity : "+d.getName()+
							" auto thread alived");
					return true;
				}
				
			}
			Log.d(TAG, "RealtimeDataActivity : "+d.getName()+
					" auto thread not alived");
			return false;
		}
		
		public int getAutoIntervalTime(BluetoothDevice d){
			Set<BluetoothDevice> set = threadMap.keySet();
			for(BluetoothDevice device : set){
				if(d.getAddress().equals(device.getAddress())){
					Log.d(TAG, "RealtimeDataActivity : "+d.getName()+
							" auto thread alived");
					HashMap<String, Object> map = threadMap.get(device);
					int time = (Integer)map.get("seconds");
					return time;
				}
				
			}
			Log.d(TAG, "RealtimeDataActivity : "+d.getName()+
					" auto thread interval not exist!");
			return -1;
		}
		
	}
	

}
