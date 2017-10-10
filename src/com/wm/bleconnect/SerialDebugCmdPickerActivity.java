package com.wm.bleconnect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class SerialDebugCmdPickerActivity extends Activity {
	private static final String TAG = ResourceUtils.TAG;
	
	public static final String EXTRA_CMD_PICKED = "EXTRA_CMD_PICKED";
	private Intent oldIntent;
	private ListView lst_cmd;
	private SimpleAdapter mAdapter;
	private String[] from = new String[]{SerialDebugActivity.EXTRA_CMD_NAME,
			SerialDebugActivity.EXTRA_CMD_RADIX_USED,
			SerialDebugActivity.EXTRA_CMD_DATA};
	private int[] to = new int[]{R.id.txt_itm_sd_pkr_0_dsp,
			R.id.txt_itm_sd_pkr_1_rdx,R.id.txt_itm_sd_pkr_2_cmd};
	
	private ArrayList<HashMap<String, Object>> cmdList =
			new ArrayList<HashMap<String, Object>>();

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_serial_debug_cmd_picker);
		oldIntent = getIntent();
		cmdList = (ArrayList<HashMap<String, Object>>)oldIntent
				.getSerializableExtra(SerialDebugActivity.EXTRA_CMD_LIST);
		Log.d(TAG, "SerialDebugCmdPickerActivity : ============== cmdList.size() = "
				+cmdList.size());
		lst_cmd = (ListView)findViewById(R.id.lstv_sd_cmd_pkr_0);
		mAdapter = new SimpleAdapter(this, getData(), 
				R.layout.listitem_sd_cmd_pkr, from, to);
		lst_cmd.setAdapter(mAdapter);
		
		lst_cmd.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				HashMap<String, Object> map = cmdList.get(position);
				Intent intent = new Intent(SerialDebugCmdPickerActivity.this,
						SerialDebugActivity.class);
				intent.putExtra(EXTRA_CMD_PICKED, position);
				setResult(position);
				SerialDebugCmdPickerActivity.this.finish();
			}
		});
		Log.d(TAG, "SerialDebugCmdPickerActivity : ==============");
	
	}
	
	private List<HashMap<String, Object>> getData(){
		List<HashMap<String, Object>> list = 
				new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map; 
		for(Map item: cmdList){
			map =  new HashMap<String, Object>();
			map.put(SerialDebugActivity.EXTRA_CMD_NAME,
					item.get(SerialDebugActivity.EXTRA_CMD_NAME));
			int radix = (Integer)item.get(SerialDebugActivity.EXTRA_CMD_RADIX_USED);
			String strRadix = "HEX :";
			if(radix == 10){
				strRadix = "DEC :";
				map.put(SerialDebugActivity.EXTRA_CMD_RADIX_USED, strRadix);
				map.put(SerialDebugActivity.EXTRA_CMD_DATA, 
						ResourceUtils.arrayToDecString(
								(int[])item.get(SerialDebugActivity.EXTRA_CMD_DATA)));

			}else{
				strRadix = "HEX :";
				map.put(SerialDebugActivity.EXTRA_CMD_RADIX_USED, strRadix);
				map.put(SerialDebugActivity.EXTRA_CMD_DATA, 
						ResourceUtils.arrayToHexString(
								(int[])item.get(SerialDebugActivity.EXTRA_CMD_DATA)));
			}
			list.add(map);
		}

		return list;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		setResult(-1);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		setResult(-1);
		return super.onKeyDown(keyCode, event);
	}
	
	
	
	
	
}
