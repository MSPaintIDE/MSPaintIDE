# Code loosly adapted from https://stackoverflow.com/a/29002207/3929546
$bytes = [System.IO.File]::ReadAllBytes("%SHORTCUT_PATH%")
$bytes[0x15] = $bytes[0x15] -bor 0x20 #set byte 21 (0x15) bit 6 (0x20) ON
[System.IO.File]::WriteAllBytes("%SHORTCUT_PATH%", $bytes)
Remove-Item $MyInvocation.MyCommand.Definition