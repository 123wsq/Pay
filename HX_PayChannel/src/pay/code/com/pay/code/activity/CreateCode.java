package com.pay.code.activity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;

public class CreateCode {

	/**
	 * 生成二维码
	 * @param msg
	 * @return
	 */
	public static Bitmap getQRCode(String msg, int width, int height){
		QRCodeWriter writer = new QRCodeWriter();
		
		try {
			BitMatrix matrix = writer.encode(msg, BarcodeFormat.QR_CODE, width, height);
			
			return bitMatrix2Bitmap(matrix);
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	 private static Bitmap bitMatrix2Bitmap(BitMatrix matrix) {  
	        int w = matrix.getWidth();  
	        int h = matrix.getHeight();  
	        int[] rawData = new int[w * h];  
	        for (int i = 0; i < w; i++) {  
	            for (int j = 0; j < h; j++) {  
	                int color = Color.WHITE;  
	                if (matrix.get(i, j)) {  
	                    color = Color.BLACK;  
	                }  
	                rawData[i + (j * w)] = color;  
	            }  
	        }  
	  
	        Bitmap bitmap = Bitmap.createBitmap(w, h, Config.RGB_565);  
	        bitmap.setPixels(rawData, 0, w, 0, 0, w, h);  
	        return bitmap;  
	    }  
	
}
