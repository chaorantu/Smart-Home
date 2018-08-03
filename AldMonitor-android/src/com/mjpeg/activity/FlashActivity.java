package com.mjpeg.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import tools.Generic;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.mjpeg.io.MjpegInputStream;

/**
 * Ӧ�ó���ִ��ʱ���������ȱ�����
 */
public class FlashActivity extends Activity {
	private Context mContext = this;
	private AutoCompleteTextView ipEdt = null;
	private EditText portEdt = null;
	private TextView hintTv = null;
	private DhcpInfo dpInfo = null;
	private WifiManager wifi = null;
	private InputStream is = null;
	private SharedPreferences sp = null;
	private Editor editor = null;
	private String port = "8080";/* �����������û�����Ķ˿� */
	private Bundle bundle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flash);/* ���ò���Ϊres/layout/flash.xml*/

		init();
		int state = wifi.getWifiState();/* ���wifi��ǰ״̬ */
		
		if (state != WifiManager.WIFI_STATE_ENABLED) {
			/**
			 * Ϊ�˳������չ�ԺͿɶ��ԣ�������toolsĿ¼����һ��Generic�࣬���кܶ෽��
			 * 1.��showMsg���������ڿ�����ʾʱ������ʾһ��Toast
			 * 2.��getSysNowTime���������ڻ�ȡ��ǰ��ϵͳʱ��
			 * 3.��getSdCardFile���������ڻ�ȡSD���ľ���·�����ɹ�����Fileֵ��ʧ�ܷ���NULL
			 * 4.��getConnectedIP���������ڻ�ȡ���ӵ�wifi�ȵ�����е��ֻ�ip���ɹ�����ArrayList<String>�͵�����
			 * 5.��getShrinkedPic���������ڻ�ȡ��Ƭ������ͼ
			 * 6.������һ��DescendSortByIndex�ࣺʵ�������ͱȽ���
			 * 7.�����DescendSortByTime�ࣺʵ����File�Ƚ���
			 */
			Generic.showMsg(this, "���wifi", false);
			finish();
		} else
			 /* ȡ��Intent�е�Bundle���� */ 
			bundle = this.getIntent().getExtras();  
		      
		    /* ȡ��Bundle�����е����� */ 
		    String strIP = bundle.getString("IP");  
		    
			autoConnect(strIP);
	}

	@Override
	/**
	 * ����finish����ʱ���ⷽ����������
	 * ����������Ϊ�գ����ø����onDestroy������Դ
	 */
	protected void onDestroy() {
		is = null;
		super.onDestroy();
	}

	private void init() {
		/**
		 * ��ȡ�ڱ�ActivityҪʹ�õĿؼ���WiFi
		 */
		hintTv = (TextView) findViewById(R.id.hintTv);
		ipEdt = (AutoCompleteTextView) findViewById(R.id.ip);
		portEdt = (EditText) findViewById(R.id.port);
		/**
		 * ��ΪҪ�õ�WIFI��Internet������AndroidMenufest.xml ���������Ȩ�� <uses-permission
		 * android:name="android.permission.INTERNET"/> <uses-permission
		 * android:name="android.permission.ACCESS_WIFI_STATE"/>
		 * <uses-permission
		 * android:name="android.permission.CHANGE_WIFI_STATE"/>
		 */
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		initSp();/* ��Ҫ�Ƿ��������ǰ��¼�ɹ��˵�IP */
	}

	/**
	 * ���������ļ�config������ /data/data/<package name>/shared_prefs/config.xml
	 * ȡ�������ļ���ip��ð�Ÿ�������Ϊ�Զ�����б�����������
	 */
	private void initSp() {
		sp = getSharedPreferences("config", MODE_PRIVATE);
		/* �����������ļ����Ժ�Ϳ���������edit�����������ļ��� */
		editor = sp.edit();
		String names[] = sp.getString("ip", "").split(":");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
				android.R.layout.simple_dropdown_item_1line, names);
		ipEdt.setAdapter(adapter);
	}

	/**
	 * �Զ����� �Ƚ���ȡ����wifi�ȵ��������ַ�����ӵ�wifi�ȵ���豸��ip�������������������߳�ɨ�������е�ip
	 * 
	 * @return
	 */
	private void autoConnect(String strIP) {
		ArrayList<String> addr = new ArrayList<String>();/* �������� ���ڴ��ip */

		dpInfo = wifi.getDhcpInfo();
		addr.add(int32ToIp(dpInfo.serverAddress));/* �ѷ���IP����������β�� */
		addr.addAll(Generic.getConnectedIP());// Adds the objects in the specified collection to this ArrayList

		// Ϊ����ִ������ʱ ���ῨסUI���ʲ����첽����ʽ�����������������Ҳ�ɲ�ʹ���첽����
		if (strIP != null) {
			new ConnectTask().execute(strIP);
		} else {
			//��Ϊ�����̵߳�ִ�з�������String����,����ҪtoArray	
			new ConnectTask().execute(addr.toArray(new String[addr.size()]));
		}
	}

	/**
	 * ����һ���ĸ�ʽ���������Ip
	 * 
	 * @param ip
	 * @return
	 */
	private String int32ToIp(int ip) {
		return (ip & 0xff) + "." + (ip >> 8 & 0xff) + "." + (ip >> 16 & 0xff)
				+ "." + (ip >> 24 & 0xff);
	}

	/**
	 * �ֶ����� Ϊ�ؼ��󶨼�������2�ַ��� 1.���������ļ������ã�findViewById()�ҵ��ؼ�������APIΪ�����Ӧ������
	 * 2.���������ļ������ã��ڲ����ļ���������Ӧ�ؼ���OnClick��Ȼ����Դ�ļ������ʵ����Ӧ�ؼ���OnClick//�����õľ����ⷽ��
	 * ��layoutĿ¼�µ�flash.xml��������connectBtn��Button�ؼ� ���"����"��ť�����ô˷���
	 * 
	 * @param v
	 */
	public void connectBtn(View v) {
		String ip = ipEdt.getText().toString();/* ��������IP */
		port = portEdt.getText().toString();/* �������Ķ˿� */

		// port����Ϊ��
		if (!port.equals("") && checkAddr(ip, Integer.valueOf(port))) {
			new ConnectTask().execute(ip);
		} else {
			Generic.showMsg(this, "����IP��Port", true);
		}
	}

	/**
	 * �ָ��ip��4�Σ�ip�˿ڷ�Χ��1000-65535
	 * 
	 * @param ip
	 * @param port
	 * @return
	 */
	private boolean checkAddr(String ip, int port) {
		if (ip.split("\\.").length != 4)
			return false;
		if (port < 1000 || port > 65535)
			return false;

		return true;
	}

	/**
	 * �����߳� ������������ں�̨�߳���ִ��http���ӣ����ӿ�ס����Ӱ��UI���У��ʺ�������ʱ��ϳ����ֲ���Ӱ��ǰ̨�̵߳����
	 * �첽������3������4��
	 * :onPreExecute()��doInBackground()��onProgressUpdate()��onPostExecute()
	 * onPreExecute()��������UI�̣߳�һ��Ϊ��̨�߳���׼���������û��ӿ���ʾ������
	 * doInBackground():��onPreExecuteִ�к����ϱ�������ִ�л��ѽϳ�ʱ��ĺ�̨���㣬������ֵ����onPostExecute
	 * onProgressUpdate():���û����� publishProgress()����������ִ�е�ʱ��δ���壬�������������������ʽ��ʾ����
	 * һ�����ڼ���һ��������������UI�ı�������ʾlogo onPostExecute():����̨����ִ�к���UI�̱߳��������Ѻ�ִ̨�еĽ��֪ͨ��UI
	 * ����һ:�����ں�̨��doInBackground�Ĳ�������
	 * ������:doInBackground�����֪ͨ��UI�̵߳ĵ�Ԫ���ͣ���������UI�߳�onProgressUpdate�Ĳ������ͣ�����û�õ�
	 * ������:doInBackground�ķ���ֵ��������onPostExecute������
	 * 
	 * @author Administrator
	 * 
	 */
	private class ConnectTask extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			for (int i = 0; i < params.length; i++) {
				String ip = params[i];/* ȡ��ÿһ��ip */

				if (ip.split("\\.").length == 4) {
					/**
					 * ��������۲컭��ʱ,Ҳ������������ַ�����ַ
					 */
					String action = "http://" + ip + ":" + port
							+ "/?action=stream";
					is = http(action);
					if (is != null) { /* ��һ�α�������IP���´ε�¼ʱ�ſ��ҵ�֮ǰ��¼�ɹ����IP */
						writeSp(ip);
						MjpegInputStream.initInstance(is);
						break;
					}
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (is != null) {
				/**
				 * Intent��Android���еĶ�����������Intentָ������Ҫִ�еĶ���(����:view,edit,dial)
				 * ��׼���ó���ִ�иù�������Ҫ�Ĳ��Ϻ�
				 * ��ֻҪ����startActivity��Androidϵͳ���Զ�Ѱ���������ָ��Ҫ���Ӧ�ó��� ��ִ�иó���
				 */
				startActivity(new Intent(FlashActivity.this, MainActivity.class));
				finish();/* ������Activity */
			} else {
				hintTv.setText(getResources()
						.getString(R.string.connect_failed));
				Generic.showMsg(mContext, "����ʧ��", true);
			}

			super.onPostExecute(result);
		}

		/**
		 * ���ܣ�http���� Android�ṩ����http�ͻ��ˣ� HttpURLConnection �� Apache HTTP
		 * Client�����Ƕ�֧��HTTPS�����ϴ��������ļ� ���ó�ʱʱ�䣬����IPV6�� connection pooling�� Apache
		 * HTTP client��Android2.2��֮ǰ�汾�н���BUG
		 * ����Android2.2��֮��HttpURLConnection�Ǹ��õ�ѡ�������������õ��� Apache HTTP Client
		 * ���Ƕ�IO�Ĳ��������漰�쳣������Ҫtry��catch
		 * 
		 * @param url
		 * @return InputStream
		 */
		private InputStream http(String url) {
			HttpResponse res;
			DefaultHttpClient httpclient = new DefaultHttpClient();/*
																	 * ����http�ͻ��ˣ�
																	 * ���ܵ������ĸ��ַ���
																	 */
			httpclient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 500);/* ���ó�ʱʱ�� */

			try {
				HttpGet hg = new HttpGet(url);/*
											 * ����GET������http API��
											 * GET������Ĭ�ϵ�HTTP���󷽷�
											 */
				res = httpclient.execute(hg);
				return res.getEntity().getContent(); // ����Ӧ�л�ȡ��Ϣʵ������
			} catch (IOException e) {
			}

			return null;
		}

	}

	/**
	 * ����SharedPreferences 1.���ж�ip�Ƿ���"ip"ֵ��û�оͽ���������data��ֵ��ip 2.ip��ֵ��ȡ����Ȼ����ð�ŷָ���
	 * 3.sp����ֻ�ܴ��10��ip�����������10�飬�����������ļ��ٸ��� 4.�������飬������е�ǰ��¼�ɹ���ip���򷵻�
	 * 5.�����ﲻ������¼�ɹ���ip���򽫵�ǰ��¼�ɹ���ip�����sp���鲢�ύ
	 * 
	 * @param ip
	 */
	private void writeSp(String data) {
		if (!sp.contains("ip")) {
			editor.putString("ip", data);
			editor.commit();
			return;
		}

		/**
		 * �����ļ�����ip����ʾ֮ǰ��¼�ɹ���
		 */
		String ip = sp.getString("ip", "");
		String[] ips = ip.split(":");

		if (ips.length >= 10) {
			editor.clear();
			editor.commit();
			editor.putString("ip", data);
			editor.commit();
			return;
		}

		for (int i = 0; i < ips.length; i++) {
			if (ips[i].equals(data))
				return;
		}
		editor.putString("ip", data + ":" + ip);/* ������ǰ�ɹ��˵�ip��ǰ�� */
		editor.commit();
	}

	/**
	 * �Զ���ɿ������ѡ�� �����"history_user"ImageView�ؼ�ʱ�����ø÷��� ����ֻ�Ǿ���ʵ��xml�ļ���Onclick
	 */
	public void showDropDown(View v) {
		ipEdt.showDropDown();
	}

}
