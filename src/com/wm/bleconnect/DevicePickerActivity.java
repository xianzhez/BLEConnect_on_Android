package com.wm.bleconnect;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DevicePickerActivity extends Activity implements OnItemClickListener{

	
	private ListView lst_devices ;
	private ArrayAdapter<String> adapter;
	private ConnectionService mService;
	private Intent oldIntent;
	private int nextActivity = -1;
	
	
	private ArrayList<BluetoothDevice> mDevicesList = new ArrayList<BluetoothDevice>();;
	private final String FLAG_NO_DEVICE = "No available device found...";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_picker);
		lst_devices = (ListView)findViewById(R.id.list_availabledevices);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_expandable_list_item_1);
		lst_devices.setAdapter(adapter);
		mService = ConnectionService.getInstance();
		oldIntent = this.getIntent();
		nextActivity = oldIntent.getIntExtra(ResourceUtils.EXTRA_INT_SHOW_CHOSEN,-1);
		
		//Set<BluetoothDevice> mDevicesSet = mService.getConnectedWMDevices();
		Set<BluetoothDevice> mDevicesSet = mService.getConnectedBLESet();
		if(mDevicesSet.size() != 0){
			mDevicesList.clear();
			for(BluetoothDevice d : mDevicesSet){
				mDevicesList.add(d);
				adapter.add(d.getName());
			}
			adapter.notifyDataSetChanged();
		}else{
			mDevicesList.clear();
			adapter.add(FLAG_NO_DEVICE);
			adapter.notifyDataSetChanged();
		}
		
		lst_devices.setOnItemClickListener(this);
		
		if(mDevicesSet.size() == 1){
			BluetoothDevice d = mDevicesList.get(0);
			ResourceUtils.setCurrentDevice(d);
			Intent next = getNextIntent();
			if(next != null)
				startActivity(getNextIntent());
			this.finish();
		}
		
		
	
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String str = (String)parent.getItemAtPosition(position);
		if(str.equals(FLAG_NO_DEVICE)){
			this.finish();
			return;
		}else{
			if(nextActivity == -1){
				this.finish();
				return;
			}
			BluetoothDevice d = mDevicesList.get(position);
			ResourceUtils.setCurrentDevice(d);
			Intent next = getNextIntent();
			if(next != null)
				startActivity(getNextIntent());
			this.finish();
		
		}
		
	}
	
	private Intent getNextIntent(){
		if(nextActivity == -1)
			return null;
		Intent intent = null;
		switch(nextActivity){
		case 0:
			intent = new Intent(getApplicationContext(), ClockActivity.class);
			break;
		case 1:
			intent = new Intent(getApplicationContext(), VersionActivity.class);
			break;
		case 2:
			intent = new Intent(getApplicationContext(), HistoryDataActivity.class);
			break;
		case 3:
			break;
		case 4:
			intent = new Intent(getApplicationContext(), CalibrationActivity.class);
			break;
		case 5:
			intent = new Intent(getApplicationContext(), OADActivity.class);
			break;
		case 6:
			break;
		case 7:
			break;
		case ResourceUtils.ACTIVITY_HISTORY_MANAGER:
			intent = new Intent(getApplicationContext(), HistoryManagerActivity.class);
			break;
		case ResourceUtils.ACTIVITY_SERIAL_DEBUG:
			intent = new Intent(getApplicationContext(), SerialDebugActivity.class);
			break;
		default:
			break;
		}
		return intent;
	}


}
