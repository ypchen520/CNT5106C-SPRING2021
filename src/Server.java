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

      String inText;
      String outText;

      ServerSocket welcomeSocket = new ServerSocket(listeningPort);

      while (true) {
        Socket connectionSocket = welcomeSocket.accept();

        BufferedReader clientIn = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

        DataOutputStream clientOut = new DataOutputStream(connectionSocket.getOutputStream());

        inText = clientIn.readLine();

        outText = "Peer " + inText + " has successfully reached server " + peerID + "\n";

        clientOut.writeBytes(outText);
      }
      // Call constructor
      // listener = new ServerSocket(listeningPort);

      // Start listening for multiple clients via infinite loop
      // while (true) {
      //   try {
      //     // Receive incoming client request
      //     Socket socket = listener.accept();
      //
      //     // Obtain the I/O streams
      //
      //     // Close the socket once finished
      //     socket.close();
      //   }
      //   catch (Exception e) {
      //     // TODO: exception handling
      //
      //   }
      // }
    }
    catch (Exception e) {
      //TODO: exception handling
      System.out.print("Error listening on port");
      e.printStackTrace();
      System.out.println(e);
    }
  }

}
