@echo off
setlocal enableextensions enabledelayedexpansion
set CP=
for %%f in (lib\*.jar) do set CP=!CP!;%%f
java -classpath %CP% zen.bricks.MainWindow
