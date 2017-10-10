package com.wm.bleconnect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MoreActivity extends Activity {
	ListView lstV_more;
	ArrayAdapter<String> mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more);
		lstV_more = (ListView)findViewById(R.id.lstv_more_0);
		mAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_expandable_list_item_1, getData());
		lstV_more.setAdapter(mAdapter);
		lstV_more.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					Intent intent = new Intent(MoreActivity.this, DevicePickerActivity.class);
					intent.putExtra(ResourceUtils.EXTRA_INT_SHOW_CHOSEN, 
							ResourceUtils.ACTIVITY_SERIAL_DEBUG);
					startActivity(intent);
					
					//for test use
/*					Intent intent = new Intent(MoreActivity.this, SerialDebugActivity.class);
					startActivity(intent);*/
					break;

				default:
					break;
				}
				
				
			}
			
		});
		
	}
	
	private String [] getData(){		
		return new String[]{"1.串口调试工具"};
	}
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
	
	}

}
