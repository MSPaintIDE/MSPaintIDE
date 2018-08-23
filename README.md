# MSPaintIDE

_For Spigot thread, check [here](https://www.spigotmc.org/threads/programming-in-ms-paint.289868). For website, click [here](https://ms-paint-i.de/)_

People joke about what IDE they use often, things like Word, MS Notepad, sometimes even _Eclipse_, and then often times MS Paint. People joke about MS Paint because it's not even a text editor, people joke about it because it doesn't have one feature in common with IDEs. Well, this application gives MS Paint a boost, and lets MS Paint highlight, compile, and execute code, with just a few clicks of a button, and only text coming from MS Paint. It is now much more practical than things like Word, Notepad, and obviously _Eclipse_.

## How it works
The way the MS Paint IDE works, is it is an application running separate from MS Paint. You input some locations for things like input image, output image location, compile to folder, etc. Once you save your code to MS Paint, you click **Compile/Execute** and the program uses a custom [OCR](https://en.wikipedia.org/wiki/Optical_character_recognition) implementation designed specially for MS Paint and code, then syntax highlights it, and then uses Java's JDK to compile the code and execute it. All output from the compiler and application compiled are outputed via images.

## Usage Tutorial
First to open the program, if you intend to compile your application, you **must** run the jar from your JDK. This can be done by doing something like `"C:\Program Files\Java\jdk1.8.0_144\bin\java.exe" -jar MSPaintIDE-2.0.2-SNAPSHOT.jar` in your commandline. This should be modified for your location of `java.exe` in your JDK's directory.

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



_Note: All options are saved in a file named options.txt in the same directory of your jar, to keep you from re choosing everything every restart_

   Once you have all of the options set, you can be free to program in MS Paint, saving to the file you set earlier. The font must be in font size **16**, with the font family being **Verdana**. If people are super interested in this (Which I doubt, but who knows) I would make it support more fonts, but right now that's not a top priority.

## Screenshots
![](https://rubbaboy.me/images/byqmep9)

![](https://rubbaboy.me/images/y55jisj)

![](https://rubbaboy.me/images/h8bg7ys)

![](https://rubbaboy.me/images/7y6b3l2)

![](https://rubbaboy.me/images/ow09uyl)

![](https://rubbaboy.me/images/7vdphau)

## Examples of program with errors:

![](https://rubbaboy.me/images/3tff4jz)

![](https://rubbaboy.me/images/wuds2rd)


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
