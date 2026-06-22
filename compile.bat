@echo off
if not exist out mkdir out

javac -d out common\MessageType.java common\Message.java common\User.java
javac -d out -cp out server\UserManager.java server\ClientHandler.java server\Server.java
javac -d out -cp out client\LoginFrame.java client\ChatFrame.java client\Client.java

if %ERRORLEVEL% == 0 (
    echo Build successful.
    echo   Server: java -cp out server.Server
    echo   Client: java -cp out client.Client
) else (
    echo Build failed.
)
