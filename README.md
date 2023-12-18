# ScriptGUI Application

## Description
A window application that allows users to write and execute scripts, and then see the result of the execution in real time.

## Details
- Graphical interface is made with JavaFX.
- Scripts are run using JavaX package.
- Application currently allows using Kotlin as a scripting language but other languages can be added easily later on.
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

## Screenshots
The application greets you with this screen:
![Welcome screen](https://github.com/zkkv/ScriptGUI/blob/main/img/screenshot_start.png)

You can see how for-loop executes and prints an index to the output pane in each iteration: 
![For-loop with printing](https://github.com/zkkv/ScriptGUI/blob/main/img/screenshot_for_loop.png)

Even if no prints to `out` in the script are done, the result of the script execution will still be printed. In this case, it's the value of call to `foo()`:
![Calling a function which multiplies two numbers](https://github.com/zkkv/ScriptGUI/blob/main/img/screenshot_multiplication.png)

In case there are any script errors or warnings, you can click on them and it will take you to the correct place in code:
![Errors in the script are clickable](https://github.com/zkkv/ScriptGUI/blob/main/img/screenshot_errors.png)

## Used Libraries
I used [RichTextFX](https://github.com/FXMisc/RichTextFX) library since there is no native JavaFX support for text areas with source code.

## Developer
Developed by zkkv, 12/2023
