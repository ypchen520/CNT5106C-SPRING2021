// TODO: swap Strings to Byte[] {Donald}

import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

public class Server {
  int peerID;
  int listeningPort;
  int numPrevPeers;
  ServerSocket listener;
  Logger log;
  MessageHandler thisMsgHandler;
  Vector<Client> clients;


  // Constructor
  public Server(int inID, int inPort, int inNum, Logger inLogger, MessageHandler inHandler, Vector<Client> inClients) {
    peerID = inID;
    listeningPort = inPort;
    numPrevPeers = inNum;
    log = inLogger;
    thisMsgHandler = inHandler;
    clients = inClients;
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

  // This needs to be called in a while(true) loop typically, the while loop needs to be in the peerProcess file in order to know when to end the loop inside the process
  // Instead of returning message, perform handshake operations and return a client object
  public void keepListening(Vector<Client> clients) {
    byte[] inHandshake = new byte[32];
    try {
      Socket connectionSocket = listener.accept();

      BufferedInputStream clientIn = new BufferedInputStream(connectionSocket.getInputStream());

      DataOutputStream clientOut = new DataOutputStream(connectionSocket.getOutputStream());

      // Read in the handshake message
      clientIn.read(inHandshake, 0, 32);

      // Get the peerID from the last 4 bytes of the message
      byte[] inPeerID = new byte[4];
      for (int i = 0; i < inPeerID.length; i++) {
        inPeerID[i] = inHandshake[28+i];
      }
      String tempString = new String(inPeerID, StandardCharsets.UTF_8);
      int serverID = Integer.parseInt(tempString);

      // Find the client which has the serverID
      int serverPos = -1;
      for (int i = 0; i < clients.size(); i++) {
        if (clients.get(i).getServerID() == serverID) {
          serverPos = i;
        }
      }
      if (serverPos == -1) {
        System.out.print("Error connecting to incoming handshake");
        System.out.println("Peer " + serverID + " is not in client vector");
      }
      else {
        // Modify client Socket
        clients.get(serverPos).connect(connectionSocket);
        peerProcess.connectedClientsVector.add(clients.get(serverPos));
        peerProcess.connectedPeerInfoVector.add(peerProcess.peerInfoVector.get(serverPos));

        // Log connection from
        log.logTcpConnection(serverID, "from");

        // Send BITFIELD message
        // Find the correct client
        int position = -1;
        for (int i = 0; i < clients.size(); i++) {
          if (clients.get(i).getServerID() == serverID) {
            position = i;
          }
        }
        if (position != -1) {
          System.out.println("Sent bitfield");
          thisMsgHandler.sendBitfieldMsg(clients.get(position));
        }
      }
      clients.get(serverPos).readMessage();
    }
    catch (Exception e) {
      System.out.print("Error connecting to incoming handshake");
      e.printStackTrace();
      System.out.println(e);
    }

  }
}
