package com.wm.bleconnect;

import java.util.Arrays;
import java.util.HashMap;

import android.util.Log;


public class Protocol {
	public static final long VersionCode = 1L; 
	private static final String TAG = ResourceUtils.TAG;
	
	private static HashMap<Integer, SendCommand> mSendCommands =  
			new HashMap<Integer, SendCommand>();
	private static HashMap<Integer, ReceiveCommand> mReceiveCommands =  
			new HashMap<Integer, ReceiveCommand>();
	
	static{
		//System time
		CreateSendCommand(0x10, 10, 3, 1);
		CreateReceiveCommand(0x93, 5, 3, 1);
		CreateSendCommand(0x40, 4,  3, 1);
		CreateReceiveCommand(0x40, 10, 3, 1);
		
		//Version code
		CreateSendCommand(0x41, 4, 3, 1);
		CreateReceiveCommand(0x41, 11, 3, 1);
		CreateSendCommand(0x11, 9, 3, 1);
		//CreateReceiveCommand(0x93, 5, 3, 1);
		
		//Exception state
		CreateReceiveCommand(0x42, 6, 3, 2);
		
		//History data
		CreateSendCommand(0x44, 4, 3, 1);
		CreateReceiveCommand(0x44, 6, 3, 1);
		CreateSendCommand(0x45, 6, 3, 1);
		CreateReceiveCommand(0x45, 18, 3, 1);
		CreateSendCommand(0x90, 5, 3, 1);
		CreateSendCommand(0x74, 4, 3, 2);
		CreateSendCommand(0x8E, 4, 3, 1);
		CreateReceiveCommand(0x8E, 8, 3, 1);
		
		//Real-time data
		CreateSendCommand(0x43, 4, 3, 1);
		CreateReceiveCommand(0x43, 16, 3, 1);
		//CreateSendCommand(0x90, 5, 3, 1);
		
		//Internal operation
		CreateSendCommand(0x70, 11, 3, 1);
		CreateSendCommand(0x80, 4, 3, 1);
		CreateReceiveCommand(0x80, 11, 3, 1);
		CreateSendCommand(0x72, 4, 3, 1);
		CreateSendCommand(0x73, 4, 3, 1);
		CreateSendCommand(0x75, 14, 3, 1);
		CreateSendCommand(0x85, 4, 3, 1);
		CreateReceiveCommand(0x85, 14, 3, 1);
		CreateSendCommand(0x76, 14, 3, 1);
		CreateSendCommand(0x86, 4, 3, 1);
		CreateReceiveCommand(0x86, 14, 3, 1);
		CreateSendCommand(0x77, 8, 3, 1);
		CreateSendCommand(0x87, 4, 3, 1);
		CreateReceiveCommand(0x87, 8, 3, 1);
		CreateSendCommand(0x78, 20, 3, 1);
		CreateSendCommand(0x88, 4, 3, 1);
		CreateReceiveCommand(0x88, 20, 3, 1);
		CreateSendCommand(0x79, 20, 3, 1);
		CreateSendCommand(0x89, 4, 3, 1);
		CreateReceiveCommand(0x89, 20, 3, 1);
		CreateSendCommand(0x7A, 20, 3, 1);
		CreateSendCommand(0x8A, 4, 3, 1);
		CreateReceiveCommand(0x8A, 20, 3, 1);
		CreateSendCommand(0x7B, 12, 3, 1);
		CreateSendCommand(0x8B, 4, 3, 1);
		CreateReceiveCommand(0x8B, 12, 3, 1);
		CreateSendCommand(0x7C, 6, 3, 1);
		CreateSendCommand(0x8C, 4, 3, 1);
		CreateReceiveCommand(0x8C, 6, 3, 1);
		CreateSendCommand(0x7D, 5, 3, 1);
		CreateSendCommand(0x8D, 4, 3, 1);
		CreateReceiveCommand(0x8D, 5, 3, 1);
		CreateSendCommand(0x7E, 5, 3, 1);
		CreateSendCommand(0x46, 4, 3, 1);
		CreateReceiveCommand(0x46, 20, 3, 1);
		
		//Common response instruction
		//CreateSendCommand(0x90, 5, 3, 1);
		//CreateReceiveCommand(0x93, 5, 3, 1);
		
		//OAD command
		CreateSendCommand(0xDF, 1, 3, 2);
		CreateReceiveCommand(0xDE, 1, 3, 2);
	}
	
	public Protocol(){
		
	}
	
	public static void main(String[] args) {
		//Protocol p = new Protocol();
		//System.out.println(mReceiveCommands.keySet());
		
	}
	
	
	private static void CreateSendCommand(int CMD, int CNT, int DeviceType, 
			int Priority){
		
		SendCommand cmd = new SendCommand(CMD, CNT, DeviceType,
				Priority);
		mSendCommands.put(CMD, cmd);
		
	}
	
	private static void CreateReceiveCommand(int CMD, int CNT, int DeviceType, 
			int Priority){
		
		ReceiveCommand cmd = new ReceiveCommand(CMD, CNT, DeviceType,
				Priority);
		mReceiveCommands.put(CMD, cmd);
		
	}
	
	public static boolean isSendCMD(int cmd){
		return mSendCommands.containsKey(cmd);
	}
	
	public static boolean isReceiveCMD(int cmd){
		return mReceiveCommands.containsKey(cmd);
	}
	
	public static int getSendCommandCNT(int cmd){
		if(!mSendCommands.containsKey(cmd)){
			return -1;
		}
		
		return mSendCommands.get(cmd).CNT;
	}
	
	public static int getReceiveCommandCNT(int cmd){
		if(!mReceiveCommands.containsKey(cmd)){
			return -1;
		}
		
		return mReceiveCommands.get(cmd).CNT;
	}
	
	/**
	 * Get size of value for given SendCommand  
	 * @param cmd
	 * @return the value size of given command. If no command found,
	 * 		return -1; 
	 */
	public static int getSendCommandValueSize(int cmd){
		if(!mSendCommands.containsKey(cmd)){
			return -1;
		}
		
		return mSendCommands.get(cmd).ValueSize;
	}
	
	/**
	 * Get size of value for given ReceiveCommand  
	 * @param cmd
	 * @return the value size of given command. If no command found,
	 * 		return -1; 
	 */
	public static int getReceiveCommandValueSize(int cmd){
		if(!mReceiveCommands.containsKey(cmd)){
			return -1;
		}
		
		return mReceiveCommands.get(cmd).ValueSize;
	}
	
	public static byte[] parseRealDataToSendData(int cmd, int[] data){
		if(!mSendCommands.containsKey(cmd))
			return null;
		if((data == null)||(data.length == 0))
			return null;
		
		byte[] sendData = null;
		
		switch(cmd){
		case 0x10:
			sendData = new byte[7];
			break;
		case 0x40:
			sendData = new byte[1];
			break;
		case 0x41:
			sendData = new byte[1];
			break;
		case 0x11:
			sendData = new byte[6];
			break;
		case 0x44:
			sendData = new byte[1];
			break;
		case 0x45:
			sendData = new byte[3];//2
			break;
		case 0x90:
			sendData = new byte[2];
			break;
		case 0x74:
			sendData = new byte[1];
			break;
		case 0x8E:
			sendData = new byte[1];
			break;
		case 0x43:
			sendData = new byte[1];
			break;
		case 0x70:
			sendData = new byte[8];//2
			break;
		case 0x80:
			sendData = new byte[1];
			break;
		case 0x72:
			sendData = new byte[1];
			break;
		case 0x73:
			sendData = new byte[1];
			break;
		case 0x75:
			sendData = new byte[11];//9
			break;
		case 0x85:
			sendData = new byte[1];
			break;
		case 0x76:
			sendData = new byte[11];//9
			break;
		case 0x86:
			sendData = new byte[1];
			break;
		case 0x77:
			sendData = new byte[5];//3
			break;
		case 0x87:
			sendData = new byte[1];
			break;
		case 0x78:
			sendData = new byte[17];//9
			break;
		case 0x88:
			sendData = new byte[1];
			break;
		case 0x79:
			sendData = new byte[17];//9
			break;
		case 0x89:
			sendData = new byte[1];
			break;
		case 0x7A:
			sendData = new byte[17];//9
			break;
		case 0x8A:
			sendData = new byte[1];
			break;
		case 0x7B:
			sendData = new byte[9];//5
			break;
		case 0x8B:
			sendData = new byte[1];
			break;
		case 0x7C:
			sendData = new byte[3];
			break;
		case 0x8C:
			sendData = new byte[1];
			break;
		case 0x7D:
			sendData = new byte[2];
			break;
		case 0x8D:
			sendData = new byte[1];
			break;
		case 0x7E:
			sendData = new byte[2];
			break;
		case 0x46:
			sendData = new byte[1];
			break;
		case 0xDF:
			sendData = new byte[1];
			break;
		default :
			break;
		}
		
		try{
			if(sendData != null){
				parseSendDataImpl(cmd, data, sendData);
			}
		}catch(Exception e){
			e.fillInStackTrace();
			e.printStackTrace();
			Log.d(TAG, "Protocol: send data is not in correct format, CMD = 0x"+
					Integer.toHexString(cmd));
			return null;
		}
		return sendData;
		
	}
	
	private static void parseSendDataImpl(int cmd, int[] ori, byte[] toSend){
		//TODO convert int to byte array
		int[] f = SendCommand.getRealDataFormat(cmd);
		if(f.length != ori.length){
			System.out.print("xianzheBLE Protocol: ERROR " +
					"parseSendDataImpl f.length != ori.length");
			return;
		}
		
		int index = 0;
		
		for(int i = 0; i < f.length ; i++){
			Log.d(TAG, "Protocol: parseSendDataImpl : index = "+index+", i = "+i
					+", f.length = "+f.length+", ori.length = "+ori.length
					+", toSend.length = "+toSend.length);
			if(f[i] == 1){
//				try{
					toSend[index] = (byte)ori[i];
					index = index + 1;
					
//				}catch(ArrayIndexOutOfBoundsException e){
//					e.fillInStackTrace();
//					e.printStackTrace();
//					Log.d(TAG, "Protocol: parseSendDataImpl :" +
//							" ArrayIndexOutOfBoundsException 1");
//				}
			}else{//f[i]>1
				byte[] tmp = new byte[f[i]];
				
				// int2array
				for(int j=(f[i]-1); j>=0; j--){
					tmp[j] = (byte)( (ori[i] >> ( 8 * (f[i]-j-1) ) ) & 0xFF);
				}
/*				for(int j=0; j < f[i]; j++){
					tmp[j] = (byte)( (ori[i] >> (8*j)) & 0xFF);
				}*/
				
				
				
				for(int n = index; n < index+f[i]; n++){
//					try{
						toSend[n] = tmp[n-index];
						
//					}catch(ArrayIndexOutOfBoundsException e){
//						e.fillInStackTrace();
//						e.printStackTrace();
//						Log.d(TAG, "Protocol: parseSendDataImpl :" +
//								" ArrayIndexOutOfBoundsException 2");
//					}
				}
				index = index + f[i];
			}
			
		}
		//to calculate checksum 
		int checksum = 0;
		for(int j= 0;j<toSend.length-1;j++){
			checksum = (checksum & 0xFF) + (toSend[j] & 0xFF);
		}
		checksum = (checksum & 0xFF) +(cmd &0xFF );
		checksum = (checksum & 0xFF) +((toSend.length+3) &0xFF);
		checksum = (checksum & 0xFF) +3;
		toSend[toSend.length - 1] = (byte)checksum;
		
		
		
	}
	
	
	/**
	 * 
	 * @param cmd
	 * @param data
	 * @return
	 */
	public static int[] parseReceivedDataToRealData(int cmd, byte[] data){
		if(!mReceiveCommands.containsKey(cmd))
			return null;
		if((data == null)||(data.length == 0))
			return null;
		
		int[] realData = null;
		switch(cmd){
		case 0x93:
			realData = new int[2];
			break;
		case 0x40:
			realData = new int[7];
			break;
		case 0x41:
			realData = new int[8];
			break;
		case 0x42:
			realData = new int[3];
			break;
		case 0x44:
			realData = new int[2];
			break;
		case 0x45:
			realData = new int[12];

			break;
		case 0x8E:
			realData = new int[3];

			break;
		case 0x43:
			realData = new int[9];

			break;
		case 0x80:
			realData = new int[8];

			break;
		case 0x85:
			realData = new int[9];

			break;
		case 0x86:
			realData = new int[9];

			break;
		case 0x87:
			realData = new int[3];

			break;
		case 0x88:
			realData = new int[9];

			break;
		case 0x89:
			realData = new int[9];

			break;
		case 0x8A:
			realData = new int[9];

			break;
		case 0x8B:
			realData = new int[5];

			break;
		case 0x8C:
			realData = new int[3];

			break;
		case 0x8D:
			realData = new int[2];

			break;
		case 0x46:
			realData = new int[8];

			break;
		case 0xDE:
			realData = new int[1];

			break;
		default :
			break;
		}
		
		if(realData != null){
			parseRealDataImpl(cmd, data, realData);
		}
		return realData;
	}
	
	private static void parseRealDataImpl(int cmd, byte[] ori, int[] real){
		int[] f = ReceiveCommand.getRealDataFormat(cmd);
		if(f.length != real.length){
			System.out.print("xianzheBLE Protocol: ERROR " +
					"parseRealDataImpl f.length != real.length");
			return;
		}
		//you can judge value size
		int index = 0;
		int i = 0;
		try{
			for(i = 0; i<f.length; i++){
				if(f[i] == 1){
					real[i] = (int)(ori[index] & 0xFF);
					index = index + 1;
				}else{// i>1
					//out of bound?
					//include start byte, exclude end byte
					byte[] tmp = Arrays.copyOfRange(ori, index, index+f[i]);
					real[i] = byteArray2Int(tmp);
					index = index+f[i]; 
				}
			}
		}catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
			System.out.print("xianzheBLE Protocol: ArrayIndexOutOfBoundsException "
					+ "parseRealDataImpl: cmd = 0x"+Integer.toHexString(cmd)
					+", ori.length = "+ori.length
					+", real.length = "+real.length
					+", f.length = "+f.length
					+", index = "+index 
					+", i = "+i);
			Log.d(TAG,"xianzheBLE Protocol: ArrayIndexOutOfBoundsException "
					+ "parseRealDataImpl: cmd = 0x"+Integer.toHexString(cmd)
					+", ori.length = "+ori.length
					+", real.length = "+real.length
					+", f.length = "+f.length
					+", index = "+index
					+", i = "+i);
			
		}
		
	
	}
	
	//TODO xianzhe : big-endian? little-endian?
/*	private static int byteArray2Int(byte[] ori){
		if(ori == null)
			return -1;
		int num = 0;
		byte tmpB;
		for(int i=(ori.length-1); i>=0; i--){
			tmpB = ori[i];
			num += (tmpB & 0xFF)<<(8*i);
		}
		return num;
	}*/
	
	private static int byteArray2Int(byte[] ori){
		if(ori == null)
			return -1;
		int num = 0;
		byte tmpB;
		for(int i=0; i<ori.length; i++){
			tmpB = ori[i];
			num = (num << 8)+(tmpB & 0xFF);
			//Log.d(TAG, "byteArray2Int : num = "+num+", i = "+i);
		}
		return num;
	}
	
}











	

abstract class Command{
	public final int CMD;
	public final int CNT;
	public final int DeviceType;
	public final int Priority;
	public final int ValueSize;
	public int RealDataSize = 0;
	
	public Command(int CMD, int CNT, int DeviceType,  
		int Priority){
		
		this.CMD = CMD;
		this.CNT = CNT;
		this.DeviceType = DeviceType;
		this.Priority = Priority;
		this.ValueSize = parseValueSize(CNT);
	}
	
	private int parseValueSize(int cnt){
		int size = 0;
		switch(cnt){
		case 10:
			size = 7;
			break;
		case 5:
			size = 2;
			break;
		case 4:
			size = 1;
			break;
		case 11:
			size = 8;
			break;
		case 9:
			size = 6;
			break;
		case 6:
			size = 3;
			break;
		case 18:
			size = 15;
			break;
		case 8:
			size = 5;
			break;
		case 16:
			size = 13;
			break;
		case 14:
			size = 11;
			break;
		case 20:
			size = 17;
			break;
		case 12:
			size = 9;
			break;
		case 1:
			size = 1;
			break;
		}
		return size;
	}
	


}













class SendCommand extends Command{
	private static int size = -1;
	private static int[] format = null; 
	
	public SendCommand(int CMD, int CNT,  int DeviceType, 
			int Priority){
		super(CMD, CNT, DeviceType, Priority);
		super.RealDataSize = getRealDataSize();
	}
	
	public int getRealDataSize(){
		return getRealDataSize(this.CMD);
	}
	
	public static int getRealDataSize(int cmd){
		query(cmd);
		return size;
	}
	
	public int[] getRealDataFormat(){;
	 	return getRealDataFormat(this.CMD);
	}
	
	public static int[] getRealDataFormat(int cmd){
		query(cmd);
		return format;
	}
	
	private static void query(int cmd){
		switch(cmd){
		case 0x10:
			size = 7;
			format = new int[7];
			ReceiveCommand.initIntArrayWithAll1(format);
			break;
		case 0x40:
			size = 1;
			format = new int[1];
			ReceiveCommand.initIntArrayWithAll1(format);
			break;
		case 0x41:
			size = 1;
			format = new int[1];
			ReceiveCommand.initIntArrayWithAll1(format);
			break;
		case 0x11:
			size = 6;
			format = new int[6];
			ReceiveCommand.initIntArrayWithAll1(format);
			break;
		case 0x44:
			size = 1;
			format = new int[1];
			ReceiveCommand.initIntArrayWithAll1(format);
			break;
		case 0x45:
			size = 2;//2,1
			format = new int[]{2,1};
	
			break;
		case 0x90:
			size = 2;
			format = new int[2];
			ReceiveCommand.initIntArrayWithAll1(format);
			break;
		case 0x74:
			size = 1;
			format = new int[1];
			ReceiveCommand.initIntArrayWithAll1(format);
			break;
		case 0x8E:
			size = 1;
			format = new int[1];
			ReceiveCommand.initIntArrayWithAll1(format);
			break;
		case 0x43:
			size = 1;
			format = new int[1];
			ReceiveCommand.initIntArrayWithAll1(format);
			break;
		case 0x70:
			size = 8;
			format = new int[8];
			ReceiveCommand.initIntArrayWithAll1(format);
			break;
		case 0x80:
			size = 1;
			format = new int[1];
			ReceiveCommand.initIntArrayWithAll1(format);
			break;
		case 0x72:
			size = 1;
			format = new int[1];
			ReceiveCommand.initIntArrayWithAll1(format);
			break;
		case 0x73:
			size = 1;
			format = new int[1];
			ReceiveCommand.initIntArrayWithAll1(format);
			break;
		case 0x75:
			size = 9;//2,1,1,1,2,1,1,1,1
			format = new int[]{2,1,1,1,2,1,1,1,1};
	
			break;
		case 0x85:
			size = 1;
			format = new int[1];
			ReceiveCommand.initIntArrayWithAll1(format);
			break;
		case 0x76:
			size = 9;//2,1,1,1,2,1,1,1,1
			format = new int[]{2,1,1,1,2,1,1,1,1};
	
			break;
		case 0x86:
			size = 1;
			format = new int[1];
			ReceiveCommand.initIntArrayWithAll1(format);
			break;
		case 0x77:
			size = 3;//2,2,1
			format = new int[]{2,2,1};
	
			break;
		case 0x87:
			size = 1;
			format = new int[1];
			ReceiveCommand.initIntArrayWithAll1(format);
			break;
		case 0x78:
			size = 9;//2,2,2,2,2,2,2,2,1
			format = new int[]{2,2,2,2,2,2,2,2,1};
	
			break;
		case 0x88:
			size = 1;
			format = new int[1];
			ReceiveCommand.initIntArrayWithAll1(format);
			break;
		case 0x79:
			size = 9;//2,2,2,2,2,2,2,2,1
			format = new int[]{2,2,2,2,2,2,2,2,1};
	
			break;
		case 0x89:
			size = 1;
			format = new int[1];
			ReceiveCommand.initIntArrayWithAll1(format);
			break;
		case 0x7A:
			size = 9;//2,2,2,2,2,2,2,2,1
			format = new int[]{2,2,2,2,2,2,2,2,1};
	
			break;
		case 0x8A:
			size = 1;
			format = new int[1];
			ReceiveCommand.initIntArrayWithAll1(format);
			break;
		case 0x7B:
			size = 5;//2,2,2,2,1
			format = new int[]{2,2,2,2,1};
	
			break;
		case 0x8B:
			size = 1;
			format = new int[1];
			ReceiveCommand.initIntArrayWithAll1(format);
			break;
		case 0x7C:
			size = 3;
			format = new int[3];
			ReceiveCommand.initIntArrayWithAll1(format);
			break;
		case 0x8C:
			size = 1;
			format = new int[1];
			ReceiveCommand.initIntArrayWithAll1(format);
			break;
		case 0x7D:
			size = 2;
			format = new int[2];
			ReceiveCommand.initIntArrayWithAll1(format);
			break;
		case 0x8D:
			size = 1;
			format = new int[1];
			ReceiveCommand.initIntArrayWithAll1(format);
			break;
		case 0x7E:
			size = 2;
			format = new int[2];
			ReceiveCommand.initIntArrayWithAll1(format);
			break;
		case 0x46:
			size = 1;
			format = new int[1];
			ReceiveCommand.initIntArrayWithAll1(format);
			break;
		case 0xDF:
			size = 1;
			format = new int[1];
			ReceiveCommand.initIntArrayWithAll1(format);
			break;
		default :
			size = -1;
			format = null;
			break;
		}
	}
	
}
















class ReceiveCommand extends Command{
	private static int size = -1;
	private static int[] format = null;
	
	
	public ReceiveCommand(int CMD, int CNT, int DeviceType, 
			int Priority){
		super(CMD, CNT, DeviceType, Priority);
		super.RealDataSize = getRealDataSize();
	}
	public int getRealDataSize(){
		query(this.CMD);
	 	return size;
	}
	
	public static int getRealDataSize(int cmd){
		query(cmd);
		return size;
	}
	
	public int[] getRealDataFormat(){
		query(this.CMD);
	 	return format;
	}
	
	public static int[] getRealDataFormat(int cmd){
		query(cmd);
		return format;
	}
	
	private static void query(int cmd){
		
		switch(cmd){
		case 0x93:
			size = 2;
			format = new int[size];
			initIntArrayWithAll1(format);
			break;
		case 0x40:
			size = 7;
			format = new int[size];
			initIntArrayWithAll1(format);
			break;
		case 0x41:
			size = 8;
			format = new int[size];
			initIntArrayWithAll1(format);
			break;
		case 0x42:
			size = 3;
			format = new int[size];
			initIntArrayWithAll1(format);
			break;
		case 0x44:
			size = 2;//2,1
			format = new int[]{2,1};
			break;
		case 0x45:
			size = 12;//1,1,1,1,2,1,2,1,1,2,1,1
			format = new int[]{1,1,1,1,2,1,2,1,1,2,1,1};
			break;
		case 0x8E:
			size = 3;//2,2,1
			format = new int[]{2,2,1};
			break;
		case 0x43:
			size = 9;//2,1,2,1,1,2,1,2,1
			format = new int[]{2,1,2,1,1,2,1,2,1};
			break;
		case 0x80:
			size = 8;
			format = new int[size];
			initIntArrayWithAll1(format);
			break;
		case 0x85:
			size = 9;//2,1,1,1,2,1,1,1,1
			format = new int[]{2,1,1,1,2,1,1,1,1};
			break;
		case 0x86:
			size = 9;//2,1,1,1,2,1,1,1,1
			format = new int[]{2,1,1,1,2,1,1,1,1};
			break;
		case 0x87:
			size = 3;//2,2,1
			format = new int[]{2,2,1};
			break;
		case 0x88:
			size = 9;//2,2,2,2,2,2,2,2,1
			format = new int[]{2,2,2,2,2,2,2,2,1};
			break;
		case 0x89:
			size = 9;//2,2,2,2,2,2,2,2,1
			format = new int[]{2,2,2,2,2,2,2,2,1};
			break;
		case 0x8A:
			size = 9;//2,2,2,2,2,2,2,2,1
			format = new int[]{2,2,2,2,2,2,2,2,1};
			break;
		case 0x8B:
			size = 5;//2,2,2,2,1
			format = new int[]{2,2,2,2,1};
			break;
		case 0x8C:
			size = 3;
			format = new int[size];
			initIntArrayWithAll1(format);
			break;
		case 0x8D:
			size = 2;
			format = new int[size];
			initIntArrayWithAll1(format);
			break;
		case 0x46:
			size = 8;//2,2,2,2,4,2,2,1
			format = new int[]{2,2,2,2,4,2,2,1};
			break;
		case 0xDE:
			size = 1;
			format = new int[size];
			initIntArrayWithAll1(format);
			break;
		default :
			size = -1;
			format = null;
			break;

		}
	
	}
	
	static void initIntArrayWithAll1(int[] ori){
		for(int i=0; i<ori.length; i++){
			ori[i] = 1;
		}
	}
	
	
}



