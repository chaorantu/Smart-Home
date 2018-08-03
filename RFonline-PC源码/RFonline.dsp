# Microsoft Developer Studio Project File - Name="RFonline" - Package Owner=<4>
# Microsoft Developer Studio Generated Build File, Format Version 6.00
# ** DO NOT EDIT **

# TARGTYPE "Win32 (x86) Application" 0x0101

CFG=RFonline - Win32 Debug
!MESSAGE This is not a valid makefile. To build this project using NMAKE,
!MESSAGE use the Export Makefile command and run
!MESSAGE 
!MESSAGE NMAKE /f "RFonline.mak".
!MESSAGE 
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "RFonline.mak" CFG="RFonline - Win32 Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "RFonline - Win32 Release" (based on "Win32 (x86) Application")
!MESSAGE "RFonline - Win32 Debug" (based on "Win32 (x86) Application")
!MESSAGE 

# Begin Project
# PROP AllowPerConfigDependencies 0
# PROP Scc_ProjName ""
# PROP Scc_LocalPath ""
CPP=cl.exe
MTL=midl.exe
RSC=rc.exe

!IF  "$(CFG)" == "RFonline - Win32 Release"

# PROP BASE Use_MFC 5
# PROP BASE Use_Debug_Libraries 0
# PROP BASE Output_Dir "Release"
# PROP BASE Intermediate_Dir "Release"
# PROP BASE Target_Dir ""
# PROP Use_MFC 5
# PROP Use_Debug_Libraries 0
# PROP Output_Dir "Release"
# PROP Intermediate_Dir "Release"
# PROP Target_Dir ""
# ADD BASE CPP /nologo /MT /W3 /GX /O2 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /Yu"stdafx.h" /FD /c
# ADD CPP /nologo /MT /W3 /GX /O2 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /FR /Yu"stdafx.h" /FD /c
# ADD BASE MTL /nologo /D "NDEBUG" /mktyplib203 /win32
# ADD MTL /nologo /D "NDEBUG" /mktyplib203 /win32
# ADD BASE RSC /l 0x804 /d "NDEBUG"
# ADD RSC /l 0x804 /d "NDEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 /nologo /subsystem:windows /machine:I386
# ADD LINK32 /nologo /subsystem:windows /machine:I386

!ELSEIF  "$(CFG)" == "RFonline - Win32 Debug"

# PROP BASE Use_MFC 5
# PROP BASE Use_Debug_Libraries 1
# PROP BASE Output_Dir "Debug"
# PROP BASE Intermediate_Dir "Debug"
# PROP BASE Target_Dir ""
# PROP Use_MFC 5
# PROP Use_Debug_Libraries 1
# PROP Output_Dir "Debug"
# PROP Intermediate_Dir "Debug"
# PROP Target_Dir ""
# ADD BASE CPP /nologo /MTd /W3 /Gm /GX /ZI /Od /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /Yu"stdafx.h" /FD /GZ /c
# ADD CPP /nologo /MTd /W3 /Gm /GX /ZI /Od /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /D "_MBCS" /FR /Yu"stdafx.h" /FD /GZ /c
# ADD BASE MTL /nologo /D "_DEBUG" /mktyplib203 /win32
# ADD MTL /nologo /D "_DEBUG" /mktyplib203 /win32
# ADD BASE RSC /l 0x804 /d "_DEBUG"
# ADD RSC /l 0x804 /d "_DEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 /nologo /subsystem:windows /debug /machine:I386 /pdbtype:sept
# ADD LINK32 /nologo /subsystem:windows /debug /machine:I386 /pdbtype:sept

!ENDIF 

# Begin Target

# Name "RFonline - Win32 Release"
# Name "RFonline - Win32 Debug"
# Begin Group "Source Files"

# PROP Default_Filter "cpp;c;cxx;rc;def;r;odl;idl;hpj;bat"
# Begin Source File

SOURCE=.\font.cpp
# End Source File
# Begin Source File

SOURCE=.\iledbarx.cpp
# End Source File
# Begin Source File

SOURCE=.\iledrectanglex.cpp
# End Source File
# Begin Source File

SOURCE=.\iledroundx.cpp
# End Source File
# Begin Source File

SOURCE=.\iplotannotationx.cpp
# End Source File
# Begin Source File

SOURCE=.\iplotaxisx.cpp
# End Source File
# Begin Source File

SOURCE=.\iplotchannelx.cpp
# End Source File
# Begin Source File

SOURCE=.\iplotdatacursorx.cpp
# End Source File
# Begin Source File

SOURCE=.\iplotdataviewx.cpp
# End Source File
# Begin Source File

SOURCE=.\iplotlabelx.cpp
# End Source File
# Begin Source File

SOURCE=.\iplotlegendx.cpp
# End Source File
# Begin Source File

SOURCE=.\iplotlimitx.cpp
# End Source File
# Begin Source File

SOURCE=.\iplottablecolumnx.cpp
# End Source File
# Begin Source File

SOURCE=.\iplottablex.cpp
# End Source File
# Begin Source File

SOURCE=.\iplottoolbarx.cpp
# End Source File
# Begin Source File

SOURCE=.\iplotx.cpp
# End Source File
# Begin Source File

SOURCE=.\iswitchrockerx.cpp
# End Source File
# Begin Source File

SOURCE=.\ithermometerx.cpp
# End Source File
# Begin Source File

SOURCE=.\picture.cpp
# End Source File
# Begin Source File

SOURCE=.\RFonline.cpp
# End Source File
# Begin Source File

SOURCE=.\RFonline.rc
# End Source File
# Begin Source File

SOURCE=.\RFonlineDlg.cpp
# End Source File
# Begin Source File

SOURCE=.\SerialPort.cpp
# End Source File
# Begin Source File

SOURCE=.\StdAfx.cpp
# ADD CPP /Yc"stdafx.h"
# End Source File
# End Group
# Begin Group "Header Files"

# PROP Default_Filter "h;hpp;hxx;hm;inl"
# Begin Source File

SOURCE=.\font.h
# End Source File
# Begin Source File

SOURCE=.\iledbarx.h
# End Source File
# Begin Source File

SOURCE=.\iledrectanglex.h
# End Source File
# Begin Source File

SOURCE=.\iledroundx.h
# End Source File
# Begin Source File

SOURCE=.\iplotannotationx.h
# End Source File
# Begin Source File

SOURCE=.\iplotaxisx.h
# End Source File
# Begin Source File

SOURCE=.\iplotchannelx.h
# End Source File
# Begin Source File

SOURCE=.\iplotdatacursorx.h
# End Source File
# Begin Source File

SOURCE=.\iplotdataviewx.h
# End Source File
# Begin Source File

SOURCE=.\iplotlabelx.h
# End Source File
# Begin Source File

SOURCE=.\iplotlegendx.h
# End Source File
# Begin Source File

SOURCE=.\iplotlimitx.h
# End Source File
# Begin Source File

SOURCE=.\iplottablecolumnx.h
# End Source File
# Begin Source File

SOURCE=.\iplottablex.h
# End Source File
# Begin Source File

SOURCE=.\iplottoolbarx.h
# End Source File
# Begin Source File

SOURCE=.\iplotx.h
# End Source File
# Begin Source File

SOURCE=.\iswitchrockerx.h
# End Source File
# Begin Source File

SOURCE=.\ithermometerx.h
# End Source File
# Begin Source File

SOURCE=.\picture.h
# End Source File
# Begin Source File

SOURCE=.\Resource.h
# End Source File
# Begin Source File

SOURCE=.\RFonline.h
# End Source File
# Begin Source File

SOURCE=.\RFonlineDlg.h
# End Source File
# Begin Source File

SOURCE=.\SerialPort.h
# End Source File
# Begin Source File

SOURCE=.\StdAfx.h
# End Source File
# End Group
# Begin Group "Resource Files"

# PROP Default_Filter "ico;cur;bmp;dlg;rc2;rct;bin;rgs;gif;jpg;jpeg;jpe"
# Begin Source File

SOURCE=.\res\bitmap1.bmp
# End Source File
# Begin Source File

SOURCE=.\res\close.bmp
# End Source File
# Begin Source File

SOURCE=.\res\gas_abnormal.bmp
# End Source File
# Begin Source File

SOURCE=.\res\gas_normal.bmp
# End Source File
# Begin Source File

SOURCE=.\res\led1.ico
# End Source File
# Begin Source File

SOURCE=.\res\led2.ico
# End Source File
# Begin Source File

SOURCE=.\res\led_100.ico
# End Source File
# Begin Source File

SOURCE=.\res\led_off.bmp
# End Source File
# Begin Source File

SOURCE=.\res\led_on.bmp
# End Source File
# Begin Source File

SOURCE=.\res\led_on.ico
# End Source File
# Begin Source File

SOURCE=.\res\led_on1.ico
# End Source File
# Begin Source File

SOURCE=.\res\led_on_48.ico
# End Source File
# Begin Source File

SOURCE=.\res\open.bmp
# End Source File
# Begin Source File

SOURCE=.\res\RFonline.ico
# End Source File
# Begin Source File

SOURCE=.\res\RFonline.rc2
# End Source File
# End Group
# Begin Source File

SOURCE=.\ReadMe.txt
# End Source File
# End Target
# End Project
# Section RFonline : {0A362341-2E5E-11D3-85BF-00105AC8B715}
# 	2:5:Class:CiLedRectangleX
# 	2:10:HeaderFile:iledrectanglex.h
# 	2:8:ImplFile:iledrectanglex.cpp
# End Section
# Section RFonline : {BEF6E003-A874-101A-8BBA-00AA00300CAB}
# 	2:5:Class:COleFont
# 	2:10:HeaderFile:font.h
# 	2:8:ImplFile:font.cpp
# End Section
# Section RFonline : {1791C036-8981-492A-BD28-F2331BC9B7C7}
# 	2:21:DefaultSinkHeaderFile:iplotx.h
# 	2:16:DefaultSinkClass:CiPlotX
# End Section
# Section RFonline : {C5412DF6-2E2F-11D3-85BF-00105AC8B715}
# 	2:21:DefaultSinkHeaderFile:ithermometerx.h
# 	2:16:DefaultSinkClass:CiThermometerX
# End Section
# Section RFonline : {217995A5-7C8E-4307-8750-34F4BEC14BB5}
# 	2:5:Class:CiPlotAnnotationX
# 	2:10:HeaderFile:iplotannotationx.h
# 	2:8:ImplFile:iplotannotationx.cpp
# End Section
# Section RFonline : {24DA2975-3806-46E5-BA56-DAC1F3C234DE}
# 	2:5:Class:CiPlotDataViewX
# 	2:10:HeaderFile:iplotdataviewx.h
# 	2:8:ImplFile:iplotdataviewx.cpp
# End Section
# Section RFonline : {0A362345-2E5E-11D3-85BF-00105AC8B715}
# 	2:21:DefaultSinkHeaderFile:iledrectanglex.h
# 	2:16:DefaultSinkClass:CiLedRectangleX
# End Section
# Section RFonline : {E5F63B89-D2A6-11D3-85C2-00A0CC3A58C9}
# 	2:5:Class:CiSwitchRockerX
# 	2:10:HeaderFile:iswitchrockerx.h
# 	2:8:ImplFile:iswitchrockerx.cpp
# End Section
# Section RFonline : {C0FC6A66-D740-4962-97D5-E0ADD4658924}
# 	2:5:Class:CiPlotAxisX
# 	2:10:HeaderFile:iplotaxisx.h
# 	2:8:ImplFile:iplotaxisx.cpp
# End Section
# Section RFonline : {D017E3C4-9CA0-4D94-9AA9-CF421E1AF6DD}
# 	2:5:Class:CiPlotX
# 	2:10:HeaderFile:iplotx.h
# 	2:8:ImplFile:iplotx.cpp
# End Section
# Section RFonline : {7BF80981-BF32-101A-8BBB-00AA00300CAB}
# 	2:5:Class:CPicture
# 	2:10:HeaderFile:picture.h
# 	2:8:ImplFile:picture.cpp
# End Section
# Section RFonline : {27FDC973-2ED3-4769-9ED7-01D81C45A32E}
# 	2:5:Class:CiPlotLimitX
# 	2:10:HeaderFile:iplotlimitx.h
# 	2:8:ImplFile:iplotlimitx.cpp
# End Section
# Section RFonline : {C5412E10-2E2F-11D3-85BF-00105AC8B715}
# 	2:5:Class:CiLedBarX
# 	2:10:HeaderFile:iledbarx.h
# 	2:8:ImplFile:iledbarx.cpp
# End Section
# Section RFonline : {0A36234F-2E5E-11D3-85BF-00105AC8B715}
# 	2:5:Class:CiLedRoundX
# 	2:10:HeaderFile:iledroundx.h
# 	2:8:ImplFile:iledroundx.cpp
# End Section
# Section RFonline : {C5412E14-2E2F-11D3-85BF-00105AC8B715}
# 	2:21:DefaultSinkHeaderFile:iledbarx.h
# 	2:16:DefaultSinkClass:CiLedBarX
# End Section
# Section RFonline : {0A362353-2E5E-11D3-85BF-00105AC8B715}
# 	2:21:DefaultSinkHeaderFile:iledroundx.h
# 	2:16:DefaultSinkClass:CiLedRoundX
# End Section
# Section RFonline : {0BFA465F-B214-41F6-B5D6-986D4991F257}
# 	2:5:Class:CiPlotToolBarX
# 	2:10:HeaderFile:iplottoolbarx.h
# 	2:8:ImplFile:iplottoolbarx.cpp
# End Section
# Section RFonline : {854A06F7-C75E-4A42-B757-B4C352FC8488}
# 	2:5:Class:CiPlotLegendX
# 	2:10:HeaderFile:iplotlegendx.h
# 	2:8:ImplFile:iplotlegendx.cpp
# End Section
# Section RFonline : {02537798-A7C9-42D3-A8EE-70088290EAA4}
# 	2:5:Class:CiPlotChannelX
# 	2:10:HeaderFile:iplotchannelx.h
# 	2:8:ImplFile:iplotchannelx.cpp
# End Section
# Section RFonline : {48ED4A15-F4EE-410F-B52E-AC3875D1608E}
# 	2:5:Class:CiPlotTableX
# 	2:10:HeaderFile:iplottablex.h
# 	2:8:ImplFile:iplottablex.cpp
# End Section
# Section RFonline : {57EE716C-BC86-4DFC-8548-5FD12F6D855F}
# 	2:5:Class:CiPlotDataCursorX
# 	2:10:HeaderFile:iplotdatacursorx.h
# 	2:8:ImplFile:iplotdatacursorx.cpp
# End Section
# Section RFonline : {E3F19885-4F77-438B-96FC-B3D16F3CC31F}
# 	2:5:Class:CiPlotTableColumnX
# 	2:10:HeaderFile:iplottablecolumnx.h
# 	2:8:ImplFile:iplottablecolumnx.cpp
# End Section
# Section RFonline : {C5412DF2-2E2F-11D3-85BF-00105AC8B715}
# 	2:5:Class:CiThermometerX
# 	2:10:HeaderFile:ithermometerx.h
# 	2:8:ImplFile:ithermometerx.cpp
# End Section
# Section RFonline : {249CF20E-8331-4C41-8C13-7C8D2A72FADA}
# 	2:5:Class:CiPlotLabelX
# 	2:10:HeaderFile:iplotlabelx.h
# 	2:8:ImplFile:iplotlabelx.cpp
# End Section
# Section RFonline : {E5F63B8D-D2A6-11D3-85C2-00A0CC3A58C9}
# 	2:21:DefaultSinkHeaderFile:iswitchrockerx.h
# 	2:16:DefaultSinkClass:CiSwitchRockerX
# End Section
