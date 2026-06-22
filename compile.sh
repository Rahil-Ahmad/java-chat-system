#!/bin/bash
mkdir -p out

javac -d out \
  common/MessageType.java \
  common/Message.java \
  common/User.java \
  server/UserManager.java \
  server/ClientHandler.java \
  server/Server.java \
  client/LoginFrame.java \
  client/ChatFrame.java \
  client/Client.java

if [ $? -eq 0 ]; then
  echo "Build successful."
  echo "  Server: java -cp out server.Server"
  echo "  Client: java -cp out client.Client"
else
  echo "Build failed."
fi
