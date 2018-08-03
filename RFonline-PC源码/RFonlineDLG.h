// RFonlineDlg.h : header file
//
#include "SerialPort.h"
//{{AFX_INCLUDES()
#include "iledroundx.h"
#include "ithermometerx.h"
#include "iplotx.h"
#include "iswitchrockerx.h"
#include "iledrectanglex.h"
#include "iledbarx.h"
//}}AFX_INCLUDES

#if !defined(AFX_RFonlineDLG_H__F83334FE_3526_411C_844F_2B9076DFDB93__INCLUDED_)
#define AFX_RFonlineDLG_H__F83334FE_3526_411C_844F_2B9076DFDB93__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#define NETWORK_EVENT WM_USER+166 //���������¼�

/////////////////////////////////////////////////////////////////////////////
// CRFonlineDlg dialog

class CRFonlineDlg : public CDialog
{
// Construction
public:
	CBitmap m_bmp[7];
	CString tmpSendString;
	CString IpAddr;
	void OnIPAddress(void);
	double tempValueBuf[4];
	BOOL AlarmFlag[4];
	BOOL LedOnOffFlag[4];
	BOOL RefreshData();
	BYTE GetDataLen(BYTE fc);
	BYTE XorCheckSum(BYTE * pBuf, BYTE len);
	int GetSystemPorts(CString *pNameList, CString *pPortList);
	CRFonlineDlg(CWnd* pParent = NULL);	// standard constructor

	SOCKET ClientSock[10];              //�洢��ͻ���ͨ�ŵ�Socket������	
	//�����¼�������	
	BOOL InitNetwork();                 //�����ʼ��		
	void OnReceive(SOCKET CurSock);     //�յ���������	
	void OnAccept(SOCKET CurSock);      //�ȴ��ͻ�����������	
	void OnClose(SOCKET CurSock);       //�ر�Socket	
	LRESULT OnSocketMsg(WPARAM wParam, LPARAM lParam); //�첽�¼��ص�����
	int Send(SOCKET fd,char *szText,int len);      //��������

	CSerialPort m_SerialPort;
	BOOL m_bNetState;          //����״̬ ��ʱΪTRUE �ر�ʱΪFALSE 
	BOOL m_bAutoState;         //�Զ�ˢ�� ��ʱΪTRUE �ر�ʱΪFALSE 
	BOOL m_bComState;          //����״̬ ��ʱΪTRUE �ر�ʱΪFALSE 
	BOOL m_bSmartState;
	BOOL m_bOutState;
	BOOL m_AlertState;
	BOOL m_LightState;
	BOOL m_bLampState1;        //�Ƶ�״̬ ����ʱΪTRUE ����ʱΪFALSE
	BOOL m_bLampState2;
	BOOL m_bLampState3;
	BOOL m_bLampState4;
    BOOL m_CurtainState;
	BOOL m_LampState;
	int PortTab[10];

// Dialog Data
	//{{AFX_DATA(CRFonlineDlg)
	enum { IDD = IDD_WSN_DIALOG };
	CButton	m_btnLampAll;
	CStatic	m_StaticNet;
	CButton	m_btnManual;
	CButton	m_btnAuto;
	CButton m_btnSmart;
	CButton m_btnOut;
	CButton	m_btnNetwork;
	CButton	m_btnCom;
	CStatic	m_StaticCom;
//	CStatic	m_StaticGas4;
//	CStatic	m_StaticGas3;
//	CStatic	m_StaticGas2;
	CStatic	m_StaticGas1;
	CStatic	m_Test;
//	CButton	m_btnLamp4;
//	CButton	m_btnLamp3;
//	CButton	m_btnLamp2;
	CButton	m_btnLamp1;
	CIPAddressCtrl	m_IPAddress;
	CEdit	m_EditText;
	CEdit   m_EditText1;
	CComboBox	m_ComSel;
	CString	m_temp1;
//	CString	m_temp2;
//	CString	m_temp3;
//	CString	m_temp4;
	CiThermometerX	m_tempDisp1;
//	CiThermometerX	m_tempDisp2;
//	CiThermometerX	m_tempDisp3;
//	CiThermometerX	m_tempDisp4;
	CiPlotX	m_plotx;
	int		m_SelectPlotIndex;
	CString	m_Humidity1;
//	CString	m_Humidity2;
	CString	m_Bri;
//	CString	m_Humidity3;
	//}}AFX_DATA

	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CRFonlineDlg)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:
	HICON m_hIcon;
	LONG OnComm(WPARAM ch, LPARAM port);

	// Generated message map functions
	//{{AFX_MSG(CRFonlineDlg)
	virtual BOOL OnInitDialog();
	afx_msg void OnSysCommand(UINT nID, LPARAM lParam);
	afx_msg void OnPaint();
	afx_msg HCURSOR OnQueryDragIcon();
	afx_msg void OnTimer(UINT nIDEvent);
	afx_msg void OnRadio1();
	afx_msg void OnRadio2();
	afx_msg void OnBtnLamp1();
	afx_msg void OnBtnManualUpdate();
	afx_msg void OnBtnAutoUpdate();
	afx_msg void OnBtnOpenCom();
	afx_msg void OnBtnOpenNet();
	afx_msg void OnBtnCloseAll();
	DECLARE_EVENTSINK_MAP()
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
public:
	afx_msg void OnBnClickedBtnBeepOn();
	afx_msg void OnBnClickedBtnBeepOff();
	afx_msg void OnBnClickedBtnDjOn();
	afx_msg void OnBnClickedBtnDjOff();
	afx_msg void OnCbnSelchangeComboport();
	afx_msg void OnEnChangeTmp1();
	afx_msg void OnBnClickedButton1();
	afx_msg void OnBnClickedButton4();
};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_RFonlineDLG_H__F83334FE_3526_411C_844F_2B9076DFDB93__INCLUDED_)
