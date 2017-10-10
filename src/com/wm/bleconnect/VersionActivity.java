package com.wm.bleconnect;


import java.util.Arrays;
import java.util.Locale;

import com.wm.bleconnect.ResourceUtils.UIHandler;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class VersionActivity extends Activity implements OnClickListener {
	private static final String TAG = ResourceUtils.TAG;
	
	EditText edtxt_version_info;
	EditText edtxt_HW_h;
	EditText edtxt_HW_l;
	TextView txt_FW_h;
	TextView txt_FW_l;
	EditText edtxt_LotNo1;
	EditText edtxt_LotNo2;
	EditText edtxt_LotNo3;
	
	Button btn_getInfo;
	Button btn_set;
	
	IntentFilter mIntentFilter;
	CMDReceiver mReceiver;
	
	UIHandler mUIHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_version);
		setTitle("当前设备 ："+ResourceUtils.getCurrentDevice().getName());
		edtxt_version_info = (EditText)findViewById(R.id.edtxt_version);
		
		edtxt_HW_h = (EditText)findViewById(R.id.edtxt_version_HW_h);
		edtxt_HW_l = (EditText)findViewById(R.id.edtxt_version_HW_l);
		txt_FW_h = (TextView)findViewById(R.id.txt_version_FW_h);
		txt_FW_l = (TextView)findViewById(R.id.txt_version_FW_l);
		edtxt_LotNo1 = (EditText)findViewById(R.id.edtxt_version_LotNo1);
		edtxt_LotNo2 = (EditText)findViewById(R.id.edtxt_version_LotNo2);
		edtxt_LotNo3 = (EditText)findViewById(R.id.edtxt_version_LotNo3);
		
		btn_getInfo = (Button)findViewById(R.id.btn_version_query);
		Log.d(TAG,"" +(btn_getInfo == null)+ (this == null));
		btn_getInfo.setOnClickListener(this);
		btn_set = (Button)findViewById(R.id.btn_version_set);
		btn_set.setOnClickListener(this);
		
		mUIHandler = UIHandler.getInstance();
		
		initReceiver();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_version_query:
			clearVersionInfo();
			int[] data0 = new int[]{0};
			ResourceUtils.sendCMDToCurrentDevice(0x41, data0);
			break;
		case R.id.btn_version_set:
			int[] data1 = getData();
			if(data1 != null)
				ResourceUtils.sendCMDToCurrentDevice(0x11, data1);
			break;
		default:
			break;
		}
		
		
	}
	
	public int[] getData(){
		int[] data = new int[6];
		try{
			data[0] = Integer.parseInt(edtxt_HW_h.getText().toString().trim());
			data[1] = Integer.parseInt(edtxt_HW_l.getText().toString().trim());
			data[2] = Integer.parseInt(edtxt_LotNo1.getText().toString().trim());
			data[3] = Integer.parseInt(edtxt_LotNo2.getText().toString().trim());
			data[4] = Integer.parseInt(edtxt_LotNo3.getText().toString().trim());
			data[5] = 0;
				
		}catch(Exception e){
			e.printStackTrace();
			ResourceUtils.toastShort("Please input correct number");
			return null;
		}
		
		return data;
	}
	
	private void initReceiver(){
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(ResourceUtils.ACTION_CMD_RECEIVED);
        mReceiver = new CMDReceiver();
        registerReceiver(mReceiver, mIntentFilter);
	}
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}
	
	private void clearVersionInfo(){
		edtxt_HW_h.setText("");
		edtxt_HW_l.setText("");
		txt_FW_h.setText("");
		txt_FW_l.setText("");
		edtxt_LotNo1.setText("");
		edtxt_LotNo2.setText("");
		edtxt_LotNo3.setText("");
	}
	
	private void receivedVersionInfo(int[] data){
		if(data.length < 7){
			return;
		}
		
		edtxt_HW_h.setText("0x"+Integer.toHexString(data[0])
				.toUpperCase(Locale.getDefault()));
		edtxt_HW_l.setText("0x"+Integer.toHexString(data[1])
				.toUpperCase(Locale.getDefault()));
		txt_FW_h.setText("0x"+Integer.toHexString(data[2])
				.toUpperCase(Locale.getDefault()));
		txt_FW_l.setText("0x"+Integer.toHexString(data[3])
				.toUpperCase(Locale.getDefault()));
		edtxt_LotNo1.setText("0x"+Integer.toHexString(data[4])
				.toUpperCase(Locale.getDefault()));
		edtxt_LotNo2.setText("0x"+Integer.toHexString(data[5])
				.toUpperCase(Locale.getDefault()));
		edtxt_LotNo3.setText("0x"+Integer.toHexString(data[6])
				.toUpperCase(Locale.getDefault()));
		
	
	}



	private class CMDReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "VersionActivity : Receive a command");
			BluetoothDevice d = intent.getParcelableExtra(ResourceUtils.DATA_BTDEVICE);
			if(!(d.getAddress().equals(ResourceUtils.getCurrentDevice().getAddress()))){
				return;
			}
			int cmd = intent.getIntExtra(ResourceUtils.DATA_RECEIVED_CMD, -1);
			Log.d(TAG, "VersionActivity : Receive a command, cmd = 0x"+Integer.toHexString(cmd));
			final int[] data = intent.getIntArrayExtra(ResourceUtils.DATA_RECEIVED_DATA);
			switch(cmd){
			case 0x41:
				mUIHandler.post(new Runnable() {
					@Override
					public void run() {
						int[] rData = Arrays.copyOfRange(data, 0, data.length-1);
						//edtxt_version_info.setText(Arrays.toString(rData));
						receivedVersionInfo(rData);
					}
				});
				break;
			case 0x93:
				if(data[0] == 17)
					ResourceUtils.toastShort("设置硬件版本成功！");
			default:
				break;
			
			}
			//ResourceUtils.sendCMDToCurrentDevice(0x90, new int[]{cmd,0});
		}
		
	}



}
