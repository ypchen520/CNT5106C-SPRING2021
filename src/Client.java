import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Client {
  Socket clientSocket;
  ObjectOutputStream out;
  ObjectInputStream in;
  String message;
  String serverName;
  int serverPort;

  // Constructor
  public void client() {

  }

  public String getMessage() {
    return message;
  }

  public String getServerName() {
    return serverName;
  }

  public int getServerPort() {
    return serverPort;
  }

  public void setMessage(String msg) {
    message = msg;
  }

  public void setServerName(String name) {
    serverName = name;
  }

  public void setServerPort(int port) {
    serverPort = port;
  }

  // Connect to TCP server
  void connect() {
    try {
      // Socket to connect to Server

    }
    catch (Exception e) {
      // TODO: exception handling

    }
  }
}
