package com.mjpeg.activity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import tools.Generic;

import com.mjpeg.activity.R;
import com.mjpeg.io.MjpegInputStream;
import com.mjpeg.view.MjpegView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

/**
 * ��¼�ɹ��󣬽�ִ���������̳���Activityʵ����OnCheckedChangeListener������
 * OnCheckedChangeListener�ǵ���RadioGroup��RadioButton��ѡ�л�ı�ʱ��һ���ص��ӿڱ�������ִ��
 * ��Ҫ��1��RadioGroup��5��RadioButton��ʵ����5���ؼ�����ת����û��ʵ�־���Ĳ�����ֻ�ǵ���ĳ����ȥִ��
 */
public class MainActivity extends Activity implements OnCheckedChangeListener {
	public static MainActivity instance = null;
	private MjpegInputStream mis = null;
	private MjpegView mjpegView = null;
	private RadioGroup mainTab = null;
	private File sdCardFile = null;
	private String picturePath = "";
	/**
	 * ����Activit��һ�δ����󣬸÷�����������
	 * 1.���ò���
	 * 2.��ʼ����Ƶ������
	 * 3.������R.java��id�ҵ��ؼ�
	 * 4.ΪRadioGroup���ü���������RadioButton�����»�ı�ʱ���������onCheckedChanged����
	 * 5.���SD������ʼ��mjpegview��ͼ�������Ϳɿ�����ػ�����
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);/*����RadioGroup��5��RadioButton*/

		instance = this;
		mis = MjpegInputStream.getInstance();
		mjpegView = (MjpegView) findViewById(R.id.mjpegview);
		mainTab = (RadioGroup) findViewById(R.id.main_tab);
		mainTab.setOnCheckedChangeListener(this);

		checkSdcard();
		initMjpegView();
	}
	/**
	 * ����:���SD·��
	 * �����SD�����򴴽����ͼƬ��picturePathĿ¼
	 */
	private void checkSdcard() {
		sdCardFile = Generic.getSdCardFile();
		if (sdCardFile == null)
			Generic.showMsg(this, "�����SD��", true);
		else {
			picturePath = sdCardFile.getAbsolutePath() + "/mjpeg/";
			File f = new File(picturePath);
			if (!(f.exists() && f.isDirectory()))
				f.mkdir();
		}
	}
	/**
	 * ����com/mjpeg/view��mjpegView.java����mjpegView���ڶ෽������ʼ���Զ���ؼ�com.mjpeg.view.MjpegView
	 * MjpegView������ͷϷ
	 */
	private void initMjpegView() {
		if (mis != null) {
			mjpegView.setSource(mis);// ����������Դ
			mjpegView.setDisplayMode(mjpegView.getDisplayMode());/*����mjpegview����ʾģʽ*/
			/**
			 * setFps��getFps������Ϊ������Ļ�����ϽǶ�̬��ʾ��ǰ��֡��
			 * �������ֻ��ۿ����棬���������ȫ����ʡȥ
			 */
			mjpegView.setFps(mjpegView.getFps());
			/**
			 * ����mjpegView�е��̵߳�run��������ʼ��ʾ����
			 */
			mjpegView.startPlay();
		}
	}

	@Override
	/**
	 * ����Activity��finishʱ���÷���������
	 * �ȵ���mjpegview��stopplay������Ȼ����ø����onDestroy����
	 */
	protected void onDestroy() {
		if (mjpegView != null)
			mjpegView.stopPlay();
		super.onDestroy();
	}

	@Override
	/**
	 * ��RadioGroup�ĳ�Ա�ı�ʱ���÷���������
	 * @parm group ��RadioButton���ڵ���
	 * @parm checkedId�����Ը������ֵ���ж����ĸ�Button
	 * 1.�Ȳ�Checked RadioButton�ؼ�
	 * 2.����RadioGroup�ҵ������Button��Ա��id
	 * 3.����ID���RadioBotton�ؼ�
	 * 4.���ݵ����ͬ��RadioBotton��ִ����Ӧ�Ĳ���
	 */
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		int radioButtonId = group.getCheckedRadioButtonId();
		// ����ID��ȡRadioButton��ʵ��
		RadioButton rb = ((RadioButton) this.findViewById(radioButtonId));
		rb.setChecked(false);

		switch (checkedId) {
		case R.id.radiobtn0:
			shotSnap(rb);
			break;
		case R.id.radiobtn1:
			/**
			 * ����ûʵ��¼���ܣ�ֻ����һ��"¼��"Toast
			 */
			Toast.makeText(this, "δʵ��¼��Ŷ", Toast.LENGTH_SHORT).show();
			break;
		case R.id.radiobtn2:
			scanPic(); /*��ӵ����*/
			break;
		case R.id.radiobtn3:
			setFullScreen(rb);
			break;
		case R.id.radiobtn4:
			/**
			 * ��ת��settingActivi.java
			 */
			startActivity(new Intent(this, SettingActivity.class));
			break;
		}

	}

	@Override
	/**
	 * �ص���һ��Activity�Ľ��������
	 * ��Activity����resume()֮ǰ,��������Activityʱ���ø÷���
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * ��ʾģʽ����
	 * 1.��õ�ǰ��ʾģʽ
	 * 2.�����ǰ��ȫ����ʾ�����"set"��ť��Button��text��Ϊ"��׼",������л�����׼ģʽ
	 * 3.�����ǰ�Ǳ�׼��ʾ����ͬһ��ť����ť��Ϊ"ȫ��",���л���ȫ����ʾģʽ
	 * @param rb
	 */
	private void setFullScreen(RadioButton rb) {
		int mode = mjpegView.getDisplayMode();

		if (mode == MjpegView.FULLSCREEN_MODE) {
			/**
			 * ������xml�ļ�����RadioButton��text��Ҳ���Ե��ÿؼ��ĵ�setText����������text
			 */
			rb.setText(R.string.fullscreen);
			mainTab.setBackgroundResource(R.drawable.maintab_toolbar_bg);/*��������*/
			mjpegView.setDisplayMode(MjpegView.KEEP_SCALE_MODE);/*��׼*/
		} else {
			rb.setText(R.string.standard);/*"��׼"*/
			mainTab.setBackgroundColor(Color.TRANSPARENT);/*͸������*/
			mjpegView.setDisplayMode(MjpegView.FULLSCREEN_MODE);/*ȫ��*/
		}
	}

	/**
	 * ����:����
	 * @parm RadioButton rb
	 * 1.��disable RadioButton,��ʹ����
	 * 2.�����SD��������picturePath�½��Ե�ǰϵͳʱ��Ϊǰ׺��ͼƬ�ļ�
	 * 3.����mjpegview��getbitmap�������λͼ
	 * 4.λͼ ��Ϊ�գ�����ͼƬ�ļ���û��������
	 * 5.����λͼ��ѹ��������ͼƬѹ��ΪJPEG��ʽ��ˢ�»��棬�ر���
	 */
	private void shotSnap(RadioButton rb) {
		Bitmap curBitmap = null;
		
		rb.setEnabled(false);
		if (sdCardFile != null) {
			BufferedOutputStream bos;
			File captureFile = new File(picturePath + Generic.getSysNowTime()
					+ ".jpg");

			try {
				curBitmap = mjpegView.getBitmap();
				if(curBitmap != null){
					bos = new BufferedOutputStream(
							new FileOutputStream(captureFile));/*File-->�����*/
					curBitmap.compress(Bitmap.CompressFormat.JPEG, 80,
						bos);/*ѹ��*/
					bos.flush();
					bos.close();
					Generic.showMsg(this, "OK", true);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Generic.showMsg(this, "����SD��", true);
		}
		rb.setEnabled(true);
	}

	/**
	 * ����:�����ɾ��ͼƬ
	 * ���ж��Ƿ���SD��
	 * ��SD�������ý�֮ǰ�����õ�picturePath���ݸ�ScanPicActivity������͹���ScanPicActivity
	 * 
	 */
	private void scanPic() {
		if (sdCardFile != null) {
			/*��������Intent��putExtra������������*/
			startActivity(new Intent(this, ScanPicActivity.class).putExtra(
					"picturePath", picturePath));
		} else {
			Generic.showMsg(this, "����SD��", true);
		}
	}

	@Override
	/**
	 * ������ֻ��ķ��ؼ��������ô˷���
	 * ����ExitActivityִ��
	 * @parm keyCode:��ֵ
	 * @parm event:��������
	 * ����ǵ���ķ��ؼ����½�������Intent��Ȼ������Activity����ת����ת�ɹ������� ʧ�ܷ��ؼ�
	 * 
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent();
			intent.setClass(this, ExitActivity.class);
			startActivity(intent);
			return true;
		}

		return false;
	}

}
