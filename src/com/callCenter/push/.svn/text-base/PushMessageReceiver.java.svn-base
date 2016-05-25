package com.callCenter.push;

import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.callCenter.activity.LoginActivity;
import com.callCenter.activity.MainActivity;
import com.callCenter.database.DBHelper;
import com.callCenter.database.MessageDB;
import com.callCenter.utils.PushUtils;

/**
 * Push消息处理receiver。请编写您需要的回调函数， 一般来说： onBind是必须的，用来处理startWork返回值；
 * onMessage用来接收透传消息； onSetTags、onDelTags、onListTags是tag相关操作的回调；
 * onNotificationClicked在通知被点击时回调； onUnbind是stopWork接口的返回值回调
 * 
 * 返回值中的errorCode，解释如下： 0 - Success 10001 - Network Problem 30600 - Internal
 * Server Error 30601 - Method Not Allowed 30602 - Request Params Not Valid
 * 30603 - Authentication Failed 30604 - Quota Use Up Payment Required 30605 -
 * Data Required Not Found 30606 - Request Time Expires Timeout 30607 - Channel
 * Token Timeout 30608 - Bind Relation Not Found 30609 - Bind Number Too Many
 * 
 * 当您遇到以上返回错误时，如果解释不了您的问题，请用同一请求的返回值requestId和errorCode联系我们追查问题。
 * 
 */
public class PushMessageReceiver extends FrontiaPushMessageReceiver {
	// 标识当前主页面是否已经结束
	private DBHelper dbHelper;
	private SQLiteDatabase database;
	public static boolean running = false;
	public static final String TAG = PushMessageReceiver.class.getSimpleName();

	/**
	 * 调用PushManager.startWork后，sdk将对push
	 * server发起绑定请求，这个过程是异步的。绑定请求的结果通过onBind返回。 如果您需要用单播推送，需要把这里获取的channel
	 * id和user id上传到应用server中，再调用server接口用channel id和user id给单个手机或者用户推送。
	 * 
	 * @param context
	 *            BroadcastReceiver的执行Context
	 * @param errorCode
	 *            绑定接口返回值，0 - 成功
	 * @param appid
	 *            应用id。errorCode非0时为null
	 * @param userId
	 *            应用user id。errorCode非0时为null
	 * @param channelId
	 *            应用channel id。errorCode非0时为null
	 * @param requestId
	 *            向服务端发起的请求id。在追查问题时有用；
	 * @return none
	 */
	@Override
	public void onBind(Context context, int errorCode, String appid,
			String userId, String channelId, String requestId) {
		System.out.println("push绑定");
		String responseString = userId;
		String uid = userId;
		String cid = channelId;
		if (errorCode == 0) {
			PushUtils.setBind(context, true);
			SharedPreferences sPreferences = context.getSharedPreferences(
					"pushInfo", context.MODE_PRIVATE);
			Editor editor = sPreferences.edit();
			editor.putString("userId", uid);
			editor.putString("channelId", cid);
			editor.commit();
		}

		// Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
		updateContent(context, responseString);
	}

	/**
	 * 接收透传消息的函数。
	 * 
	 * @param context
	 *            上下文
	 * @param message
	 *            推送的消息
	 * @param customContentString
	 *            自定义内容,为空或者json字符串
	 */
	@Override
	public void onMessage(Context context, String message,
			String customContentString) {
		String messageString = "透传消息 message=\"" + message
				+ "\" customContentString=" + customContentString;
		Log.d(TAG, messageString);

		// 自定义内容获取方式，mykey和myvalue对应透传消息推送时自定义内容中设置的键和值
		if (customContentString != null & customContentString != "") {
			JSONObject customJson = null;
			try {
				customJson = new JSONObject(customContentString);
				String myvalue = null;
				if (customJson.isNull("flag")) {
					myvalue = customJson.getString("flag");
					String s = myvalue;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
		updateContent(context, messageString);
	}

	/**
	 * 接收通知点击的函数。注：推送通知被用户点击前，应用无法通过接口获取通知的内容。
	 * 
	 * @param context
	 *            上下文
	 * @param title
	 *            推送的通知的标题
	 * @param description
	 *            推送的通知的描述
	 * @param customContentString
	 *            自定义内容，为空或者json字符串
	 */
	@Override
	public void onNotificationClicked(Context context, String message_title,
			String description, String customContentString) {
		String notifyString = "通知点击 title=\"" + message_title
				+ "\" description=\"" + description + "\" customContent="
				+ customContentString;
		System.out.println("pushNotification：" + notifyString);
		Log.d(TAG, notifyString);
		dbHelper = new DBHelper(context);
		// 判断当前是否退出系统 还是系统在运行当中
		if (customContentString != null & customContentString != "") {
			if (running) {
				// Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
				updateContent(context, customContentString);
			} else {
				try {

					String id, time, title, flag, message, dep;
					JSONObject object = new JSONObject(customContentString);
					flag = object.getString("flag");
					id = object.getString("gg_id");
					time = object.getString("times");
					title = object.getString("title");
					message = object.getString("contents");
					dep = object.getString("dep");
					if (flag.equals("gg")) {
						saveMessage2NotRead(id, time, title, message, dep);
					} else if (flag.equals("gw")) {
						saveGw2Waiting(id, time, title);
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				start(context);

			}
		}

	}

	/**
	 * setTags() 的回调函数。
	 * 
	 * @param context
	 *            上下文
	 * @param errorCode
	 *            错误码。0表示某些tag已经设置成功；非0表示所有tag的设置均失败。
	 * @param successTags
	 *            设置成功的tag
	 * @param failTags
	 *            设置失败的tag
	 * @param requestId
	 *            分配给对云推送的请求的id
	 */
	@Override
	public void onSetTags(Context context, int errorCode,
			List<String> sucessTags, List<String> failTags, String requestId) {
		String responseString = "onSetTags errorCode=" + errorCode
				+ " sucessTags=" + sucessTags + " failTags=" + failTags
				+ " requestId=" + requestId;
		Log.d(TAG, responseString);

		// Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
		updateContent(context, responseString);
	}

	/**
	 * delTags() 的回调函数。
	 * 
	 * @param context
	 *            上下文
	 * @param errorCode
	 *            错误码。0表示某些tag已经删除成功；非0表示所有tag均删除失败。
	 * @param successTags
	 *            成功删除的tag
	 * @param failTags
	 *            删除失败的tag
	 * @param requestId
	 *            分配给对云推送的请求的id
	 */
	@Override
	public void onDelTags(Context context, int errorCode,
			List<String> sucessTags, List<String> failTags, String requestId) {
		String responseString = "onDelTags errorCode=" + errorCode
				+ " sucessTags=" + sucessTags + " failTags=" + failTags
				+ " requestId=" + requestId;
		Log.d(TAG, responseString);

		// Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
		updateContent(context, responseString);
	}

	/**
	 * listTags() 的回调函数。
	 * 
	 * @param context
	 *            上下文
	 * @param errorCode
	 *            错误码。0表示列举tag成功；非0表示失败。
	 * @param tags
	 *            当前应用设置的所有tag。
	 * @param requestId
	 *            分配给对云推送的请求的id
	 */
	@Override
	public void onListTags(Context context, int errorCode, List<String> tags,
			String requestId) {
		String responseString = "onListTags errorCode=" + errorCode + " tags="
				+ tags;
		Log.d(TAG, responseString);

		// Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
		updateContent(context, responseString);
	}

	/**
	 * PushManager.stopWork() 的回调函数。
	 * 
	 * @param context
	 *            上下文
	 * @param errorCode
	 *            错误码。0表示从云推送解绑定成功；非0表示失败。
	 * @param requestId
	 *            分配给对云推送的请求的id
	 */
	@Override
	public void onUnbind(Context context, int errorCode, String requestId) {
		System.out.println("push启动");
		String responseString = "onUnbind errorCode=" + errorCode
				+ " requestId = " + requestId;
		Log.d(TAG, responseString);

		// 解绑定成功，设置未绑定flag，
		if (errorCode == 0) {
			PushUtils.setBind(context, false);
		}
		// Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
		updateContent(context, responseString);
	}

	// 没有退出当前信息 主页面进入后台 直接启动主页面
	private void updateContent(Context context, String content) {

		JSONObject object = null;
		String id = null, time, title, flag, message, dep;
		try {
			object = new JSONObject(content);
			flag = object.getString("flag");
			id = object.getString("gg_id");
			time = object.getString("times");
			title = object.getString("title");
			message = object.getString("contents");
			dep = object.getString("dep");

			PushUtils.logStringCache = flag + "," + id + "," + time + ","
					+ title;

			if (flag.equals("gg")) {
				saveMessage2NotRead(id, time, title, message, dep);
			} else if (flag.equals("gw")) {
				saveGw2Waiting(id, time, title);
			}

			MainActivity.running = true;

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Intent intent = new Intent();
		intent.putExtra("id", id);
		intent.setClass(context.getApplicationContext(), MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.getApplicationContext().startActivity(intent);
	}

	// 当退出了当前系统 需要启动登陆页面
	private void start(Context context) {

		Intent intent = new Intent();
		intent.setClass(context.getApplicationContext(), LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.getApplicationContext().startActivity(intent);
	}

	// 保存新消息到未读数据库
	private void saveGw2Waiting(String id, String time, String title) {
		database = dbHelper.getWritableDatabase();
		Cursor cursor = database.rawQuery("SELECT id FROM " + DBHelper.GW_START
				+ " WHERE id=?", new String[] { id });
		if (cursor.getCount() == 0) {
			ContentValues values = new ContentValues();
			values.put(MessageDB.ID, id);
			values.put(MessageDB.TIME, time);
			values.put(MessageDB.TITLE, title);
			values.put(MessageDB.FLAG, "waiting");
			database.insert(DBHelper.GW_START, null, values);
		}

	}

	// 保存新消息到未读数据库
	private void saveMessage2NotRead(String id, String time, String title,
			String message, String dep) {
		database = dbHelper.getWritableDatabase();
		Cursor cursor = database.rawQuery("SELECT id FROM " + DBHelper.MESSAGE
				+ " WHERE id=?", new String[] { id });
		if (cursor.getCount() == 0) {
			ContentValues values = new ContentValues();
			values.put(MessageDB.ID, id);
			values.put(MessageDB.TIME, time);
			values.put(MessageDB.TITLE, title);
			values.put(MessageDB.MESSAGE, message);
			values.put(MessageDB.BM, dep);
			values.put(MessageDB.FLAG, "no");
			database.insert(DBHelper.MESSAGE, null, values);
		}

	}

}
