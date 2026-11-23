@echo off
setlocal
cd /d "%~dp0"
if not "%JAVA_HOME%"=="" (
  set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
) else (
  set "JAVA_EXE=java.exe"
)
"%JAVA_EXE%" -cp "%~dp0gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.BootstrapGradleWrapperMain %*
exit /b %ERRORLEVEL%
