
@SET APPLICATION_HOME=%~dp0
@SET JAVA_VM=java

@if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" SET JAVA_VM="%JAVA_HOME%\bin\java.exe"
%JAVA_VM% -Dapplication.properties=file:///%APPLICATION_HOME%/application.properties -jar wrapper.jar 
