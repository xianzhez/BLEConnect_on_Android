package com.wm.bleconnect;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import com.wm.bleconnect.ResourceUtils.UIHandler;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

public class ClockActivity extends Activity implements OnClickListener,
			OnFocusChangeListener{
	private static final String TAG = ResourceUtils.TAG;
	
	Button btnGetBLETime;
	Button btnSyncSysTime;
	Button btnSyncCtmTime;
	EditText edTxtBLETime;
	EditText edTxtTime;
	EditText edTxtDate;
	TextView txtRefresh;
	Calendar mCal;
	UIHandler mUIHandler;
	IntentFilter mIntentFilter;
	CMDReceiver mReceiver;
	DatePickerDialog mDatePickerDialog;
	TimePickerDialog mTimepPickerDialog;
	int[] clockPickedDate = new int[7];
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        
        setTitle("当前设备 ："+ResourceUtils.getCurrentDevice().getName());
    	btnGetBLETime = (Button)findViewById(R.id.btn_clk_getDeviceTime);
    	btnSyncSysTime = (Button)findViewById(R.id.btn_clk_SyncSysTime);
    	btnSyncCtmTime = (Button)findViewById(R.id.btn_clk_SyncCtmTime);
    	edTxtBLETime = (EditText)findViewById(R.id.edtxt_clk_deviceTime);
    	edTxtTime = (EditText)findViewById(R.id.edtxt_clk_time);
    	edTxtDate = (EditText)findViewById(R.id.edtxt_clk_date);
    	txtRefresh = (TextView)findViewById(R.id.txt_clk_refresh_time);
        mCal = Calendar.getInstance();
        mUIHandler = UIHandler.getInstance();
        
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(ResourceUtils.ACTION_CMD_RECEIVED);
        mReceiver = new CMDReceiver();
        registerReceiver(mReceiver, mIntentFilter);
        
        btnGetBLETime.setOnClickListener(this);
        btnSyncSysTime.setOnClickListener(this);
        btnSyncCtmTime.setOnClickListener(this);
        edTxtDate.setText((mCal.get(Calendar.YEAR))%100+"-"+mCal.get(Calendar.MONTH)
        		+"-"+mCal.get(Calendar.DAY_OF_MONTH));
        edTxtDate.setOnClickListener(this);
        edTxtTime.setText(mCal.get(Calendar.HOUR_OF_DAY)+":"
        		+mCal.get(Calendar.MINUTE)+":"+mCal.get(Calendar.SECOND));
        edTxtTime.setOnClickListener(this);
        edTxtBLETime.setOnFocusChangeListener(this);
        
        mDatePickerDialog = new DatePickerDialog(this,
        		new OnDateSetListener() {
					
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,
							int dayOfMonth) {
						edTxtDate.setText(year%100+"-"+(monthOfYear+1)+"-"+dayOfMonth);
					}
				},
				mCal.get(Calendar.YEAR), mCal.get(Calendar.MONTH), 
				mCal.get(Calendar.DAY_OF_MONTH));
        
        mTimepPickerDialog = new TimePickerDialog(this,
        		new OnTimeSetListener() {
					
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
						edTxtTime.setText(hourOfDay+":"+minute+":"
						+mCal.get(Calendar.SECOND));
					}
				}, mCal.get(Calendar.HOUR_OF_DAY),
				mCal.get(Calendar.MINUTE),true);
        
        
		//SimpleDateFormat df = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		//xianzhe
/*		try {
			Date d1 = df.parse("2004-03-26 13:31:40");
			Date d2 = df.parse("00-00-00 11:30:24");
			long l=d1.getTime()-d2.getTime();
			long day=l/(24*60*60*1000);
			long hour=(l/(60*60*1000)-day*24);
			long min=((l/(60*1000))-day*24*60-hour*60);
			long s=(l/1000-day*24*60*60-hour*60*60-min*60);
			txtRefresh.setText(""+day+" "+hour+" "+min+" "+s);
		} catch (ParseException e) {
			e.printStackTrace();
		}*/ 
        
        
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_clk_getDeviceTime:
			ResourceUtils.sendCMDToCurrentDevice(0x40, new int[]{0});
			txtRefresh.setText("");
			edTxtBLETime.clearComposingText();
			break;
		case R.id.btn_clk_SyncSysTime:
			int[] data0 = {mCal.get(Calendar.YEAR)%100,mCal.get(Calendar.MONTH)+1,
					mCal.get(Calendar.DAY_OF_MONTH),mCal.get(Calendar.HOUR_OF_DAY),
					mCal.get(Calendar.MINUTE),mCal.get(Calendar.SECOND),0};
			ResourceUtils.toastShort(Arrays.toString(data0));
			ResourceUtils.sendCMDToCurrentDevice(0x10, data0);
			break;
			
		case R.id.btn_clk_SyncCtmTime:
			int[] data = new int[7];
			String date = edTxtDate.getText().toString().trim();
			String[] dat = date.split("\\-");
			if(dat.length != 3){
				ResourceUtils.toastShort("Please input correct date.");
			}
			for(int i = 0;i<dat.length;i++){
				data[i] = Integer.parseInt(dat[i]);
			}
			String time = edTxtTime.getText().toString().trim();
			String[] tim = time.split("\\:");
			if(tim.length != 3){
				ResourceUtils.toastShort("Please input correct time.");
			}
			for(int j = 0;j<tim.length;j++){
				data[j+3] = Integer.parseInt(tim[j]);
			}
			Log.d(TAG, "ClockActivity : set device time as "+Arrays.toString(data));
			ResourceUtils.sendCMDToCurrentDevice(0x10, data);
			break;
		case R.id.edtxt_clk_deviceTime:
			break;
		case R.id.edtxt_clk_time:
			mTimepPickerDialog.show();
			break;
		case R.id.edtxt_clk_date:
			mDatePickerDialog.show();
			break;
		default:
			break;
		}
	}
	
	
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if(hasFocus){
			switch(v.getId()){
				
			case R.id.edtxt_clk_time:
				mTimepPickerDialog.show();
				break;
			case R.id.edtxt_clk_date:
				mDatePickerDialog.show();
				break;
			default:
				break;
			}
			
		}
	}
	
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}
	
	private class CMDReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "ClockActivity : Receive a command");
			BluetoothDevice d = intent.getParcelableExtra(ResourceUtils.DATA_BTDEVICE);
			if(!(d.getAddress().equals(ResourceUtils.getCurrentDevice().getAddress()))){
				return;
			}
			int cmd = intent.getIntExtra(ResourceUtils.DATA_RECEIVED_CMD, -1);
			Log.d(TAG, "ClockActivity : Receive a command, cmd = 0x"+Integer.toHexString(cmd));
			final int[] data = intent.getIntArrayExtra(ResourceUtils.DATA_RECEIVED_DATA);
			switch(cmd){
			case -1:
				Log.d(TAG, "ClockActivity : Receive a command -1 ");
				break;
			case 0x40:
				Log.d(TAG, "ClockActivity : Receive a command 0x40 ");
				mUIHandler.post(new Runnable() {
					
					@Override
					public void run() {
						if(data.length != 7){
							ResourceUtils.toastShort("Wrong time format received");
						}
						String time = data[0]+"-"+data[1]+"-"+data[2]+
								" "+data[3]+":"+data[4]+":"+data[5];
						edTxtBLETime.setText(data[0]+"-"+data[1]+"-"+data[2]+
								" "+data[3]+":"+data[4]+":"+data[5]);
						String sysTime = ResourceUtils.getSystemTime();
						//txtRefresh.setText();
						SimpleDateFormat df = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
						//xianzhe
						try {
							Date d1 = df.parse(time);
							Date d2 = df.parse(sysTime);
							long l=d1.getTime()-d2.getTime();
							long day=l/(24*60*60*1000);
							long hour=(l/(60*60*1000)-day*24);
							long min=((l/(60*1000))-day*24*60-hour*60);
							long s=(l/1000-day*24*60*60-hour*60*60-min*60);
							if(min > 10){
								txtRefresh.setText(" >10 min");
							}else{
								txtRefresh.setText(min+" min "+s+" sec");
							}
						} catch (ParseException e) {
							e.printStackTrace();
							ResourceUtils.toastLong("ERROR: 时间错误");
						} 
						
					}
				});
				break;
			case 0x93:
				//ResourceUtils.toastShort("Set time successfully");
				if(data[0] == 16){
					ResourceUtils.toastShort("Set time successfully");
				}
				break;
			}
		
		//ResourceUtils.sendCMDToCurrentDevice(0x90, new int[]{cmd,0});
		}
		
	}



}
