package com.wm.bleconnect;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;

public class FileWriteEngine implements Runnable {
	private static final String TAG = ResourceUtils.TAG;
	public static final String HistoryAppending = "WMHistoryData.html";
	public static final String RawDataAppending = "WMRawData.html";
	
	private static final int WHAT_WRITE_HISTORY = 0x001;
	private static final int WHAT_WRITE_RAW_DATA = 0x002;
	
	private static final String EXTRA_DATA_INT_ARRAY = "EXTRA_DATA_INT_ARRAY";
	private static final String EXTRA_DATA_BYTE_ARRAY = "EXTRA_DATA_BYTE_ARRAY";
	private static final String EXTRA_BT_DEVICE = "EXTRA_BT_DEVICE";
	private BlockingQueue<Message> queue;
	private Thread writeThread;
	private boolean running = true;
	private ConnectionService mService;
	private Calendar mCal = Calendar.getInstance();
	OutputStream stream;
	
	
	public FileWriteEngine(ConnectionService service){
		this.mService = service;
		queue = new LinkedBlockingQueue<Message>();
		writeThread = new Thread(this);
		writeThread.setDaemon(true);
		writeThread.setName("BLE write thread");
		writeThread.start();
	}
	
	@Override
	public void run() {
		
		while(running){
			try {
				Message msg = queue.take();
				if(msg.what == WHAT_WRITE_HISTORY){
					int cmd = msg.arg1;
					Bundle b = msg.getData();
					BluetoothDevice d = b.getParcelable(EXTRA_BT_DEVICE);
					int[] data = b.getIntArray(EXTRA_DATA_INT_ARRAY);
					writeImpl(d, cmd, data);
				}else if(msg.what == WHAT_WRITE_RAW_DATA){
					Bundle b = msg.getData();
					BluetoothDevice d = b.getParcelable(EXTRA_BT_DEVICE);
					byte[] data = b.getByteArray(EXTRA_DATA_BYTE_ARRAY);
					writeImpl(d, data);
				}
				
			} catch (InterruptedException e) {
				e.printStackTrace();
				running = false;
				Log.d(TAG, "InterruptedException - queue take was interrupted");
			}
			
		}

	}
	
	
	
	private void writeImpl(BluetoothDevice d, int cmd, int[] data){
		StringBuilder fileNameBuilder = new StringBuilder(d.getAddress());
		while(fileNameBuilder.indexOf(":")!= -1){
			fileNameBuilder.deleteCharAt(fileNameBuilder.indexOf(":"));
		}
		fileNameBuilder.append(HistoryAppending);
		fileNameBuilder.insert(0, ResourceUtils.MY_DIR);
		File file = new File(fileNameBuilder.toString());
		
		boolean isCreated = false;
		if(!file.exists()){
			try {
				isCreated = file.createNewFile();
				Log.d(TAG, "file does not exist, create file "+isCreated);
			} catch (IOException e) {
				e.printStackTrace();
				Log.d(TAG, "IOException - FileWriteEngine : write int[], " +
						"file create failed...");
				Log.d(TAG, "IOException - FileWriteEngine : data name = "
						+fileNameBuilder.toString());
				return;
			}
		}
		
		FileWriter writer = null;
		try {
			 writer = new FileWriter(file, true);
		} catch (IOException e) {
			e.printStackTrace();
			Log.d(TAG, "IOException - FileWriteEngine : write int[],  " 
					+"file cannot be opened for writing.");
			return;
		}
		BufferedWriter w = new BufferedWriter(writer);
		try {
			
			if(isCreated){
				w.write("<div>"+"DeviceName : "+d.getName()+"</div>");
				w.newLine();
				w.write("<div>"+"MacAddress : "+d.getAddress()+"</div>");
			}
			
			w.newLine();
			w.write("<div>"+ResourceUtils.getSystemTime()+"    :");
			w.write("0x"+Integer.toHexString(cmd)+" "
					+(Protocol.getReceiveCommandCNT(cmd))+" 3 ");
			w.write(Arrays.toString(data)+"</div>");
			w.flush();
			Log.d(TAG, "FileWriteEngine : history data write successfully!");
		} catch (IOException e) {
			e.printStackTrace();
			Log.d(TAG, "IOException - FileWriteEngine : write int[],  " 
					+"write to file failed, writer is closed or another "
					+"I/O error occurs..");
		}
		try {
			w.close();
		} catch (IOException e) {
			Log.d(TAG, "IOException - FileWriteEngine : write int[],  " 
					+"write to file failed writer is closed or another "
					+"I/O error occurs..");
		}
		
		
	}
	
	private void writeImpl(BluetoothDevice d, byte[] data){
		String fileName = parseHistoryFileName(d);
		File file = new File(fileName.toString());
		boolean isCreated = false;
		if(!file.exists()){
			try {
				isCreated = file.createNewFile();
				Log.d(TAG, "file does not exist, create file "+isCreated);
			} catch (IOException e) {
				e.printStackTrace();
				Log.d(TAG, "IOException - FileWriteEngine : write byte[], " +
						"file create failed...");
				return;
			}
		}
		
		FileWriter writer = null;
		try {
			 writer = new FileWriter(file);
		} catch (IOException e) {
			e.printStackTrace();
			Log.d(TAG, "IOException - FileWriteEngine : write byte[],  " 
					+"file cannot be opened for writing.");
			return;
		}
		BufferedWriter w = new BufferedWriter(writer);
		try {
			if(isCreated){
				w.write("DeviceName : "+d.getName());
				w.newLine();
				w.write("MacAddress : "+d.getAddress());
			}
			
			w.newLine();
			w.write(ResourceUtils.getSystemTime()+"    :");
			w.write(Arrays.toString(data));
			w.flush();
		} catch (IOException e) {
			e.printStackTrace();
			Log.d(TAG, "IOException - FileWriteEngine : write byte[],  " 
					+"write to file failed, writer is closed or another "
					+"I/O error occurs..");
		}
		try {
			w.close();
		} catch (IOException e) {
			Log.d(TAG, "IOException - FileWriteEngine : write byte[],  " 
					+"write to file failed writer is closed or another "
					+"I/O error occurs..");
		}
	}
	
	
	public void writeHistoryData(BluetoothDevice d, int cmd, int[] data){
		Message msg = Message.obtain();
		msg.what = WHAT_WRITE_HISTORY;
		msg.arg1 = cmd;
		Bundle b = new Bundle();
		b.putParcelable(EXTRA_BT_DEVICE, d);
		b.putIntArray(EXTRA_DATA_INT_ARRAY, data);
		msg.setData(b);
		queue.add(msg);
	}
	
	public void writeRawData(BluetoothDevice d, byte[] data){
		Message msg = Message.obtain();
		msg.what = WHAT_WRITE_HISTORY;
		Bundle b = new Bundle();
		b.putParcelable(EXTRA_BT_DEVICE, d);
		b.putByteArray(EXTRA_DATA_BYTE_ARRAY, data);
		msg.setData(b);
		queue.add(msg);
	}

	public static String getHistoryFileName(BluetoothDevice d){
		if(d == null){
			return null;
		}
		String fileName = parseHistoryFileName(d);
		File file = new File(fileName);
		if(!file.exists()){
			return null;			
		}else{
			return fileName;
		}
	}
	
	private static String parseHistoryFileName(BluetoothDevice d){
		StringBuilder fileNameBuilder = new StringBuilder(d.getAddress());
		while(fileNameBuilder.indexOf(":")!= -1){
			fileNameBuilder.deleteCharAt(fileNameBuilder.indexOf(":"));
		}
		fileNameBuilder.append(HistoryAppending);
		fileNameBuilder.insert(0, ResourceUtils.MY_DIR);
		return fileNameBuilder.toString();
	}
	
	//for test
	public static void main(String[] args) {

	}

}
