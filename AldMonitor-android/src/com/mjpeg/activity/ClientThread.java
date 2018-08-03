package com.mjpeg.activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class ClientThread extends Thread {

	private OutputStream outputStream = null;
	private InputStream inputStream = null;
	private Socket socket;
	private SocketAddress socketAddress;
	public static Handler childHandler;
	private boolean RxFlag = true;
	private RxThread rxThread;
	final int TEXT_INFO = 12;
	static final int RX_EXIT = 11;
	static final int TX_DATA = 10;
	Context mainContext;
	Message msg;
	private String strIP;
	final int SERVER_PORT = 33333;
	byte cNodeData[][] = new byte[4][6]; // [5] 0=温度 1=湿度 2=气体 3=灯
	int RxCount = 0, nRecvLen, index = 4;
	byte CheckSum;
	// byte strRxBuf[] = new byte[256];
	int ucRecvLen = 7;

	public ClientThread(String ip) {
		strIP = ip;
	}

	// 连接网络
	void connect() {
		RxFlag = true;
		socketAddress = new InetSocketAddress(strIP, SERVER_PORT);
		socket = new Socket();

		try {
			socket.connect(socketAddress, SERVER_PORT);
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();

			msg = WsnActivity.mainHandler.obtainMessage(
					WsnActivity.TIPS_UPDATE_UI, "连接成功");
			WsnActivity.mainHandler.sendMessage(msg);

			rxThread = new RxThread();
			rxThread.start();

		} catch (IOException e) {
			try {
				sleep(10);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			msg = WsnActivity.mainHandler.obtainMessage(
					WsnActivity.TIPS_UPDATE_UI, "无法连接到服务器");
			WsnActivity.mainHandler.sendMessage(msg);
			e.printStackTrace();
		} catch (NumberFormatException e) {

		}
	}

	void initChildHandler() {

		Looper.prepare(); // 在子线程中创建Handler必须初始化Looper

		childHandler = new Handler() {
			// 子线程消息处理中心
			public void handleMessage(Message msg) {

				// 接收主线程及其他线程的消息并处理...
				switch (msg.what) {
				case TX_DATA:
					int len = msg.arg1;

					try {
						if (len != 7) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						outputStream.write((byte[]) msg.obj, 0, len);
						outputStream.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;

				case RX_EXIT:
					RxFlag = false;
					try {
						if (socket.isConnected()) {
							inputStream.close();
							outputStream.close();
							socket.close();
						}

					} catch (IOException e1) {
						e1.printStackTrace();
					}

					childHandler.getLooper().quit();// 结束消息队列

					break;

				default:
					break;
				}

			}
		};

		// 启动该线程的消息队列
		Looper.loop();

	}

	public void run() {
		connect();
		initChildHandler();
		msg = WsnActivity.mainHandler.obtainMessage(WsnActivity.TIPS_UPDATE_UI,
				"与服务器断开连接");
		WsnActivity.mainHandler.sendMessage(msg);
	}

	byte GetDataLen(byte fc) {
		byte len = 0;

		switch (fc) {
		case 0x01:
			len = 26;
			break;
		case 0x07:
		case 0x08:
		case 0x0A:
		case 0x0B:
		case 0x0C:
		case 0x0D:
			len = 7;
			break;
		}

		return len;
	}

	byte XorCheckSum(byte pBuf[], int len) {
		byte i;
		byte byRet = 0, b;
		int nRet = 0;

		if (len == 0)
			return byRet;
		else
			byRet = pBuf[0];

		for (i = 1; i < len; i++)
			nRet = byRet ^ pBuf[i];
		byRet = (byte) nRet;
		return byRet;
	}

	// socket 接收线程
	public class RxThread extends Thread {
		public void run() {
			try {
				while (socket.isConnected() && RxFlag) {
					byte strRxBuf[] = new byte[30];
					byte i;
					int RxIndex, len, readBytes = 0;

					while (readBytes < ucRecvLen) {
						len = inputStream.read(strRxBuf, readBytes, ucRecvLen
								- readBytes);

						if (strRxBuf[0] == 0x3A && len >= 4) {
							ucRecvLen = GetDataLen(strRxBuf[3]);
							readBytes += len;
						} else {
							strRxBuf[0] = 0;
							readBytes = 0;
						}
						if (len == -1)
							break;
					}

					if (strRxBuf[ucRecvLen - 1] == 0x23)
					{
						if (ucRecvLen == 26) {
							for (i = 0; i < WsnActivity.MAX_NODE; i++) {
								System.arraycopy(strRxBuf, index,
										WsnActivity.NodeData[i], 0, 5);
								index += 5;
							}
							index = 4;
							msg = WsnActivity.mainHandler.obtainMessage(
									WsnActivity.RX_DATA_UPDATE_UI, "Connect");
							WsnActivity.mainHandler.sendMessage(msg);
							// RefreshData();
							// OnReceive(ClientSock[0]); //andy
							len =readBytes= 0;
							strRxBuf[0] = 0;
							ucRecvLen = 7;
						} else {
							len =readBytes= 0;
							strRxBuf[0] = 0;
							ucRecvLen = 7;
						}

					} else {
						len =readBytes= 0;
						strRxBuf[0] = 0;
						ucRecvLen = 7;
					}

					if (index > 100) {
						index = 0;
						len =readBytes= 0;
						strRxBuf[0] = 0;
						ucRecvLen = 7;
					}

				}
				if (socket.isConnected())
					socket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}