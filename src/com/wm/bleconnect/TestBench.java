package com.wm.bleconnect;

import java.util.Arrays;
import java.util.Set;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

public class TestBench {
	private static final String TAG = ResourceUtils.TAG;
	private static ConnectionService sInstance;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static void test(){
		TestBenchThread testThread = new TestBenchThread();
		testThread.start();
	}
	
	public static class TestBenchThread extends Thread{

		@Override
		public void run() {
			super.run();
			Log.e(TAG, "TestBench : test() ");
			sInstance = ConnectionService.getInstance();
			Log.e(TAG, "TestBench : getConnectedWMDevices() ");
			Set<BluetoothDevice> deviceSet = sInstance.getConnectedWMDevices();
			Log.e(TAG, "TestBench : test() deviceSet size = "+deviceSet.size());
			for(BluetoothDevice d : deviceSet){
				ConnectThread t = sInstance.getConnectThreadByDevice(d);
				Log.e(TAG, "TestBench : test() preparing data");
				byte[] data = null;
				int checksum = 0;
				boolean testAll = true;
				
				if(testAll)
				{
				checksum = 0;
				data = new byte[]{(byte)0x93, (byte)5, 3, (byte)17, 0};
				for(int i =0; i<(data.length-1); i++){
					checksum = checksum + (data[i]&0xFF);
				}
				data[data.length-1] = (byte)(checksum-3);
				Log.d(TAG, "TestBench: SEND DATA : CMD = 0x93, checksum = "+(data[data.length-1]&0xFF));
				Log.d(TAG, "TestBench : SEND DATA : "+Arrays.toString(data));
				t._sendCMDTestAsBLEClient(data);

				checksum = 0;
				data = new byte[]{(byte)0x40, (byte)10, 3, (byte)15, (byte)5, (byte)25, (byte)21, (byte)41, (byte)22, 0};
				for(int i =0; i<(data.length-1); i++){
					checksum = checksum + (data[i]&0xFF);
				}
				data[data.length-1] = (byte)(checksum-3);
				Log.d(TAG, "TestBench: SEND DATA : CMD = 0x40, checksum = "+(data[data.length-1]&0xFF));
				Log.d(TAG, "TestBench : SEND DATA : "+Arrays.toString(data));
				t._sendCMDTestAsBLEClient(data);

				checksum = 0;
				data = new byte[]{(byte)0x41, (byte)11, 3, (byte)2, (byte)48, (byte)55, (byte)29, (byte)66, (byte)68, (byte)77, 0};
				for(int i =0; i<(data.length-1); i++){
					checksum = checksum + (data[i]&0xFF);
				}
				data[data.length-1] = (byte)(checksum-3);
				Log.d(TAG, "TestBench: SEND DATA : CMD = 0x41, checksum = "+(data[data.length-1]&0xFF));
				Log.d(TAG, "TestBench : SEND DATA : "+Arrays.toString(data));
				t._sendCMDTestAsBLEClient(data);

				checksum = 0;
				data = new byte[]{(byte)0x42, (byte)6, 3, (byte)47, (byte)87, 0};
				for(int i =0; i<(data.length-1); i++){
					checksum = checksum + (data[i]&0xFF);
				}
				data[data.length-1] = (byte)(checksum-3);
				Log.d(TAG, "TestBench: SEND DATA : CMD = 0x42, checksum = "+(data[data.length-1]&0xFF));
				Log.d(TAG, "TestBench : SEND DATA : "+Arrays.toString(data));
				t._sendCMDTestAsBLEClient(data);

				checksum = 0;
				data = new byte[]{(byte)0x44, (byte)6, 3, (byte)22, (byte)6, 0};
				for(int i =0; i<(data.length-1); i++){
					checksum = checksum + (data[i]&0xFF);
				}
				data[data.length-1] = (byte)(checksum-3);
				Log.d(TAG, "TestBench: SEND DATA : CMD = 0x44, checksum = "+(data[data.length-1]&0xFF));
				Log.d(TAG, "TestBench : SEND DATA : "+Arrays.toString(data));
				t._sendCMDTestAsBLEClient(data);

				checksum = 0;
				data = new byte[]{(byte)0x45, (byte)18, 3, (byte)41, (byte)94, (byte)31, (byte)13, (byte)93, (byte)46, (byte)16, (byte)52, (byte)72, (byte)61, (byte)53, (byte)70, (byte)35, (byte)21, 0};
				for(int i =0; i<(data.length-1); i++){
					checksum = checksum + (data[i]&0xFF);
				}
				data[data.length-1] = (byte)(checksum-3);
				Log.d(TAG, "TestBench: SEND DATA : CMD = 0x45, checksum = "+(data[data.length-1]&0xFF));
				Log.d(TAG, "TestBench : SEND DATA : "+Arrays.toString(data));
				t._sendCMDTestAsBLEClient(data);

				checksum = 0;
				data = new byte[]{(byte)0x8E, (byte)8, 3, (byte)22, (byte)34, (byte)65, (byte)73, 0};
				for(int i =0; i<(data.length-1); i++){
					checksum = checksum + (data[i]&0xFF);
				}
				data[data.length-1] = (byte)(checksum-3);
				Log.d(TAG, "TestBench: SEND DATA : CMD = 0x8e, checksum = "+(data[data.length-1]&0xFF));
				Log.d(TAG, "TestBench : SEND DATA : "+Arrays.toString(data));
				t._sendCMDTestAsBLEClient(data);

				checksum = 0;
				data = new byte[]{(byte)0x43, (byte)16, 3, (byte)47, (byte)98, (byte)52, (byte)56, (byte)9, (byte)15, (byte)96, (byte)44, (byte)67, (byte)45, (byte)(1+(int)(Math.random()*100)), (byte)55, 0};
				for(int i =0; i<(data.length-1); i++){
					checksum = checksum + (data[i]&0xFF);
				}
				data[data.length-1] = (byte)(checksum-3);
				Log.d(TAG, "TestBench: SEND DATA : CMD = 0x43, checksum = "+(data[data.length-1]&0xFF));
				Log.d(TAG, "TestBench : SEND DATA : "+Arrays.toString(data));
				t._sendCMDTestAsBLEClient(data);

				checksum = 0;
				data = new byte[]{(byte)0x80, (byte)11, 3, (byte)64, (byte)34, (byte)38, (byte)33, (byte)63, (byte)17, (byte)50, 0};
				for(int i =0; i<(data.length-1); i++){
					checksum = checksum + (data[i]&0xFF);
				}
				data[data.length-1] = (byte)(checksum-3);
				Log.d(TAG, "TestBench: SEND DATA : CMD = 0x80, checksum = "+(data[data.length-1]&0xFF));
				Log.d(TAG, "TestBench : SEND DATA : "+Arrays.toString(data));
				t._sendCMDTestAsBLEClient(data);

				checksum = 0;
				data = new byte[]{(byte)0x85, (byte)14, 3, (byte)59, (byte)41, (byte)72, (byte)23, (byte)53, (byte)52, (byte)19, (byte)16, (byte)93, (byte)5, 0};
				for(int i =0; i<(data.length-1); i++){
					checksum = checksum + (data[i]&0xFF);
				}
				data[data.length-1] = (byte)(checksum-3);
				Log.d(TAG, "TestBench: SEND DATA : CMD = 0x85, checksum = "+(data[data.length-1]&0xFF));
				Log.d(TAG, "TestBench : SEND DATA : "+Arrays.toString(data));
				t._sendCMDTestAsBLEClient(data);

				checksum = 0;
				data = new byte[]{(byte)0x86, (byte)14, 3, (byte)22, (byte)98, (byte)58, (byte)14, (byte)70, (byte)44, (byte)22, (byte)20, (byte)13, (byte)91, 0};
				for(int i =0; i<(data.length-1); i++){
					checksum = checksum + (data[i]&0xFF);
				}
				data[data.length-1] = (byte)(checksum-3);
				Log.d(TAG, "TestBench: SEND DATA : CMD = 0x86, checksum = "+(data[data.length-1]&0xFF));
				Log.d(TAG, "TestBench : SEND DATA : "+Arrays.toString(data));
				t._sendCMDTestAsBLEClient(data);

				checksum = 0;
				data = new byte[]{(byte)0x87, (byte)8, 3, (byte)22, (byte)62, (byte)70, (byte)19, 0};
				for(int i =0; i<(data.length-1); i++){
					checksum = checksum + (data[i]&0xFF);
				}
				data[data.length-1] = (byte)(checksum-3);
				Log.d(TAG, "TestBench: SEND DATA : CMD = 0x87, checksum = "+(data[data.length-1]&0xFF));
				Log.d(TAG, "TestBench : SEND DATA : "+Arrays.toString(data));
				t._sendCMDTestAsBLEClient(data);

				checksum = 0;
				data = new byte[]{(byte)0x88, (byte)20, 3, (byte)5, (byte)16, (byte)40, (byte)16, (byte)41, (byte)58, (byte)68, (byte)35, (byte)48, (byte)54, (byte)94, (byte)1, (byte)44, (byte)67, (byte)51, (byte)93, 0};
				for(int i =0; i<(data.length-1); i++){
					checksum = checksum + (data[i]&0xFF);
				}
				data[data.length-1] = (byte)(checksum-3);
				Log.d(TAG, "TestBench: SEND DATA : CMD = 0x88, checksum = "+(data[data.length-1]&0xFF));
				Log.d(TAG, "TestBench : SEND DATA : "+Arrays.toString(data));
				t._sendCMDTestAsBLEClient(data);

				checksum = 0;
				data = new byte[]{(byte)0x89, (byte)20, 3, (byte)87, (byte)47, (byte)91, (byte)1, (byte)91, (byte)73, (byte)59, (byte)71, (byte)59, (byte)38, (byte)56, (byte)42, (byte)78, (byte)5, (byte)96, (byte)39, 0};
				for(int i =0; i<(data.length-1); i++){
					checksum = checksum + (data[i]&0xFF);
				}
				data[data.length-1] = (byte)(checksum-3);
				Log.d(TAG, "TestBench: SEND DATA : CMD = 0x89, checksum = "+(data[data.length-1]&0xFF));
				Log.d(TAG, "TestBench : SEND DATA : "+Arrays.toString(data));
				t._sendCMDTestAsBLEClient(data);

				checksum = 0;
				data = new byte[]{(byte)0x8A, (byte)20, 3, (byte)33, (byte)9, (byte)6, (byte)3, (byte)6, (byte)33, (byte)75, (byte)14, (byte)18, (byte)94, (byte)21, (byte)33, (byte)54, (byte)45, (byte)55, (byte)95, 0};
				for(int i =0; i<(data.length-1); i++){
					checksum = checksum + (data[i]&0xFF);
				}
				data[data.length-1] = (byte)(checksum-3);
				Log.d(TAG, "TestBench: SEND DATA : CMD = 0x8a, checksum = "+(data[data.length-1]&0xFF));
				Log.d(TAG, "TestBench : SEND DATA : "+Arrays.toString(data));
				t._sendCMDTestAsBLEClient(data);

				checksum = 0;
				data = new byte[]{(byte)0x8B, (byte)12, 3, (byte)69, (byte)24, (byte)25, (byte)63, (byte)78, (byte)13, (byte)19, (byte)16, 0};
				for(int i =0; i<(data.length-1); i++){
					checksum = checksum + (data[i]&0xFF);
				}
				data[data.length-1] = (byte)(checksum-3);
				Log.d(TAG, "TestBench: SEND DATA : CMD = 0x8b, checksum = "+(data[data.length-1]&0xFF));
				Log.d(TAG, "TestBench : SEND DATA : "+Arrays.toString(data));
				t._sendCMDTestAsBLEClient(data);

				checksum = 0;
				data = new byte[]{(byte)0x8C, (byte)6, 3, (byte)43, (byte)99, 0};
				for(int i =0; i<(data.length-1); i++){
					checksum = checksum + (data[i]&0xFF);
				}
				data[data.length-1] = (byte)(checksum-3);
				Log.d(TAG, "TestBench: SEND DATA : CMD = 0x8c, checksum = "+(data[data.length-1]&0xFF));
				Log.d(TAG, "TestBench : SEND DATA : "+Arrays.toString(data));
				t._sendCMDTestAsBLEClient(data);

				checksum = 0;
				data = new byte[]{(byte)0x8D, (byte)5, 3, (byte)100, 0};
				for(int i =0; i<(data.length-1); i++){
					checksum = checksum + (data[i]&0xFF);
				}
				data[data.length-1] = (byte)(checksum-3);
				Log.d(TAG, "TestBench: SEND DATA : CMD = 0x8d, checksum = "+(data[data.length-1]&0xFF));
				Log.d(TAG, "TestBench : SEND DATA : "+Arrays.toString(data));
				t._sendCMDTestAsBLEClient(data);

				checksum = 0;
				data = new byte[]{(byte)0x46, (byte)20, 3, (byte)46, (byte)36, (byte)100, (byte)16, (byte)9, (byte)5, (byte)96, (byte)7, (byte)62, (byte)77, (byte)77, (byte)78, (byte)61, (byte)57, (byte)49, (byte)82, 0};
				for(int i =0; i<(data.length-1); i++){
					checksum = checksum + (data[i]&0xFF);
				}
				data[data.length-1] = (byte)(checksum-3);
				Log.d(TAG, "TestBench: SEND DATA : CMD = 0x46, checksum = "+(data[data.length-1]&0xFF));
				Log.d(TAG, "TestBench : SEND DATA : "+Arrays.toString(data));
				t._sendCMDTestAsBLEClient(data);

				checksum = 0;
				data = new byte[]{(byte)0xDE, (byte)1, 3, 0};
				for(int i =0; i<(data.length-1); i++){
					checksum = checksum + (data[i]&0xFF);
				}
				data[data.length-1] = (byte)(checksum-3);
				Log.d(TAG, "TestBench: SEND DATA : CMD = 0xde, checksum = "+(data[data.length-1]&0xFF));
				Log.d(TAG, "TestBench : SEND DATA : "+Arrays.toString(data));
				t._sendCMDTestAsBLEClient(data);


				}
				else{
					checksum = 0;
					data = new byte[]{(byte)0x80, (byte)11, 3, (byte)64, (byte)34, (byte)38, (byte)33, (byte)63, (byte)17, (byte)50, 0};
					for(int i =0; i<(data.length-1); i++){
						checksum = checksum + (data[i]&0xFF);
					}
					data[data.length-1] = (byte)(checksum-3);
					Log.d(TAG, "TestBench: SEND DATA : CMD = 0x80, checksum = "+(data[data.length-1]&0xFF));
					Log.d(TAG, "TestBench : SEND DATA : "+Arrays.toString(data));
					t._sendCMDTestAsBLEClient(data);
				}






				
				

				
				
			}
		
		}
		
	}
	

}
