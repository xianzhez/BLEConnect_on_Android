package com.wm.bleconnect;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class SerialCustomedCMDActivity extends Activity implements OnClickListener{
	public static final String EXTRA_CMD_CUSTOMED_ORI = "EXTRA_CMD_CUSTOMED_ORI";
	public static final String EXTRA_CMD_CUSTOMED_NEW = "EXTRA_CMD_CUSTOMED_NEW";
	public static final int RESULT_CODE_CMD_CUSTOMED_OK_CHECKED = 1;
	public static final int RESULT_CODE_CMD_CUSTOMED_OK_NOT_CHECKED = 2;
	public static final int RESULT_CODE_CMD_CUSTOMED_CANCEL = 3;
	
	
	
	private EditText edt_sd_ctm_cmd_0_name;
	private CheckBox chkBx_sd_ctm_cmd_0_checksum;
	private Button btn_sd_ctm_cmd_0_confirm;
	private Button btn_sd_ctm_cmd_1_cancel;
	
	private Intent oldIntent;
	private HashMap<String, Object> oriCMDMap = new HashMap<String, Object>();
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_serial_customed_cmd);
		initViews();
		oldIntent = getIntent();
		oriCMDMap = (HashMap<String, Object>)oldIntent.getSerializableExtra(EXTRA_CMD_CUSTOMED_ORI);
	}
	
	private void initViews(){
		edt_sd_ctm_cmd_0_name = (EditText)findViewById(R.id.edt_sd_ctm_cmd_0_name);
		chkBx_sd_ctm_cmd_0_checksum = (CheckBox)findViewById(R.id.chkBx_sd_ctm_cmd_0_checksum);
		btn_sd_ctm_cmd_0_confirm = (Button)findViewById(R.id.btn_sd_ctm_cmd_0_confirm);
		btn_sd_ctm_cmd_1_cancel = (Button)findViewById(R.id.btn_sd_ctm_cmd_1_cancel);
		btn_sd_ctm_cmd_0_confirm.setOnClickListener(this);
		btn_sd_ctm_cmd_1_cancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_sd_ctm_cmd_0_confirm:
			oriCMDMap.put(SerialDebugActivity.EXTRA_CMD_NAME, getEdtInput());
			Intent intent = new Intent();
			intent.putExtra(EXTRA_CMD_CUSTOMED_NEW, oriCMDMap);
			if(isChecked()){
				setResult(RESULT_CODE_CMD_CUSTOMED_OK_CHECKED, intent);
			}else{
				setResult(RESULT_CODE_CMD_CUSTOMED_OK_NOT_CHECKED, intent);
			}
			this.finish();
			break;
		case R.id.btn_sd_ctm_cmd_1_cancel:
			setResult(RESULT_CODE_CMD_CUSTOMED_CANCEL);
			this.finish();
			break;
		default:
			break;
		}
		
	}
	
	private boolean isChecked(){
		return chkBx_sd_ctm_cmd_0_checksum.isChecked();
	}
	
	private String getEdtInput(){
		String str = edt_sd_ctm_cmd_0_name.getText().toString();
		if(str != null){
			return str.trim();
		}
		return "";
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		setResult(RESULT_CODE_CMD_CUSTOMED_CANCEL);
		this.finish();
	}

}
