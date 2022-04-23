;SwtProjectTemplate Installer Script

;--------------------------------
;Include Modern UI

  !include "MUI.nsh"

;--------------------------------
;General

  ;Name and file
  Name "ProjectName"
  OutFile "..\ExeFileName-Installer.exe"

  ;Default installation folder
  InstallDir "$PROGRAMFILES\OrgName\ProjectName"
  
  ;Get installation folder from registry if available
  InstallDirRegKey HKCU "Software\ProjectName" ""

;--------------------------------
;Interface Settings

  !define MUI_ABORTWARNING
	!define MUI_HEADERIMAGE ".\nsis\installersplash.bmp"
	!define MUI_HEADERIMAGE_BITMAP_NOSTRETCH
	!define MUI_HEADERIMAGE_BITMAP ".\nsis\installersplash.bmp"
	!define MUI_ICON ".\nsis\install.ico"
	!define MUI_UNICON ".\nsis\uninstall.ico"

;--------------------------------
;Pages

  !insertmacro MUI_PAGE_LICENSE ".\nsis\license.txt"
  !insertmacro MUI_PAGE_COMPONENTS
  !insertmacro MUI_PAGE_DIRECTORY
  !insertmacro MUI_PAGE_INSTFILES

  	!define MUI_FINISHPAGE_RUN
	!define MUI_FINISHPAGE_RUN_TEXT "Run ProjectName?"
	!define MUI_FINISHPAGE_RUN_FUNCTION "LaunchLink"
	!insertmacro MUI_PAGE_FINISH
  
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES
  
;--------------------------------
;Languages
 
  !insertmacro MUI_LANGUAGE "English"

;--------------------------------
;Installer Sections

Section "ProjectName (required)" SecDummy

  SectionIn RO

  ;Files to be installed
  SetOutPath "$INSTDIR"
  
   	File "ExeFileName.exe"
   	File "launch4j\icon.ico"
	File "swt-win32-3235.dll"
	File "appsettings.ini"

	SetOutPath "$INSTDIR\lib"

	File "lib\swt.jar"
	File "lib\activation.jar"
	File "lib\commons-logging.jar" 
	File "lib\dom4j-1.6.1.jar"
	File "lib\log4j-1.2.8.jar" 
	File "lib\mail.jar"
 
  SetOutPath "$INSTDIR"

    ; Write the installation path into the registry
  WriteRegStr HKLM "SOFTWARE\ProjectName" "Install_Dir" "$INSTDIR"
  
  ; Write the uninstall keys for Windows
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\ProjectName" "DisplayName" "ProjectName"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\ProjectName" "UninstallString" '"$INSTDIR\uninstall.exe"'
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\ProjectName" "NoModify" 1
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\ProjectName" "NoRepair" 1
  WriteUninstaller "uninstall.exe"
  
SectionEnd

; Optional section (can be disabled by the user)
Section "Start Menu Shortcuts"
  CreateDirectory "$SMPROGRAMS\ProjectName"
  CreateShortCut "$SMPROGRAMS\ProjectName\Uninstall.lnk" "$INSTDIR\uninstall.exe" "" "$INSTDIR\uninstall.exe"
  CreateShortCut "$SMPROGRAMS\ProjectName\ProjectName.lnk" "$INSTDIR\ExeFileName.exe" "" "$INSTDIR\icon.ico"
SectionEnd

;--------------------------------
;Uninstaller Section

Section "Uninstall"

  ; Remove registry keys
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\ProjectName"
  DeleteRegKey HKLM "SOFTWARE\ProjectName"
  DeleteRegKey /ifempty HKCU "Software\ProjectName"

	; Remove shortcuts
  RMDir /r "$SMPROGRAMS\ProjectName"

  ; Remove directories used
  RMDir /r "$INSTDIR"

SectionEnd

Function LaunchLink
  ExecShell """$SMPROGRAMS\ProjectName\ProjectName.lnk"
FunctionEnd



