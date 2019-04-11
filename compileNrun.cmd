REM for some reason this doesn't work, java still doesn't recognize mysql to be within the classpath.
REM so I started learning to use gradle.


@echo off

if "%DEBUG%" == "" (

    @echo off
) else (

    @echo on
)

REM using modules by javafx

set modulePath=c:\Program Files\Java\javafx-sdk-11.0.1\lib
set mPATH=c:\projects\java\financialmanager\java\
set CLASSPATH=%mPATH%;%mPATH%mysql-connector-java\mysql-connector-java.jar;

set extraParams= -cp "%CLASSPATH%" --module-path "%modulePath%" --add-modules=javafx.controls,javafx.fxml
javac %extraParams% java\*.java


REM goto end if compilation was unsuccessful or if no run class was defined.
if "%1"=="" (
    echo no run class was defined, exiting...
    goto end
)

java %extraParams% %1

:end
@echo on
