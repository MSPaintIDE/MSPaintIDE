Set oWS = WScript.CreateObject("WScript.Shell")
strHomeFolder = oWS.ExpandEnvironmentStrings("%USERPROFILE%")
scriptdir = CreateObject("Scripting.FileSystemObject").GetParentFolderName(WScript.ScriptFullName)

sLinkFile = scriptdir & "\Uninstall MS Paint IDE.lnk"

Set oLink = oWS.CreateShortcut(sLinkFile)

oLink.TargetPath = "C:\Windows\system32\cmd.exe"
oLink.Arguments = "/c java -jar " & strHomeFolder & "\AppData\Local\MSPaintIDE\MSPaintIDE.jar uninstall"
oLink.IconLocation = strHomeFolder & "\AppData\Local\MSPaintIDE\images\uninstall.ico"

oLink.Save