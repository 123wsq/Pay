package com.hx.newland.cashin.swing.xdl;

public  class ByteUtils {

	/**
	 * 十六进制 转换 byte[]
	 * 
	 * @param hexStr
	 * @return
	 */
	public static byte[] hexString2ByteArray(String hexStr) {
		if (hexStr == null)
			return null;
		if (hexStr.length() % 2 != 0) {
			return null;
		}
		byte[] data = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			char hc = hexStr.charAt(2 * i);
			char lc = hexStr.charAt(2 * i + 1);
			byte hb = hexChar2Byte(hc);
			byte lb = hexChar2Byte(lc);
			if (hb < 0 || lb < 0) {
				return null;
			}
			int n = hb << 4;
			data[i] = (byte) (n + lb);
		}
		return data;
	}

	public static byte hexChar2Byte(char c) {
		if (c >= '0' && c <= '9')
			return (byte) (c - '0');
		if (c >= 'a' && c <= 'f')
			return (byte) (c - 'a' + 10);
		if (c >= 'A' && c <= 'F')
			return (byte) (c - 'A' + 10);
		return -1;
	}

	/**
	 * byte[] 转 16进制字符串
	 * 
	 * @param arr
	 * @return
	 */
	public static String byteArray2HexString(byte[] arr) {
		StringBuilder sbd = new StringBuilder();
		for (byte b : arr) {
			String tmp = Integer.toHexString(0xFF & b);
			if (tmp.length() < 2)
				tmp = "0" + tmp;
			sbd.append(tmp);
		}
		return sbd.toString();
	}

	public static String byteArray2HexStringWithSpace(byte[] arr) {
		StringBuilder sbd = new StringBuilder();
		for (byte b : arr) {
			String tmp = Integer.toHexString(0xFF & b);
			if (tmp.length() < 2)
				tmp = "0" + tmp;
			sbd.append(tmp);
			sbd.append(" ");
		}
		return sbd.toString();
	}

	static public String getBCDString(byte[] data, int start, int end) {
		byte[] t = new byte[end - start + 1];
		System.arraycopy(data, start, t, 0, t.length);
		return ByteUtils.byteArray2HexString(t);
	}

	static public String getHexString(byte[] data, int start, int end) {
		byte[] t = new byte[end - start + 1];
		System.arraycopy(data, start, t, 0, t.length);
		return ByteUtils.byteArray2HexStringWithSpace(t);
	}
	
	
	static public String byte2string(byte[] data){
		StringBuffer result = new StringBuffer();
		for (byte b : data) {
			String s = String.format("%,", b);
			result.append(s);
		}
		return result.toString();
	}
	
	
	public static byte[] process(byte[] pin, byte[] accno) {
//	    byte arrAccno[] = getHAccno(accno);
//	    byte arrPin[] = getHPin(pin);
	    byte arrRet[] = new byte[8];
	    //PIN BLOCK 格式等于 PIN 按位异或 主帐号;
	    for (int i = 0; i < 8; i++) {
	      arrRet[i] = (byte) (pin[i] ^ accno[i]);
	    }
	    return arrRet;
	}

	/**
	   * getHPin
	   * 对密码进行转换
	   * PIN格式
	   * BYTE 1 PIN的长度
	   * BYTE 2 – BYTE 3/4/5/6/7   4--12个PIN(每个PIN占4个BIT)
	   * BYTE 4/5/6/7/8 – BYTE 8   FILLER “F” (每个“F“占4个BIT)
	   * @param pin String
	   * @return byte[]
	   */
	private static byte[] getHPin(String pin) {
	    byte arrPin[] = pin.getBytes();
	    byte encode[] = new byte[8];
	    encode[0] = (byte) 0x06;
	    encode[1] = (byte) uniteBytes(arrPin[0], arrPin[1]);
	    encode[2] = (byte) uniteBytes(arrPin[2], arrPin[3]);
	    encode[3] = (byte) uniteBytes(arrPin[4], arrPin[5]);
	    encode[4] = (byte) 0xFF;
	    encode[5] = (byte) 0xFF;
	    encode[6] = (byte) 0xFF;
	    encode[7] = (byte) 0xFF;
	    return encode;
	}

	/**
	   * getHAccno
	   * 对帐号进行转换
	   * BYTE 1 — BYTE 2 0X0000
	   * BYTE 3 — BYTE 8 12个主帐号
	   * 取主帐号的右12位（不包括最右边的校验位），不足12位左补“0X00”。
	   * @param accno String
	   * @return byte[]
	   */
	private static byte[] getHAccno(String accno) {
	    //取出主帐号；
	    int len = accno.length();
	    byte arrTemp[] = accno.substring(len < 13 ? 0 : len - 13, len - 1).getBytes();
	    
	    int in = arrTemp.length;
	    
	    byte arrAccno[] = new byte[12];
	    for (int i = 0; i < 12; i++) {
	      arrAccno[i] = (i <= arrTemp.length ? arrTemp[i] : (byte) 0x00);
	    }
	    byte encode[] = new byte[8];
	    encode[0] = (byte) 0x00;
	    encode[1] = (byte) 0x00;
	    encode[2] = (byte) uniteBytes(arrAccno[0], arrAccno[1]);
	    encode[3] = (byte) uniteBytes(arrAccno[2], arrAccno[3]);
	    encode[4] = (byte) uniteBytes(arrAccno[4], arrAccno[5]);
	    encode[5] = (byte) uniteBytes(arrAccno[6], arrAccno[7]);
	    encode[6] = (byte) uniteBytes(arrAccno[8], arrAccno[9]);
	    encode[7] = (byte) uniteBytes(arrAccno[10], arrAccno[11]);
//	    printHexString("encoded accno：", encode);
	    return encode;
	}
	
	 public static byte uniteBytes(byte src0, byte src1) {
		  byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
		    .byteValue();
		  _b0 = (byte) (_b0 << 4);
		  byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
		    .byteValue();
		  byte ret = (byte) (_b0 ^ _b1);
		  return ret;
		 }
	 
	 public static byte[] getPinBlock(String pin) {
			String block0 = "000000000000";
			String blockF = "FFFFFFFFFFFF";
			int pinLen = pin.length();
			byte[] pinInfo = new byte[8];
			byte[] subInfo;
			
			// Byte 2 �C Byte 3/4/5/6/7 4--12��PIN(ÿ��PINռ4��BIT)
			if (pinLen < 4) {
				pin = block0.substring(0, 4-pinLen) + pin;
			} else if (pinLen > 12) {
				pin = pin.substring(0, 12);
			} else if (pinLen%2 != 0) {
				pin = pin + "F";
			}
			pinLen = pin.length();
			pinInfo[0] = (byte)pinLen;
			subInfo = hex2Bin(pin + blockF.substring(0, 14-pinLen));
			System.arraycopy(subInfo, 0, pinInfo, 1, 7);
			return pinInfo;
		}
	 
	 public static byte[] hex2Bin(String hex) {
			byte[] hexbin = hex.getBytes();
			byte[] data = new byte[(hexbin.length + 1) / 2];

			for (int i = 0; i < hexbin.length; i++) {
				int v = Math.abs(hexbin[i]);
				if (v >= '0' && v <= '9')
					v = v - '0';
				else if (v >= 'A' && v <= 'F')
					v = v - 'A' + 10;
				else if (v >= 'a' && v <= 'f')
					v = v - 'a' + 10;
				else
					v = 0;
				v &= 0x0f;
				if (i % 2 == 0) {
					v <<= 4;
				}
				data[i / 2] |= v;
			}
			return data;

		}
		public static byte[] getPanInfo(String pan ) {
			String block0 = "0000000000000";
			int panLen = pan.length();
			byte[] panInfo = new byte[8];
			byte[] subInfo;
			panInfo[0] = 0x00;
			panInfo[1] = 0x00;
			if (panLen < 13) {
				pan = block0.substring(0, 13-panLen) + pan; 
				subInfo = hex2Bin(pan.substring(0, panLen-1));
			} else {
				subInfo = hex2Bin(pan.substring(panLen-13, panLen-1));
			}
			System.arraycopy(subInfo, 0, panInfo, 2, 6);
			return panInfo;
		}
}