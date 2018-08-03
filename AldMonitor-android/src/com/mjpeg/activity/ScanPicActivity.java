package com.mjpeg.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.security.auth.callback.Callback;

import tools.Generic;

import com.mjpeg.pic.PicAdapter;
import com.mjpeg.pic.PicAdapter.viewHolder;
import com.mjpeg.pic.PicEntity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * ��Ƭ�����
 * �������listview����listview��һЩ����
 * @author Administrator
 */
public class ScanPicActivity extends Activity implements OnItemClickListener {
	private static int UPDATE_DATA = 1; 
	private Context mContext = this;
	private String picturePath = "";/*��������Mainactivity�����ͼƬ·��*/
	private PicAdapter mAdapter = null;
	private ListView mListView = null;
	/**
	 * ����һ��Handler�����ڴ�����յ�����Ϣ
	 * �����յ�����ϢΪUPDATE_DATAʱ��������Ƭ
	 * Handlerһ��������ʼ���½���
	 */
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.what == UPDATE_DATA)//������Ƭ
				/**
				 * mAdapter�Ǽ̳���BaseAdapter������notifyDataSetChanged����
				 * �ⷽ����֪ͨ����ͼƬ��Դ��caller��callerʹ����Щ��Դʱ��Ҫ���� 
				 */
				mAdapter.notifyDataSetChanged();
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/**
		 * pic_listview����ʼ����listview���ڣ���res/layout/pic_listview.xml����
		 */
		setContentView(R.layout.pic_listview);

		init();
	}

	/**
	 * 1.����id�ҵ�listview�ؼ�
	 * 2.���Intent��picturePath�ַ���,������listview����ΪpicturePath
	 * 3.ΪͼƬ�½�һ��Adapter
	 * 4.��Adapter�󶨵�listview��ҲΪlistview��һ��������
	 * 5.���ü����̵߳�ִ�з���������ͼƬ
	 */
	private void init() {
		mListView = (ListView) findViewById(R.id.list);
		picturePath = getIntent().getStringExtra("picturePath");
		setTitle(picturePath);
		if (!picturePath.equals("")) {
			mAdapter = new PicAdapter(mContext);
			mListView.setAdapter(mAdapter);
			mListView.setOnItemClickListener(ScanPicActivity.this);
			new LoadPicTask().execute();
		}else
			Generic.showMsg(mContext, "����SdCard", true);
	}

	/**
	 * �����listview����Ŀʱ�������ø÷���
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		/**
		 * ÿһ��File����һ���ļ�����
		 * 1.����textView������Ӧ��File
		 * 2.����Intent����������Action
		 * �������Ŀ��ʱ�򣬼���ͼƬ
		 */
		File f = new File(picturePath
				+ ((viewHolder) view.getTag()).textView.getText());/*ǰ��PicAdapter�Ѿ����ú�View��Tag*/
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);/*����intent��Action����,����Ӧ��ʱ���ҵ�*/
		intent.setDataAndType(Uri.fromFile(f), "image/*");/*����Intent��������Դ���������ͣ���ʾĬ�ϴ�ͼƬ*/
		startActivity(intent);
	}

	@Override
	/**
	 * ����:����ֻ���"MENU"��������menu.xml�����"����ɾ��"
	 * ����MenuInflater:
	 * MenuInflater������ʵ����MenuĿ¼�µ�Menu�����ļ��ġ���ͳ�����ϵĲ˵�������ҪOverride Activity��onCreateOptionsMenu
	 * Ȼ�����������Menu.add��Menu��һ����item�ӽ������Ƚϸ���,��ͨ��ʹ��MenuInflater���԰�Menu�Ĺ���ֱ�ӷ���Menu�����ļ���
	 * ����ʵ��ģ�ͣ�Model������ͼ��View���ķ��룬����Ҳ������ˬ����,��LayoutInflater��ȣ�MenuInflater���÷��򵥶���
	 * ���ȣ�MenuInflater��ȡ����ֻ��һ�֣�Activity.getMenuInflater()
	 * ��Σ�MenuInflater.inflater(int menuRes,Menu menu)(���ﲻ����inflater����static�����������������ã�ֻ��Ϊ����������)�ķ���ֵ��void��
	 * ��;�����MenuInflater.inflater���û�к��������ˡ���˵��ͨ�����ַ�ʽ��Menu�����ļ�д�ú�Ͳ����ڳ����ж�̬�޸���
	 * ������LayoutInflater.inflater����������ֵ��View�ͣ����Խ��к����Ľ�һ������
	 * ���⣬MenuInflaterֻ��һ��void inflater(int menuRes,Menu menu)�ǹ��췽��
	 * Menu�����ļ��е�icon��Դ��Android�Դ��ģ�ͨ������android��drawable/"��ȡ�� 
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	/**
	 * ����Ҫɾ����Ƭ ����û��ѡ��Ҫɾ����Щ��Ƭ���ͻ���ʾ����ѡ������Ƭ��
	 * ���ѡ������ɾ������Ƭ��������ɾ��ѡ���ͼƬ�𣿡��Ի��򣬵�ȷ�� ִ��ɾ����������֮��ɾ
	 * �ⷽ����ʾ���menu�˵���Ĳ���
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mAdapter == null || mAdapter.getSelectItem().size() == 0) {
			Generic.showMsg(mContext, "��ѡ������Ƭ", true);
			return true;
		}

		/**
		 * �����Ի��򣬲�ʵ��onClick����
		 */
		new AlertDialog.Builder(this).setMessage("ɾ��ѡ���ͼƬ��")
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (delPics()) /*ɾ����Ƭ�ɹ�*/
							Generic.showMsg(mContext, "ɾ���ɹ�", true);
						else
							Generic.showMsg(mContext, "ɾ��ʧ��", true);
						mAdapter.notifyDataSetChanged();// ���͸�Handler
					}
				}).setNegativeButton("ȡ��", null).create().show();

		return true;
	}

	/**
	 * ɾ����Ƭ����
	 * 1.��ô��ʵ�������
	 * 2.�������Ŀ��
	 * 3.����(�������)
	 * 4.����������ɾ��ѡ�е���Ƭ
	 * 5.�Ƴ��������ʵ��
	 * @return
	 */
	private boolean delPics() {
		List<PicEntity> picList = mAdapter.getData();/*�����̻߳�������ݵ�list��,���listview�Ĵ��ͼƬʵ���list���͵�����*/
		List<Integer> selectItemList = null;

		selectItemList = mAdapter.getSelectItem();/*��ȡlistview����Ŀ����*/

		sortDescendByIndex(selectItemList);
		for (int i = 0; i < selectItemList.size(); i++) { /*����������Ŀ*/
			PicEntity entity = picList.get(selectItemList.get(i));/*���pictureʵ��*/

			if (!new File(picturePath + "/" + entity.getName()).delete())/*ɾ��*/
				return false;

			if (!picList.remove(entity))/*�Ƴ�*/
				return false;
		}

		return true;
	}

	/**
	 * �������Ž�������
	 * 
	 * @param list:��������
	 */
	private void sortDescendByIndex(List<Integer> list) {
		Collections.sort(list, new Generic.DescendSortByIndex());
	}
	
	/**
	 * ��������޸�ʱ�併������
	 * 
	 * @param list:File����
	 */
	private void sortDescendByTime(List<File> list) {
		Collections.sort(list, new Generic.DescendSortByTime());
	}

	/**
	 * ������Ƭ�߳�
	 * 
	 * @author Administrator
	 * 
	 */
	private class LoadPicTask extends AsyncTask<String, Integer, String> {
		private int step = 5;
		private List<File> picList = new ArrayList<File>();
		
		@Override
		/**
		 * 1.���г�picturepath��File����
		 * 2.����picFile���飬��������ÿһ��File����list����
		 * 3.�����ļ����޸�ʱ������
		 */
		protected void onPreExecute() {
			File[] picFile = new File(picturePath).listFiles();
			
			for(int i=0; i<picFile.length; i++)
				picList.add(picFile[i]);
			
			sortDescendByTime(picList);
			super.onPreExecute();
		}
		
		/**
		 * �̷߳�ҳ��������
		 * 1.�½����File�����PicEntity������
		 * 2.���list���������ݲ������ݴ�ŵ�File����
		 * 3.���File�����ݣ�����File�����ҵ�pictureʵ�壬�������
		 * 4.����õ�pictureʵ�����list
		 */
		@Override
		protected String doInBackground(String... params) {
			//���ڴ�ŷ�ҳfile����
			List<File> tmpList = new ArrayList<File>();
			//���ڴ�ŷ�ҳ�õ���picʵ��
			List<PicEntity> tmpEntity = new ArrayList<PicEntity>();
			int sum = picList.size();// 5
			int time = sum / step + 1;// 5 / 5 + 1 = 2			
			/**
			 * ����time�Σ�һ�μ���step��
			 * ��һ��for����time��
			 * �ڶ���for��time=0������0-4�ţ�time=1������5-9�Σ���������
			 */
			for(int j=0; j<time; j++){ 
				for(int i=0; (i<step)&&(j*step+i<sum); i++)
					tmpList.add(picList.get(j*step+i));
				
				if(tmpList.size() > 0){
					tmpEntity = getPicEntity(tmpList);
					tmpList.clear();
					mAdapter.addData(tmpEntity);
				
					handler.sendEmptyMessage(UPDATE_DATA);/*��ͼƬ��������Ϣ*/
				}
			}
			return null;
		}

		/**
		 * �õ���ҳfile��Ӧ��picʵ�弯��
		 * 1.��ô�������list<File>���͵�������File
		 * 2.�����File������ļ���
		 * 3.����ļ��������ض���׺�����ģ�����pictrueʵ�岢�����List
		 * @param path
		 * @return
		 */
		private List<PicEntity> getPicEntity(List<File> picList) {
			List<PicEntity> list = new ArrayList<PicEntity>();/*�������PicEntity��list*/

			for (int i = 0; i < picList.size(); i++) {
				File f = picList.get(i);
				if (f.isFile()) {
					String fileName = f.getName();

					if (fileName.endsWith(".jpg") || fileName.endsWith("jpeg")
							|| fileName.endsWith(".png"))
						list.add(new PicEntity(fileName, Generic.getShrinkedPic(f)));
				}
			}

			return list;
		}

	}
}
