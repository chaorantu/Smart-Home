package com.mjpeg.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * ������ؼ� ����������
 * �ṩ�˲����ļ�����Ϊ�����ļ��ؼ��󶨼�����
 * @author Administrator
 *
 */
public class ExitActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exit_dialog);
	}

	
	public void exitButtonYes(View v) {
		this.finish();
		MainActivity.instance.finish();//����MainActivity
	}

	public void exitButtonNo(View v) {
		this.finish();// ��������
	}
}
