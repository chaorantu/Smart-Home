// RFonline.h : main header file for the RFonline application
//

#if !defined(AFX_RFonline_H__034758CE_EE6E_4AFC_A7AF_2A62E0D0EBBA__INCLUDED_)
#define AFX_RFonline_H__034758CE_EE6E_4AFC_A7AF_2A62E0D0EBBA__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#ifndef __AFXWIN_H__
	#error include 'stdafx.h' before including this file for PCH
#endif

#include "resource.h"		// main symbols

/////////////////////////////////////////////////////////////////////////////
// CRFonlineApp:
// See RFonline.cpp for the implementation of this class
//

class CRFonlineApp : public CWinApp
{
public:
	CRFonlineApp();

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CRFonlineApp)
	public:
	virtual BOOL InitInstance();
	//}}AFX_VIRTUAL

// Implementation

	//{{AFX_MSG(CRFonlineApp)
		// NOTE - the ClassWizard will add and remove member functions here.
		//    DO NOT EDIT what you see in these blocks of generated code !
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};


/////////////////////////////////////////////////////////////////////////////

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_RFonline_H__034758CE_EE6E_4AFC_A7AF_2A62E0D0EBBA__INCLUDED_)
