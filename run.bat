@echo off
if not exist bin mkdir bin
echo Compiling...
javac -d bin src/com/jobsphere/core/*.java src/com/jobsphere/ui/*.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b %errorlevel%
)
echo Running Job Sphere...
java -cp bin com.jobsphere.ui.MainFrame
pause
