# ScriptGUI Application

## Description
A window application that allows users to write and execute scripts, and then see the result of the execution in real time.

## Details
- Graphical interface is made with JavaFX.
- Scripts are run using JavaX package.
- Application currently allows to use Kotlin as a scripting language but other languages can be added easily later on.
- Users can click on errors to get to the place in the script source code where they appear.
- Keywords of the scripting language are highlighted in the editor pane.
- Execution of a script can be stopped at any time.
- The result of the execution is shown in the output pane in real time.

## How to Build
1. Start by cloning the repository and navigating to the root folder.
2. Open the terminal.
3. Run one of the following.
- Mac/Linux:
```
./gradlew build
```
- Windows:
```
gradlew build
```
Alternatively, use an IDE in which you can execute Gradle `build` configuration.

## How to Run
Similarly to `build`, execute `run`. The application window should appear.

## Used Libraries
I used [RichTextFX](https://github.com/FXMisc/RichTextFX) library since there is no native JavaFX support for text areas with source code.

## Developer
Developed by zkkv, 12/2023
