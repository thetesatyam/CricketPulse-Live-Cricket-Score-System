@echo off
cd /d "%~dp0cricketpulse-backend"
call mvnw.cmd spring-boot:run
pause
