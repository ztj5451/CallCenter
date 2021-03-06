package com.callCenter.media;

import java.io.File;

import com.callCenter.utils.TimeUtils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

public class MediaManager {
	public static final int RESULT_CAPTURE_IMAGE = 1;// 照相的requestCode
	public static final int REQUEST_CODE_TAKE_VIDEO = 2;// 摄像的照相的requestCode
	public static final int REQUEST_CODE_ALBUM =3;
	public static String image_name = "";
	public static String strVideoPath = "";

	/**
	 * 普通照相
	 * 
	 * @param activity
	 * @param path
	 */
	public static void cameraMethod(Activity activity, String path) {
		Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		StringBuffer fileName = new StringBuffer();
		String imageName = fileName.append(TimeUtils.getImageName())
				.append(".jpg").toString();
		File out = new File(path);
		if (!out.exists()) {
			out.mkdirs();
		}

		out = new File(path, imageName);
		image_name = imageName;// 该照片的绝对路径
		Uri uri = Uri.fromFile(out);
		imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		imageCaptureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		activity.startActivityForResult(imageCaptureIntent,
				RESULT_CAPTURE_IMAGE);

	}

	/**
	 * 拍摄视频
	 */
	public static void videoMethod(Activity activity, String path) {

		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
		activity.startActivityForResult(intent, REQUEST_CODE_TAKE_VIDEO);
	}
	public static void Image_From_Album(Activity activity)
	{

		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				"image/*");
		activity.startActivityForResult(intent, REQUEST_CODE_ALBUM);
		
	}

}
