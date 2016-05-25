package com.callCenter.utils;

import java.io.File;
import java.io.FileOutputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CompressPicture {
	//对图片进行压缩并进行保存
	public static void Compress(String filePath) {

		if (filePath != null) {

			try {
				File f = new File(filePath);

				Bitmap bm = getSmallBitmap(filePath);

				FileOutputStream fos = new FileOutputStream(new File(
						getFileDir(), "press_" + f.getName()));

				bm.compress(Bitmap.CompressFormat.JPEG, 30, fos);

			} catch (Exception e) {

			}

		}
	}
	//对图片进行压缩
	public static Bitmap getSmallBitmap(String filePath) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, 480, 800);
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}
	//计算图片的宽度与高度
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}
	/**
	 * 获取保存图片的目录
	 * 
	 * @return
	 */
	public static File getFileDir() {
		File dir = new File(SettingUtils.get("press_image_dir"));
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

}
