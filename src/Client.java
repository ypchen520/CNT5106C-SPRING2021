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
  int peerID;
  int serverID;
  int serverPort;

  // Constructor
  public Client(String inMessage, String inServer, int inPeerID, int inServerID, int inServerPort) {
    message = inMessage;
    serverName = inServer;
    peerID = inPeerID;
    serverID = inServerID;
    serverPort = inServerPort;
  }

  // Get peerID
  public int getPeerID() {
    return peerID;
  }

  // Set peerID
  public void setPeerID(int inID) {
    peerID = inID;
  }

  public int getServerID() {
    return serverID;
  }

  public void setServerID(int inID) {
    serverID = inID;
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
    // If statement for testing (converts localhost to something that works as localhost)
    try {
      String sentence;
      String returnedMessage;

      if (serverName.equals("localhost")) {
        System.out.println("Converting to localhost address");
        System.out.println("Connecting to localhost with port " + serverPort);
        clientSocket = new Socket(InetAddress.getByName(null), serverPort);
      }
      else {
        clientSocket = new Socket(serverName, serverPort);
      }



      DataOutputStream outStream = new DataOutputStream(clientSocket.getOutputStream());
      BufferedReader inStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

      sentence = "test";

      outStream.writeBytes(message + '\n');

      System.out.println("Client " + peerID + " attempting to connect to " + serverID + " with message " + message);

      returnedMessage = inStream.readLine();

      System.out.println("FROM SERVER: " + returnedMessage);

      clientSocket.close();
    }
    catch (Exception e) {
      System.out.print("Error connecting on server");
      e.printStackTrace();
      System.out.println(e);
    }

  }
}
