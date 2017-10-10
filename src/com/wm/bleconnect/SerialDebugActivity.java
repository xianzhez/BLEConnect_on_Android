package com.wm.bleconnect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;


import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class SerialDebugActivity extends Activity implements OnClickListener{
	private static final String TAG = ResourceUtils.TAG;
	
	public static final String EXTRA_CMD_LIST = "EXTRA_CMD_LIST";
	public static final String EXTRA_CMD_NAME = "EXTRA_CMD_NAME";//String
	public static final String EXTRA_CMD_DATA = "EXTRA_CMD_DATA";//int[]
	public static final String EXTRA_CMD_RADIX_USED = "EXTRA_CMD_RADIX_USED";//int
	
	private static int REQUEST_CODE_CMD_PICK = 1;
	private static int REQUEST_CODE_CMD_CUSTOM = 2;
	private volatile static int RADIX_IN_USE = 16;
	private static final int RADIX_DEC = 10;
	private static final int RADIX_HEX = 16;
	
	private static final int WHAT_SEND_INPUT = 0x001;

	private RadioGroup rdiGrp_sd_0;
	private RadioButton rdiBtn_sd_0_HEX;
	private RadioButton rdiBtn_sd_1_DEC;
	private ScrollView mConsole;
	private TextView txt_console;
	private EditText edt_sd_0;
	private EditText edt_sd_1;
	private EditText edt_sd_2;
	private EditText edt_sd_3;
	private EditText edt_sd_4;
	private EditText edt_sd_5;
	private EditText edt_sd_6;
	private EditText edt_sd_7;
	private EditText edt_sd_8;
	private EditText edt_sd_9;
	private EditText edt_sd_10;
	private EditText edt_sd_11;
	private EditText edt_sd_12;
	private EditText edt_sd_13;
	private EditText edt_sd_14;
	private EditText edt_sd_15;
	private EditText edt_sd_16;
	private EditText edt_sd_17;
	private EditText edt_sd_18;
	private EditText edt_sd_19;
	
	private Button btn_sd_0_send;
	private Button btn_sd_1_clear;
	private Button btn_sd_2_cmds;
	private Button btn_sd_3_add;

	private MyHandler mHandler;
	private IntentFilter mIntentFilter;
	private CMDReceiver mReceiver;
	
	private ArrayList<HashMap<String, Object>> cmdList =
			new ArrayList<HashMap<String, Object>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_serial);
		initViews();
		mHandler = new MyHandler();
		initReceiver();
		initCMDs();
	}
	
	private void initViews(){
		rdiGrp_sd_0 = (RadioGroup)findViewById(R.id.rdiGrp_sd_0);
		rdiGrp_sd_0.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rdiBtn_sd_0_HEX:
					RADIX_IN_USE = RADIX_HEX;
					Log.d(TAG, "SerialDebugActivity : RADIX_IN_USE changed to RADIX_HEX");
					break;
				case R.id.rdiBtn_sd_1_DEC:
					RADIX_IN_USE = RADIX_DEC;
					Log.d(TAG, "SerialDebugActivity : RADIX_IN_USE changed to RADIX_DEC");
					break;
				default:
					break;
				}
				
			}
		});
		
		rdiBtn_sd_0_HEX = (RadioButton)findViewById(R.id.rdiBtn_sd_0_HEX);
		rdiBtn_sd_1_DEC = (RadioButton)findViewById(R.id.rdiBtn_sd_1_DEC);

		
		
		mConsole = (ScrollView)findViewById(R.id.sclV_sd_0);
		txt_console = (TextView)findViewById(R.id.txt_sd_1_console);
		
		edt_sd_0 = (EditText)findViewById(R.id.edt_sd_0);
		edt_sd_1 = (EditText)findViewById(R.id.edt_sd_1);
		edt_sd_2 = (EditText)findViewById(R.id.edt_sd_2);
		edt_sd_3 = (EditText)findViewById(R.id.edt_sd_3);
		edt_sd_4 = (EditText)findViewById(R.id.edt_sd_4);
		edt_sd_5 = (EditText)findViewById(R.id.edt_sd_5);
		edt_sd_6 = (EditText)findViewById(R.id.edt_sd_6);
		edt_sd_7 = (EditText)findViewById(R.id.edt_sd_7);
		edt_sd_8 = (EditText)findViewById(R.id.edt_sd_8);
		edt_sd_9 = (EditText)findViewById(R.id.edt_sd_9);
		edt_sd_10 = (EditText)findViewById(R.id.edt_sd_10);
		edt_sd_11 = (EditText)findViewById(R.id.edt_sd_11);
		edt_sd_12 = (EditText)findViewById(R.id.edt_sd_12);
		edt_sd_13 = (EditText)findViewById(R.id.edt_sd_13);
		edt_sd_14 = (EditText)findViewById(R.id.edt_sd_14);
		edt_sd_15 = (EditText)findViewById(R.id.edt_sd_15);
		edt_sd_16 = (EditText)findViewById(R.id.edt_sd_16);
		edt_sd_17 = (EditText)findViewById(R.id.edt_sd_17);
		edt_sd_18 = (EditText)findViewById(R.id.edt_sd_18);
		edt_sd_19 = (EditText)findViewById(R.id.edt_sd_19);
		
		btn_sd_0_send = (Button)findViewById(R.id.btn_sd_0_send);
		btn_sd_0_send.setOnClickListener(this);
		btn_sd_1_clear = (Button)findViewById(R.id.btn_sd_1_clear);
		btn_sd_1_clear.setOnClickListener(this);
		btn_sd_2_cmds = (Button)findViewById(R.id.btn_sd_2_cmds);
		btn_sd_2_cmds.setOnClickListener(this);
		btn_sd_3_add = (Button)findViewById(R.id.btn_sd_3_add);
		btn_sd_3_add.setOnClickListener(this);
		
		
	}
	
	private void initReceiver(){
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(ResourceUtils.ACTION_CMD_RECEIVED);
		mIntentFilter.addAction(ResourceUtils.ACTION_CMD_SEND_RAW_DATA);
		mReceiver = new CMDReceiver();
		registerReceiver(mReceiver, mIntentFilter);
	}
	
	private void initCMDs(){
		HashMap<String, Object> map ;
		int[] data;
		
		map = new HashMap<String, Object>();
		map.put(EXTRA_CMD_NAME, "1.查询系统时间");
		data = new int[]{0x40, 0x4, 0x3, 0x47};
		map.put(EXTRA_CMD_DATA, data);
		map.put(EXTRA_CMD_RADIX_USED, RADIX_HEX);
		cmdList.add(map);
		
		map = new HashMap<String, Object>();
		map.put(EXTRA_CMD_NAME, "2.查询版本号");
		data = new int[]{0x41, 0x4, 0x3, 0x48};
		map.put(EXTRA_CMD_DATA, data);
		map.put(EXTRA_CMD_RADIX_USED, RADIX_HEX);
		cmdList.add(map);
		
		map = new HashMap<String, Object>();
		map.put(EXTRA_CMD_NAME, "3.查询历史数据的总数");
		data = new int[]{0x44 ,0x4, 0x3, 0x4B};
		map.put(EXTRA_CMD_DATA, data);
		map.put(EXTRA_CMD_RADIX_USED, RADIX_HEX);
		cmdList.add(map);
		
		map = new HashMap<String, Object>();
		map.put(EXTRA_CMD_NAME, "4.查询存储指针和清除指针的值");
		data = new int[]{0x8E ,0x4, 0x3, 0x95};
		map.put(EXTRA_CMD_DATA, data);
		map.put(EXTRA_CMD_RADIX_USED, RADIX_HEX);
		cmdList.add(map);
		
		map = new HashMap<String, Object>();
		map.put(EXTRA_CMD_NAME, "5.查询实时数据");
		data = new int[]{0x43 ,0x4, 0x3, 0x4A};
		map.put(EXTRA_CMD_DATA, data);
		map.put(EXTRA_CMD_RADIX_USED, RADIX_HEX);
		cmdList.add(map);
		
		map = new HashMap<String, Object>();
		map.put(EXTRA_CMD_NAME, "6.查询数据更新间隔");
		data = new int[]{0x80 ,0x4, 0x3, 0x87};
		map.put(EXTRA_CMD_DATA, data);
		map.put(EXTRA_CMD_RADIX_USED, RADIX_HEX);
		cmdList.add(map);
		
		map = new HashMap<String, Object>();
		map.put(EXTRA_CMD_NAME, "7.显示模块自检");
		data = new int[]{0x72 ,0x4, 0x3, 0x79};
		map.put(EXTRA_CMD_DATA, data);
		map.put(EXTRA_CMD_RADIX_USED, RADIX_HEX);
		cmdList.add(map);
		
		map = new HashMap<String, Object>();
		map.put(EXTRA_CMD_NAME, "8.出厂设置");
		data = new int[]{0x73 ,0x4, 0x3, 0x7A};
		map.put(EXTRA_CMD_DATA, data);
		map.put(EXTRA_CMD_RADIX_USED, RADIX_HEX);
		cmdList.add(map);
		
		map = new HashMap<String, Object>();
		map.put(EXTRA_CMD_NAME, "9.查询超标及其回差");
		data = new int[]{ 0x85,0x4, 0x3, 0x8C};
		map.put(EXTRA_CMD_DATA, data);
		map.put(EXTRA_CMD_RADIX_USED, RADIX_HEX);
		cmdList.add(map);
		
		map = new HashMap<String, Object>();
		map.put(EXTRA_CMD_NAME, "10.查询危险及其回差");
		data = new int[]{0x86 ,0x4, 0x3, 0x8D};
		map.put(EXTRA_CMD_DATA, data);
		map.put(EXTRA_CMD_RADIX_USED, RADIX_HEX);
		cmdList.add(map);
		
		map = new HashMap<String, Object>();
		map.put(EXTRA_CMD_NAME, "11.查询PM的校准参数KB");
		data = new int[]{0x87 ,0x4, 0x3, 0x8E};
		map.put(EXTRA_CMD_DATA, data);
		map.put(EXTRA_CMD_RADIX_USED, RADIX_HEX);
		cmdList.add(map);
		
		map = new HashMap<String, Object>();
		map.put(EXTRA_CMD_NAME, "12.查询EM的校准参数1-4");
		data = new int[]{0x88 ,0x4, 0x3, 0x8F};
		map.put(EXTRA_CMD_DATA, data);
		map.put(EXTRA_CMD_RADIX_USED, RADIX_HEX);
		cmdList.add(map);
		
		map = new HashMap<String, Object>();
		map.put(EXTRA_CMD_NAME, "13.查询EM的校准参数5-8");
		data = new int[]{0x89 ,0x4, 0x3, 0x90};
		map.put(EXTRA_CMD_DATA, data);
		map.put(EXTRA_CMD_RADIX_USED, RADIX_HEX);
		cmdList.add(map);
		
		
		map = new HashMap<String, Object>();
		map.put(EXTRA_CMD_NAME, "14.查询VOC的校准参数1-4");
		data = new int[]{0x8A ,0x4, 0x3,0x91 };
		map.put(EXTRA_CMD_DATA, data);
		map.put(EXTRA_CMD_RADIX_USED, RADIX_HEX);
		cmdList.add(map);
		
		
		map = new HashMap<String, Object>();
		map.put(EXTRA_CMD_NAME, "15.查询VOC的校准参数5-6");
		data = new int[]{0x8B ,0x4, 0x3, 0x92};
		map.put(EXTRA_CMD_DATA, data);
		map.put(EXTRA_CMD_RADIX_USED, RADIX_HEX);
		cmdList.add(map);
		
		map = new HashMap<String, Object>();
		map.put(EXTRA_CMD_NAME, "16.查询TH的校准参数KB");
		data = new int[]{0x8C,0x4, 0x3, 0x93};
		map.put(EXTRA_CMD_DATA, data);
		map.put(EXTRA_CMD_RADIX_USED, RADIX_HEX);
		cmdList.add(map);
		
		map = new HashMap<String, Object>();
		map.put(EXTRA_CMD_NAME, "17.查询ADXL345的检测阈值");
		data = new int[]{0x8D ,0x4, 0x3, 0x94};
		map.put(EXTRA_CMD_DATA, data);
		map.put(EXTRA_CMD_RADIX_USED, RADIX_HEX);
		cmdList.add(map);
		
		map = new HashMap<String, Object>();
		map.put(EXTRA_CMD_NAME, "18.查询原始采样值");
		data = new int[]{0x46 ,0x4, 0x3, 0x4D};
		map.put(EXTRA_CMD_DATA, data);
		map.put(EXTRA_CMD_RADIX_USED, RADIX_HEX);
		cmdList.add(map);
		

		
/*		map = new HashMap<String, Object>();
		map.put(EXTRA_CMD_NAME, ".");
		data = new int[]{ ,0x4, 0x3, };
		map.put(EXTRA_CMD_DATA, data);
		map.put(EXTRA_CMD_RADIX_USED, RADIX_HEX);
		cmdList.add(map);*/
		
		
	}
	
	private void addCustomedCMD(String name, int[] data, int radix){
		
		HashMap<String, Object> map ;
		map = new HashMap<String, Object>();
		map.put(EXTRA_CMD_NAME, ""+(cmdList.size()+1)+"."+name);
		map.put(EXTRA_CMD_DATA, data);
		map.put(EXTRA_CMD_RADIX_USED, RADIX_HEX);
		cmdList.add(map);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}
	
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "SerialDebugActivity : onActivityResult  resultCode = "+resultCode
				+"requestCode = "+requestCode);
		if(requestCode == REQUEST_CODE_CMD_PICK){
			if(resultCode == -1){
				return;
			}
			int cmdPicked = resultCode;
			HashMap<String, Object> map = cmdList.get(cmdPicked);
			int radix = (Integer)map.get(EXTRA_CMD_RADIX_USED);
			int[] cmdData = (int[])map.get(EXTRA_CMD_DATA);
			setEdtText(cmdData, radix);
		}else if(requestCode == REQUEST_CODE_CMD_CUSTOM){
			if(resultCode == SerialCustomedCMDActivity
						.RESULT_CODE_CMD_CUSTOMED_CANCEL){
				return;
			}else if(resultCode == SerialCustomedCMDActivity
						.RESULT_CODE_CMD_CUSTOMED_OK_CHECKED){
				@SuppressWarnings("unchecked")
				HashMap<String, Object> newCMDMap = 
						(HashMap<String, Object>)data.getSerializableExtra(SerialCustomedCMDActivity
								.EXTRA_CMD_CUSTOMED_NEW);
				int[] cmdData = (int[])newCMDMap.get(EXTRA_CMD_DATA);
				int[] cmdDataChecked = checkSum(cmdData);
				newCMDMap.put(EXTRA_CMD_DATA, cmdDataChecked);
				cmdList.add(newCMDMap);
			}else if(resultCode == SerialCustomedCMDActivity
						.RESULT_CODE_CMD_CUSTOMED_OK_NOT_CHECKED){
				@SuppressWarnings("unchecked")
				HashMap<String, Object> newCMDMap = 
						(HashMap<String, Object>)data.getSerializableExtra(SerialCustomedCMDActivity
								.EXTRA_CMD_CUSTOMED_NEW);
				cmdList.add(newCMDMap);
			}
			return;
		}
	
	}
	
	private int[] checkSum(int[] data){
		int sum = 0;
		for(int i = 0; i<data.length-1;i++){
			sum = (sum & 0xFF )+ (data[i] & 0xFF) ;
		}
		data[data.length-1] = (sum & 0xFF);
		return data;
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_sd_0_send:
			mHandler.sendEmptyMessage(WHAT_SEND_INPUT);
			break;
		case R.id.btn_sd_1_clear:
			clearAllInput();
			break;
		case R.id.btn_sd_2_cmds:
			Intent intent0 = new Intent(this, SerialDebugCmdPickerActivity.class);
			intent0.putExtra(EXTRA_CMD_LIST, cmdList);
			startActivityForResult(intent0, REQUEST_CODE_CMD_PICK);
			break;
		case R.id.btn_sd_3_add:
			HashMap<String, Object> map = getCustomedOriData();
			if(map == null){
				ResourceUtils.toastShort("Please input your data");
				return;
			}
			Intent intent1 = new Intent(this, SerialCustomedCMDActivity.class);
			intent1.putExtra(SerialCustomedCMDActivity.EXTRA_CMD_CUSTOMED_ORI,
					getCustomedOriData());
			startActivityForResult(intent1, REQUEST_CODE_CMD_CUSTOM);
			break;

		default:
			break;
		}
	}
	
	private HashMap<String, Object> getCustomedOriData(){
		HashMap<String, Object> map = new HashMap<String, Object>();
		int[] data = getEdtInput();
		if(data == null){
			return null;
		}
		//map.put(EXTRA_CMD_NAME, "2.查询版本号");
		map.put(EXTRA_CMD_DATA, data);
		map.put(EXTRA_CMD_RADIX_USED, RADIX_IN_USE);
		
		return map;

	}
	
	private void changeRadioGroupRadix(int radix){
		switch (radix) {
		case RADIX_DEC:
			rdiBtn_sd_0_HEX.setChecked(false);
			rdiBtn_sd_1_DEC.setChecked(true);
			break;
		case RADIX_HEX:
			rdiBtn_sd_0_HEX.setChecked(true);
			rdiBtn_sd_1_DEC.setChecked(false);
			break;
		default:
			break;
		}
	}
	
	private void clearAllInput(){
		edt_sd_0.setText("");
		edt_sd_1.setText("");
		edt_sd_2.setText("");
		edt_sd_3.setText("");
		edt_sd_4.setText("");
		edt_sd_5.setText("");
		edt_sd_6.setText("");
		edt_sd_7.setText("");
		edt_sd_8.setText("");
		edt_sd_9.setText("");
		edt_sd_10.setText("");
		edt_sd_11.setText("");
		edt_sd_12.setText("");
		edt_sd_13.setText("");
		edt_sd_14.setText("");
		edt_sd_15.setText("");
		edt_sd_16.setText("");
		edt_sd_17.setText("");
		edt_sd_18.setText("");
		edt_sd_19.setText("");

		Log.d(TAG, "SerialDebugActivity : clearAllInput()");

	}
	
	
	private int[] getEdtInput(){
		int[] data = new int[20];
		String[] strInput = new String[20];
		int count = 0;
		if(RADIX_IN_USE == RADIX_HEX){
			Log.d(TAG, "SerialDebugActivity : RADIX_IN_USE = RADIX_HEX");			
		}else{
			Log.d(TAG, "SerialDebugActivity : RADIX_IN_USE = RADIX_DEC");
		}

		try{
			String input0 = edt_sd_0.getText().toString().trim();
			Log.d(TAG, "edt_sd_0 get input "+input0);
			data[0] = Integer.parseInt(input0, RADIX_IN_USE);
			count++;
			strInput[0] = input0;
		}catch(NumberFormatException e){
			String input0 = edt_sd_0.getText().toString().trim();
			if(input0.equals("")){
					data[0] = 0;
					strInput[0] = "";
			}else{
				e.printStackTrace();
				ResourceUtils.toastShort("Please input correct parameter in 0");
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
			ResourceUtils.toastShort("Please input correct parameter 0");
			return null;
		}

		try{
			String input1 = edt_sd_1.getText().toString().trim();
			Log.d(TAG, "edt_sd_1 get input "+input1);
			data[1] = Integer.parseInt(input1, RADIX_IN_USE);
			count++;
			strInput[1] = input1;
		}catch(NumberFormatException e){
			String input1 = edt_sd_1.getText().toString().trim();
			if(input1.equals("")){
					data[1] = 0;
					strInput[1] = "";
			}else{
				e.printStackTrace();
				ResourceUtils.toastShort("Please input correct parameter in 1");
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
			ResourceUtils.toastShort("Please input correct parameter 1");
			return null;
		}

		try{
			String input2 = edt_sd_2.getText().toString().trim();
			Log.d(TAG, "edt_sd_2 get input "+input2);
			data[2] = Integer.parseInt(input2, RADIX_IN_USE);
			count++;
			strInput[2] = input2;
		}catch(NumberFormatException e){
			String input2 = edt_sd_2.getText().toString().trim();
			if(input2.equals("")){
					data[2] = 0;
					strInput[2] = "";
			}else{
				e.printStackTrace();
				ResourceUtils.toastShort("Please input correct parameter in 2");
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
			ResourceUtils.toastShort("Please input correct parameter 2");
			return null;
		}

		try{
			String input3 = edt_sd_3.getText().toString().trim();
			Log.d(TAG, "edt_sd_3 get input "+input3);
			data[3] = Integer.parseInt(input3, RADIX_IN_USE);
			count++;
			strInput[3] = input3;
		}catch(NumberFormatException e){
			String input3 = edt_sd_3.getText().toString().trim();
			if(input3.equals("")){
					data[3] = 0;
					strInput[3] = "";
			}else{
				e.printStackTrace();
				ResourceUtils.toastShort("Please input correct parameter in 3");
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
			ResourceUtils.toastShort("Please input correct parameter 3");
			return null;
		}

		try{
			String input4 = edt_sd_4.getText().toString().trim();
			Log.d(TAG, "edt_sd_4 get input "+input4);
			data[4] = Integer.parseInt(input4, RADIX_IN_USE);
			count++;
			strInput[4] = input4;
		}catch(NumberFormatException e){
			String input4 = edt_sd_4.getText().toString().trim();
			if(input4.equals("")){
					data[4] = 0;
					strInput[4] = "";
			}else{
				e.printStackTrace();
				ResourceUtils.toastShort("Please input correct parameter in 4");
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
			ResourceUtils.toastShort("Please input correct parameter 4");
			return null;
		}

		try{
			String input5 = edt_sd_5.getText().toString().trim();
			Log.d(TAG, "edt_sd_5 get input "+input5);
			data[5] = Integer.parseInt(input5, RADIX_IN_USE);
			count++;
			strInput[5] = input5;
		}catch(NumberFormatException e){
			String input5 = edt_sd_5.getText().toString().trim();
			if(input5.equals("")){
					data[5] = 0;
					strInput[5] = "";
			}else{
				e.printStackTrace();
				ResourceUtils.toastShort("Please input correct parameter in 5");
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
			ResourceUtils.toastShort("Please input correct parameter 5");
			return null;
		}

		try{
			String input6 = edt_sd_6.getText().toString().trim();
			Log.d(TAG, "edt_sd_6 get input "+input6);
			data[6] = Integer.parseInt(input6, RADIX_IN_USE);
			count++;
			strInput[6] = input6;
		}catch(NumberFormatException e){
			String input6 = edt_sd_6.getText().toString().trim();
			if(input6.equals("")){
					data[6] = 0;
					strInput[6] = "";
			}else{
				e.printStackTrace();
				ResourceUtils.toastShort("Please input correct parameter in 6");
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
			ResourceUtils.toastShort("Please input correct parameter 6");
			return null;
		}

		try{
			String input7 = edt_sd_7.getText().toString().trim();
			Log.d(TAG, "edt_sd_7 get input "+input7);
			data[7] = Integer.parseInt(input7, RADIX_IN_USE);
			count++;
			strInput[7] = input7;
		}catch(NumberFormatException e){
			String input7 = edt_sd_7.getText().toString().trim();
			if(input7.equals("")){
					data[7] = 0;
					strInput[7] = "";
			}else{
				e.printStackTrace();
				ResourceUtils.toastShort("Please input correct parameter in 7");
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
			ResourceUtils.toastShort("Please input correct parameter 7");
			return null;
		}

		try{
			String input8 = edt_sd_8.getText().toString().trim();
			Log.d(TAG, "edt_sd_8 get input "+input8);
			data[8] = Integer.parseInt(input8, RADIX_IN_USE);
			count++;
			strInput[8] = input8;
		}catch(NumberFormatException e){
			String input8 = edt_sd_8.getText().toString().trim();
			if(input8.equals("")){
					data[8] = 0;
					strInput[8] = "";
			}else{
				e.printStackTrace();
				ResourceUtils.toastShort("Please input correct parameter in 8");
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
			ResourceUtils.toastShort("Please input correct parameter 8");
			return null;
		}

		try{
			String input9 = edt_sd_9.getText().toString().trim();
			Log.d(TAG, "edt_sd_9 get input "+input9);
			data[9] = Integer.parseInt(input9, RADIX_IN_USE);
			count++;
			strInput[9] = input9;
		}catch(NumberFormatException e){
			String input9 = edt_sd_9.getText().toString().trim();
			if(input9.equals("")){
					data[9] = 0;
					strInput[9] = "";
			}else{
				e.printStackTrace();
				ResourceUtils.toastShort("Please input correct parameter in 9");
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
			ResourceUtils.toastShort("Please input correct parameter 9");
			return null;
		}

		try{
			String input10 = edt_sd_10.getText().toString().trim();
			Log.d(TAG, "edt_sd_10 get input "+input10);
			data[10] = Integer.parseInt(input10, RADIX_IN_USE);
			count++;
			strInput[10] = input10;
		}catch(NumberFormatException e){
			String input10 = edt_sd_10.getText().toString().trim();
			if(input10.equals("")){
					data[10] = 0;
					strInput[10] = "";
			}else{
				e.printStackTrace();
				ResourceUtils.toastShort("Please input correct parameter in 10");
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
			ResourceUtils.toastShort("Please input correct parameter 10");
			return null;
		}

		try{
			String input11 = edt_sd_11.getText().toString().trim();
			Log.d(TAG, "edt_sd_11 get input "+input11);
			data[11] = Integer.parseInt(input11, RADIX_IN_USE);
			count++;
			strInput[11] = input11;
		}catch(NumberFormatException e){
			String input11 = edt_sd_11.getText().toString().trim();
			if(input11.equals("")){
					data[11] = 0;
					strInput[11] = "";
			}else{
				e.printStackTrace();
				ResourceUtils.toastShort("Please input correct parameter in 11");
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
			ResourceUtils.toastShort("Please input correct parameter 11");
			return null;
		}

		try{
			String input12 = edt_sd_12.getText().toString().trim();
			Log.d(TAG, "edt_sd_12 get input "+input12);
			data[12] = Integer.parseInt(input12, RADIX_IN_USE);
			count++;
			strInput[12] = input12;
		}catch(NumberFormatException e){
			String input12 = edt_sd_12.getText().toString().trim();
			if(input12.equals("")){
					data[12] = 0;
					strInput[12] = "";
			}else{
				e.printStackTrace();
				ResourceUtils.toastShort("Please input correct parameter in 12");
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
			ResourceUtils.toastShort("Please input correct parameter 12");
			return null;
		}

		try{
			String input13 = edt_sd_13.getText().toString().trim();
			Log.d(TAG, "edt_sd_13 get input "+input13);
			data[13] = Integer.parseInt(input13, RADIX_IN_USE);
			count++;
			strInput[13] = input13;
		}catch(NumberFormatException e){
			String input13 = edt_sd_13.getText().toString().trim();
			if(input13.equals("")){
					data[13] = 0;
					strInput[13] = "";
			}else{
				e.printStackTrace();
				ResourceUtils.toastShort("Please input correct parameter in 13");
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
			ResourceUtils.toastShort("Please input correct parameter 13");
			return null;
		}

		try{
			String input14 = edt_sd_14.getText().toString().trim();
			Log.d(TAG, "edt_sd_14 get input "+input14);
			data[14] = Integer.parseInt(input14, RADIX_IN_USE);
			count++;
			strInput[14] = input14;
		}catch(NumberFormatException e){
			String input14 = edt_sd_14.getText().toString().trim();
			if(input14.equals("")){
					data[14] = 0;
					strInput[14] = "";
			}else{
				e.printStackTrace();
				ResourceUtils.toastShort("Please input correct parameter in 14");
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
			ResourceUtils.toastShort("Please input correct parameter 14");
			return null;
		}

		try{
			String input15 = edt_sd_15.getText().toString().trim();
			Log.d(TAG, "edt_sd_15 get input "+input15);
			data[15] = Integer.parseInt(input15, RADIX_IN_USE);
			count++;
			strInput[15] = input15;
		}catch(NumberFormatException e){
			String input15 = edt_sd_15.getText().toString().trim();
			if(input15.equals("")){
					data[15] = 0;
					strInput[15] = "";
			}else{
				e.printStackTrace();
				ResourceUtils.toastShort("Please input correct parameter in 15");
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
			ResourceUtils.toastShort("Please input correct parameter 15");
			return null;
		}

		try{
			String input16 = edt_sd_16.getText().toString().trim();
			Log.d(TAG, "edt_sd_16 get input "+input16);
			data[16] = Integer.parseInt(input16, RADIX_IN_USE);
			count++;
			strInput[16] = input16;
		}catch(NumberFormatException e){
			String input16 = edt_sd_16.getText().toString().trim();
			if(input16.equals("")){
					data[16] = 0;
					strInput[16] = "";
			}else{
				e.printStackTrace();
				ResourceUtils.toastShort("Please input correct parameter in 16");
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
			ResourceUtils.toastShort("Please input correct parameter 16");
			return null;
		}

		try{
			String input17 = edt_sd_17.getText().toString().trim();
			Log.d(TAG, "edt_sd_17 get input "+input17);
			data[17] = Integer.parseInt(input17, RADIX_IN_USE);
			count++;
			strInput[17] = input17;
		}catch(NumberFormatException e){
			String input17 = edt_sd_17.getText().toString().trim();
			if(input17.equals("")){
					data[17] = 0;
					strInput[17] = "";
			}else{
				e.printStackTrace();
				ResourceUtils.toastShort("Please input correct parameter in 17");
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
			ResourceUtils.toastShort("Please input correct parameter 17");
			return null;
		}

		try{
			String input18 = edt_sd_18.getText().toString().trim();
			Log.d(TAG, "edt_sd_18 get input "+input18);
			data[18] = Integer.parseInt(input18, RADIX_IN_USE);
			count++;
			strInput[18] = input18;
		}catch(NumberFormatException e){
			String input18 = edt_sd_18.getText().toString().trim();
			if(input18.equals("")){
					data[18] = 0;
					strInput[18] = "";
			}else{
				e.printStackTrace();
				ResourceUtils.toastShort("Please input correct parameter in 18");
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
			ResourceUtils.toastShort("Please input correct parameter 18");
			return null;
		}

		try{
			String input19 = edt_sd_19.getText().toString().trim();
			Log.d(TAG, "edt_sd_19 get input "+input19);
			data[19] = Integer.parseInt(input19, RADIX_IN_USE);
			count++;
			strInput[19] = input19;
		}catch(NumberFormatException e){
			String input19 = edt_sd_19.getText().toString().trim();
			if(input19.equals("")){
					data[19] = 0;
					strInput[19] = "";
			}else{
				e.printStackTrace();
				ResourceUtils.toastShort("Please input correct parameter in 19");
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
			ResourceUtils.toastShort("Please input correct parameter 19");
			return null;
		}




		int strIndex = 0;
		int flag = -1;
		for(int i = data.length-1; i>=0; i--){
			Log.d(TAG, "SerialDebugActivity : getEdtInput() i = "+i); 
			if(!strInput[i].equals("")){
				strIndex = i;
				flag = 1;
				break;
			}
		}
		if(flag == -1){
			return null;
		}
		
		int[] finalData = new int[strIndex+1];
		for(int i = 0; i<=strIndex;i++){
			finalData[i] = data[i];
		}
		
		return finalData;
	
	
	}
	
	private void setEdtText(int[] data, int radix){
		Log.d(TAG, "SerialDebugActivity : setEdtText() data = "+Arrays.toString(data)
				+", radix = "+radix);
		
		if(data.length == 0){
			return;
		}
		changeRadioGroupRadix(radix);
		int index = data.length - 1;

		
		if(radix == RADIX_HEX){
			edt_sd_0.setText(Integer.toHexString(data[0]).toUpperCase(Locale.getDefault()));
		}else{
			edt_sd_0.setText(Integer.toString(data[0]));
		}
		if(index == 0){
			return;
		}

		if(radix == RADIX_HEX){
			edt_sd_1.setText(Integer.toHexString(data[1]).toUpperCase(Locale.getDefault()));
		}else{
			edt_sd_1.setText(Integer.toString(data[1]));
		}
		if(index == 1){
			return;
		}

		if(radix == RADIX_HEX){
			edt_sd_2.setText(Integer.toHexString(data[2]).toUpperCase(Locale.getDefault()));
		}else{
			edt_sd_2.setText(Integer.toString(data[2]));
		}
		if(index == 2){
			return;
		}

		if(radix == RADIX_HEX){
			edt_sd_3.setText(Integer.toHexString(data[3]).toUpperCase(Locale.getDefault()));
		}else{
			edt_sd_3.setText(Integer.toString(data[3]));
		}
		if(index == 3){
			return;
		}

		if(radix == RADIX_HEX){
			edt_sd_4.setText(Integer.toHexString(data[4]).toUpperCase(Locale.getDefault()));
		}else{
			edt_sd_4.setText(Integer.toString(data[4]));
		}
		if(index == 4){
			return;
		}

		if(radix == RADIX_HEX){
			edt_sd_5.setText(Integer.toHexString(data[5]).toUpperCase(Locale.getDefault()));
		}else{
			edt_sd_5.setText(Integer.toString(data[5]));
		}
		if(index == 5){
			return;
		}

		if(radix == RADIX_HEX){
			edt_sd_6.setText(Integer.toHexString(data[6]).toUpperCase(Locale.getDefault()));
		}else{
			edt_sd_6.setText(Integer.toString(data[6]));
		}
		if(index == 6){
			return;
		}

		if(radix == RADIX_HEX){
			edt_sd_7.setText(Integer.toHexString(data[7]).toUpperCase(Locale.getDefault()));
		}else{
			edt_sd_7.setText(Integer.toString(data[7]));
		}
		if(index == 7){
			return;
		}

		if(radix == RADIX_HEX){
			edt_sd_8.setText(Integer.toHexString(data[8]).toUpperCase(Locale.getDefault()));
		}else{
			edt_sd_8.setText(Integer.toString(data[8]));
		}
		if(index == 8){
			return;
		}

		if(radix == RADIX_HEX){
			edt_sd_9.setText(Integer.toHexString(data[9]).toUpperCase(Locale.getDefault()));
		}else{
			edt_sd_9.setText(Integer.toString(data[9]));
		}
		if(index == 9){
			return;
		}

		if(radix == RADIX_HEX){
			edt_sd_10.setText(Integer.toHexString(data[10]).toUpperCase(Locale.getDefault()));
		}else{
			edt_sd_10.setText(Integer.toString(data[10]));
		}
		if(index == 10){
			return;
		}

		if(radix == RADIX_HEX){
			edt_sd_11.setText(Integer.toHexString(data[11]).toUpperCase(Locale.getDefault()));
		}else{
			edt_sd_11.setText(Integer.toString(data[11]));
		}
		if(index == 11){
			return;
		}

		if(radix == RADIX_HEX){
			edt_sd_12.setText(Integer.toHexString(data[12]).toUpperCase(Locale.getDefault()));
		}else{
			edt_sd_12.setText(Integer.toString(data[12]));
		}
		if(index == 12){
			return;
		}

		if(radix == RADIX_HEX){
			edt_sd_13.setText(Integer.toHexString(data[13]).toUpperCase(Locale.getDefault()));
		}else{
			edt_sd_13.setText(Integer.toString(data[13]));
		}
		if(index == 13){
			return;
		}

		if(radix == RADIX_HEX){
			edt_sd_14.setText(Integer.toHexString(data[14]).toUpperCase(Locale.getDefault()));
		}else{
			edt_sd_14.setText(Integer.toString(data[14]));
		}
		if(index == 14){
			return;
		}

		if(radix == RADIX_HEX){
			edt_sd_15.setText(Integer.toHexString(data[15]).toUpperCase(Locale.getDefault()));
		}else{
			edt_sd_15.setText(Integer.toString(data[15]));
		}
		if(index == 15){
			return;
		}

		if(radix == RADIX_HEX){
			edt_sd_16.setText(Integer.toHexString(data[16]).toUpperCase(Locale.getDefault()));
		}else{
			edt_sd_16.setText(Integer.toString(data[16]));
		}
		if(index == 16){
			return;
		}

		if(radix == RADIX_HEX){
			edt_sd_17.setText(Integer.toHexString(data[17]).toUpperCase(Locale.getDefault()));
		}else{
			edt_sd_17.setText(Integer.toString(data[17]));
		}
		if(index == 17){
			return;
		}

		if(radix == RADIX_HEX){
			edt_sd_18.setText(Integer.toHexString(data[18]).toUpperCase(Locale.getDefault()));
		}else{
			edt_sd_18.setText(Integer.toString(data[18]));
		}
		if(index == 18){
			return;
		}

		if(radix == RADIX_HEX){
			edt_sd_19.setText(Integer.toHexString(data[19]).toUpperCase(Locale.getDefault()));
		}else{
			edt_sd_19.setText(Integer.toString(data[19]));
		}
		if(index == 19){
			return;
		}




		
		
	}
	
	private void printToConsole(boolean isHost, byte[] array){
		if(RADIX_IN_USE == RADIX_HEX){
			printToConsole(isHost, ResourceUtils.arrayToHexString(array));
		}else{
			printToConsole(isHost, ResourceUtils.arrayToDecString(array));
		}
		
	}
	
	private void printToConsole(boolean isHost, int[] array){
		if(RADIX_IN_USE == RADIX_HEX){
			printToConsole(isHost, ResourceUtils.arrayToHexString(array));
		}else{
			printToConsole(isHost, ResourceUtils.arrayToDecString(array));
		}
		
	}
	
	
	
	private void printToConsole(boolean isHost, String s){
		String caller = "UNKNOWN";
		if(isHost){
			caller = "A";
		}else{
			caller = "B";
		}
		
		String time = ResourceUtils.getSystemTimeClock();
		txt_console.append(caller+"-"+time+"	: "+s);
		txt_console.append("\n");
		txt_console.append(" ");
		if(!mConsole.isFocused()){
			mConsole.fullScroll(ScrollView.FOCUS_DOWN);			
		}
	}
	
	private class MyHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case WHAT_SEND_INPUT:
				sendInput();
				break;

			default:
				break;
			}
			
		}
		
		private void sendInput(){
			int[] data = getEdtInput();
			if(data == null){
				Log.d(TAG, "SerialDebugActivity : sendInput() data == null, stop send.");
				ResourceUtils.toastShort("No Data Sent");
				return;
			}
			Log.d(TAG, "SerialDebugActivity : Get input "+Arrays.toString(data));
			int cmd = data[0];
			int cnt = Protocol.getReceiveCommandCNT(cmd);
			if(cnt == -1){
				ResourceUtils.toastShort("Wrong cmd sent");
			}
			
			byte[] sendData = new byte[data.length];
			for(int i=0; i< data.length; i++){
				sendData[i] = (byte)(data[i]&0xFF);
			}
			ResourceUtils.sendCMDRawDataToCurrentDevice(sendData);
			//printToConsole(true, Arrays.toString(data));
			
		}
		
	}
	

	
	
	private class CMDReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(ResourceUtils.ACTION_CMD_RECEIVED)){
				Log.d(TAG, "SerialDebugActivity : Receive a command");
				BluetoothDevice d = intent.getParcelableExtra(ResourceUtils.DATA_BTDEVICE);
				if(!(d.getAddress().equals(ResourceUtils.getCurrentDevice().getAddress()))){
					return;
				}
				int cmd = intent.getIntExtra(ResourceUtils.DATA_RECEIVED_CMD, -1);
				Log.d(TAG, "SerialDebugActivity : Receive a command, cmd = 0x"+Integer.toHexString(cmd));
				final int[] data = intent.getIntArrayExtra(ResourceUtils.DATA_RECEIVED_DATA);
				byte[] rawData = intent.getByteArrayExtra(ResourceUtils.DATA_RECEIVED_RAW_DATA);
				printToConsole(false, rawData);
				
			}else if(action.equals(ResourceUtils.ACTION_CMD_SEND_RAW_DATA)){
				byte[] sentRawData = intent.getByteArrayExtra(ResourceUtils.DATA_SEND_RAW_DATA);
				printToConsole(true, sentRawData);
			}

		}
		
	}


	
	
}
