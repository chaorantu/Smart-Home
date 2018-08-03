// RFonlineDlg.cpp : implementation file
//

#include "stdafx.h"
#include "RFonline.h"
#include "RFonlineDlg.h"
#include "iPlotChannelX.h"
#include "iPlotAxisX.h"

#include <atlbase.h>//首先要包含头文件
#include "SortStringArray.h"


#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

#define DEFAULT_PORT    33333
#define MAX_NODE        0x04
#define BUFFER_SIZE     128

SOCKET ServerSock;
SOCKET ClientSock;
char szBuf[BUFFER_SIZE];
char strTxBuf[BUFFER_SIZE];
BYTE strRxBuf[BUFFER_SIZE];
BYTE ucRecvLen=0;                //报文长度
int	 RxIndex = 0;	             //接收数据下标 计数
BYTE LampAllState=0;             //默认第一次打开所有灯
int j=0;
BYTE NodeData[MAX_NODE][6];  //[5] 0=温度 1=湿度 2=气体 3=灯

/////////////////////////////////////////////////////////////////////////////
// CAboutDlg dialog used for App About

class CAboutDlg : public CDialog
{
public:
	CAboutDlg();

// Dialog Data
	//{{AFX_DATA(CAboutDlg)
	enum { IDD = IDD_ABOUTBOX };
	//}}AFX_DATA

	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CAboutDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:
	//{{AFX_MSG(CAboutDlg)
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

CAboutDlg::CAboutDlg() : CDialog(CAboutDlg::IDD)
{
	//{{AFX_DATA_INIT(CAboutDlg)
	//}}AFX_DATA_INIT
}

void CAboutDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CAboutDlg)
	//}}AFX_DATA_MAP
}

BEGIN_MESSAGE_MAP(CAboutDlg, CDialog)
	//{{AFX_MSG_MAP(CAboutDlg)
		// No message handlers
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CRFonlineDlg dialog

CRFonlineDlg::CRFonlineDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CRFonlineDlg::IDD, pParent)
{
	//{{AFX_DATA_INIT(CRFonlineDlg)
	//}}AFX_DATA_INIT
	// Note that LoadIcon does not require a subsequent DestroyIcon in Win32
	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);
}

void CRFonlineDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CRFonlineDlg)
	DDX_Control(pDX, IDC_BTN_CLOSE_ALL, m_btnLampAll);
	DDX_Control(pDX, IDC_STATIC_NETWORK, m_StaticNet);
	DDX_Control(pDX, IDC_BTN_UPDATE_MANUAL, m_btnManual);
	DDX_Control(pDX, IDC_BTN_UPDATE_AUTO, m_btnAuto);
	DDX_Control(pDX, IDC_BUTTON1, m_btnSmart);
	DDX_Control(pDX, IDC_BUTTON4, m_btnOut);
	DDX_Control(pDX, IDC_BTN_OPEN_NET, m_btnNetwork);
	DDX_Control(pDX, IDC_BTN_OPEN_COM, m_btnCom);
	DDX_Control(pDX, IDC_STATIC_COM, m_StaticCom);
	DDX_Control(pDX, IDC_STATIC_GAS1, m_StaticGas1);
	DDX_Control(pDX, IDC_Test, m_Test);
	DDX_Control(pDX, IDC_BTN_LAMP1, m_btnLamp1);
	DDX_Control(pDX, IDC_IPADDRESS, m_IPAddress);
	DDX_Control(pDX, IDC_EDIT_REVMSG, m_EditText);
	DDX_Control(pDX, IDC_EDIT1, m_EditText1);
	DDX_Control(pDX, IDC_COMBOPORT, m_ComSel);
	DDX_Text(pDX, IDC_TMP1, m_temp1);
	DDX_Control(pDX, IDC_ITHERMOMETERX1, m_tempDisp1);
	DDX_Control(pDX, IDC_IPLOTX1, m_plotx);
	DDX_Radio(pDX, IDC_RADIO1, m_SelectPlotIndex);
	DDX_Text(pDX, IDC_HUMIDITY1, m_Humidity1);
	DDX_Text(pDX, IDC_BRI, m_Bri);
	//}}AFX_DATA_MAP
}
BEGIN_MESSAGE_MAP(CRFonlineDlg, CDialog)
	//{{AFX_MSG_MAP(CRFonlineDlg)
	ON_WM_SYSCOMMAND()
	ON_WM_PAINT()
	ON_WM_QUERYDRAGICON()
	ON_WM_TIMER()
	ON_BN_CLICKED(IDC_RADIO1, OnRadio1)
	ON_BN_CLICKED(IDC_RADIO2, OnRadio2)
	ON_BN_CLICKED(IDC_BTN_LAMP1, OnBtnLamp1)
	ON_BN_CLICKED(IDC_BTN_UPDATE_MANUAL, OnBtnManualUpdate)
	ON_BN_CLICKED(IDC_BTN_UPDATE_AUTO, OnBtnAutoUpdate)
	ON_BN_CLICKED(IDC_BTN_OPEN_COM, OnBtnOpenCom)
	ON_BN_CLICKED(IDC_BTN_OPEN_NET, OnBtnOpenNet)
	ON_MESSAGE(WM_COMM_RXCHAR, OnComm)
	ON_MESSAGE(NETWORK_EVENT, OnSocketMsg)
	ON_BN_CLICKED(IDC_BTN_CLOSE_ALL, OnBtnCloseAll)
	//}}AFX_MSG_MAP
	ON_BN_CLICKED(IDC_BTN_BEEP_ON, &CRFonlineDlg::OnBnClickedBtnBeepOn)
	ON_BN_CLICKED(IDC_BTN_BEEP_OFF, &CRFonlineDlg::OnBnClickedBtnBeepOff)
	ON_BN_CLICKED(IDC_BTN_DJ_ON, &CRFonlineDlg::OnBnClickedBtnDjOn)
	ON_BN_CLICKED(IDC_BTN_DJ_OFF, &CRFonlineDlg::OnBnClickedBtnDjOff)
	ON_CBN_SELCHANGE(IDC_COMBOPORT, &CRFonlineDlg::OnCbnSelchangeComboport)
	ON_EN_CHANGE(IDC_TMP1, &CRFonlineDlg::OnEnChangeTmp1)
	ON_BN_CLICKED(IDC_BUTTON1, &CRFonlineDlg::OnBnClickedButton1)
	ON_BN_CLICKED(IDC_BUTTON4, &CRFonlineDlg::OnBnClickedButton4)
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CRFonlineDlg message handlers

BOOL CRFonlineDlg::OnInitDialog()
{
	CDialog::OnInitDialog();

	// Add "About..." menu item to system menu.

	// IDM_ABOUTBOX must be in the system command range.
	ASSERT((IDM_ABOUTBOX & 0xFFF0) == IDM_ABOUTBOX);
	ASSERT(IDM_ABOUTBOX < 0xF000);

	CMenu* pSysMenu = GetSystemMenu(FALSE);
	if (pSysMenu != NULL)
	{
		CString strAboutMenu;
		strAboutMenu.LoadString(IDS_ABOUTBOX);
		if (!strAboutMenu.IsEmpty())
		{
			pSysMenu->AppendMenu(MF_SEPARATOR);
			pSysMenu->AppendMenu(MF_STRING, IDM_ABOUTBOX, strAboutMenu);
		}
	}

	// Set the icon for this dialog.  The framework does this automatically
	//  when the application's main window is not a dialog
	SetIcon(m_hIcon, TRUE);			// Set big icon
	SetIcon(m_hIcon, FALSE);		// Set small icon
	
	// TODO: Add extra initialization here

	//获取系统中的串口个数
	CString pNameList[10];
	CString pPortList[10];

	CSortStringArray sortArray;

	int SerialCount=GetSystemPorts(pNameList,pPortList);

	//向Combo Box中添加串口个数
	for(int j=0;j<SerialCount;j++)
	{
		sortArray.Add(pPortList[j]);
	}
	sortArray.Sort();

	for(int i=0;i<SerialCount;i++)
	{
		((CComboBox*)GetDlgItem(IDC_COMBOPORT))->AddString(sortArray.GetAt(i));
		if(i == 0)
			m_ComSel.SetCurSel(0);
	}

	m_ComSel.SetCurSel(0);      //默认串口为Com1
	m_bComState = FALSE;        //初始化串口为关闭状态
	m_bAutoState = FALSE;       //初始化自动刷新为关闭状态
	m_bNetState = FALSE;        //初始化网络为关闭状态
	m_bSmartState=FALSE;
	m_bOutState=FALSE;
	m_CurtainState=FALSE;
	m_bLampState1=0;
	m_LampState=TRUE;
	GetDlgItem(IDC_BTN_UPDATE_MANUAL)->EnableWindow(FALSE);
	GetDlgItem(IDC_BTN_UPDATE_AUTO)->EnableWindow(FALSE);
	GetDlgItem(IDC_BTN_OPEN_NET)->EnableWindow(FALSE);
	GetDlgItem(IDC_BTN_CLOSE_ALL)->EnableWindow(FALSE);
	m_plotx.RemoveAllChannels();
	m_plotx.AddChannel();
	m_plotx.GetChannel(0).SetTitleText("终端1");
	m_bmp[0].LoadBitmap(IDB_BMP_OPEN);
	m_bmp[1].LoadBitmap(IDB_BMP_CLOSE);
	m_bmp[2].LoadBitmap(IDB_BMP_LED_ON);
	m_bmp[3].LoadBitmap(IDB_BMP_LED_OFF);
	m_bmp[4].LoadBitmap(IDB_BMP_GAS_ON);
	m_bmp[5].LoadBitmap(IDB_BMP_GAS_OFF);
	m_bmp[6].LoadBitmap(IDB_BMP_LED_ON);
	OnIPAddress();	     //获得本机IP地址 m_IPAddress.SetAddress(192,168,1,168);	
	m_IPAddress.SetWindowText(IpAddr);

	m_SelectPlotIndex = 0;
	//初始化灯的状态，1为熄灭状态，0为点亮
	strRxBuf[7] = 1;
	strRxBuf[11] = 1;
	strRxBuf[15] = 1;
    strRxBuf[19] = 1;
	strRxBuf[3] = 0x01; 
	m_btnLamp1.SetBitmap(HBITMAP(m_bmp[2]));
	RefreshData();      //初始化画面上所有终端的数据
	
	return TRUE;  // return TRUE  unless you set the focus to a control
}

void CRFonlineDlg::OnSysCommand(UINT nID, LPARAM lParam)
{
	if ((nID & 0xFFF0) == IDM_ABOUTBOX)
	{
		CAboutDlg dlgAbout;
		dlgAbout.DoModal();
	}
	else
	{
		CDialog::OnSysCommand(nID, lParam);
	}
}

// If you add a minimize button to your dialog, you will need the code below
//  to draw the icon.  For MFC applications using the document/view model,
//  this is automatically done for you by the framework.
void CRFonlineDlg::OnPaint() 
{
	if (IsIconic())
	{
		CPaintDC dc(this); // device context for painting

		SendMessage(WM_ICONERASEBKGND, (WPARAM) dc.GetSafeHdc(), 0);

		// Center icon in client rectangle
		int cxIcon = GetSystemMetrics(SM_CXICON);
		int cyIcon = GetSystemMetrics(SM_CYICON);
		CRect rect;
		GetClientRect(&rect);
		int x = (rect.Width() - cxIcon + 1) / 2;
		int y = (rect.Height() - cyIcon + 1) / 2;

		// Draw the icon
		dc.DrawIcon(x, y, m_hIcon);
	}
	else
	{
		CDialog::OnPaint();
	}
}

// The system calls this to obtain the cursor to display while the user drags
//  the minimized window.
HCURSOR CRFonlineDlg::OnQueryDragIcon()
{
	return (HCURSOR) m_hIcon;
}

//查询所有终端报文3A 00 ff 01 C4 23
//响应：3A 00 FF 01 1E 48 01 01 00 00 01 01 00 00 00 00 00 00 00 00 92 23
LONG CRFonlineDlg::OnComm(WPARAM ch, LPARAM port)
{
	BYTE CheckSum=0;

	strRxBuf[RxIndex] = (BYTE)ch;
	RxIndex++;

	if (strRxBuf[0] == 0x3A)
	{
		if(RxIndex == 4)	
		{
			ucRecvLen = GetDataLen(strRxBuf[3]);	
			if (ucRecvLen == 0)
			{
				strRxBuf[0] = 0;
				RxIndex = 0;
			}
		}

		if (ucRecvLen != 0 && RxIndex >= ucRecvLen)
		{
			CheckSum = XorCheckSum(strRxBuf, RxIndex-2);
			//检查报文是否正确
			if (CheckSum == strRxBuf[RxIndex-2] && strRxBuf[RxIndex-1] == 0x23) 
			{
				RefreshData();  //更新画面中的数据
				//OnReceive(ClientSock[0]); //andy
				RxIndex=0;
				memset(strRxBuf, 0, BUFFER_SIZE);
			} 
			else
			{
				RxIndex=0;
				memset(strRxBuf, 0, BUFFER_SIZE);
			}
		}
	} 
	else
	{
		RxIndex=0;
		memset(strRxBuf, 0, BUFFER_SIZE);
	}

	return 0;
}

//BYTE strTest[30] ={0x3A,0x00,0xFF,0x01,0x1E,0x48,0x01,0x01,0x00,0x00,0x01,0x01,0xED,0x43,0x00,0x00,0x19,0x45,0x00,0x00,0x60,0x23,0x00};
//响应：3A 00 FF 01 1E 48 01 01 00 00 01 01 00 00 00 00 00 00 00 00 92 23
BOOL CRFonlineDlg::RefreshData()
{
	BYTE i, index=4,SendBuf[8]={0x3A,0x00,0x05,0x07,0x01,0x39,0x23,0x00};
	
	switch(strRxBuf[3]) //switch(strTest[3])
	{
	case 0x01:
		for (i=0; i<MAX_NODE; i++)
		{
			memcpy(NodeData[i], &strRxBuf[index], 5);//memcpy(NodeData[i], &strTest[index], 4);
			index += 5;
		}
		//终端1
		m_temp1.Format("%d ℃", (char)NodeData[0][0]);  //温度
		m_Humidity1.Format("%d %%", NodeData[0][1]);    //湿度
		m_Bri.Format("%d %%", NodeData[0][3]);
		if (NodeData[0][2] == 1)
			m_StaticGas1.SetBitmap(HBITMAP(m_bmp[4]));  //气体高电平时正常
		else
			m_StaticGas1.SetBitmap(HBITMAP(m_bmp[5]));  //气体低电平时异常

		if (NodeData[0][4] == 1)
			m_Test.SetBitmap(HBITMAP(m_bmp[6]));    //灯亮
		else
			m_Test.SetBitmap(HBITMAP(m_bmp[3]));    //灯灭

		//显示温度计
		m_tempDisp1.SetPosition((char)NodeData[0][0]);									
				
		if (m_SelectPlotIndex == 0)        //显示温度曲线
		{
			m_plotx.GetChannel(0).AddYElapsedSeconds((char)NodeData[0][0]);
		} 
		else if (m_SelectPlotIndex == 1)   //显示湿度曲线
		{
			m_plotx.GetChannel(0).AddYElapsedSeconds(NodeData[0][1]);
		}

		break;
	case 0x0A:
		if (!m_bAutoState)
		{
			switch(strRxBuf[2])   //Addr = strRxBuf[2];
			{
			case 0x01:
				if (strRxBuf[4] == 0)
					m_btnLamp1.SetBitmap(HBITMAP(m_bmp[2]));
				else
					m_btnLamp1.SetBitmap(HBITMAP(m_bmp[3]));
				break;
			case 0xFF: //还应判断终端是否在线
				if (strRxBuf[4] == 0)
				{
					m_btnLamp1.SetBitmap(HBITMAP(m_bmp[2]));
				}
				else
				{
					m_btnLamp1.SetBitmap(HBITMAP(m_bmp[3]));
				}
				break;
			default:
				break;
			}
		}

		break;
	default:
		break;
	}

	UpdateData(FALSE);

	return TRUE;
}

int CRFonlineDlg::GetSystemPorts(CString *pNameList, CString *pPortList)
{
	CRegKey RegKey;   
	int nCount = 0;  

	//打开注册表
	if(RegKey.Open(HKEY_LOCAL_MACHINE, "Hardware\\DeviceMap\\SerialComm", KEY_READ) == ERROR_SUCCESS)   
	{   
		while(true)   
		{   
			char ValueName[_MAX_PATH];   
			unsigned char ValueData[_MAX_PATH];   
			DWORD nValueSize = _MAX_PATH;   
			DWORD nDataSize = _MAX_PATH;   
			DWORD nType;   

			//通过枚举获取，如果再无其他项则终止
			if(::RegEnumValue(HKEY(RegKey), nCount, ValueName, &nValueSize, NULL, &nType, ValueData, &nDataSize) == ERROR_NO_MORE_ITEMS)   
			{   
				break;   
			}   

			if(pNameList)   //名称
				pNameList[nCount] = ValueName;   

			if(pPortList)   //值
				pPortList[nCount] = ValueData;   

			nCount++;       //继续探索下一个
		}   
	}   

	return nCount;

}

BEGIN_EVENTSINK_MAP(CRFonlineDlg, CDialog)
    //{{AFX_EVENTSINK_MAP(CRFonlineDlg)
	//}}AFX_EVENTSINK_MAP
END_EVENTSINK_MAP()

void CRFonlineDlg::OnRadio1() 
{
	m_SelectPlotIndex = 0;

	m_plotx.SetTitleText("温度趋势图");

	m_plotx.RemoveAllChannels();
	m_plotx.AddChannel();
	m_plotx.GetChannel(0).SetTitleText("终端1");
	//Re-Setup Axes
	m_plotx.GetYAxis(0).SetMin(0);
	m_plotx.GetYAxis(0).SetSpan(40);

	m_EditText.Clear();
	m_EditText.SetWindowText("查看温度趋势图");	
}

void CRFonlineDlg::OnRadio2() 
{
	m_SelectPlotIndex = 1;
	m_plotx.SetTitleText("湿度趋势图");

	m_plotx.RemoveAllChannels();
	m_plotx.AddChannel();
	m_plotx.GetChannel(0).SetTitleText("终端1");
	m_plotx.GetYAxis(0).SetMin(0);
	m_plotx.GetYAxis(0).SetSpan(100);

	m_EditText.Clear();
	m_EditText.SetWindowText("查看湿度趋势图");	
}

void CRFonlineDlg::OnBtnManualUpdate() 
{
	BYTE SendBuf[7]={0x3A,0x00,0xFF,0x01,0xC4,0x23,0x00};  //查询所有终端报文3A 00 ff 01 C4 23

	if (m_bComState == TRUE)
	{		
		m_EditText.Clear();
		m_EditText.SetWindowText("手动刷新所有终端的数据");
		
		m_SerialPort.WriteToPort(SendBuf, 6);
		//Sleep(20);
	} 
	else
	{
		MessageBox("请先打开串口!");
	}
}

void CRFonlineDlg::OnBtnAutoUpdate() 
{
	if (m_bComState == TRUE)
	{
		m_EditText.Clear();

		if (!m_bAutoState)
		{
			m_bAutoState = TRUE;
			m_EditText.SetWindowText("启动自动刷新");
			SetTimer(1,1000,NULL);	//开启定时器 1秒更新一次数据
			m_btnAuto.SetWindowText("停止自动刷新");
		} 
		else
		{
			m_bAutoState = FALSE;
			KillTimer(1);			
			m_EditText.SetWindowText("停止自动刷新");
			m_btnAuto.SetWindowText("启动自动刷新");
		}

	} 
	else
	{
		MessageBox("请先打开串口!");
	}	
}

void CRFonlineDlg::OnTimer(UINT nIDEvent) 
{
	BYTE SendBuf[7]={0x3A,0x00,0xFF,0x01,0xC4,0x23,0x00};  //查询所有终端报文3A 00 ff 01 C4 23
	BYTE SendBuf1[8]={0x3A,0x00,0x05,0x07,0x01,0x39,0x23,0x00};
	BYTE SendBuf2[8]={0x3A,0x00,0x05,0x07,0x00,0x38,0x23,0x00};
	BYTE SendBuf3[8]={0x3A,0x00,0x05,0x09,0x01,0x37,0x23,0x00};
	BYTE SendBuf4[8]={0x3A,0x00,0x05,0x09,0x00,0x36,0x23,0x00};
	BYTE SendBuf5[8]={0x3A,0x00,0x06,0x08,0x01,0x35,0x23,0x00};
	BYTE SendBuf6[8]={0x3A,0x00,0x06,0x08,0x02,0x36,0x23,0x00};
	switch (nIDEvent)
	{
	case 1:
		{
			m_SerialPort.WriteToPort(SendBuf, 6);
			//KillTimer(1);
		}
		break;
   case 2:
	   {
		if(NodeData[0][2] == 0){
			m_SerialPort.WriteToPort(SendBuf1, 7); 
			}
		if(NodeData[0][2] == 1){
			m_SerialPort.WriteToPort(SendBuf2, 7); 
			}
	   }
	   break;
	    case 3:
	   {
		if(NodeData[0][3] > 50){
			m_SerialPort.WriteToPort(SendBuf3, 7); 
			}
		if(NodeData[0][3] < 50) {
			m_SerialPort.WriteToPort(SendBuf4, 7);
			}
		
	   }
	   break;
	       case 4:
	   {
		if(NodeData[0][3] > 50){
			m_SerialPort.WriteToPort(SendBuf6, 7); 
			j++;
			if(j==50){
				m_CurtainState=TRUE;}
			}
		if((NodeData[0][3] < 50)&&(j!=0)) {
			m_SerialPort.WriteToPort(SendBuf5, 7); 
			j--;
			if(j==0){
				m_CurtainState=FALSE;}
			}
		
	   }
	   break;
	    case 5:
	   {
		if(NodeData[0][4] == 1){
			m_SerialPort.WriteToPort(SendBuf1, 7); 
			}
		if(NodeData[0][4] == 0){
			m_SerialPort.WriteToPort(SendBuf2, 7); 
			}
	   }
	   break;
	default:
		break;
	}
	
	CDialog::OnTimer(nIDEvent);
}

void CRFonlineDlg::OnBtnOpenCom() 
{
	CString strCom, strPort;
	int nPort = 0;// PortTab[m_ComSel.GetCurSel()];
	m_ComSel.GetLBText(m_ComSel.GetCurSel(), strCom);   //m_ComID.GetCurSel()+1; //得到串口号
	strPort = strCom.Mid(3);
	nPort = atoi(strPort);
	
	if (!m_bComState)
	{
		if (m_SerialPort.InitPort(this, nPort, 115200, 'N', 8, 1, EV_RXCHAR|EV_RXFLAG, 512)) //115200   
		{
			m_bComState = TRUE;
			m_SerialPort.StartMonitoring(); //启动串口监视线程
			m_EditText.Clear();
			m_EditText.SetWindowText("打开串口,波特率115200 8N1");
			m_StaticCom.SetBitmap(HBITMAP(m_bmp[0]));
			m_btnCom.SetWindowText("关闭串口"); 
		} 
		else
		{
			AfxMessageBox("没有发现,或被其它设备占用");
			m_bComState = FALSE;
			m_StaticCom.SetBitmap(HBITMAP(m_bmp[1]));
			m_btnCom.SetWindowText("打开串口");
		}

		//使以下按钮不可用 
		GetDlgItem(IDC_BTN_UPDATE_MANUAL)->EnableWindow(TRUE);
		GetDlgItem(IDC_BTN_UPDATE_AUTO)->EnableWindow(TRUE);
		GetDlgItem(IDC_BTN_OPEN_NET)->EnableWindow(TRUE);
		GetDlgItem(IDC_BTN_CLOSE_ALL)->EnableWindow(TRUE);
	} 
	else
	{
		KillTimer(1);		
		m_SerialPort.ClosePort();
		m_bComState = FALSE;        //初始化串口为关闭状态
		m_bAutoState = FALSE;       //初始化自动刷新为关闭状态
		m_bNetState = FALSE;        //初始化网络为关闭状态
		//使以下按钮可用
		GetDlgItem(IDC_BTN_UPDATE_MANUAL)->EnableWindow(FALSE);
		GetDlgItem(IDC_BTN_UPDATE_AUTO)->EnableWindow(FALSE);
		GetDlgItem(IDC_BTN_OPEN_NET)->EnableWindow(FALSE);
		GetDlgItem(IDC_BTN_CLOSE_ALL)->EnableWindow(FALSE);
		m_StaticCom.SetBitmap(HBITMAP(m_bmp[1]));
		m_btnCom.SetWindowText("打开串口"); 
		
		m_EditText.Clear();
		m_EditText.SetWindowText("关闭串口");
	}
}

BYTE CRFonlineDlg::GetDataLen(BYTE fc)
{
    BYTE len=0;

    switch(fc)
    {
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
	default:
		break;
    }
    
    return len;
}


BYTE CRFonlineDlg::XorCheckSum(BYTE * pBuf, BYTE len)
{
	BYTE i;
	BYTE byRet=0;
	
	if(len == 0)
		return byRet;
	else
		byRet = pBuf[0];
	
	for(i = 1; i < len; i ++)
		byRet = byRet ^ pBuf[i];
	
	return byRet;
}

//-------------------------------------------------------------------
// 网络相关函数
void CRFonlineDlg::OnBtnOpenNet() 
{
	m_EditText.Clear();
	
	if (!m_bNetState)
	{
		m_bNetState = TRUE;
		InitNetwork();	
		m_StaticNet.SetBitmap(HBITMAP(m_bmp[0]));
		m_btnNetwork.SetWindowText("停止网络服务"); 	
		m_EditText.SetWindowText("服务器已启动,等待连接...");		
	} 
	else
	{
		m_bNetState = FALSE;
		OnClose(ServerSock);
		m_StaticNet.SetBitmap(HBITMAP(m_bmp[1]));
		m_btnNetwork.SetWindowText("启动网络服务"); 
		m_EditText.SetWindowText("服务器已关闭");		
	}
}

BOOL CRFonlineDlg::InitNetwork()
{
	WSADATA wsaData;	
	
	BOOL ret = WSAStartup(MAKEWORD(2,2), &wsaData);	//初始化TCP协议	
	if(ret != 0)		
	{
		MessageBox("初始化网络协议失败!");		
		return FALSE;		
	}
	
	ServerSock = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);	//创建服务器端套接字
	if(ServerSock == INVALID_SOCKET)		
	{
		MessageBox("创建套接字失败!");		
		closesocket(ServerSock);		
		WSACleanup();		
		return FALSE;		
	}
	
	//设置服务器地址
	sockaddr_in localaddr;	
	localaddr.sin_family = AF_INET;	
	localaddr.sin_port = htons(DEFAULT_PORT);		
	localaddr.sin_addr.S_un.S_addr=INADDR_ANY;
	
	//绑定到本地一个端口上
	if(bind(ServerSock, (struct sockaddr*)&localaddr, sizeof(sockaddr)) == SOCKET_ERROR)		
	{		
		MessageBox("绑定地址失败!");		
		closesocket(ServerSock);		
		WSACleanup();		
		return FALSE;		
	}
	
	//将SeverSock设置为异步非阻塞模式，并为它注册各种网络异步事件
	if(WSAAsyncSelect(ServerSock, m_hWnd, NETWORK_EVENT,
		FD_ACCEPT | FD_CLOSE | FD_READ | FD_WRITE) == SOCKET_ERROR)		
	{		
		MessageBox("注册网络异步事件失败!");		
		WSACleanup();		
		return FALSE;		
	}
	
	listen(ServerSock, 5);	//监听
	
	return TRUE;	
}

LRESULT CRFonlineDlg::OnSocketMsg(WPARAM wParam, LPARAM lParam)
{	
	SOCKET ClientSock= (SOCKET)wParam; //wParam参数标志了网络事件发生的套接口
    
	if(WSAGETSELECTERROR(lParam))  
    {  
        closesocket(ClientSock);  
        return false;  
    } 

	switch(WSAGETSELECTEVENT(lParam))
	{	
	case FD_ACCEPT: 	
		OnAccept(ClientSock);	//客户端连接请求事件
		break;
	case FD_READ: 
		OnReceive(ClientSock);	//收到网络数据包	
		break;		
	case FD_CLOSE: 	
		closesocket(ClientSock);
		break;		
	default: 
		break;	
	}

	return TRUE;
}

void CRFonlineDlg::OnAccept(SOCKET ClientSock)
{	
	char dispBuf[50];
	SOCKET client;
	sockaddr_in ClientAddr;
	int	nLen = sizeof(sockaddr);

	client = accept(ServerSock, (sockaddr*)&ClientAddr, &nLen);
	char *pAddrname = inet_ntoa(ClientAddr.sin_addr);//得到客户端的IP地址。
	sprintf(dispBuf,"%s 连接成功,ID=%d",pAddrname,client);
	m_EditText.SetWindowText(dispBuf);
}

void CRFonlineDlg::OnClose(SOCKET ClientSock)
{	
	m_EditText.SetWindowText("客户端与客户端连接已断开...");

	closesocket(ClientSock);
	WSACleanup();
}

//查询所有终端报文3A 00 ff 01 C4 23
//响应：3A 00 FF 01 1E 48 01 01 00 00 01 01 00 00 00 00 00 00 00 00 92 23
void CRFonlineDlg::OnReceive(SOCKET ClientSock)
{	
	int i, index=4, len=0;
	BYTE strData[17];

	len = recv(ClientSock, szBuf, BUFFER_SIZE, 0);
	if(len == -1)  
		closesocket(ClientSock);  
    else  
	{
		if(len >= 6)
		{
			switch(szBuf[3])
			{
			case 0x01:
				//OnBtnManualUpdate(); //获得终端所有数据
				//Sleep(10);
				memset(strData, 0, sizeof(strData));
				strTxBuf[0] = 0x3A;
				strTxBuf[1] = 0x00;
				strTxBuf[2] = -(char)0xFF;
				strTxBuf[3] = 0x01;

				for (i=0; i<5; i++)
				{
					memcpy(strTxBuf+index, NodeData[i], 5);
					index += 5;
				}
				strTxBuf[index-5] = XorCheckSum((BYTE *)strTxBuf, 24);
				index = 25;
				strTxBuf[index] = 0x23;
				Send(ClientSock, strTxBuf, index+1);
				break;			
			case 0x0A: //控制灯命令
				switch((BYTE)szBuf[2])
				{
				case 1:
					OnBtnLamp1();
					break;
				             case 0xff:
					OnBtnCloseAll();
					break;
				default: 
		            break;	
				}
				break;
			case 0x07: //蜂鸣器
				if(szBuf[4] == 0x01)
					 OnBnClickedBtnBeepOn();
				else
					OnBnClickedBtnBeepOff();
				break;
			case 0x08: //电机
				if(szBuf[4] == 0x01)
					 OnBnClickedBtnDjOn();
				else
					OnBnClickedBtnDjOff();
				break;
			}		  	
		}
	}	
}

int CRFonlineDlg::Send(SOCKET fd, char *szText, int len)
{
	int count, rc;

	count = len;
	while(count>0)
	{
		rc = send(fd,szText,count,0);
		if(rc == SOCKET_ERROR)
		{
			return -1;
		}
		if(rc == 0)
			return len-count;

		szText += rc;
		count -= rc;
	}
	 
	return len;
}

//获取本机IP地址和计算机名	
void CRFonlineDlg::OnIPAddress()
{
	WORD wVersionRequested;  
	WSADATA wsaData;  
	char name[255];  
	
	PHOSTENT hostinfo;  
	wVersionRequested = MAKEWORD(2,  0);  
    
	if(WSAStartup(wVersionRequested, &wsaData) == 0)
	{  		
		if(gethostname(name,  sizeof(name)) == 0)  
		{  
			if((hostinfo = gethostbyname(name)) != NULL)  
			{  
				IpAddr = inet_ntoa(*(struct in_addr *)*hostinfo->h_addr_list);  
			}  
		}    
		WSACleanup();  
	}
}
//-------------------------------------------------------------------

void CRFonlineDlg::OnBtnLamp1() 
{
	BYTE SendBuf[8]={0x3A,0x00,0x01,0x0A,0x00,0x00,0x23,0x00};
	
	if (m_bComState == FALSE)
	{
		MessageBox("控制终端时请先打开串口!");
		return;
	}
	
	if(m_bLampState1==1)
	{
		m_bLampState1=0;
		m_btnLamp1.SetBitmap(HBITMAP(m_bmp[2]));
	}
	else
	{
		m_bLampState1=1;
		m_btnLamp1.SetBitmap(HBITMAP(m_bmp[3]));
	}
	SendBuf[4] = m_bLampState1; 
	SendBuf[5] = XorCheckSum(SendBuf, 6);

	m_SerialPort.WriteToPort(SendBuf, 7);  //发命令使灯亮灭	
}

void CRFonlineDlg::OnBtnCloseAll() 
{
	BYTE SendBuf[8]={0x3A,0x00,0xFF,0x0A,0x00,0x00,0x23,0x00};
	
	if (m_bComState == FALSE)
	{
		MessageBox("控制终端时请先打开串口!");
		return;
	}
		
	if (LampAllState == 0) {		
		SendBuf[4] = LampAllState;
		LampAllState = 1;
		m_btnLampAll.SetWindowText("关闭所有灯");

	} else {
		SendBuf[4] = LampAllState;
		LampAllState = 0;
		m_btnLampAll.SetWindowText("打开所有灯");
	}

	SendBuf[5] = XorCheckSum(SendBuf, 6);
	m_SerialPort.WriteToPort(SendBuf, 7);
}

//-----------------------------------------------------------------
void CRFonlineDlg::OnBnClickedBtnBeepOn()
{
	BYTE SendBuf[8]={0x3A,0x00,0x05,0x07,0x01,0x39,0x23,0x00};
	
	if (m_bComState == FALSE)
	{
		MessageBox("控制终端时请先打开串口!");
		return;
	}

	m_SerialPort.WriteToPort(SendBuf, 7); 
}


void CRFonlineDlg::OnBnClickedBtnBeepOff()
{
	BYTE SendBuf[8]={0x3A,0x00,0x05,0x07,0x00,0x38,0x23,0x00};
	
	if (m_bComState == FALSE)
	{
		MessageBox("控制终端时请先打开串口!");
		return;
	}
	m_SerialPort.WriteToPort(SendBuf, 7);  //发命令使灯亮灭	
}

//发送：3A 00 06 08 01 35 23      打开终端地址为0006的窗帘 电机右转
void CRFonlineDlg::OnBnClickedBtnDjOn()
{
	BYTE strBuf[8]={0x3A,0x00,0x06,0x08,0x01,0x35,0x23,0x00};

	if (m_bComState == FALSE)
	{
		MessageBox("控制终端时请先打开串口!");
		return;
	}
	m_SerialPort.WriteToPort(strBuf, 7);  //发命令使灯亮灭	
}

//发送：3A 00 06 08 02 36 23      关闭终端地址为0006的窗帘 电机左转
void CRFonlineDlg::OnBnClickedBtnDjOff()
{
	BYTE strBuf[8]={0x3A,0x00,0x06,0x08,0x02,0x36,0x23,0x00};

	if (m_bComState == FALSE)
	{
		MessageBox("控制终端时请先打开串口!");
		return;
	}
	m_SerialPort.WriteToPort(strBuf, 7);  //发命令使灯亮灭	
}


void CRFonlineDlg::OnCbnSelchangeComboport()
{
	// TODO: 在此添加控件通知处理程序代码
}


void CRFonlineDlg::OnEnChangeTmp1()
{
	// TODO:  如果该控件是 RICHEDIT 控件，它将不
	// 发送此通知，除非重写 CDialog::OnInitDialog()
	// 函数并调用 CRichEditCtrl().SetEventMask()，
	// 同时将 ENM_CHANGE 标志“或”运算到掩码中。

	// TODO:  在此添加控件通知处理程序代码
}


void CRFonlineDlg::OnBnClickedButton1()
{
   
	if (m_bAutoState == TRUE)
	{
		m_EditText.Clear();

		if (!m_bSmartState)
		{
			m_bSmartState = TRUE;
			m_EditText1.SetWindowText("启动智能模式");
			SetTimer(3,600,NULL);
			SetTimer(4,500,NULL);
			m_btnSmart.SetWindowText("停止智能模式");
		} 
		else
		{
			m_bSmartState = FALSE;	
			KillTimer(3);
			KillTimer(4);
			m_EditText1.SetWindowText("停止智能模式");
			m_btnSmart.SetWindowText("启动智能模式");
		}

	} 
	else
	{
		MessageBox("请先打开自动刷新!");
	}	
	// TODO: 在此添加控件通知处理程序代码
}


void CRFonlineDlg::OnBnClickedButton4()
{
	if(!m_bOutState)
	{
		if(m_bSmartState == TRUE)
		{
			m_bSmartState = FALSE;	
			KillTimer(3);
			KillTimer(4);
			m_btnSmart.SetWindowText("启动智能模式");
		}
		SetTimer(2,500,NULL);
		SetTimer(5,200,NULL);
		m_bOutState=TRUE;
		m_btnSmart.EnableWindow(FALSE);
		m_EditText1.SetWindowText("启动外出模式");
		m_btnOut.SetWindowText("关闭外出模式");
		m_btnLamp1.EnableWindow(FALSE);
		m_btnLampAll.EnableWindow(FALSE);
		GetDlgItem(IDC_BTN_BEEP_ON)->EnableWindow(FALSE);
		GetDlgItem(IDC_BTN_BEEP_OFF)->EnableWindow(FALSE);
		GetDlgItem(IDC_BTN_DJ_ON)->EnableWindow(FALSE);
		GetDlgItem(IDC_BTN_DJ_OFF)->EnableWindow(FALSE);
		GetDlgItem(IDC_BTN_LAMP1)->EnableWindow(FALSE);
	}
	else
	{
		KillTimer(2);
		KillTimer(5);
		m_bOutState=FALSE;
		m_btnSmart.EnableWindow(TRUE);
		m_EditText1.SetWindowText("关闭外出模式");
		m_btnOut.SetWindowText("启动外出模式");
		m_btnLamp1.EnableWindow(TRUE);
		m_btnLampAll.EnableWindow(TRUE);
		GetDlgItem(IDC_BTN_BEEP_ON)->EnableWindow(TRUE);
		GetDlgItem(IDC_BTN_BEEP_OFF)->EnableWindow(TRUE);
		GetDlgItem(IDC_BTN_DJ_ON)->EnableWindow(TRUE);
		GetDlgItem(IDC_BTN_DJ_OFF)->EnableWindow(TRUE);
		GetDlgItem(IDC_BTN_LAMP1)->EnableWindow(TRUE);
	}
	// TODO: 在此添加控件通知处理程序代码
}
