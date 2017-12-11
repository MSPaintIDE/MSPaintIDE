# MSPaintIDE

People joke about what IDE they use often, things like Word, MS Notepad, sometimes even _Eclipse_, and then often times MS Paint. People joke about MS Paint because it's not even a text editor, people joke about it because it doesn't have one feature in common with IDEs. Well, this application gives MS Paint a boost, and lets MS Paint highlight, compile, and execute code, with just a few clicks of a button, and only text coming from MS Paint. It is now much more practical than things like Word, Notepad, and obviously _Eclipse_.

## How it works
The way the MS Paint IDE works, is it is an application running separate from MS Paint. You input some locations for things like input image, output image location, compile to folder, etc. Once you save your code to MS Paint, you click **Compile/Execute** and the program uses a custom [OCR](https://en.wikipedia.org/wiki/Optical_character_recognition) implementation designed specially for MS Paint and code, then syntax highlights it, and then uses Java's JDK to compile the code and execute it. All output from the compiler and application compiled are outputed via images.

## Usage Tutorial
First to open the program, if you intend to compile your application, you **must** run the jar from your JDK. This can be done by doing something like `C:\Program Files\Java\jdk1.8.0_112\bin\java.exe -jar MSPaintIDE-1.0-SNAPSHOT.jar` in your commandline. This should be modified for your location of `java.exe` in your JDK's directory.

Before you do anything with highlighting, compiling and executing, you must set some paths first. You can manually input them in the text boxes, or click _Change_ and select files, or type in names in the selector. Here is what the following options do and should be set at:

#### Input Image
The only image you are saving from MS Notepad.

#### Highlighted Image
The path of the input file that will be highlighted to. This should be separate from your input.

#### Cache File
The location of a .txt document to save the cache of the image. If the input image hasn't changed since this file has been modified, it will read from this and _not_ the input image. This is to save a lot of time reading text.

#### Class Output
A folder to put all the compiled files.

#### Letter Directory
The directory of all the letters, this should have come packaged with the release, so select that folder's location.

#### Compiler Output
The image file that will contain all compiler output, like status, times, and errors. If no image is found by this name and location, one will be created.

#### Program Output
The image file that will contain all of the compiled program's output, like status, times, and errors. If no image is found by this name and location, one will be created.


_Note: All options are saved in a file named options.txt in the same directory of your jar, to keep you from re choosing everything every restart_

## Screenshots
![](https://rubbaboy.me/images/ihwxt7y)

![](https://rubbaboy.me/images/ow09uyl)

![](https://rubbaboy.me/images/7vdphau)

## Examples of program with errors:

![](https://rubbaboy.me/images/3tff4jz)

![](https://rubbaboy.me/images/wuds2rd)
