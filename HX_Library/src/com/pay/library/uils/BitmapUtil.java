package com.pay.library.uils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.widget.ImageView;

public class BitmapUtil {

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * 将Bitmap对象转为字符串
	 * 
	 * @param bm
	 * @return
	 */
	public static String Bitmap2String(Bitmap bm) {
		/*ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		int options = 100;

		while (baos.toByteArray().length / 1024 > 70) {
			baos.reset();
			options -= 5;
			bm.compress(Bitmap.CompressFormat.JPEG, options, baos);
		}
		return Base64Util.encode(baos.toByteArray());*/
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
	    byte[] b = baos.toByteArray();
	    return Base64Util.encode(baos.toByteArray());
	}

	/**
	 * 将Bitmap对象转为字符串
	 * 
	 * @param bm
	 * @return
	 */
	public static String Bitmap2String(Bitmap bm, int size) {
		/*ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		int options = 100;
		while (baos.toByteArray().length / 1024 > size) { // 循环判断如果压缩后图片是否大于sizekb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			options -= 5;// 每次都减少5
			bm.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中

		}
		return Base64Util.encode(baos.toByteArray());*/
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
	    byte[] b = baos.toByteArray();
	    return Base64Util.encode(baos.toByteArray());
	}

	/**
	 * 
	 * 
	 * 
	 * @param path
	 *            文件的路径
	 * @param width
	 *            设定的宽度
	 * @param height
	 *            设定的高度
	 * @return
	 */

	/**
	 * 1.再次获取bitmap先进行释放,在获取压缩后的bitmap;
	 * 2.使用BitmapFactory.Options的inSampleSize参数来缩放;
	 * 
	 * @param imageView
	 * @param path
	 * @param width
	 * @param height
	 * @return bitmap
	 */
	@SuppressWarnings("static-access")
	public static Bitmap resizeImageFirstMethod(ImageView imageView,
			String path, int width, int height) {

		Bitmap bitmap = null;
		imageView.setDrawingCacheEnabled(true); // ImageView对象必须做如下设置后，才能获取其中的图像
		bitmap = imageView.getDrawingCache();// 获取空间里面的照片
		imageView.setDrawingCacheEnabled(false);
		if (bitmap != null && !bitmap.isRecycled()) {

			bitmap.recycle();
			bitmap = null;

		}
		bitmap = resizeImageFirstMethod(path, width, height);
		return bitmap;
	}

	/**
	 * 
	 * 使用BitmapFactory.Options的inSampleSize参数来缩放
	 * 
	 * @param path
	 *            文件的路径
	 * @param width
	 *            设定的宽度
	 * @param height
	 *            设定的高度
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static Bitmap resizeImageFirstMethod(String path, int width,
			int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;// 不加载bitmap到内存中
		bitmap = options.inBitmap;
		int t = options.inDensity;
		bitmap = BitmapFactory.decodeFile(path, options);
		int outWidth = options.outWidth;
		int outHeight = options.outHeight;
		options.inDither = false; // 图片不抖动
		// options.inPurgeable=true; //使得内存可以被回收
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inSampleSize = 1;

		if (outWidth != 0 && outHeight != 0 && width != 0 && height != 0) {
			int sampleSize = (outWidth / width + outHeight / height) / 2;
			options.inSampleSize = sampleSize;

		}

		options.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(path, options);
		// return new BitmapDrawable(BitmapFactory.decodeFile(path, options));
		return bitmap;
	}

	/**
	 * 
	 * 使用BitmapFactory.Options的inSampleSize参数来缩放
	 * 
	 * @param path
	 *            文件的路径
	 * @param reqHeight
	 *            设定的宽度
	 * @param reqWidth
	 *            设定的高度
	 * @return
	 */
	public static Bitmap resizeImageSecondMethod(String path, int reqWidth,
			int reqHeight) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;// 不加载bitmap到内存中
		bitmap = BitmapFactory.decodeFile(path, options);
		int outWidth = options.outWidth;
		int outHeight = options.outHeight;
		options.inDither = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inSampleSize = 1;

		if (outHeight > reqHeight || outWidth > reqWidth) {
			// 计算出实际宽高和目标宽高的比率
			final int heightRatio = Math.round((float) outHeight
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) outWidth
					/ (float) reqWidth);
			// 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
			// 一定都会大于等于目标的宽和高。
			options.inSampleSize = heightRatio < widthRatio ? heightRatio
					: widthRatio;
		}
		options.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(path, options);
		return bitmap;
	}

	/**
	 * 使用BitmapFactory.Options的inSampleSize参数来缩放
	 * 
	 * @param path
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap resizeImageThirdMethod(String path, int reqWidth,
			int reqHeight) {

		Bitmap bitmap = null;
		// 首先不加载图片,仅获取图片尺寸
		final BitmapFactory.Options options = new BitmapFactory.Options();
		// 当inJustDecodeBounds设为true时,不会加载图片仅获取图片尺寸信息
		options.inJustDecodeBounds = true;
		// 此时仅会将图片信息会保存至options对象内,decode方法不会返回bitmap对象
		bitmap = BitmapFactory.decodeFile(path, options);
		int outWidth = options.outWidth;
		int outHeight = options.outHeight;
		options.inDither = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inSampleSize = 1;

		if (outHeight > reqHeight || outWidth > reqWidth) {

			final int halfHeight = outHeight / 2;
			final int halfWidth = outWidth / 2;

			// 压缩比例值每次循环两倍增加,
			// 直到原图宽高值的一半除以压缩值后都~大于所需宽高值为止
			// while ((halfHeight / options.inSampleSize) >= reqHeight&&
			// (halfWidth / options.inSampleSize) >= reqWidth) {
			// options.inSampleSize *= 2;
			while ((int) (Math.round((float) halfHeight
					/ (float) options.inSampleSize)) >= reqHeight
					&& ((int) (Math.round((float) halfWidth
							/ (float) options.inSampleSize))) >= reqWidth) {
				options.inSampleSize *= 2;

			}
		}
		// 当inJustDecodeBounds设为false时,BitmapFactory.decode...就会返回图片对象了
		options.inJustDecodeBounds = false;
		// 利用计算的比例值获取压缩后的图片对象
		bitmap = BitmapFactory.decodeFile(path, options);
		return bitmap;
	}

	/**
	 * 
	 * @param path
	 *            图片的路径
	 * @param reqWidth
	 *            手机屏幕的宽度
	 * @param reqHeight
	 *            手机屏幕的高度
	 * @return 返回字符数组
	 */
	public static String bitmapTransformString(String path, int reqWidth,
			int reqHeight, int reqSize) {
		Bitmap bitmap = resizeImageSecondMethod(path, reqWidth, reqHeight);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int options = 100;
		bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
		while (baos.toByteArray().length / 1024 > reqSize) {

			baos.reset();// 重置baos即清空baos
			options -= 5;// 每次都减少5
			if (options <=5) {
				options = 5;
			}
			bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中

		}
//		if (bitmap != null && !bitmap.isRecycled()) {// 处理完以后得回收一下
//
//			bitmap.isRecycled();
//			bitmap = null;
//
//		}
		System.out.println("????????????????????????????????????????????????/");
		System.out.println(baos.toByteArray().length);
		
		System.out.println("????????????????????????????????????????????????/");
		System.out.println(Base64Util.encode(baos.toByteArray()).length());
		return Base64Util.encode(baos.toByteArray());
	}

}
