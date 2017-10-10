package com.wm.bleconnect;


import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

public class HistoryManagerActivity extends Activity implements OnClickListener,OnCheckedChangeListener {
	private static final String TAG = ResourceUtils.TAG;
	Button btn_0_query;
	Button btn_1_get;
	Button btn_2_interrupt;
	Button btn_3_pointer;
	Button btn_4_review;
	Button btn_5_confirm;
	EditText edt_hst_count;
	TextView txt_console;
	ScrollView mConsole;
	CheckBox chkBox;
	
/*	btn_0_query;
	btn_1_get;
	btn_2_interrupt;
	btn_3_pointer;
	btn_4_review;*/
	
	private CMDReceiver mCMDReceiver;
	private IntentFilter mFilter;
	
	private Calendar mCal = Calendar.getInstance();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history_manager);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		
		btn_0_query = (Button)findViewById(R.id.btn_hst_mgr_0);
		btn_1_get = (Button)findViewById(R.id.btn_hst_mgr_1);
		btn_2_interrupt = (Button)findViewById(R.id.btn_hst_mgr_2);
		btn_3_pointer = (Button)findViewById(R.id.btn_hst_mgr_3);
		btn_4_review = (Button)findViewById(R.id.btn_hst_mgr_4);
		btn_5_confirm = (Button)findViewById(R.id.btn_hst_mgr_5);
		
		btn_0_query.setOnClickListener(this);
		btn_1_get.setOnClickListener(this);
		btn_2_interrupt.setOnClickListener(this);
		btn_3_pointer.setOnClickListener(this);
		btn_4_review.setOnClickListener(this);
		btn_5_confirm.setOnClickListener(this);
		
		edt_hst_count = (EditText)findViewById(R.id.edt_hst_mgr_0);
		txt_console = (TextView)findViewById(R.id.txt_hst_mgr_1_console);
		mConsole = (ScrollView)findViewById(R.id.sclV_hst_mgr_0);
		chkBox = (CheckBox)findViewById(R.id.chkBox_hst_mgr_0);
		chkBox.setOnCheckedChangeListener(this);
		chkBox.setChecked(false);
		//mConsole.setOverScrollMode(ScrollView.);

		initReceiver();

		
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_hst_mgr_0:
			Log.d(TAG,"HistoryManagerActivity : button 0 pressed");
			printToConsole(true, "0x44 [checkSum]");
			ResourceUtils.sendCMDToCurrentDevice(0x44, new int[]{0});
			break;
		case R.id.btn_hst_mgr_1:
			Log.d(TAG,"HistoryManagerActivity : button 1 pressed");
			int count = 0;
			try{
				count = Integer.parseInt(edt_hst_count.getText().toString());
			}catch(Exception e){
				e.printStackTrace();
				ResourceUtils.toastShort("Please input correct parameter");
				return;
			}
			int[] data1 = new int[]{count,0};
			printToConsole(true, "0x45 ["+data1[0]+", checkSum]");
			ResourceUtils.sendCMDToCurrentDevice(0x45, data1);
			break;
		case R.id.btn_hst_mgr_2:
			Log.d(TAG,"HistoryManagerActivity : button 2 pressed");
			//printToConsole(true, "0x74 [checkSum]");
			//ResourceUtils.sendCMDToCurrentDevice(0x74, new int[]{0});
			printToConsole(true, "0x45 [0, checkSum]");
			ResourceUtils.sendCMDToCurrentDevice(0x45, new int[]{0,0});
			break;
		case R.id.btn_hst_mgr_3:
			Log.d(TAG,"HistoryManagerActivity : button 3 pressed");
			printToConsole(true, "0x8E [checkSum]");
			ResourceUtils.sendCMDToCurrentDevice(0x8E, new int[]{0});
			break;
		case R.id.btn_hst_mgr_4:
			Log.d(TAG,"HistoryManagerActivity : button 4 pressed");
			String name = FileWriteEngine.getHistoryFileName(
					ResourceUtils.getCurrentDevice());
			if(name == null){
				ResourceUtils.toastShort("Something wrong with history file.");
				Log.d(TAG,"HistoryManagerActivity : ERROR Something wrong " +
						"with history file.");
				return;
			}
			Intent intent = new Intent(this, DisplayActivity.class);
			intent.putExtra(ResourceUtils.DATA_HISTORY_FILE_NAME, name);
			startActivity(intent);
			break;
		case R.id.btn_hst_mgr_5:
			Log.d(TAG,"HistoryManagerActivity : button 5 pressed");
			//confirm manually
			if(ResourceUtils.HISTORY_DATA_CONFIRM_MANUALLY != chkBox.isChecked()){
				ResourceUtils.toastShort("ERROR: chkBox not identical with Res");
				break;
			}
			if(chkBox.isChecked()){
				ResourceUtils.sendCMDToCurrentDevice(0x90, new int[]{0x45,0});
				printToConsole(true, "0x90 [0x45, checkSum] //cmd confirmed manually");				
			}else{
				ResourceUtils.toastShort("chkBox not checked!");
			}
			break;

		default:
			break;
		}

	}
	
	private void initReceiver(){
		mCMDReceiver = new CMDReceiver();
		mFilter = new IntentFilter();
		mFilter.addAction(ResourceUtils.ACTION_CMD_RECEIVED);
		registerReceiver(mCMDReceiver, mFilter);
	}
	
	
	
	
	@Override
	protected void onStop() {
		super.onStop();
		
		unregisterReceiver(mCMDReceiver);
		
		//interrupt history data query.
		ResourceUtils.changeHistoryDataConfirmState(true);//checked, need confirm manually
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
	}


	private void printToConsole(boolean isHost, String s){
		String caller = "UNKNOWN";
		if(isHost){
			caller = "A";
		}else{
			caller = "B";
		}
		/*String time = "-"+(mCal.get(Calendar.YEAR))%100+"-"+mCal.get(Calendar.MONTH)
        		+"-"+mCal.get(Calendar.DAY_OF_MONTH)+" "
				+mCal.get(Calendar.HOUR_OF_DAY)+":"+mCal.get(Calendar.MINUTE)
				+":"+mCal.get(Calendar.SECOND)+"	: ";*/
		String time = ResourceUtils.getSystemTime();
		txt_console.append(caller+"-"+time+"	: "+s);
		txt_console.append("\n");
		txt_console.append(" ");
		mConsole.fullScroll(ScrollView.FOCUS_DOWN);
	}




	private class CMDReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "HistoryManagerActivity : Receive a command");
			BluetoothDevice d = intent.getParcelableExtra(ResourceUtils.DATA_BTDEVICE);
			if(!(d.getAddress().equals(ResourceUtils.getCurrentDevice().getAddress()))){
				return;
			}
			int cmd = intent.getIntExtra(ResourceUtils.DATA_RECEIVED_CMD, -1);
			Log.d(TAG, "HistoryManagerActivity : Receive a command, cmd = 0x"+Integer.toHexString(cmd));
			final int[] data = intent.getIntArrayExtra(ResourceUtils.DATA_RECEIVED_DATA);
			switch(cmd){
			case 0x44:
				int count = data[0];
				printToConsole(false, "0x44 "+Arrays.toString(data));
				edt_hst_count.setText(""+count);
				break;
			case 0x45:
				Log.d(TAG, "HistoryManagerActivity : Receive a command 0x45"
						+" and print it to console");
				printToConsole(false, "0x45 "+Arrays.toString(data));
				break;
			case 0x8E:
				Log.d(TAG, "HistoryManagerActivity : Receive a command 0x8E"
						+" and print it to console");
				printToConsole(false, "0x8E"+Arrays.toString(data));
				break;
			case -1:
				Log.d(TAG, "HistoryManagerActivity : Receive a wrong command -1");
				break;
			default:
				break;
			}
		}
		
	}




	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		if(buttonView.getId() == chkBox.getId()){
			if(isChecked){
				//checked, to confirm manually
				ResourceUtils.changeHistoryDataConfirmState(isChecked);
			}else{
				//unchecked, to confirm automatically
				ResourceUtils.changeHistoryDataConfirmState(isChecked);
			}
		}
	}

}
