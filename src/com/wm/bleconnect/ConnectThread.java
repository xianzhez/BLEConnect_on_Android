package com.wm.bleconnect;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Arrays;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ConnectThread extends Thread {
	private static final String TAG = ResourceUtils.TAG;
	
	private BluetoothDevice mDevice;
	private BluetoothSocket mSocket;
	private Handler mHandler;
	private ConnectionService mService;
	
	private InputStream ins;
	private OutputStream ous;
	
	private boolean isRunning = false;
	
	
	public ConnectThread(BluetoothDevice device, BluetoothSocket socket, 
			Handler handler, ConnectionService service) throws IOException {
		setDevice(device);
		setSocket(socket);
		this.mHandler = handler;
		this.mService = service;
		init();
	}
	
	public ConnectThread(BluetoothDevice device, BluetoothSocket socket, 
			InputStream ins, OutputStream ous,
			Handler handler, ConnectionService service) throws IOException {
		setDevice(device);
		setSocket(socket);
		this.ins = ins;
		this.ous = ous;
		this.mHandler = handler;
		this.mService = service;
		//init();
	}
	
	
	
	public BluetoothDevice getDevice() {
		return mDevice;
	}


	private void setDevice(BluetoothDevice mDevice) {
		this.mDevice = mDevice;
	}


	public BluetoothSocket getSocket() {
		return mSocket;
	}

	private void setSocket(BluetoothSocket mSocket) {
		this.mSocket = mSocket;
	}
	
	private void init() throws IOException{
		try {
			mSocket.connect();
			ous = mSocket.getOutputStream();
			ins = mSocket.getInputStream();
		} catch (IOException e) {
			e.fillInStackTrace();
			Log.e(TAG, "ConnectThread : 1 IOException connect to "+mDevice.getName()
					+" failed.");
			throw e;
		}
	}



	@Override
	public void run() {
		super.run();
		isRunning = true;
		
		
		//TODO receive data
		
		byte[] Buf = new byte[1];//size to be decided
		int cmd;
		int cnt;
		int dt;
		byte[] dataBuf = null;
		int checksum;
		
		while(isRunning){
			Log.d(TAG, "ConnectThread : "+mDevice.getName()+" receive thread is running");
			try {
				ins.read(Buf);
				cmd = (int) (Buf[0]&0xFF);
				Log.d(TAG, "ConnectThread : "+mDevice.getName()+" receive cmd = 0x" + Integer.toHexString(cmd));
				ins.read(Buf);//read CNT
				cnt = (int)(Buf[0]&0xFF);
				if(cnt != (Protocol.getReceiveCommandCNT(cmd))){
					Log.d(TAG, "ConnectThread : receive WRONG CNT. " +
							Protocol.getReceiveCommandCNT(cmd));
					continue;
				}
				ins.read(Buf);//read DeviceType
				dt = (int)(Buf[0]&0xFF);
				if(dt != 3){
					Log.d(TAG, "ConnectThread : "+mDevice.getName()+" receive WRONG DeviceType.");
					continue;
				}
				dataBuf = prepareDataBuffer(cmd);
				if(dataBuf == null){
					continue;
				}
				ins.read(dataBuf);
				checksum = cmd + cnt;//exclude dt !
				if(dataBuf.length == 1){
					if((((byte)checksum) &0xFF)==(dataBuf[0]&0xFF)){
						Log.d(TAG, "ConnectThread : 1 CHECKSUM TRUE!");
					}else{
						Log.d(TAG, "ConnectThread : 1 CHECKSUM FLASE........!!!");
						Log.d(TAG, "ConnectThread : 1 CHECKSUM checksum = "
								+(((byte)checksum) &0xFF));
						Log.d(TAG, "ConnectThread : 1 CHECKSUM ( dataBuf[0]&0xFF) = "
								+(dataBuf[0]&0xFF) );
					}
				}else{
					for(int i = 0; i< (dataBuf.length-1); i++){
						checksum = checksum + (dataBuf[i]&0XFF);
					}
					if((((byte)checksum) &0xFF) ==( dataBuf[dataBuf.length-1] & 0xFF)){
						Log.d(TAG, "ConnectThread : 2 CHECKSUM TRUE!");
					}else{
						Log.d(TAG, "ConnectThread : 2 CHECKSUM FLASE............!!!");
						Log.d(TAG, "ConnectThread : 2 CHECKSUM checksum = "
								+(((byte)checksum) &0xFF));
						Log.d(TAG, "ConnectThread : 2 CHECKSUM ( dataBuf[dataBuf.length-1] & 0xFF) = "
								+( dataBuf[dataBuf.length-1] & 0xFF));
					}
				}
				handleReceivedCommand(cmd, dataBuf);
				
				
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "ConnectThread : "+mDevice.getName()+" 1 IOException receive from "
				+mDevice.getName() + " failed.");
				isRunning = false;
				return;
			}
		}
		
		
	}
	
	private void handleReceivedCommand(int cmd, byte[] dataBuf) {
		
		int[] realData = Protocol.parseReceivedDataToRealData( cmd, dataBuf);
		
		//String dataStr = new String(dataBuf);
		Log.d(TAG, "ConnectThread : receive data with cmd : [0x"
				+Integer.toHexString(cmd) +"] "+ Arrays.toString(realData));
		
		Message msg = Message.obtain();
		msg.what = ConnectionService.H.FLAG_CMD_RECEIVED;
		msg.arg1 = cmd;
		
		Bundle data = new Bundle();
		data.putParcelable(ResourceUtils.DATA_BTDEVICE, mDevice);
		data.putInt(ResourceUtils.DATA_RECEIVED_CMD, cmd);
		data.putIntArray(ResourceUtils.DATA_RECEIVED_DATA, realData);
		
		msg.setData(data);
		mHandler.handleMessage(msg);
	}

	//
	public boolean sendCMD(int cmd, int[] data){
		if((cmd > 255)||(cmd < 0)){
			return false;
		}
		
		byte cmdB = (byte)cmd;
		byte[] sendData = Protocol.parseRealDataToSendData(cmd, data);
		if(sendData == null){
			Log.e(TAG, "ConnectThread : Protocol ERROR send to "
					+mDevice.getName() + " failed, data is not in correct format.");
			return false;
		}
		try {
			Log.e(TAG, "ConnectThread : "+mDevice.getName()+" sending to "+mDevice.getName());
			ous.write(cmdB);
			ous.write(sendData.length+3);
			ous.write(3);
			ous.write(sendData);
			ous.flush();
			Log.e(TAG, "ConnectThread : send to "+mDevice.getName()+" successfully!");
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "ConnectThread : "+mDevice.getName()+" IOException send to "
					+mDevice.getName() + " failed.");
			return false;
		}
		
		return true;
	}
	
	private byte[] prepareDataBuffer(int cmd){
		//TODO decide buffer size according to protocol
		// if cmd is not an CMD???
		int size = Protocol.getReceiveCommandValueSize(cmd);
		if(size == -1){
			Log.e(TAG, "ConnectThread : data received is not a CMD !!!");
			return null;
		}else{
			return new byte[size];			
		}
	}
	
	
	public boolean _sendCMDTestAsBLEClient(byte[] data){
		Log.e(TAG, "ConnectThread : _sendCMDTestAsBLEClient sending !!!");
		try {
			ous.write(data);
			ous.flush();
			Log.e(TAG, "ConnectThread : _sendCMDTestAsBLEClient send SUCCESSFULLY !!!");
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "ConnectThread : _sendCMDTestAsBLEClient send FAILED !!!");
			return false;
		}
	}
	
	public boolean stopConnect(){
		isRunning = false;
		try {
			ous.close();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "ConnectThread : stopConnect outputstream stop failed !!!");
		}
		try {
			ins.close();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "ConnectThread : stopConnect inputstream stop failed !!!");
		}
		return true;
	}
	
	
}
