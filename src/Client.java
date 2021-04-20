// TODO: swap Strings to Byte[] {Donald}

import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

public class Client {
  Socket clientSocket;
  DataOutputStream clientOut;
  BufferedInputStream clientIn;
  // String message;
  byte[] message = null;
  String serverName;
  int peerID;
  int serverID;
  int serverPort;
  Logger log;

  // Constructor
  public Client(byte[] inMessage, String inServer, int inPeerID, int inServerID, int inServerPort, Logger inLogger) {
    message = inMessage;
    serverName = inServer;
    peerID = inPeerID;
    serverID = inServerID;
    serverPort = inServerPort;
    log = inLogger;
  }

  public void setSocket(Socket inSocket) {
    clientSocket = inSocket;
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

  // Connect function, provided socket (if is sending handshake before listening, generated from peerProcess.java, if connecting after receiving a handshake message then generated by Server.java)
  void connect(Socket inSocket) {

    try {
      clientSocket = inSocket;

      clientOut = new DataOutputStream(clientSocket.getOutputStream());
      clientIn = new BufferedInputStream(clientSocket.getInputStream());

      // Since this is called if and only if the client is initialized but has not been connected previously, message is already initialized to be the appropriate handshake message
      // Send initial handshake message
      clientOut.write(message);

      // Responses will be read from buffer via readMessage();

      // Log connection to peer
      log.logTcpConnection(serverID, "to");
    }
    catch (Exception e) {
      System.out.print("Error connecting on server");
      e.printStackTrace();
      System.out.println(e);
    }
  }

  // Sends an already generated message from an ActualMessage object
  public void sendMessage(ActualMessage msg) {
    try {
      this.setMessage(msg.createMessage());
      clientOut.write(message);
    }
    catch (Exception e) {
      System.out.print("Error transmitting");
      e.printStackTrace();
      System.out.println(e);
    }

  }

  // Reads a byte[] message from the buffer then process into an ActualMessage, then respond based on the message type if response is necessary
  // Call this in an infinite loop? need to make sure that an infinite loop here doesn't cause the serversocket loop to break
  // OK actually, make that loop have the timer on it and it reads the message if there
  public void readMessage() {

    //TODO: if receiving handshake message, log "connected from" and ignore the rest of the message (handshake messages will only be sent here if it was the second one to send it)

    // TODO: this is code copied from old Server.java functionality, need to modify to use correct in/out streams and not create a new socket
    String inText = "N/A";
    String outText;
    byte[] inBytes;
    byte[] inType;
    byte[] msgLength;
    byte[] fullMessage;
    int messageLength;
    try {
      // Read the first 4 bytes to determine the remaining length of the message
      msgLength = new byte[4];
      clientIn.read(msgLength, 0, 4);
      // First, check if the message is a handshake message instead of an actual message, in which case there is a fixed length and the first 4 bytes do not correspond to message length
      if (msgLength[0] == 80 && msgLength[1] == 50 && msgLength[2] == 80 && msgLength[3] == 70) {
        // Message is HandshakeMessage with full length 32 bytes, so there are 28 bytes remaining to read in
        inBytes = new byte[28];
        clientIn.read(inBytes, 0, 28);
        // Get peerID from the handshake
        // Get the peerID from the last 4 bytes of the message
        byte[] inPeerID = new byte[4];
        for (int i = 0; i < inPeerID.length; i++) {
          inPeerID[i] = inBytes[24+i];
        }
        String tempString = new String(inPeerID, StandardCharsets.UTF_8);
        int serverID = Integer.parseInt(tempString);

        log.logTcpConnection(serverID, "from");
      }
      else {
        // Message is ActualMessage

        //TODO: swap this from being placeholder length to actually working code, this else statement won't be reached yet so just need to initialize things for it to compile
        messageLength = Utils.convertByteArrayToInt(msgLength);
        inType = new byte[1];
        inBytes = new byte[messageLength - 1];
        clientIn.read(inType, 0, 1);
        clientIn.read(inBytes, 0, messageLength);

        // fullMessage = new byte[msgLength.length + inBytes.length];
        // System.arraycopy(msgLength, 0, fullMessage, 0, msgLength.length);
        // System.arraycopy(inBytes, 0, fullMessage, msgLength.length, inBytes.length);



        ActualMessage actualMsg = new ActualMessage();
        byte[] msgLenRaw = Arrays.copyOf(msgLength, msgLength.length);
        byte typeRaw = inType[0];
        byte[] msgPayloadRaw = Arrays.copyOf(inBytes, inBytes.length);

        int msgLen = Utils.convertByteArrayToInt(msgLenRaw);
        actualMsg.setMessageLength(msgLen);
        actualMsg.setPayload(msgPayloadRaw);

        switch(typeRaw) {

            case (byte) 0:
                actualMsg.setMessageType(ActualMessage.MessageType.CHOKE);
                break;
            case (byte) 1:
                actualMsg.setMessageType(ActualMessage.MessageType.UNCHOKE);
                break;
            case (byte) 2:
                actualMsg.setMessageType(ActualMessage.MessageType.INTERESTED);
                break;
            case (byte) 3:
                actualMsg.setMessageType(ActualMessage.MessageType.NOT_INTERESTED);
                break;
            case (byte) 4:
                actualMsg.setMessageType(ActualMessage.MessageType.HAVE);
                break;
            case (byte) 5:
                actualMsg.setMessageType(ActualMessage.MessageType.BITFIELD);
                break;
            case (byte) 6:
                actualMsg.setMessageType(ActualMessage.MessageType.REQUEST);
                break;
            case (byte) 7:
                actualMsg.setMessageType(ActualMessage.MessageType.PIECE);
                break;
            default:
                System.out.println("Wrong type");
        }

      }

      //Send back PeerID (only used for handshake messages)

    }
    catch (Exception e) {
      System.out.print("Error listening on port");
      e.printStackTrace();
      System.out.println(e);
      fullMessage = new byte[0];
    }
  }

  public void closeSocket() {
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
