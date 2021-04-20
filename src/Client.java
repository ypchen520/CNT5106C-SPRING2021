// TODO: swap Strings to Byte[]

import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

public class Client {
  Socket clientSocket;
  ObjectOutputStream out;
  ObjectInputStream in;
  // String message;
  byte[] message = null;
  String serverName;
  int peerID;
  int serverID;
  int serverPort;

  // Constructor
  public Client(byte[] inMessage, String inServer, int inPeerID, int inServerID, int inServerPort) {
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

  public byte[] getMessage() {
    return message;
  }

  public String getServerName() {
    return serverName;
  }

  public int getServerPort() {
    return serverPort;
  }

  public void setMessage(byte[] msg) {
    message = msg;
  }

  public void setServerName(String name) {
    serverName = name;
  }

  public void setServerPort(int port) {
    serverPort = port;
  }

  // Connect for handshake
  void connect() {
    // If statement for testing (converts localhost to something that works as localhost)
    try {
      String sentence;
      String returnedMessage;

      if (serverName.equals("localhost")) {
        clientSocket = new Socket(InetAddress.getByName(null), serverPort);
      }
      else {
        clientSocket = new Socket(serverName, serverPort);
      }



      DataOutputStream outStream = new DataOutputStream(clientSocket.getOutputStream());
      BufferedReader inStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

      sentence = "test";

      String messageString = new String(message, StandardCharsets.UTF_8);

      outStream.writeBytes(messageString + '\n');

      returnedMessage = inStream.readLine();


    }
    catch (Exception e) {
      System.out.print("Error connecting on server");
      e.printStackTrace();
      System.out.println(e);
    }

  }

  // Connect for transmitting data
  void transmit() {
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

      String messageString = new String(message, StandardCharsets.UTF_8);

      outStream.writeBytes(messageString + '\n');

      System.out.println("Client " + peerID + " attempting to connect to " + serverID + " with message " + message);

      returnedMessage = inStream.readLine();

      System.out.println("FROM SERVER: " + returnedMessage);


    }
    catch (Exception e) {
      System.out.print("Error connecting on server");
      e.printStackTrace();
      System.out.println(e);
    }
  }

  void closeSocket() {
    try {
      clientSocket.close();
    }
    catch (Exception e) {
      System.out.print("Error closing connection");
      e.printStackTrace();
      System.out.println(e);
    }
  }
}
