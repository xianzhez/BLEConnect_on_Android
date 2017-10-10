package com.wm.bleconnect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;


public class ShowActivity extends Activity implements OnClickListener,OnItemClickListener{
	private static final String TAG = ResourceUtils.TAG;
	
	public static final String FILTER_ACTION_CMD_RECEIVED = "ACTION_CMD_RECEIVED";
	public static final String FILTER_ACTION_2 = "connect";
	
	MyReceiver mReceiver;
	IntentFilter mUIFilter;
	
//	HandlerThread mHandlerThread;
	Handler mHandler;
//	Button btn_connect;
//	Button btn_test;
	GridView gridView;
	SimpleAdapter adapter;
	ProgressDialog initDialog;
	int[] itemIcon = {R.drawable._1clock,R.drawable._2version,R.drawable._3history,
			R.drawable._4realtimedata,R.drawable._5calibration,
			R.drawable._6oad,R.drawable._7more,R.drawable._8device};
	String[] itemText = {"1.时间","2.版本","3.历史数据","4.实时数据","5.校准","6.OAD命令","7.更多","8.设备管理"};
	
	//Connected device	
	String[] from = new String[]{"name","address","state"};
	int[] to = new int[]{R.id.txt_de_lst_itm_0, R.id.txt_de_lst_itm_1,
			R.id.txt_de_lst_itm_2};
	ListView lst_devices;
	MySimpleAdapter lstAdapter;
	ArrayList<Map<String,Object>> mConnectedDevices = new ArrayList<Map<String,Object>>();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        init();

//        btn_connect = (Button)findViewById(R.id.btn_connect);
//        btn_connect.setOnClickListener(this);
//        btn_test = (Button)findViewById(R.id.btn_test);
//        btn_test.setOnClickListener(this);
        gridView = (GridView)findViewById(R.id.activity_show_gridView);
        adapter = new SimpleAdapter(getApplicationContext(),
        		getData(), R.layout.show_grid_item_layout, 
        		new String[]{"image", "text"}, 
        		new int[]{R.id.image_show_item, R.id.txt_show_item});

        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);
        
        lst_devices = (ListView)findViewById(R.id.lstv_show_0);
        lstAdapter = new MySimpleAdapter(getApplicationContext(), getDevicesData(),
				R.layout.device_list_item, from, to);
		lst_devices.setAdapter(lstAdapter);
    }
    
    

    private void init(){
    	BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    	if(adapter == null){
    		Toast.makeText(getApplicationContext(), "当前设备不支持蓝牙",
    				Toast.LENGTH_SHORT).show();
    		this.finish();
    		return;
    	}else{
    		if(!adapter.isEnabled()){
    			adapter.enable();
    			Toast.makeText(getApplicationContext(), "蓝牙已打开",
        				Toast.LENGTH_SHORT).show();
    		}
    	}
    	//start service in receiver when app directory has been initialized.
    	Log.d(TAG, "ShowActivity : ===========init().");
    	mUIFilter = new IntentFilter("com.wm.bleconnect.ConnectionService.UI");
    	mUIFilter.addAction(FILTER_ACTION_CMD_RECEIVED);
    	mUIFilter.addAction(ResourceUtils.ACTION_APP_INITIALIZED);
    	mUIFilter.addAction(ResourceUtils.ACTION_BLE_CONNECTED);
    	mUIFilter.addAction(ResourceUtils.ACTION_BLE_DISCONNECTED);
    	mReceiver = new MyReceiver();
    	registerReceiver(mReceiver, mUIFilter);
    	
//    	mHandlerThread = new HandlerThread("BLE_UI_Handler");
//    	mHandlerThread.start();
    	mHandler = new Handler();
    	ResourceUtils.appinit(this);
    	 
    	initDialog = new ProgressDialog(this);
    	initDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    	initDialog.setMessage("Initializing...");
    	initDialog.setIndeterminate(false);
    	initDialog.setCancelable(false);
    	
    	initDialog.show();
        
        //make directory for this app
        
        
    }
    
    private List<Map<String, Object>> getData(){
    	Map<String, Object> map = null;
    	List<Map<String, Object>> l = new ArrayList<Map<String, Object>>();
    	for(int i=0;i<itemIcon.length;i++){
    		map = new HashMap<String, Object>();
    		map.put("image", itemIcon[i]);
    		map.put("text", itemText[i]);
    		map.put("count", i);
    		l.add(map);
    	}
    	return l;
    }
    
    @Override
	public void onClick(View v) {
    	switch(v.getId()){
//    	case R.id.btn_connect:
//    		Log.d(TAG, "ShowActivity : Button Connect pressed.");
//    		Intent connIn = new Intent(this, ConnectActivity.class); 
//    		startActivity(connIn);
//    		break;
//    	case R.id.btn_test:
//    		Log.d(TAG, "ShowActivity : Button Test pressed.");
//    		TestBench.test();
//    		break;
    	}
    	
	}
    

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		@SuppressWarnings("unchecked")
		Map<String, Object> itemMap = 
				(Map<String, Object>) parent.getItemAtPosition(position);
		int count = (Integer) itemMap.get("count");
		Log.d(TAG, "ShowActivity : you clicked item "+count);
		Intent intent = null;
		switch(count){
		case 0:
//			intent = new Intent(getApplicationContext(),ClockActivity.class);
//			break;
		case 1:
		case 4:
		case 5:
			intent = new Intent(getApplicationContext(),DevicePickerActivity.class);
			intent.putExtra(ResourceUtils.EXTRA_INT_SHOW_CHOSEN, count);
			break;
		case 2:
			intent = new Intent(getApplicationContext(),HistoryDataActivity.class);
			break;
		case 3:
			intent = new Intent(getApplicationContext(),RealtimeDataActivity.class);
			break;
		case 6:
			//intent = new Intent(getApplicationContext(),DisplayActivity.class);
			intent = new Intent(getApplicationContext(),MoreActivity.class);
			break;
		case 7:
			intent = new Intent(getApplicationContext(), ConnectActivity.class); 
    		break;
		default:
			intent = new Intent(getApplicationContext(),DevicePickerActivity.class);
			intent.putExtra(ResourceUtils.EXTRA_INT_SHOW_CHOSEN, count);
			break;
		}
		startActivity(intent);
	}
    
	
    

    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

/*		if(requestCode == 1){
			final BluetoothDevice d = 
					data.getParcelableExtra(ResourceUtils.DATA_BTDEVICE);
			Thread t = new Thread(new Runnable() {
				
				@Override
				public void run() {
					ConnectionService.getInstance().connectToDevice(d);
					
				}
			});
			t.start();
		}*/
    
    }



/*	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
    
    
    
    @Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
    }



	@Override
	protected void onStart() {
		super.onStart();
	}
	



	@Override
	protected void onResume() {
		super.onResume();
		mConnectedDevices.clear();
		addConnectedDevices();
	}




	public class MyReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			if(action.equals("com.wm.bleconnect.ConnectionService.UI")){
				String str = intent.getStringExtra("test");
				if(str != null){
					Log.d(TAG, "ShowActivity : Receive a string : "+str);
				}				
			}else if(action.equals(FILTER_ACTION_CMD_RECEIVED)){
				Log.d(TAG, "ShowActivity : Receive a CMD from BLE...");
				
				
				
			}else if(action.equals(ResourceUtils.ACTION_APP_INITIALIZED)){
				Log.d(TAG, "ShowActivity : APP has been initialized.");
				Toast.makeText(getApplicationContext(), "APP initialized !!!", Toast.LENGTH_SHORT).show();
				Intent sIntent = new Intent(ShowActivity.this, ConnectionService.class);
				
		        startService(sIntent);
		        Log.d(TAG, "ShowActivity : to dismiss dialog");
		        if((initDialog != null)&&(initDialog.isShowing())){
		        	initDialog.dismiss();
		        	Log.d(TAG, "ShowActivity : dismissed dialog");
		        }
		        
			}else if((action.equals(ResourceUtils.ACTION_BLE_CONNECTED))
					||action.equals(ResourceUtils.ACTION_BLE_DISCONNECTED)){
				Log.d(TAG, "ShowActivity : Receiver ACTION_BLE_CONNECTED" +
						" or ACTION_BLE_DISCONNECTED.");
				mConnectedDevices.clear();
				addConnectedDevices();
				
				
			}else{
				Log.d(TAG, "ShowActivity : Receiver UNKNOWN action.");
			}
			
		}
    	
    }
	



    
    
//    public class UIHandler extends Handler{
//    	
//    	public UIHandler (Looper looper){
//    		super(looper);
//    	}
//    	
//		@Override
//		public void handleMessage(Message msg) {
//			// TODO Auto-generated method stub
//			super.handleMessage(msg);
//		}
//    	
//    }
	
	
	
	/**
	 * Connected devices
	 */
	
	private List<Map<String,Object>> getDevicesData(){
			return mConnectedDevices;
	}
	
	private void addConnectedDevices(){
		ConnectionService service = ConnectionService.getInstance();
		if(service == null){
			return;
		}
		
		HashMap<String, Object> map ;
		Set<BluetoothDevice> cntDev = service.getConnectedBLESet();
		Log.d(TAG, "ShowActivity : cntDev size = "+cntDev.size());
		for(BluetoothDevice d : cntDev){
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
			Log.d(TAG, "ShowActivity : device found "+d.getName()+" connected "
					+ResourceUtils.isBLEConnected(d));
			map.put("state", state);
			map.put("device", d);
			mConnectedDevices.add(map);
		}
		lstAdapter.notifyDataSetChanged();
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
			btn_conn.setVisibility(Button.INVISIBLE);
			btn_conn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d(TAG, "ShowActivity : Button connect pressed");
					Map<String,Object> m = mConnectedDevices.get(mPosition);
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
					Log.d(TAG, "ShowActivity :Connecting to "+d.getName());
				}
			});
			Button btn_discon = (Button) convertView
					.findViewById(R.id.btn_de_lst_itm_1);
			btn_discon.setVisibility(Button.INVISIBLE);
			btn_discon.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d(TAG, "ShowActivity : Button disconnect pressed");
					Map<String,Object> m = mConnectedDevices.get(mPosition);
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
					Log.d(TAG, "ShowActivity :Disonnecting to "+d.getName());
				}
			});
			return convertView;
		}


	}

	


    
}
