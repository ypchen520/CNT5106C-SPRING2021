// TODO: swap Strings to Byte[] {Donald}

import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Server {
  int peerID;
  int listeningPort;
  int numPrevPeers;
  ServerSocket listener;

  // Constructor
  public Server(int inID, int inPort, int inNum) {
    peerID = inID;
    listeningPort = inPort;
    numPrevPeers = inNum;
  }

  // Get function for port number
  public int getListeningPort() {
    return listeningPort;
  }

  // Set function for port number
  public void setListeningPort(int portNumber) {
    listeningPort = portNumber;
  }

  // Get function for peer ID
  public int getPeerID() {
    return peerID;
  }

  public void setPeerID(int newID) {
    peerID = newID;
  }

  public int getNumPrevPeers() {
    return numPrevPeers;
  }

  public void setNumPrevPeers(int num) {
    numPrevPeers = num;
  }

  // Start listening to the port number
  public void startListening() {
    try {
      listener = new ServerSocket(listeningPort);
    }
    catch (Exception e) {
      System.out.print("Error starting listening on port");
      e.printStackTrace();
      System.out.println(e);
    }
  }

  // This needs to be called in a while(true) loop typically, the while loop needs to be in the peerProcess file in order for the client and server to run from a single process
  public byte[] keepListening() {
    String inText = "N/A";
    String outText;
    try {
      Socket connectionSocket = listener.accept();

      BufferedReader clientIn = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

      DataOutputStream clientOut = new DataOutputStream(connectionSocket.getOutputStream());

      inText = clientIn.readLine();

      outText = "Peer " + inText + " has successfully reached server " + peerID + "\n";

      clientOut.writeBytes(outText);

    }
    catch (Exception e) {
      System.out.print("Error listening on port");
      e.printStackTrace();
      System.out.println(e);
    }
    return inText.getBytes();
  }

}
