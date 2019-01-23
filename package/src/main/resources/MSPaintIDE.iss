; MS Paint IDE Inno Setup File, adapted by Adam Yarris from Santulator under the Apache Licence, Version 2.0.

#define MyAppName "MS Paint IDE"
#define MyAppVersion "@bundle.version@"
#define MyAppPublisher "Adam Yarris"
#define MyAppURL "https://ms-paint-i.de/"
#define MyAppExeName "MSPaintIDE.exe"

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{DDC394B2-1233-4E42-BBD5-8AB3D51AFCD0}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
;AppVerName={#MyAppName} {#MyAppVersion}
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
SetupIconFile="@icon.file@"

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "@executable.location@"; DestDir: "{app}"; Flags: ignoreversion
Source: "@bundle.content@"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Icons]
Name: "{commonprograms}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"
Name: "{commondesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}"; Flags: nowait postinstall skipifsilent

[Registry]
Root: HKLM; Subkey: "Software\Classes\.ppf"; ValueType: string; ValueName: ""; ValueData: "MSPaintIDE"; Flags: uninsdeletevalue
Root: HKLM; Subkey: "Software\Classes\MSPaintIDE"; ValueType: string; ValueName: ""; ValueData: "MS Paint IDE"; Flags: uninsdeletekey
Root: HKLM; Subkey: "Software\Classes\MSPaintIDE\DefaultIcon"; ValueType: string; ValueName: ""; ValueData: "{app}\icon.ico"
Root: HKLM; Subkey: "Software\Classes\MSPaintIDE\shell\open\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""%1"""