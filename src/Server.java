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
      // TODO: Data logging

      listener = new ServerSocket(listeningPort);

      // Maybe handle handshake stuff here?

    }
    catch (Exception e) {
      //TODO: exception handling
      System.out.print("Error starting listening on port");
      e.printStackTrace();
      System.out.println(e);
    }
  }

  // This needs to be called in a while(true) loop typically, the while loop needs to be in the peerProcess file in order for
  public void keepListening() {
    try {
      String inText;
      String outText;

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
  }

}
