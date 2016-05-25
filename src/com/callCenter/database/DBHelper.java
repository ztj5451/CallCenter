package com.callCenter.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 手机离线所用到的数据库 把所需数据存储到手机数据库中
 * 
 * @author Administrator
 * 
 */
public class DBHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "callCenter.db";
	// 数据库版本
	private static final int DATABASE_VERSION = 1;
	// 通讯录数据表
	public static final String TXL_USER = "txl_user";
	// 通讯录 树形结构
	public static final String TXL_BM = "txl_bm";
	// 工作日志表
	public static final String WORK_LOG = "work_log";
	// 企业信息
	public static final String QY_INFO = "qy_info";
	// 拍照获取经纬度
	public static final String PICTURE_TRAIL = "picture_trail";
	// 被定位企业
	public static final String ISLOCATED_QY = "islocate_qy";
	// 通知公告
	public static final String MESSAGE = "message";
	// 公文发起
	public static final String GW_START = "gw_start";
	// 移动巡查
	public static final String YDXC = "ydxc";

	// 数据库构造方法
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

	}
	// 创建的时候调用
	@Override
	public void onCreate(SQLiteDatabase db) {
		// 通讯 User表结构
		String txl_user = "CREATE TABLE " + TXL_USER + "(" + Txl_User.TABLE_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + Txl_User.ID
				+ " TEXT," + Txl_User.PX + " TEXT," + Txl_User.NAME + " TEXT,"
				+ Txl_User.SEX + " TEXT," + Txl_User.BMID + " TEXT,"
				+ Txl_User.BMIDS + " TEXT," + Txl_User.ZW + " TEXT,"
				+ Txl_User.MOBILE + " TEXT," + Txl_User.OFFICE + " TEXT,"
				+ Txl_User.PHOTO + " TEXT," + Txl_User.SORT_KEY + " TEXT,"
				+ Txl_User.FLAG + " TEXT )";
		// 通讯录 Bm表结构
		String txl_bm = "CREATE TABLE " + TXL_BM + " (" + Txl_Bm.TABLE_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + Txl_Bm.ID + " TEXT,"
				+ Txl_Bm.PID + " TEXT," + Txl_Bm.PX + " TEXT," + Txl_Bm.IDS
				+ " TEXT," + Txl_Bm.NAME + " TEXT );";
		// 工作日志表结构

		String work_log = "CREATE TABLE " + WORK_LOG + " (" + Work_Log.ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + Work_Log.START_TIME
				+ " TEXT," + Work_Log.START_ADD + " TEXT," + Work_Log.START_LAT
				+ " TEXT," + Work_Log.START_LON + " TEXT," + Work_Log.END_TIME
				+ " TEXT," + Work_Log.END_ADD + " TEXT," + Work_Log.END_LAT
				+ " TEXT," + Work_Log.END_LON + " TEXT," + Work_Log.YDXC
				+ " INTEGER," + Work_Log.SEND_MESSAGE + " INTEGER,"
				+ Work_Log.WZCHECK + " INTEGER," + Work_Log.GW_START
				+ " INTEGER," + Work_Log.GW_HANDL + " INTEGER )";
		// 企业信息表结构
		String qy_info = "CREATE TABLE " + QY_INFO + " (" + Qy_Info.ID
				+ " TEXT," + Qy_Info.NAME + " TEXT," + Qy_Info.PY + " TEXT,"
				+ Qy_Info.ZCH + " TEXT," + Qy_Info.ADDRESS + " TEXT )";
		// 拍摄照片获取经纬度信息
		String picture_trail = "CREATE TABLE " + PICTURE_TRAIL + " ("
				+ Picture_Trail.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ Picture_Trail.IMAGE_NAME + " TEXT," + Picture_Trail.LAT
				+ " TEXT," + Picture_Trail.LON + " TEXT )";
		// 被定为企业
//		String isLocated_qy = "CREATE TABLE " + ISLOCATED_QY + " ("
//				+ IsLocated_Qy.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//				+ IsLocated_Qy.NAME + " TEXT," + IsLocated_Qy.STATE + " TEXT,"
//				+ IsLocated_Qy.ADDRESS + " TEXT," + IsLocated_Qy.DATE_TIME
//				+ " TEXT )";
		// 通知公告
		String message = "CREATE TABLE " + MESSAGE + " (" + MessageDB.TABLE_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + MessageDB.ID
				+ " TEXT," + MessageDB.TITLE + " TEXT," + MessageDB.MESSAGE
				+ " TEXT," + MessageDB.BM + " TEXT," + MessageDB.USER
				+ " TEXT," + MessageDB.TIME + " TEXT," + MessageDB.FLAG
				+ " TEXT )";
		// 公文
		String gw_start = "CREATE TABLE " + GW_START + " (" + Gw_Start.TABLE_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + Gw_Start.ID
				+ " TEXT," + Gw_Start.RECEIVER + " TEXT," + Gw_Start.TITLE
				+ " TEXT," + Gw_Start.CONTENT + " TEXT," + Gw_Start.USER
				+ " TEXT," + Gw_Start.TIME + " TEXT," + Gw_Start.ATTACHMENT
				+ " TEXT," + Gw_Start.FLAG + " TEXT )";
		// 移动巡查
		String ydxc_sql = "CREATE TABLE " + YDXC + " (" + Ydxc_DB.ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + Ydxc_DB.QY_NAME
				+ " TEXT," + Ydxc_DB.JCLB + " TEXT," + Ydxc_DB.JCRY + " TEXT,"
				+ Ydxc_DB.JCQK + " TEXT," + Ydxc_DB.JCYJ + " TEXT,"
				+ Ydxc_DB.CLJG + " TEXT," + Ydxc_DB.QZCL + " TEXT,"
				+ Ydxc_DB.TIME + " TEXT )";
		// 创建表
		db.execSQL(qy_info);
		db.execSQL(work_log);
		db.execSQL(txl_user);
		db.execSQL(txl_bm);
		db.execSQL(picture_trail);
//		db.execSQL(isLocated_qy);
		db.execSQL(message);
		db.execSQL(gw_start);
		db.execSQL(ydxc_sql);
	}

	// 数据库版本更新的时候调用
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 删除通讯录 人员信息
		db.execSQL(dropSql(TXL_USER));
		// 删除通讯录组织结构信息
		db.execSQL(dropSql(TXL_BM));
		// 删除工作日志表
		db.execSQL(dropSql(WORK_LOG));
		// 删除企业信息表
		db.execSQL(dropSql(QY_INFO));
		// 删除照片轨迹点
		db.execSQL(dropSql(PICTURE_TRAIL));
		// 删除被定位企业表
		db.execSQL(dropSql(ISLOCATED_QY));
		//
		db.execSQL(dropSql(MESSAGE));
		//
		db.execSQL(dropSql(GW_START));
		//
		db.execSQL(dropSql(YDXC));
		onCreate(db);

	}

	// 删除sql语句
	public String dropSql(String dbName) {
		return "DROP TABLE IF EXISTS " + dbName;
	}


}
