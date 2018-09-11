# MSPaintIDE

_For Spigot thread, check [here](https://www.spigotmc.org/threads/programming-in-ms-paint.289868). For website, click [here](https://ms-paint-i.de/). For a demo/installation tutorial video, check [here](https://www.youtube.com/watch?v=eyH4aXlB1Js)_

People joke about what IDE they use often, things like Word, MS Notepad, sometimes even _Eclipse_, and then often times MS Paint. People joke about MS Paint because it's not even a text editor, people joke about it because it doesn't have one feature in common with IDEs. Well, this application gives MS Paint a boost, and lets MS Paint highlight, compile, and execute code, with just a few clicks of a button, and only text coming from MS Paint. It is now much more practical than things like Word, Notepad, and obviously _Eclipse_.

## How it works
The way the MS Paint IDE works, is it is an application running separate from MS Paint. You input some locations for things like input image, output image location, compile to folder, etc. Once you save your code to MS Paint, you click **Compile/Execute** and the program uses a custom [OCR](https://en.wikipedia.org/wiki/Optical_character_recognition) implementation designed specially for MS Paint and code, then syntax highlights it, and then uses Java's JDK to compile the code and execute it. All output from the compiler and application compiled are outputed via images.

## Usage Tutorial
There is now an installation and demo video, for anything not explained fully here, or just for those who want to see the IDE in action: [https://www.youtube.com/watch?v=eyH4aXlB1Js](https://www.youtube.com/watch?v=eyH4aXlB1Js)

MS Paint IDE can be used in two different ways. You can either just run the jar (Following the instructions below), or you can run the installer as administrator, which provides a much cleaner way of using the program, and also allows you to right click any text file and edit it with MS Paint.

### With the Installer
The easiest way of using MS Paint IDE is via the installer. All is required for you to do is run the commandprompt as administrator, and run `java -jar MSPaintIDE.jar install` with `MSPaintIDE.jar` pointing to your downloaded MS Paint IDE jar. It will then remove the downloaded file and replace it with a shortcut with an icon. The shortcut is bound to ran with your latest installed JDK on your machine, so running the IDE doesn't require anything but running the shortcut. To uninstall the program, all is needed is to run the uninstaller in `%LocalAppData%\MSPaintIDE`.

### Without the Installer
First to open the program, if you intend to compile your application, you **must** run the jar from your JDK. This can be done by doing something like `"C:\Program Files\Java\jdk1.8.0_144\bin\java.exe" -jar MSPaintIDE-2.1.1-SNAPSHOT.jar` in your commandline. This should be modified for your location of `java.exe` in your JDK's directory.

### After Initial Installation
Before you do anything with highlighting, compiling and executing, you must set some paths first. You can manually input them in the text boxes, or click _Change_ and select files, or type in names in the selector. Here is what the following options do and should be set at:

#### Input Image/Image Folder
The only image (or folder of images) you are saving from MS Paint.

#### Highlighted Out Directory
The path of the directory for input files that will be highlighted to. This should be separate from your input.

#### Cache File Directory
The location of where the .txt documents should be save to as the cache of the image. If the input image hasn't changed since this file has been modified, it will read from this and not the input image. This is to save a lot of time reading text.

#### Class File Output
A folder to put all the compiled files.

#### Compiled Jar Output
The file which the compiled .jar should be placed.

#### Library jar(s) path
The location of any libraries to be compiled in the classpath of your project. This can be a single .jar file, or a directory containing multiple .jars.

#### Compile other file(s) path
The location for other files (eg. META-INF, plugin.yml, etc) to be compiled in the jar. This may be one singular file or a directory of files to be placed into the jar.

#### Letter Directory
The directory of all the letters, this should have come packaged with the release, so select that folder's location.

#### Compiler Output
The image file that will contain all compiler output, like status, times, and errors. If no image is found by this name and location, one will be created.

#### Program Output
The image file that will contain all of the compiled program's output, like status, times, and errors. If no image is found by this name and location, one will be created.

### Git Features
MS Paint IDE has only the important Git features, allowing you to do anything you would normally do on an IDE.

#### Create Repo
The `Create Repo` button simply does `git init` for your project, relative to the input image directory/file.

#### Add Files
Clicking the `Add Files` button will open a dialog for you to select as many files/directories as you want that will be added to the Git repo. If an image file is found, it will be scanned with the OCR, and that scanned version will be added to Git.

#### Add Remote
The `Add Remote` button will add whatever is in the text field to the right of the button as a remote origin.

The text box must contain an SSH origin containing authentication. A template for what is required is:
`https://Username:token@github.com/Username/Repo.git`
Please note the `token` in the template can be an authentication token to your account (Accessed/managed by going to GitHub's `Settings > Developer Settings > Personal access tokens` and generating a token) **or** the password for your account.

If you don't want the remote origin to be visible in the text box and in the console output to the right (for use in tutorials or demonstrations) you can click the visibility button to the right in the text field, and it will toggle the visibility for the origin.

#### Commit
Clicking the `Commit` button will simply make a commit with the message in the text box to the right.

#### Push
The `Push` button will push all unpushed commits to the remote origin.

### File Editing
**Note: This only works if you used the installer**

MS Paint IDE allows you to edit any text file on your system in MS Paint, allowing you to make whole projects just with MS Paint; no need to make your README's in MS Notepad any more!

To use this feature, right click any file (Preferably a text file) and click `Edit With MS Paint IDE`

![](https://ms-paint-i.de/images/context-menu.png)


After you click the context menu button, a MS Paint window will pop up with an image version of your file. You may edit this, and upon saving, the program's OCR will convert the image to text, saving it again.

[Here's a video example](https://youtu.be/FZnvNTZr7DQ) of this feature being used from beginning to end.


_Note: All options are saved in a file named options.txt in the same directory of your jar, to keep you from re choosing everything every restart_

   Once you have all of the options set, you can be free to program in MS Paint, saving to the file you set earlier. The font must be in font size **16**, with the font family being **Verdana**. If people are super interested in this (Which I doubt, but who knows) I would make it support more fonts, but right now that's not a top priority.

## Screenshots

Light theme variants of the screenshots available on [the website](https://ms-paint-i.de/), change the website theme from light to dark to see both variants.

![](https://ms-paint-i.de/images/screenshot-1-dark.png)

![](https://ms-paint-i.de/images/screenshot-2-dark.png)

![](https://ms-paint-i.de/images/screenshot-3-dark.png)

![](https://ms-paint-i.de/images/screenshot-4-dark.png)

![](https://ms-paint-i.de/images/screenshot-5.png)

![](https://ms-paint-i.de/images/screenshot-6.png)

## Examples of program with errors:

![](https://ms-paint-i.de/images/screenshot-7.png)

![](https://ms-paint-i.de/images/screenshot-8.png)


### Donations
If anyone would like to support me with donations, I would be extremely grateful, and would help support for future suggested projects :) If you donate and would like your name posted here, please PM me!

Thank you to:

[@Mr. Midnight](https://www.spigotmc.org/members/11614/)

[@Stef](https://www.spigotmc.org/members/18736/)

[@Meepidy](https://www.spigotmc.org/members/191302/)

[@iCodeHaven](https://www.spigotmc.org/members/482937/)

Feel free to donate via [PayPal](https://rubbaboy.fund/)

## Special Thanks To:
![](https://www.yourkit.com/images/yklogo.png)

YourKit supports open source projects with its full-featured Java Profiler.
YourKit, LLC is the creator of <a href="https://www.yourkit.com/java/profiler/">YourKit Java Profiler</a>
and <a href="https://www.yourkit.com/.net/profiler/">YourKit .NET Profiler</a>,
innovative and intelligent tools for profiling Java and .NET applications.
