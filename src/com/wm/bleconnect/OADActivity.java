package com.wm.bleconnect;

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

public class OADActivity extends Activity implements OnClickListener{
	private static final String TAG = ResourceUtils.TAG;

	private Button btn_boot;
	private IntentFilter mIntentFilter;
	private CMDReceiver mReceiver;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_oad);
		btn_boot = (Button)findViewById(R.id.btn_oad_boot);
		btn_boot.setOnClickListener(this);
		initReceiver();
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
	

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_oad_boot:
			ResourceUtils.sendCMDToCurrentDevice(0xDF, new int[]{0});
			break;
		default:
			break;
		}
	}
	
	private class CMDReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "OADActivity : Receive a command");
			BluetoothDevice d = intent.getParcelableExtra(ResourceUtils.DATA_BTDEVICE);
			if(!(d.getAddress().equals(ResourceUtils.getCurrentDevice().getAddress()))){
				return;
			}
			int cmd = intent.getIntExtra(ResourceUtils.DATA_RECEIVED_CMD, -1);
			Log.d(TAG, "OADActivity : Receive a command, cmd = 0x"+Integer.toHexString(cmd));
			final int[] data = intent.getIntArrayExtra(ResourceUtils.DATA_RECEIVED_DATA);
			switch(cmd){
			case 0xDE:
				ResourceUtils.toastShort("接收Boot成功！");
				break;
			}
		//No reply???
		//ResourceUtils.sendCMDToCurrentDevice(0x90, new int[]{cmd,0});
		}
		
	}



}
