<h1 align="center">
  <a href="http://hits.dwyl.io/RubbaBoy/MSPaintIDE"><img src="http://hits.dwyl.io/RubbaBoy/MSPaintIDE.svg" alt="HitCount"/></a>
  <a href="https://discord.gg/RXmPkPJ" style="color: transparent">
        <img src="https://img.shields.io/discord/528423806453415972.svg?logo=discord"
            alt="MS Paint IDE's Discord server">
  </a>
  <img src="https://img.shields.io/github/stars/MSPaintIDE/MSPaintIDE.svg?label=Stars&style=flat" alt="Stars"/>
  <a href="https://github.com/RubbaBoy/MSPaintIDE/issues"><img src="https://img.shields.io/github/issues/MSPaintIDE/MSPaintIDE.svg" alt="GitHub issues"/></a>
  <a href="https://github.com/RubbaBoy/MSPaintIDE/blob/master/LICENSE.txt"><img src="https://img.shields.io/github/license/MSPaintIDE/MSPaintIDE.svg" alt="GitHub issues"/></a>
    <a href="https://opencollective.com/MSPaintIDE"><img alt="open collective backers and sponsors" src="https://img.shields.io/opencollective/all/MSPaintIDE.svg"></a>
</h1>
<h1 align="center">
    <img src="https://ms-paint-i.de/images/Logo-Header.png">
</h1>
<p align="center">
  <b>
    <a href="https://ms-paint-i.de/">Website</a> |
    <a href="https://wiki.ms-paint-i.de/">Wiki</a> |
    <a href="https://discord.gg/RXmPkPJ">Discord</a> |
    <a href="https://www.youtube.com/watch?v=eyH4aXlB1Js">Installation/Demo</a> |
    <a href="https://github.com/MSPaintIDE/NewOCR">Custom OCR</a>
  </b>
</p>


People joke about what IDE they use often, things like Word, MS Notepad, sometimes even _Eclipse_, and then often times MS Paint. People joke about MS Paint because it's not even a text editor, people joke about it because it doesn't have one feature in common with IDEs. Well, this application gives MS Paint a boost, and lets MS Paint highlight, compile, and execute code, with just a few clicks of a button, and only text coming from MS Paint. It is now much more practical than things like Word, Notepad, and obviously _Eclipse_.

## Features

MS Paint IDE has grown an insane amount over the past year, and I plan for it to grow even more in the following year. Currently, these are some of the features present in MS Paint IDE:

- Can read, parse, and highlight code from purely image files
- Finding and replacing of text from image files
- Supports Java, Python, JavaScript, and Go, with many more planned
  - Includes [LSP](https://microsoft.github.io/language-server-protocol/) support alongside a very simple language API to add custom languages
- Git tools, to create and manage your project in Git from within the IDE
- Google Assistant support
- Right-click context menu to open and edit ANY text file in an MS Paint instance
- Integrated buttons within MS Paint (No program modifications) to do basic IDE tasks
- Native installer for easy usage
- Full Discord RPC Support
- IDE theming via CSS files
- Program and compiler outputs as image logs

## How It Works

MS Paint IDE is an application that runs alongside MS Paint that reads its images you make from MS Paint. It then uses a custom [OCR](https://github.com/MSPaintIDE/NewOCR/) to read, parse, highlight, and execute the text. All output files and logs are created as images, to show you the program output, and compilation/interpretation output.

## Wiki

Every feature, button, usage instructions, and whatever you want is on the wiki, which is the best place to start if you're interested in trying out the IDE. [https://wiki.ms-paint-i.de/](https://wiki.ms-paint-i.de/)

The wiki also includes screenshots of nearly everything, with videos coming soon upon the final 3.0.0 release.

## Contributing

Contributors are always welcome, and for setting up the repository locally, there's a wiki page walking through the whole process: [https://wiki.ms-paint-i.de/developing](https://wiki.ms-paint-i.de/developing)

## Donations

I've been doing mainly this project for over a year now, and even though I'm not making money off of it, I would be extremely appreciative for any contributions to help pay for the domain and the website's VPS. If you donate and want to be mentioned here, just put it in the donation message. Feel free to donate via  [PayPal](https://paypal.me/RubbaBoy) or [Open Collective](https://opencollective.com/mspaintide).

Thank you very much to the current donors:

[@Mr. Midnight](https://www.spigotmc.org/members/11614/)

[@Stef](https://www.spigotmc.org/members/18736/)

[@iCodeHaven](https://www.spigotmc.org/members/482937/)

[@AL1L](https://al1l.com/)

<h2 name="special-thanks">Special Thanks To:</h2>

![](https://www.yourkit.com/images/yklogo.png)

YourKit supports open source projects with its full-featured Java Profiler.
YourKit, LLC is the creator of <a href="https://www.yourkit.com/java/profiler/">YourKit Java Profiler</a>
and <a href="https://www.yourkit.com/.net/profiler/">YourKit .NET Profiler</a>,
innovative and intelligent tools for profiling Java and .NET applications.