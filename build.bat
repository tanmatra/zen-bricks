@echo off
set DIST=build\dist
set LIB=%DIST%\lib
if "%1" == "pack" goto pack
if "%1" == "dist" goto dist
set PLUGINS=C:\bin\eclipse\plugins
mkdir %LIB%
copy %PLUGINS%\org.eclipse.swt_3.6.0.*.jar %LIB%
copy %PLUGINS%\org.eclipse.swt.win32.win32.x86_3.6.0.*.jar %LIB%
copy %PLUGINS%\org.eclipse.jface_3.6.0.*.jar %LIB%
copy %PLUGINS%\org.eclipse.core.commands_3.6.0.*.jar %LIB%
copy %PLUGINS%\org.eclipse.core.runtime_3.6.0.*.jar %LIB%
copy %PLUGINS%\org.eclipse.equinox.common_3.6.0.*.jar %LIB%
mkdir %DIST%\themes
copy themes %DIST%\themes
copy run.bat %DIST%
:pack
jar cf %LIB%\zen.jar -C build\classes zen
:dist
jar cfM build\zen.bricks.win32_0.1.zip -C %DIST% .
