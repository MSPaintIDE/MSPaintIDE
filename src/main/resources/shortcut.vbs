Set oWS = WScript.CreateObject("WScript.Shell")
strHomeFolder = oWS.ExpandEnvironmentStrings("%USERPROFILE%")
scriptdir = CreateObject("Scripting.FileSystemObject").GetParentFolderName(WScript.ScriptFullName)

sLinkFile = scriptdir & "\MS Paint IDE.lnk"

Set oLink = oWS.CreateShortcut(sLinkFile)

oLink.TargetPath = """JDK_PATH_HERE"""
oLink.Arguments = "-jar " & strHomeFolder & "\AppData\Local\MSPaintIDE\MSPaintIDE.jar"
oLink.IconLocation = strHomeFolder & "\AppData\Local\MSPaintIDE\images\ms-paint-logo.ico"

oLink.Save