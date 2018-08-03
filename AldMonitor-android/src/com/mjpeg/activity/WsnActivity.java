package com.mjpeg.activity;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class WsnActivity extends Activity {
	private TextView textNode1, textTemp1,
			textTemp2, textHumi1, textHumi2;
	private ImageButton btnLamp1;
	static TextView textTips;
	private ImageView ivGas1,ivsecurity;
	private Button btnNetwork, btnExit, btnLampAll;
	private Button btnBeepON, btnBeepOFF, btnCurtainON, btnCurtainOFF;
	static final int RX_DATA_UPDATE_UI = 1;
	final int TX_DATA_UPDATE_UI = 2;
	static final int TIPS_UPDATE_UI = 3;
	final int READ_ALL_INFO = 4;
	final int WRITE_LAMP = 5;
	final int WRITE_LAMP_ALL = 6;
	
	static final int WR_CMD = 7;
	final int BEEP_ON = 1;
	final int BEEP_OFF = 2;
	final int CN_ON = 3;
	final int CN_OFF = 4;
	
	static final int MAX_NODE = 4;

	public static Handler mainHandler;
	private ClientThread clientThread = null;
	private Timer mainTimer;

	static byte NodeData[][] = new byte[MAX_NODE][6];; // [5] 0=温度 1=湿度 2=气体 3=灯
	byte SendBuf[] = { 0x3A, 0x00, 0x01, 0x0A, 0x00, 0x00, 0x23, 0x00 };
	byte strBeepON[] = { 0x3A, 0x00, 0x05, 0x07, 0x01, 0x39, 0x23, 0x00 };
	byte strBeepOFF[] = { 0x3A, 0x00, 0x05, 0x07, 0x00, 0x3C, 0x23, 0x00 };
	byte strCN_ON[] = { 0x3A, 0x00, 0x06, 0x08, 0x01, 0x35, 0x23, 0x00 };
	byte strCN_OFF[] = { 0x3A, 0x00, 0x06, 0x08, 0x02, 0x36, 0x23, 0x00 };
	private String strTemp, strHumi;
	private Message MainMsg;
	final byte LAMP_ON = 0;
	final byte LAMP_OFF = 1;
	private byte LampAllState = LAMP_ON;
	private String strIpAddr = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.wsn_main);/* 设置布局为res/layout/flash.xml */

		initControl();
		initMainHandler();
	}

	class ButtonClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			/*if (clientThread == null && (v.getId() != R.id.btn_exit)
					&& (v.getId() != R.id.btn_network)
					&& (v.getId() != R.id.btn_video)) {
				textTips.setText("提示信息：请先连接网络");
				return;
			}
*/
			switch (v.getId()) {
			case R.id.btn_network: // 连接网络
				showDialog(WsnActivity.this);
				break;
			case R.id.btn_lamp_all: // 广播操作 开关所有灯
				MainMsg = mainHandler.obtainMessage(TX_DATA_UPDATE_UI,
						WRITE_LAMP_ALL, 0xFF);
				mainHandler.sendMessage(MainMsg);
				break;
			case R.id.image_lamp1: // 开关终端1的灯
				MainMsg = mainHandler.obtainMessage(TX_DATA_UPDATE_UI,
						WRITE_LAMP, 1);
				mainHandler.sendMessage(MainMsg);
				break;
			case R.id.btn_beep_on: //新增
				MainMsg = mainHandler.obtainMessage(WR_CMD,	BEEP_ON, 5);
				mainHandler.sendMessage(MainMsg);
				break;	
			case R.id.btn_beep_off:
				MainMsg = mainHandler.obtainMessage(WR_CMD, BEEP_OFF, 5);
				mainHandler.sendMessage(MainMsg);
				break;	
			case R.id.btn_cn_on:
				MainMsg = mainHandler.obtainMessage(WR_CMD, CN_ON, 6);
				mainHandler.sendMessage(MainMsg);
				break;	
			case R.id.btn_cn_off:
				MainMsg = mainHandler.obtainMessage(WR_CMD, CN_OFF, 6);
				mainHandler.sendMessage(MainMsg);
				break;	
			case R.id.btn_exit: // 退出系统
				if (clientThread != null) {
					MainMsg = ClientThread.childHandler
							.obtainMessage(ClientThread.RX_EXIT);
					ClientThread.childHandler.sendMessage(MainMsg);
				}

				System.exit(0);
				break;
			}
		}
	}

	// 显示连接对话框
	private void showDialog(Context context) {
		final EditText editIP = new EditText(context);
		editIP.setText("172.20.10.6");

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		// builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("请输入服务器IP地址");
		builder.setView(editIP);
		builder.setPositiveButton("连接", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				strIpAddr = editIP.getText().toString();
				boolean ret = isIPAddress(strIpAddr);

				if (ret) {
					textTips.setText("服务器IP地址:" + strIpAddr);
				} else {
					strIpAddr = null;
					textTips.setText("IP地址不合法，请重新设置");
					return;
				}

				clientThread = new ClientThread(strIpAddr);// 建立客户端线程
				clientThread.start();

				mainTimer = new Timer();// 定时查询所有终端信息
				setTimerTask();
			}
		});
		builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if (clientThread != null) {
					MainMsg = ClientThread.childHandler
							.obtainMessage(ClientThread.RX_EXIT);
					ClientThread.childHandler.sendMessage(MainMsg);
					textTips.setText("与服务器断开连接");
				}
			}
		});

		builder.show();
	}

	private void setTimerTask() {
		mainTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (clientThread != null) {
					MainMsg = mainHandler.obtainMessage(TX_DATA_UPDATE_UI,
							READ_ALL_INFO, 0xFF);
					mainHandler.sendMessage(MainMsg);
				}
			}
		}, 500, 450);// 表示500毫秒之后，每隔1000毫秒执行一次
	}

	// 通知客户端线程 发送消息
	void SendData(byte buffer[], int len) {
		MainMsg = ClientThread.childHandler.obtainMessage(ClientThread.TX_DATA,
				len, 0, (Object) buffer);
		ClientThread.childHandler.sendMessage(MainMsg);

	}

	void initMainHandler() {
		mainHandler = new Handler() {

			// 主线程消息处理中心
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case RX_DATA_UPDATE_UI:
					// 终端1
					strTemp = "温度：" + NodeData[0][0] + "℃";
					textTemp1.setText(strTemp);
					strHumi = "湿度：" + NodeData[0][1] + "%";
					textHumi1.setText(strHumi);
					if (NodeData[0][2] == 1)
						ivGas1.setImageResource(R.drawable.gas_off); // 气体高电平时正常
					else
						ivGas1.setImageResource(R.drawable.gas_on); // 气体低电平时异常

					if (NodeData[0][3] == 0) // 低电平亮，高电平灭
						btnLamp1.setImageResource(R.drawable.lamp_on); // 灯亮
					else
						btnLamp1.setImageResource(R.drawable.lamp_off); // 灯灭

					// 终端2
					strTemp = "亮度：" + NodeData[0][3] ;
					textTemp2.setText(strTemp);
					
					
					if (NodeData[0][4] == 1) // 低电平亮，高电平灭
						{ivsecurity.setImageResource(R.drawable.ledon); // 灯亮
					    textHumi2.setText("有人闯入");}
					else
						{ivsecurity.setImageResource(R.drawable.ledoff); // 灯灭
						//textHumi2.setTextColor(0x99FFFF);
					    textHumi2.setText("无人闯入");}
					break;

				case TX_DATA_UPDATE_UI: // msg.arg1保存功能码 arg2保存终端地址
					switch (msg.arg1) {
					case READ_ALL_INFO:
						SendBuf[2] = (byte) msg.arg2;// 0xFF;
						SendBuf[3] = 0x01; // FC
						SendBuf[4] = (byte) 0xC4;
						SendBuf[5] = (byte) 0x23;

						SendData(SendBuf, 6); // 查询所有终端报文3A 00 FF 01 C4 23
						break;
					case WRITE_LAMP:
						SendBuf[2] = (byte) msg.arg2;
						SendBuf[3] = 0x0A;
						if (NodeData[SendBuf[2] - 1][3] == LAMP_ON) { // 当前灯处于熄灭状态，发命令将灯点亮
							NodeData[SendBuf[2] - 1][3] = 0x01;
							SendBuf[4] = 0x01; // data
						} else {
							NodeData[SendBuf[2] - 1][3] = 0x00;
							SendBuf[4] = 0x00;
						}

						SendBuf[5] = XorCheckSum(SendBuf, 5);

						SendData(SendBuf, 7); // 发命令控制灯 3A 00 01 0A 00 00 23
						break;
					case WRITE_LAMP_ALL:
						SendBuf[2] = (byte) 0xFF;
						SendBuf[3] = 0x0A;
						SendBuf[4] = LampAllState;
						if (LampAllState == LAMP_ON) {
							LampAllState = LAMP_OFF;
						} else {
							LampAllState = LAMP_ON;
						}
						SendBuf[5] = XorCheckSum(SendBuf, 5);

						SendData(SendBuf, 7); // 发命令控制灯
						break;

					default:
						break;
					}
					break;
				case WR_CMD: // msg.arg1保存编号  arg2保存功能码
					switch (msg.arg1) {
					case BEEP_ON:
						SendData(strBeepON, 7);
						break;
					case BEEP_OFF:
						SendData(strBeepOFF, 7);
						break;
					case CN_ON:
						SendData(strCN_ON, 7);
						break;
					case CN_OFF:
						SendData(strCN_OFF, 7);
						break;
					default:
						break;
					}
					break;
				case TIPS_UPDATE_UI:
					String str = (String) msg.obj;
					textTips.setText(str);
					break;
				}
				super.handleMessage(msg);
			}
		};
	}

	void initControl() {
		// ----------------------node 1----------------------
		textNode1 = (TextView) findViewById(R.id.node_title1);
		textNode1.setBackgroundResource(R.drawable.node1);
		textTemp1 = (TextView) findViewById(R.id.temperature1);
		textTemp1.setText(R.string.init_temp);
		textHumi1 = (TextView) findViewById(R.id.humidity1);
		textHumi1.setText(R.string.init_humi);
		ivGas1 = (ImageView) findViewById(R.id.image_gas1);
		ivGas1.setImageResource(R.drawable.gas_off);
		btnLamp1 = (ImageButton) findViewById(R.id.image_lamp1);
		btnLamp1.setImageResource(R.drawable.lamp_off);
		// ----------------------node 2----------------------
		textTemp2 = (TextView) findViewById(R.id.temperature2);
		textTemp2.setText(R.string.init_bri);
		textHumi2 = (TextView) findViewById(R.id.humidity2);
		textHumi2.setText(R.string.init_security);
		ivsecurity=(ImageView)findViewById(R.id.image_security);
		ivsecurity.setImageResource(R.drawable.ledoff);
		textTips = (TextView) findViewById(R.id.Tips);
		textTips.setText(R.string.init_tips);

		btnNetwork = (Button) findViewById(R.id.btn_network);
		btnNetwork.setOnClickListener(new ButtonClick());
		btnExit = (Button) findViewById(R.id.btn_exit);
		btnExit.setOnClickListener(new ButtonClick());
		btnLampAll = (Button) findViewById(R.id.btn_lamp_all);
		btnLampAll.setOnClickListener(new ButtonClick());
		btnLamp1.setOnClickListener(new ButtonClick());
		btnBeepON = (Button) findViewById(R.id.btn_beep_on);
		btnBeepON.setOnClickListener(new ButtonClick());
		btnBeepOFF = (Button) findViewById(R.id.btn_beep_off);
		btnBeepOFF.setOnClickListener(new ButtonClick());
		btnCurtainON = (Button) findViewById(R.id.btn_cn_on);
		btnCurtainON.setOnClickListener(new ButtonClick());
		btnCurtainOFF = (Button) findViewById(R.id.btn_cn_off);
		btnCurtainOFF.setOnClickListener(new ButtonClick());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mainTimer.cancel();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	// 判断输入IP是否合法
	private boolean isIPAddress(String ipaddr) {
		boolean flag = false;
		Pattern pattern = Pattern
				.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
		Matcher m = pattern.matcher(ipaddr);
		flag = m.matches();
		return flag;
	}

	private byte XorCheckSum(byte[] pBuf, int len) {
		int i;
		byte byRet = 0;

		if (len == 0)
			return byRet;
		else
			byRet = pBuf[0];

		for (i = 1; i < len; i++)
			byRet = (byte) (byRet ^ pBuf[i]);

		return byRet;
	}

}
