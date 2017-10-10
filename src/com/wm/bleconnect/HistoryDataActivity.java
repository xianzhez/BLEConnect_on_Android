package com.wm.bleconnect;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class HistoryDataActivity extends Activity implements OnClickListener{

	private static final String TAG = ResourceUtils.TAG;
	
	private Button btn_mgr; 
	
	
	private ListView lst_hst_files;
	private SimpleAdapter mAdapter;
	private String[] from = new String[]{"fileName", "deviceName", "address"};
	private int[] to = new int[]{R.id.txt_itm_hst_file_0_name,
			R.id.txt_itm_hst_file_1_deName,R.id.txt_itm_hst_file_2_address};
	public static final String DeviceNameFlag = "DeviceName : ";
	public static final String MacAddressFlag = "MacAddress : ";
	
	List<HashMap<String, String>> fileList ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history_data);
		btn_mgr = (Button)findViewById(R.id.btn_hst_data_0);
		btn_mgr.setOnClickListener(this);
		lst_hst_files = (ListView)findViewById(R.id.lstv_hst_data_0);
		mAdapter = new SimpleAdapter(this, getData(), 
				R.layout.listitem_hst_file, from, to);
		lst_hst_files.setAdapter(mAdapter);
		lst_hst_files.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				HashMap<String, String> map = fileList.get(position);
				String path = map.get("fullName");
				
				Intent intent = new Intent(HistoryDataActivity.this, 
						DisplayActivity.class);
				intent.putExtra(ResourceUtils.DATA_HISTORY_FILE_NAME, path);
				startActivity(intent);
				
			}
		});
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private List<HashMap<String, String>> getData(){
		fileList = new ArrayList<HashMap<String, String>>();
		List<HashMap<String, String>> filesInfo = getFilesInfo();
		HashMap<String, String> map; 
		for(Map item: filesInfo){
			map =  new HashMap<String, String>();
			map.put("fileName", "File	: "+(String) item.get("fileName"));
			map.put("deviceName", "Device	: "+(String) item.get("deviceName"));
			map.put("address", "MacAddress	: "+(String) item.get("address"));
			map.put("fullName", (String) item.get("fullName"));
			fileList.add(map);
		}

		return fileList;
	}
	
	private ArrayList<HashMap<String, String>> getFilesInfo(){
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map;
		ArrayList<String> files = getFiles();
		Log.d(TAG, "HistoryDataActivity : getFilesInfo()");
		for(String item: files){
			map =  new HashMap<String, String>();
			File file = new File(item);
			if((!file.exists())||(file.isDirectory())){
				return list;
			}
			map.put("fileName", file.getName());
			map.put("deviceName", getDeviceNameFromFile(file));
			map.put("address", getDeviceAddressFromFile(file));
			map.put("fullName", file.getPath());
			list.add(map);
		}

		return list;
	}
	
	private ArrayList<String> getFiles(){
		return ResourceUtils.getHistoryFiles();
	}
	
	private String getDeviceNameFromFile(File f){
		return getDeviceInfoFromFile(f)[1];
	}
	
	private String getDeviceAddressFromFile(File f){
		return getDeviceInfoFromFile(f)[2];
	}
	
	private String[] getDeviceInfoFromFile(File f){
		String[] info = new String[]{"FileName","DeviceName","MacAddress"};
		try {
			info[0] = f.getName();
			
			FileReader reader = new FileReader(f);
			char[] buf = new char[1024];
			reader.read(buf);
			String out = new String(buf);
			
			int nameStart = out.indexOf(DeviceNameFlag)+DeviceNameFlag.length();
			int nameEnd = out.indexOf("</div>", nameStart);
			info[1] = out.substring(nameStart, nameEnd);
			
			int addressStart = out.indexOf(MacAddressFlag)+MacAddressFlag.length();
			int addressEnd = out.indexOf("</div>", addressStart);
			info[2] = out.substring(addressStart, addressEnd);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.d(TAG, "HistoryDataActivity : getDeviceNameFromFile() " +
					"FileNotFoundException");
		} catch (IOException e) {
			e.printStackTrace();
			Log.d(TAG, "HistoryDataActivity : getDeviceNameFromFile() " +
					"IOException");
		}
		return info;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_hst_data_0:
			Intent intent = new Intent(getApplicationContext(),DevicePickerActivity.class);
			intent.putExtra(ResourceUtils.EXTRA_INT_SHOW_CHOSEN, 
					ResourceUtils.ACTIVITY_HISTORY_MANAGER);
			startActivity(intent);
			break;
		

		default:
			break;
		}
		
		
	}
	
}
