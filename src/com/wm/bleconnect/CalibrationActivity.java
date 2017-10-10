package com.wm.bleconnect;

import java.util.Arrays;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class CalibrationActivity extends Activity implements OnClickListener{
	EditText edt_cal_0_0;
	EditText edt_cal_0_1;
	EditText edt_cal_0_2;
	EditText edt_cal_0_3;
	EditText edt_cal_0_4;
	EditText edt_cal_0_5;
	EditText edt_cal_0_6;
	EditText edt_cal_3_0;
	EditText edt_cal_3_1;
	EditText edt_cal_3_2;
	EditText edt_cal_3_3;
	EditText edt_cal_3_4;
	EditText edt_cal_3_5;
	EditText edt_cal_3_6;
	EditText edt_cal_3_7;
	EditText edt_cal_4_0;
	EditText edt_cal_4_1;
	EditText edt_cal_4_2;
	EditText edt_cal_4_3;
	EditText edt_cal_4_4;
	EditText edt_cal_4_5;
	EditText edt_cal_4_6;
	EditText edt_cal_4_7;
	EditText edt_cal_5_0;
	EditText edt_cal_5_1;
	EditText edt_cal_6_0;
	EditText edt_cal_6_1;
	EditText edt_cal_6_2;
	EditText edt_cal_6_3;
	EditText edt_cal_6_4;
	EditText edt_cal_6_5;
	EditText edt_cal_6_6;
	EditText edt_cal_6_7;
	EditText edt_cal_7_0;
	EditText edt_cal_7_1;
	EditText edt_cal_7_2;
	EditText edt_cal_7_3;
	EditText edt_cal_7_4;
	EditText edt_cal_7_5;
	EditText edt_cal_7_6;
	EditText edt_cal_7_7;
	EditText edt_cal_8_0;
	EditText edt_cal_8_1;
	EditText edt_cal_8_2;
	EditText edt_cal_8_3;
	EditText edt_cal_8_4;
	EditText edt_cal_8_5;
	EditText edt_cal_8_6;
	EditText edt_cal_8_7;
	EditText edt_cal_9_0;
	EditText edt_cal_9_1;
	EditText edt_cal_9_2;
	EditText edt_cal_9_3;
	EditText edt_cal_10_0;
	EditText edt_cal_10_1;
	EditText edt_cal_11_0;
	EditText edt_cal_13_0;
	EditText edt_cal_13_1;
	EditText edt_cal_13_2;
	EditText edt_cal_13_3;
	EditText edt_cal_13_4;
	EditText edt_cal_13_5;
	EditText edt_cal_13_6;

	
	Button btn_cal_0_0;
	Button btn_cal_0_1;
	Button btn_cal_1_0;
	Button btn_cal_2_0;
	Button btn_cal_3_0;
	Button btn_cal_3_1;
	Button btn_cal_4_0;
	Button btn_cal_4_1;
	Button btn_cal_5_0;
	Button btn_cal_5_1;
	Button btn_cal_6_0;
	Button btn_cal_6_1;
	Button btn_cal_7_0;
	Button btn_cal_7_1;
	Button btn_cal_8_0;
	Button btn_cal_8_1;
	Button btn_cal_9_0;
	Button btn_cal_9_1;
	Button btn_cal_10_0;
	Button btn_cal_10_1;
	Button btn_cal_11_0;
	Button btn_cal_11_1;
	Button btn_cal_12_0;
	Button btn_cal_12_1;
	Button btn_cal_13_0;
	Button btn_cal_13_1;
	

	private static final String TAG = ResourceUtils.TAG;
	private IntentFilter mIntentFilter;
	private CMDReceiver mReceiver;
	
	Handler mUIHandler;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calibration);
		setTitle("当前设备 ："+ResourceUtils.getCurrentDevice().getName());
		
		findEditText();
		findButtonAndSetListener();
		
		mUIHandler = new Handler();
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
	
	private void findEditText(){
		edt_cal_0_0 = (EditText)findViewById(R.id.edt_cal_0_0);
		edt_cal_0_1 = (EditText)findViewById(R.id.edt_cal_0_1);
		edt_cal_0_2 = (EditText)findViewById(R.id.edt_cal_0_2);
		edt_cal_0_3 = (EditText)findViewById(R.id.edt_cal_0_3);
		edt_cal_0_4 = (EditText)findViewById(R.id.edt_cal_0_4);
		edt_cal_0_5 = (EditText)findViewById(R.id.edt_cal_0_5);
		edt_cal_0_6 = (EditText)findViewById(R.id.edt_cal_0_6);
		edt_cal_3_0 = (EditText)findViewById(R.id.edt_cal_3_0);
		edt_cal_3_1 = (EditText)findViewById(R.id.edt_cal_3_1);
		edt_cal_3_2 = (EditText)findViewById(R.id.edt_cal_3_2);
		edt_cal_3_3 = (EditText)findViewById(R.id.edt_cal_3_3);
		edt_cal_3_4 = (EditText)findViewById(R.id.edt_cal_3_4);
		edt_cal_3_5 = (EditText)findViewById(R.id.edt_cal_3_5);
		edt_cal_3_6 = (EditText)findViewById(R.id.edt_cal_3_6);
		edt_cal_3_7 = (EditText)findViewById(R.id.edt_cal_3_7);
		edt_cal_4_0 = (EditText)findViewById(R.id.edt_cal_4_0);
		edt_cal_4_1 = (EditText)findViewById(R.id.edt_cal_4_1);
		edt_cal_4_2 = (EditText)findViewById(R.id.edt_cal_4_2);
		edt_cal_4_3 = (EditText)findViewById(R.id.edt_cal_4_3);
		edt_cal_4_4 = (EditText)findViewById(R.id.edt_cal_4_4);
		edt_cal_4_5 = (EditText)findViewById(R.id.edt_cal_4_5);
		edt_cal_4_6 = (EditText)findViewById(R.id.edt_cal_4_6);
		edt_cal_4_7 = (EditText)findViewById(R.id.edt_cal_4_7);
		edt_cal_5_0 = (EditText)findViewById(R.id.edt_cal_5_0);
		edt_cal_5_1 = (EditText)findViewById(R.id.edt_cal_5_1);
		edt_cal_6_0 = (EditText)findViewById(R.id.edt_cal_6_0);
		edt_cal_6_1 = (EditText)findViewById(R.id.edt_cal_6_1);
		edt_cal_6_2 = (EditText)findViewById(R.id.edt_cal_6_2);
		edt_cal_6_3 = (EditText)findViewById(R.id.edt_cal_6_3);
		edt_cal_6_4 = (EditText)findViewById(R.id.edt_cal_6_4);
		edt_cal_6_5 = (EditText)findViewById(R.id.edt_cal_6_5);
		edt_cal_6_6 = (EditText)findViewById(R.id.edt_cal_6_6);
		edt_cal_6_7 = (EditText)findViewById(R.id.edt_cal_6_7);
		edt_cal_7_0 = (EditText)findViewById(R.id.edt_cal_7_0);
		edt_cal_7_1 = (EditText)findViewById(R.id.edt_cal_7_1);
		edt_cal_7_2 = (EditText)findViewById(R.id.edt_cal_7_2);
		edt_cal_7_3 = (EditText)findViewById(R.id.edt_cal_7_3);
		edt_cal_7_4 = (EditText)findViewById(R.id.edt_cal_7_4);
		edt_cal_7_5 = (EditText)findViewById(R.id.edt_cal_7_5);
		edt_cal_7_6 = (EditText)findViewById(R.id.edt_cal_7_6);
		edt_cal_7_7 = (EditText)findViewById(R.id.edt_cal_7_7);
		edt_cal_8_0 = (EditText)findViewById(R.id.edt_cal_8_0);
		edt_cal_8_1 = (EditText)findViewById(R.id.edt_cal_8_1);
		edt_cal_8_2 = (EditText)findViewById(R.id.edt_cal_8_2);
		edt_cal_8_3 = (EditText)findViewById(R.id.edt_cal_8_3);
		edt_cal_8_4 = (EditText)findViewById(R.id.edt_cal_8_4);
		edt_cal_8_5 = (EditText)findViewById(R.id.edt_cal_8_5);
		edt_cal_8_6 = (EditText)findViewById(R.id.edt_cal_8_6);
		edt_cal_8_7 = (EditText)findViewById(R.id.edt_cal_8_7);
		edt_cal_9_0 = (EditText)findViewById(R.id.edt_cal_9_0);
		edt_cal_9_1 = (EditText)findViewById(R.id.edt_cal_9_1);
		edt_cal_9_2 = (EditText)findViewById(R.id.edt_cal_9_2);
		edt_cal_9_3 = (EditText)findViewById(R.id.edt_cal_9_3);
		edt_cal_10_0 = (EditText)findViewById(R.id.edt_cal_10_0);
		edt_cal_10_1 = (EditText)findViewById(R.id.edt_cal_10_1);
		edt_cal_11_0 = (EditText)findViewById(R.id.edt_cal_11_0);
		edt_cal_13_0 = (EditText)findViewById(R.id.edt_cal_13_0);
		edt_cal_13_1 = (EditText)findViewById(R.id.edt_cal_13_1);
		edt_cal_13_2 = (EditText)findViewById(R.id.edt_cal_13_2);
		edt_cal_13_3 = (EditText)findViewById(R.id.edt_cal_13_3);
		edt_cal_13_4 = (EditText)findViewById(R.id.edt_cal_13_4);
		edt_cal_13_5 = (EditText)findViewById(R.id.edt_cal_13_5);
		edt_cal_13_6 = (EditText)findViewById(R.id.edt_cal_13_6);
	}

	
	private void findButtonAndSetListener(){
		btn_cal_0_0 = (Button)findViewById(R.id.btn_cal_0_0);
		btn_cal_0_0.setOnClickListener(this);
		btn_cal_0_1 = (Button)findViewById(R.id.btn_cal_0_1);
		btn_cal_0_1.setOnClickListener(this);
		btn_cal_1_0 = (Button)findViewById(R.id.btn_cal_1_0);
		btn_cal_1_0.setOnClickListener(this);
		btn_cal_2_0 = (Button)findViewById(R.id.btn_cal_2_0);
		btn_cal_2_0.setOnClickListener(this);
		btn_cal_3_0 = (Button)findViewById(R.id.btn_cal_3_0);
		btn_cal_3_0.setOnClickListener(this);
		btn_cal_3_1 = (Button)findViewById(R.id.btn_cal_3_1);
		btn_cal_3_1.setOnClickListener(this);
		btn_cal_4_0 = (Button)findViewById(R.id.btn_cal_4_0);
		btn_cal_4_0.setOnClickListener(this);
		btn_cal_4_1 = (Button)findViewById(R.id.btn_cal_4_1);
		btn_cal_4_1.setOnClickListener(this);
		btn_cal_5_0 = (Button)findViewById(R.id.btn_cal_5_0);
		btn_cal_5_0.setOnClickListener(this);
		btn_cal_5_1 = (Button)findViewById(R.id.btn_cal_5_1);
		btn_cal_5_1.setOnClickListener(this);
		btn_cal_6_0 = (Button)findViewById(R.id.btn_cal_6_0);
		btn_cal_6_0.setOnClickListener(this);
		btn_cal_6_1 = (Button)findViewById(R.id.btn_cal_6_1);
		btn_cal_6_1.setOnClickListener(this);
		btn_cal_7_0 = (Button)findViewById(R.id.btn_cal_7_0);
		btn_cal_7_0.setOnClickListener(this);
		btn_cal_7_1 = (Button)findViewById(R.id.btn_cal_7_1);
		btn_cal_7_1.setOnClickListener(this);
		btn_cal_8_0 = (Button)findViewById(R.id.btn_cal_8_0);
		btn_cal_8_0.setOnClickListener(this);
		btn_cal_8_1 = (Button)findViewById(R.id.btn_cal_8_1);
		btn_cal_8_1.setOnClickListener(this);
		btn_cal_9_0 = (Button)findViewById(R.id.btn_cal_9_0);
		btn_cal_9_0.setOnClickListener(this);
		btn_cal_9_1 = (Button)findViewById(R.id.btn_cal_9_1);
		btn_cal_9_1.setOnClickListener(this);
		btn_cal_10_0 = (Button)findViewById(R.id.btn_cal_10_0);
		btn_cal_10_0.setOnClickListener(this);
		btn_cal_10_1 = (Button)findViewById(R.id.btn_cal_10_1);
		btn_cal_10_1.setOnClickListener(this);
		btn_cal_11_0 = (Button)findViewById(R.id.btn_cal_11_0);
		btn_cal_11_0.setOnClickListener(this);
		btn_cal_11_1 = (Button)findViewById(R.id.btn_cal_11_1);
		btn_cal_11_1.setOnClickListener(this);
		btn_cal_12_0 = (Button)findViewById(R.id.btn_cal_12_0);
		btn_cal_12_0.setOnClickListener(this);
		btn_cal_12_1 = (Button)findViewById(R.id.btn_cal_12_1);
		btn_cal_12_1.setOnClickListener(this);
		btn_cal_13_0 = (Button)findViewById(R.id.btn_cal_13_0);
		btn_cal_13_0.setOnClickListener(this);
		btn_cal_13_1 = (Button)findViewById(R.id.btn_cal_13_1);
		btn_cal_13_1.setOnClickListener(this);

	}
	
	
	@Override
	public void onClick(View v) {
		String str = "not found";
		int[] sData;
		switch(v.getId()){
		
		case R.id.btn_cal_0_0 :
			str = "0_0";
			sData = new int[8];
			try{
				sData[0] = Integer.parseInt(edt_cal_0_0.getText().toString());
				sData[1] = Integer.parseInt(edt_cal_0_1.getText().toString());
				sData[2] = Integer.parseInt(edt_cal_0_2.getText().toString());
				sData[3] = Integer.parseInt(edt_cal_0_3.getText().toString());
				sData[4] = Integer.parseInt(edt_cal_0_4.getText().toString());
				sData[5] = Integer.parseInt(edt_cal_0_5.getText().toString());
				sData[6] = Integer.parseInt(edt_cal_0_6.getText().toString());
			}catch(Exception e){
				e.printStackTrace();
				ResourceUtils.toastShort("Please input correct parameter");
				return;
			}
			sData[sData.length-1] = 0;
			Log.d(TAG, "Calibration  btn_cal_0_0 get data : "+Arrays.toString(sData));
			ResourceUtils.sendCMDToCurrentDevice(0x70, sData);
			
			break;
		case R.id.btn_cal_0_1 :
			str = "0_1";
			sData = new int[]{0};
			ResourceUtils.sendCMDToCurrentDevice(0x80, sData);
			edt_cal_0_0.clearComposingText();
			edt_cal_0_1.clearComposingText();
			edt_cal_0_2.clearComposingText();
			edt_cal_0_3.clearComposingText();
			edt_cal_0_4.clearComposingText();
			edt_cal_0_5.clearComposingText();
			edt_cal_0_6.clearComposingText();
			break;
		case R.id.btn_cal_1_0 :
			str = "1_0";
			sData = new int[]{0};
			ResourceUtils.sendCMDToCurrentDevice(0x72, sData);
			
			break;
		case R.id.btn_cal_2_0 :
			str = "2_0";
			sData = new int[]{0};
			ResourceUtils.sendCMDToCurrentDevice(0x73, sData);
			
			break;
		case R.id.btn_cal_3_0 :
			str = "3_0";
			sData = new int[9];
			try{
				sData[0] = Integer.parseInt(edt_cal_3_0.getText().toString());
				sData[1] = Integer.parseInt(edt_cal_3_1.getText().toString());
				sData[2] = Integer.parseInt(edt_cal_3_2.getText().toString());
				sData[3] = Integer.parseInt(edt_cal_3_3.getText().toString());
				sData[4] = Integer.parseInt(edt_cal_3_4.getText().toString());
				sData[5] = Integer.parseInt(edt_cal_3_5.getText().toString());
				sData[6] = Integer.parseInt(edt_cal_3_6.getText().toString());
				sData[7] = Integer.parseInt(edt_cal_3_7.getText().toString());
			}catch(Exception e){
				e.printStackTrace();
				ResourceUtils.toastShort("Please input correct parameter");
				return;
			}
			sData[sData.length-1] = 0;
			ResourceUtils.sendCMDToCurrentDevice(0x75, sData);
			
			break;
		case R.id.btn_cal_3_1 :
			str = "3_1";
			sData = new int[]{0};
			ResourceUtils.sendCMDToCurrentDevice(0x85, sData);
			edt_cal_3_0.clearComposingText();
			edt_cal_3_1.clearComposingText();
			edt_cal_3_2.clearComposingText();
			edt_cal_3_3.clearComposingText();
			edt_cal_3_4.clearComposingText();
			edt_cal_3_5.clearComposingText();
			edt_cal_3_6.clearComposingText();
			edt_cal_3_7.clearComposingText();
			break;
		case R.id.btn_cal_4_0 :
			str = "4_0";
			sData = new int[9];
			try{
				sData[0] = Integer.parseInt(edt_cal_4_0.getText().toString());
				sData[1] = Integer.parseInt(edt_cal_4_1.getText().toString());
				sData[2] = Integer.parseInt(edt_cal_4_2.getText().toString());
				sData[3] = Integer.parseInt(edt_cal_4_3.getText().toString());
				sData[4] = Integer.parseInt(edt_cal_4_4.getText().toString());
				sData[5] = Integer.parseInt(edt_cal_4_5.getText().toString());
				sData[6] = Integer.parseInt(edt_cal_4_6.getText().toString());
				sData[7] = Integer.parseInt(edt_cal_4_7.getText().toString());
			}catch(Exception e){
				e.printStackTrace();
				ResourceUtils.toastShort("Please input correct parameter");
				return;
			}
			sData[sData.length-1] = 0;
			ResourceUtils.sendCMDToCurrentDevice(0x76, sData);
			
			break;
		case R.id.btn_cal_4_1 :
			str = "4_1";
			sData = new int[]{0};
			ResourceUtils.sendCMDToCurrentDevice(0x86, sData);
			edt_cal_4_0.clearComposingText();
			edt_cal_4_1.clearComposingText();
			edt_cal_4_2.clearComposingText();
			edt_cal_4_3.clearComposingText();
			edt_cal_4_4.clearComposingText();
			edt_cal_4_5.clearComposingText();
			edt_cal_4_6.clearComposingText();
			edt_cal_4_7.clearComposingText();
			break;
		case R.id.btn_cal_5_0 :
			str = "5_0";
			sData = new int[3];
			try{
				sData[0] = Integer.parseInt(edt_cal_5_0.getText().toString());
				sData[1] = Integer.parseInt(edt_cal_5_1.getText().toString());
			}catch(Exception e){
				e.printStackTrace();
				ResourceUtils.toastShort("Please input correct parameter");
				return;
			}
			sData[sData.length-1] = 0;
			ResourceUtils.sendCMDToCurrentDevice(0x77, sData);
			
			break;
		case R.id.btn_cal_5_1 :
			str = "5_1";
			sData = new int[]{0};
			ResourceUtils.sendCMDToCurrentDevice(0x87, sData);
			edt_cal_5_0.clearComposingText();
			edt_cal_5_1.clearComposingText();
			break;
		case R.id.btn_cal_6_0 :
			str = "6_0";
			sData = new int[9];
			try{
				sData[0] = Integer.parseInt(edt_cal_6_0.getText().toString());
				sData[1] = Integer.parseInt(edt_cal_6_1.getText().toString());
				sData[2] = Integer.parseInt(edt_cal_6_2.getText().toString());
				sData[3] = Integer.parseInt(edt_cal_6_3.getText().toString());
				sData[4] = Integer.parseInt(edt_cal_6_4.getText().toString());
				sData[5] = Integer.parseInt(edt_cal_6_5.getText().toString());
				sData[6] = Integer.parseInt(edt_cal_6_6.getText().toString());
				sData[7] = Integer.parseInt(edt_cal_6_7.getText().toString());
			}catch(Exception e){
				e.printStackTrace();
				ResourceUtils.toastShort("Please input correct parameter");
				return;
			}
			sData[sData.length-1] = 0;
			ResourceUtils.sendCMDToCurrentDevice(0x78, sData);
			
			break;
		case R.id.btn_cal_6_1 :
			str = "6_1";
			sData = new int[]{0};
			ResourceUtils.sendCMDToCurrentDevice(0x88, sData);
			edt_cal_6_0.clearComposingText();
			edt_cal_6_1.clearComposingText();
			edt_cal_6_2.clearComposingText();
			edt_cal_6_3.clearComposingText();
			edt_cal_6_4.clearComposingText();
			edt_cal_6_5.clearComposingText();
			edt_cal_6_6.clearComposingText();
			edt_cal_6_7.clearComposingText();
			break;
		case R.id.btn_cal_7_0 :
			str = "7_0";
			sData = new int[9];
			try{
				sData[0] = Integer.parseInt(edt_cal_7_0.getText().toString());
				sData[1] = Integer.parseInt(edt_cal_7_1.getText().toString());
				sData[2] = Integer.parseInt(edt_cal_7_2.getText().toString());
				sData[3] = Integer.parseInt(edt_cal_7_3.getText().toString());
				sData[4] = Integer.parseInt(edt_cal_7_4.getText().toString());
				sData[5] = Integer.parseInt(edt_cal_7_5.getText().toString());
				sData[6] = Integer.parseInt(edt_cal_7_6.getText().toString());
				sData[7] = Integer.parseInt(edt_cal_7_7.getText().toString());
			}catch(Exception e){
				e.printStackTrace();
				ResourceUtils.toastShort("Please input correct parameter");
				return;
			}
			sData[sData.length-1] = 0;
			ResourceUtils.sendCMDToCurrentDevice(0x79, sData);
			
			break;
		case R.id.btn_cal_7_1 :
			str = "7_1";
			sData = new int[]{0};
			ResourceUtils.sendCMDToCurrentDevice(0x89, sData);
			edt_cal_7_0.clearComposingText();
			edt_cal_7_1.clearComposingText();
			edt_cal_7_2.clearComposingText();
			edt_cal_7_3.clearComposingText();
			edt_cal_7_4.clearComposingText();
			edt_cal_7_5.clearComposingText();
			edt_cal_7_6.clearComposingText();
			edt_cal_7_7.clearComposingText();
			break;
		case R.id.btn_cal_8_0 :
			str = "8_0";
			sData = new int[9];
			try{
				sData[0] = Integer.parseInt(edt_cal_8_0.getText().toString());
				sData[1] = Integer.parseInt(edt_cal_8_1.getText().toString());
				sData[2] = Integer.parseInt(edt_cal_8_2.getText().toString());
				sData[3] = Integer.parseInt(edt_cal_8_3.getText().toString());
				sData[4] = Integer.parseInt(edt_cal_8_4.getText().toString());
				sData[5] = Integer.parseInt(edt_cal_8_5.getText().toString());
				sData[6] = Integer.parseInt(edt_cal_8_6.getText().toString());
				sData[7] = Integer.parseInt(edt_cal_8_7.getText().toString());
			}catch(Exception e){
				e.printStackTrace();
				ResourceUtils.toastShort("Please input correct parameter");
				return;
			}
			sData[sData.length-1] = 0;
			ResourceUtils.sendCMDToCurrentDevice(0x7A, sData);
			
			break;
		case R.id.btn_cal_8_1 :
			str = "8_1";
			sData = new int[]{0};
			ResourceUtils.sendCMDToCurrentDevice(0x8A, sData);
			edt_cal_8_0.clearComposingText();
			edt_cal_8_1.clearComposingText();
			edt_cal_8_2.clearComposingText();
			edt_cal_8_3.clearComposingText();
			edt_cal_8_4.clearComposingText();
			edt_cal_8_5.clearComposingText();
			edt_cal_8_6.clearComposingText();
			edt_cal_8_7.clearComposingText();
			break;
		case R.id.btn_cal_9_0 :
			str = "9_0";
			sData = new int[5];
			try{
				sData[0] = Integer.parseInt(edt_cal_9_0.getText().toString());
				sData[1] = Integer.parseInt(edt_cal_9_1.getText().toString());
				sData[2] = Integer.parseInt(edt_cal_9_2.getText().toString());
				sData[3] = Integer.parseInt(edt_cal_9_3.getText().toString());
			}catch(Exception e){
				e.printStackTrace();
				ResourceUtils.toastShort("Please input correct parameter");
				return;
			}
			sData[sData.length-1] = 0;
			ResourceUtils.sendCMDToCurrentDevice(0x7B, sData);
			
			break;
		case R.id.btn_cal_9_1 :
			str = "9_1";
			sData = new int[]{0};
			ResourceUtils.sendCMDToCurrentDevice(0x8B, sData);
			edt_cal_9_0.clearComposingText();
			edt_cal_9_1.clearComposingText();
			edt_cal_9_2.clearComposingText();
			edt_cal_9_3.clearComposingText();
			break;
		case R.id.btn_cal_10_0 :
			str = "10_0";
			sData = new int[3];
			try{
				sData[0] = Integer.parseInt(edt_cal_10_0.getText().toString());
				sData[1] = Integer.parseInt(edt_cal_10_1.getText().toString());
			}catch(Exception e){
				e.printStackTrace();
				ResourceUtils.toastShort("Please input correct parameter");
				return;
			}
			sData[sData.length-1] = 0;
			ResourceUtils.sendCMDToCurrentDevice(0x7C, sData);
			
			break;
		case R.id.btn_cal_10_1 :
			str = "10_1";
			sData = new int[]{0};
			ResourceUtils.sendCMDToCurrentDevice(0x8C, sData);
			edt_cal_10_0.clearComposingText();
			edt_cal_10_1.clearComposingText();
			break;
		case R.id.btn_cal_11_0 :
			str = "11_0";
			sData = new int[2];
			try{
				sData[0] = Integer.parseInt(edt_cal_11_0.getText().toString());
			}catch(Exception e){
				e.printStackTrace();
				ResourceUtils.toastShort("Please input correct parameter");
				return;
			}
			sData[sData.length-1] = 0;
			ResourceUtils.sendCMDToCurrentDevice(0x7D, sData);
			
			break;
		case R.id.btn_cal_11_1 :
			str = "11_1";
			sData = new int[]{0};
			ResourceUtils.sendCMDToCurrentDevice(0x8D, sData);
			edt_cal_11_0.clearComposingText();
			break;
		case R.id.btn_cal_12_0 :
			str = "12_0";
			sData = new int[]{0xE3,0};
			ResourceUtils.sendCMDToCurrentDevice(0x7E, sData);
			
			break;
		case R.id.btn_cal_12_1 :
			str = "12_1";
			sData = new int[]{0x3E,0};
			ResourceUtils.sendCMDToCurrentDevice(0x7E, sData);
			
			break;
		case R.id.btn_cal_13_0 :
			str = "13_0";
			sData = new int[8];
			try{
				sData[0] = Integer.parseInt(edt_cal_13_0.getText().toString());
				sData[1] = Integer.parseInt(edt_cal_13_1.getText().toString());
				sData[2] = Integer.parseInt(edt_cal_13_2.getText().toString());
				sData[3] = Integer.parseInt(edt_cal_13_3.getText().toString());
				sData[4] = Integer.parseInt(edt_cal_13_4.getText().toString());
				sData[5] = Integer.parseInt(edt_cal_13_5.getText().toString());
				sData[6] = Integer.parseInt(edt_cal_13_6.getText().toString());
			}catch(Exception e){
				e.printStackTrace();
				ResourceUtils.toastShort("Please input correct parameter");
				return;
			}
			sData[sData.length-1] = 0;
			//ResourceUtils.sendCMDToCurrentDevice(0x46, sData);
			
			break;
		case R.id.btn_cal_13_1 :
			str = "13_1";
			sData = new int[]{0};
			ResourceUtils.sendCMDToCurrentDevice(0x46, sData);
			edt_cal_13_0.clearComposingText();
			edt_cal_13_1.clearComposingText();
			edt_cal_13_2.clearComposingText();
			edt_cal_13_3.clearComposingText();
			edt_cal_13_4.clearComposingText();
			edt_cal_13_5.clearComposingText();
			edt_cal_13_6.clearComposingText();
			break;

		default:
			break;
		
		}
		Log.d(TAG, "CalibrationActivity : You clicked "+str);
		
	}

	
	
	
	
	private class CMDReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "CalibrationActivity : Receive a command");
			BluetoothDevice d = intent.getParcelableExtra(ResourceUtils.DATA_BTDEVICE);
			if(!(d.getAddress().equals(ResourceUtils.getCurrentDevice().getAddress()))){
				return;
			}
			final int cmd = intent.getIntExtra(ResourceUtils.DATA_RECEIVED_CMD, -1);
			Log.d(TAG, "CalibrationActivity : Receive a command, cmd = 0x"+Integer.toHexString(cmd));
			final int[] data = intent.getIntArrayExtra(ResourceUtils.DATA_RECEIVED_DATA);
			final byte[] rawData = intent.getByteArrayExtra(ResourceUtils.DATA_RECEIVED_RAW_DATA);
			mUIHandler.post(new Runnable() {
				
				@Override
				public void run() {
					//TODO catch exception
					switch(cmd){
					case 0x80:
						edt_cal_0_0.setText(""+data[0]);
						edt_cal_0_1.setText(""+data[1]);
						edt_cal_0_2.setText(""+data[2]);
						edt_cal_0_3.setText(""+data[3]);
						edt_cal_0_4.setText(""+data[4]);
						edt_cal_0_5.setText(""+data[5]);
						edt_cal_0_6.setText(""+data[6]);
						
						break;
					case 0x85:
						edt_cal_3_0.setText(""+data[0]);
						edt_cal_3_1.setText(""+data[1]);
						edt_cal_3_2.setText(""+data[2]);
						edt_cal_3_3.setText(""+data[3]);
						edt_cal_3_4.setText(""+data[4]);
						edt_cal_3_5.setText(""+data[5]);
						edt_cal_3_6.setText(""+data[6]);
						edt_cal_3_7.setText(""+data[7]);
						
						break;
					case 0x86:
						edt_cal_4_0.setText(""+data[0]);
						edt_cal_4_1.setText(""+data[1]);
						edt_cal_4_2.setText(""+data[2]);
						edt_cal_4_3.setText(""+data[3]);
						edt_cal_4_4.setText(""+data[4]);
						edt_cal_4_5.setText(""+data[5]);
						edt_cal_4_6.setText(""+data[6]);
						edt_cal_4_7.setText(""+data[7]);
						
						break;
					case 0x87:
						edt_cal_5_0.setText(""+data[0]);
						int cal_5_1 = (short)data[1];
						edt_cal_5_1.setText(""+cal_5_1);
						
						
						break;
					case 0x88:
						edt_cal_6_0.setText(""+data[0]);
						edt_cal_6_1.setText(""+data[1]);
						edt_cal_6_2.setText(""+data[2]);
						edt_cal_6_3.setText(""+data[3]);
						edt_cal_6_4.setText(""+data[4]);
						edt_cal_6_5.setText(""+data[5]);
						edt_cal_6_6.setText(""+data[6]);
						edt_cal_6_7.setText(""+data[7]);
						
						break;
					case 0x89:
						edt_cal_7_0.setText(""+data[0]);
						edt_cal_7_1.setText(""+data[1]);
						edt_cal_7_2.setText(""+data[2]);
						edt_cal_7_3.setText(""+data[3]);
						edt_cal_7_4.setText(""+data[4]);
						edt_cal_7_5.setText(""+data[5]);
						edt_cal_7_6.setText(""+data[6]);
						edt_cal_7_7.setText(""+data[7]);
						
						break;
					case 0x8A:
						edt_cal_8_0.setText(""+data[0]);
						edt_cal_8_1.setText(""+data[1]);
						edt_cal_8_2.setText(""+data[2]);
						edt_cal_8_3.setText(""+data[3]);
						edt_cal_8_4.setText(""+data[4]);
						edt_cal_8_5.setText(""+data[5]);
						edt_cal_8_6.setText(""+data[6]);
						edt_cal_8_7.setText(""+data[7]);
						
						break;
					case 0x8B:
						edt_cal_9_0.setText(""+data[0]);
						edt_cal_9_1.setText(""+data[1]);
						edt_cal_9_2.setText(""+data[2]);
						edt_cal_9_3.setText(""+data[3]);
						
						break;
					case 0x8C:
						edt_cal_10_0.setText(""+data[0]);
						byte cal_10_1 = (byte)data[1];
						edt_cal_10_1.setText(""+cal_10_1);
						
						break;
					case 0x8D:
						edt_cal_11_0.setText(""+data[0]);
						
						break;
					case 0x46:
						edt_cal_13_0.setText(""+data[0]);
						edt_cal_13_1.setText(""+data[1]);
						edt_cal_13_2.setText(""+data[2]);
						edt_cal_13_3.setText(""+data[3]);
						byte[] byte_cal_13_4 = Arrays.copyOfRange(rawData, 11, 15);
						//byte[] byte_cal_13_4 = new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff}; 
						long cal_13_4 = 0L;
						for(int i=0;i<byte_cal_13_4.length;i++){
							cal_13_4 = (cal_13_4<<8) + (byte_cal_13_4[i] & 0xFF);
						}
						edt_cal_13_4.setText(""+cal_13_4);
						edt_cal_13_5.setText(""+data[5]);
						edt_cal_13_6.setText(""+data[6]);
						
						break;

					case 0x93:
						ResourceUtils.toastShort("设置成功！");
						break;

					default:
						break;
					
					}
					if(cmd != 0x93){
						ResourceUtils.toastShort("Data updated");
					}
				}
			});
			
		//ResourceUtils.sendCMDToCurrentDevice(0x90, new int[]{cmd,0});
		}
		
	}
	
	
}
