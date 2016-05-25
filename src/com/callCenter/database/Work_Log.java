package com.callCenter.database;

import android.provider.BaseColumns;

/**
 * 
 * 
 * @author Administrator
 * 
 */
public class Work_Log implements BaseColumns {

	public static final String TABLE_ID = "table_id";
	public static final String ID = "id";// id
	public static final String START_TIME = "start_time";// ��ʼʱ��
	public static final String START_ADD = "start_add";// ��ʼ�ص�
	public static final String START_LAT = "start_lat";// ��ʼγ��
	public static final String START_LON = "start_lon";// ��ʼ
	public static final String END_TIME = "end_time";// ����ʱ��
	public static final String END_LAT = "end_lat";// ����γ��
	public static final String END_LON = "end_lon";// �����
	public static final String END_ADD = "end_add";// ����ص�
	public static final String YDXC = "ydcx";// �ƶ�Ѳ�������
	public static final String SEND_MESSAGE = "send_message";// ������Ϣ������
	public static final String WZCHECK = "wz_check";// λ��У�������
	public static final String GW_START = "gw_start";// ���ķ��������
	public static final String GW_HANDL="gw_handle";//���?������

}
