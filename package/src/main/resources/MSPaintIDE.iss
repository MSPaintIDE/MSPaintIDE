; MS Paint IDE Inno Setup File, adapted by Adam Yarris from Santulator under the Apache Licence, Version 2.0.

#define MyAppName "MS Paint IDE"
#define MyAppVersion "@bundle.version@"
#define MyAppPublisher "Adam Yarris"
#define MyAppURL "https://ms-paint-i.de/"
#define MyAppExeName "MSPaintIDE.exe"
#define InstLogoDir "@icon.directory@\installer"

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{DDC394B2-1233-4E42-BBD5-8AB3D51AFCD0}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
AppVerName="{#MyAppName} {#MyAppVersion}"
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={pf}\{#MyAppName}
DisableProgramGroupPage=auto
DisableDirPage=no
OutputBaseFilename=MSPaintIDE-@bundle.version@
Compression=lzma
SolidCompression=yes
ChangesAssociations=yes
SetupIconFile="@icon.directory@\icon.ico"
UninstallDisplayIcon="{app}\MSPaintIDE.ico"
WizardSmallImageFile={#InstLogoDir}\small-logo-55.bmp,{#InstLogoDir}\small-logo-64.bmp,{#InstLogoDir}\small-logo-97.bmp,{#InstLogoDir}\small-logo-110.bmp,{#InstLogoDir}\small-logo-120.bmp,{#InstLogoDir}\small-logo-140.bmp

[Types]
Name: "full"; Description: "Full installation"
Name: "compact"; Description: "Compact installation"
Name: "custom"; Description: "Custom installation"; Flags: iscustom

[Components]
Name: "program"; Description: "MS Paint IDE"; Types: full compact custom; Flags: fixed
Name: "context_menu"; Description: "Context menu"; Types: full
Name: "ppf_file"; Description: ".ppf file extension"; Types: full
Name: "readme"; Description: "Readme"; Types: full

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "@executable.location@"; DestDir: "{app}"; Flags: ignoreversion
Source: "@bundle.content@"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "@bundle.readme@"; DestDir: "{app}"; Flags: ignoreversion; Components: readme
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Icons]
Name: "{commonprograms}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"
Name: "{commondesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}"; Flags: nowait postinstall skipifsilent

[UninstallDelete]
Type: filesandordirs; Name: "{localappdata}\MSPaintIDE"
Type: filesandordirs; Name: "{app}"

[Registry]
Root: HKCR; Subkey: ".ppf"; ValueType: string; ValueName: ""; ValueData: "MSPaintIDE"; Flags: uninsdeletevalue; Components: ppf_file
Root: HKCR; Subkey: "MSPaintIDE"; ValueType: string; ValueName: ""; ValueData: "MS Paint IDE"; Flags: uninsdeletekey; Components: ppf_file
Root: HKCR; Subkey: "MSPaintIDE\DefaultIcon"; ValueType: string; ValueName: ""; ValueData: "{app}\MSPaintIDE.ico"; Components: ppf_file
Root: HKCR; Subkey: "MSPaintIDE\shell\open\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""%1"""; Components: ppf_file

Root: HKCR; Subkey: "*\shell\MSPaintIDE"; ValueType: string; ValueName: ""; ValueData: "Edit with MS Paint IDE"; Components: context_menu
Root: HKCR; Subkey: "*\shell\MSPaintIDE\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""%1"""; Components: context_menu
Root: HKCR; Subkey: "*\shell\MSPaintIDE"; ValueType: string; ValueName: "Icon"; ValueData: "{app}\MSPaintIDE.ico"; Components: context_menu
Root: HKCR; Subkey: "*\shell\MSPaintIDE"; ValueType: expandsz; ValueName: "Position"; ValueData: "Top"; Components: context_menu