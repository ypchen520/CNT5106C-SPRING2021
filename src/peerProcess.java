// TODO: setup directory structure and {Yu-Peng}

// TODO: swap Strings to Byte[] for Client/Server objects {Donald}

// package src;

// import com.sun.org.apache.bcel.internal.generic.NEW;
//
// import sun.misc.OSEnvironment;

import java.io.*;
import java.util.*;
// import src.RemotePeerInfo;
import java.net.Socket;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class peerProcess{

	public static int indexID;
    // getConfiguration method reads in the PeerInfo cfg file.
    // Original code from the StartRemotePeers.java file provided on the course website
    static Vector<RemotePeerInfo> peerInfoVector = new Vector<RemotePeerInfo>();
    static Vector<Socket> clientSockets = new Vector<Socket>();
    static Vector<DataOutputStream> clientOutstreams = new Vector<DataOutputStream>();
    static Vector<BufferedReader> clientInstreams = new Vector<BufferedReader>();
    // static Vector<MessageHandler> thisMsgHandler = new Vector<MessageHandler>();


    public static ArrayList<RemotePeerInfo> interestedPeers = new ArrayList<>();
    public static ArrayList<Integer> requestedPieces = new ArrayList<>();

    static Vector<Client> clients = new Vector<Client>();
    public static int maxPieces;
    public static Object lock = new Object();
    public static CommonUtil comUtil;
    public static int hasOriginalFile;

    // public static void addPeerConnection(int selfPos) {
    //   try {
    //     String in;
    //     String out;
    //
    //     BufferedReader textIn = new BufferedReader (new InputStreamReader(System.in));
    //
    //     Socket clientSocket = new Socket(InetAddress.getByName(null), peerInfoVector.elementAt(selfPos - 1).getListeningPort());
    //
    //     DataOutputStream outStream = new DataOutputStream(clientSocket.getOutputStream());
    //
    //     BufferedReader inStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    //
    //
    //
    //     outStream.writeBytes(in + '\n');
    //
    //     modifiedSentence = inStream.readLine();
    //
    //     System.out.println("FROM SERVER: " + modifiedSentence);
    //
    //     clientSocket.close();
    //   }
    //   catch (Exception e) {
    //     System.out.print("Error connecting client");
    //   }
    // }

    public static void closePeerConnections() {

    }

    public static void getConfiguration()
    {
      String st;
      int i1;
      try {
        // Read in configuration file
        // Final TODO: make sure this one is uncommented and the other is commented before submitting
        // BufferedReader in = new BufferedReader(new FileReader("PeerInfo.cfg"));

        // Alternative configuration file for local testing, leave one of them commented out
        BufferedReader in = new BufferedReader(new FileReader("PeerInfoLocal.cfg"));

        while((st = in.readLine()) != null) {

           String[] tokens = st.split("\\s+");
           int peerID = Integer.parseInt(tokens[0]);
           String hostname = tokens[1];
           int port = Integer.parseInt(tokens[2]);
           int hasFile = Integer.parseInt(tokens[3]);
           // peerProcess.hasOriginalFile = hasFile;
             //System.out.println("tokens begin ----");
             //for (int x=0; x<tokens.length; x++) {
             //    System.out.println(tokens[x]);
             //}
               //System.out.println("tokens end ----");

             peerInfoVector.addElement(new RemotePeerInfo(peerID, hostname, port, hasFile));

        }

        in.close();
      }
      catch (Exception ex) {
        System.out.println(ex.toString());
      }

    }

    public static void getCommon()
    {
      String st;
      // int i1;
      try {
        // Read in common configuration file
        // Final TODO: make sure this one is uncommented and the other is commented before submitting
        // BufferedReader in = new BufferedReader(new FileReader("PeerInfo.cfg"));

        // Alternative configuration file for local testing, leave one of them commented out
        int numNeighbors = 0;
        int unchockingInterval = 0;
        int optUnchockingInterval = 0;
        String fileName = "";
        int fileSize = 0;
        int pieceSize = 0;

        BufferedReader in = new BufferedReader(new FileReader("Common.cfg"));

        while((st = in.readLine()) != null) {

            String[] tokens = st.split("\\s+");

            switch(tokens[0]){
              case "NumberOfPreferredNeighbors":
                numNeighbors = Integer.parseInt(tokens[1]);
                break;
              case "UnchokingInterval":
                unchockingInterval = Integer.parseInt(tokens[1]);
                break;
              case "OptimisticUnchokingInterval":
                optUnchockingInterval = Integer.parseInt(tokens[1]);
                break;
              case "FileName":
                fileName = tokens[1];
                break;
              case "FileSize":
                fileSize = Integer.parseInt(tokens[1]);
                break;
              case "PieceSize":
                pieceSize = Integer.parseInt(tokens[1]);
                break;
            }
            //System.out.println("tokens begin ----");
            //for (int x=0; x<tokens.length; x++) {
            //    System.out.println(tokens[x]);
            //}
            //System.out.println("tokens end ----");
        }
        maxPieces=FileHandler.getMaxPieces();
        peerProcess.comUtil = new CommonUtil(numNeighbors, unchockingInterval, optUnchockingInterval, fileName, fileSize, pieceSize);
        in.close();
      }
      catch (Exception ex) {
        System.out.println(ex.toString());
      }

    }

    public static void main (String[] args){

      int peerID;
      Logger thisLog;
      FileHandler thisFileHandler;
      // Check command line arguments
      // Should be one and only one argument containing the peerID
      if (args.length == 1) {
        peerID = Integer.parseInt(args[0]);

        // Read in PeerInfo.cfg using a modified function provided on the course website
        getConfiguration();
        getCommon();
        thisLog = new Logger(peerID);
        thisFileHandler = new FileHandler(peerID, comUtil.getFileName(), comUtil.getfileSize(), comUtil.getpieceSize(), peerInfoVector.get(indexID).getHasFileOrNot());
        thisFileHandler.readFromFile();
        System.out.println("Has file: " + peerInfoVector.get(indexID).getHasFileOrNot());
        MessageHandler thisMsgHandler = new MessageHandler(comUtil, peerID, thisLog, thisFileHandler);


        // Find position of the peerID from the command line arguments in peerInfoVector
        int selfPos = -1;
        int pos = 0;

        while (pos < peerInfoVector.size() && selfPos == -1) {
          if (peerID == peerInfoVector.get(pos).getPeerID()) {
              selfPos = pos;
              indexID=selfPos;
              if(peerInfoVector.get(indexID).getHasFileOrNot()==1) {
            	  for(int i =0;i<maxPieces-1;i++) {
            		  peerInfoVector.get(pos).pieceIndex.add(i);
            	  }
              }
              break;
          }
          pos++;
        }
        System.out.println("Self listening port: " + peerInfoVector.elementAt(selfPos).getListeningPort());

        //If not in CFG output an error
        if (selfPos == -1) {
          System.out.print("Error: ID not in configuration file");
          System.exit(0);
        }



        // Start listening to port
        /*
        Futing:
            read the bytes bytep[] bytes
            judge if the bytes is handshake message,if true:

                bytes[] peerId = Arrays.copyOfRange(bytes,28,32);
                int peerID = MessageHandle.getPeerId(peerID);
                HandshakeMessage handshakemessage = new HandshakeMessage(peerID);

            if false:(actual message)

                bytes[] Length = Arrays.copyOfRange(bytes,0,4);
                int messageLength = MessageHandle.getMessageLength(Length);
                鈥nd also type and payload鈥︹��
                ActualMessage actualmessage = new Actualmessage(messageLength,messageType,messagePayload);

                MessageHandler.onReceivePayload(actualMessage)

        */

        // Create vector of clients initialized to have a handshake message for each peer in the configuration file
        // Clients are not connected, so they do not have a socket or in/out streams until connect(Socket clientSocket) is called
        for (int i = 0; i < peerInfoVector.size(); i++) {
          int newPeerID = peerInfoVector.elementAt(selfPos).getPeerID();
          int newServerPort = peerInfoVector.elementAt(i).getListeningPort();
          // String newMessage = String.valueOf(peerInfoVector.elementAt(selfPos).getPeerID());
          HandshakeMessage newHandshake = new HandshakeMessage(peerID);
          byte[] newMessage = newHandshake.createHandshake();

          String newServerName = peerInfoVector.elementAt(i).getHostName();
          int newServerID = peerInfoVector.elementAt(i).getPeerID();
          Client newClient = new Client(newMessage, newServerName, newPeerID, newServerID, newServerPort, thisLog, thisMsgHandler, clients);
          clients.add(newClient);
        }
        boolean[] connectedClients = new boolean[clients.size()];
        for (int i = 0; i < clients.size(); i++) {
          connectedClients[i] = false;
        }

        // Listen on port
        int thisPort = (peerInfoVector.elementAt(selfPos).getListeningPort());
        int thisPeerID = (peerInfoVector.elementAt(selfPos).getPeerID());
        int numPrevPeers = (selfPos);
        Server listenServer = new Server(thisPeerID, thisPort, numPrevPeers, thisLog, thisMsgHandler, clients);


        // Call handshake to each peer before it in the CFG then set loop to listen for new connections
        // Initial handshakes
        listenServer.startListening();
        boolean finishedListening = false;
        for (int i = 0; i < selfPos; i++) {
          Socket clientSocket;
          try {
            // Generate the socket (if statement is for testing purposes since "localhost" doesn't work as a serverName)
            if (clients.get(i).getServerName().equals("localhost")) {
              clientSocket = new Socket(InetAddress.getByName(null), clients.get(i).getServerPort());
            }
            else {
              clientSocket = new Socket(clients.get(i).getServerName(), clients.get(i).getServerPort());
            }
            // Perform the handshake
            clients.get(i).connect(clientSocket);
            connectedClients[i] = true;

            // Read in returned handshake message
            clients.get(i).readMessage();
          }
          catch (Exception e) {
            System.out.print("Error sending handshake to peer " + clients.get(i).getServerID());
            e.printStackTrace();
            System.out.println(e);
          }

        }

        // Listen for new handshakes and perform operations
        while(!finishedListening) {
          // Receive next data from peers
          listenServer.keepListening(clients);
          // If message is a handshake, return a handshake and add the peer to the connected clients list
        //   String inMessageString = new String(inMessage, StandardCharsets.UTF_8);
        //   if (inMessageString.substring(0, 28).equals("P2PFILESHARINGPROJ0000000000")) {
        //     // Get ID of peer that sent the handshake
        //     int handshakePeerID = Integer.parseInt(inMessageString.substring(inMessageString.length() - 4));
        //     // Find position of peer in vectors
        //     int location = -1;
        //     for (int i = 0; i < clients.size(); i++) {
        //       if (clients.get(i).getServerID() == handshakePeerID) {
        //         location = i;
        //       }
        //     }
        //     // Check that peerID is valid from the configuration file
        //     if (location == -1) {
        //       // TODO: error message or exception or something, not super important but should probably do it if we have time
        //     }
        //     else {
        //       try {
        //         thisLog.logTcpConnection(clients.get(location).getServerID(), "from");
        //       }
        //       catch (Exception e) {
        //         System.out.print("Error receiving handshake from peer " + clients.get(location).getServerID());
        //         e.printStackTrace();
        //         System.out.println(e);
        //       }
        //
        //       // Check if handshake message has already been sent to and received by the peer that initiated this handShakeMsg
        //       // If handshake has not been made the other way, send a message back to complete the handshake
        //       if (connectedClients[location] != true) {
        //         try {
        //           clients.get(location).connect();
        //           thisLog.logTcpConnection(clients.get(location).getServerID(), "to");
        //         }
        //         catch (Exception e) {
        //           System.out.print("Error sending handshake to peer " + clients.get(location).getServerID());
        //           e.printStackTrace();
        //           System.out.println(e);
        //         }
        //       }
        //
        //       // Mark the peer as connected
        //       connectedClients[location] = true;
        //     }
        //
        //     // TODO: - Reference new MessageHandler {Donald}
        //     // Send BITFIELD
        //     // Make ActualMessage object with information
        //     // Call ActualMessage.createMessage() to generate the byte[] message
        //     // Send the byte[] message through the appropriate client
        //
        //   }
        //   // If message is not a handshake (and not an empty response from the while loop waiting for a client connection), handle based on ActualMessage type
        //   else if (inMessage.length != 0) {
        //     // TODO: {Yu-Peng} - old MessageHandler functions
        //     // ActualMessage receivedMessage = new ActualMessage()
        //     // Make a new ActualMessage to put in function
        //     // Call onReceiveMessage() (or just paste the functionality here) to work out what the message is        //
        //     // TODO: have ActualMessage object - Reference new MessageHandler {Donald}
        //     // Create Peer object (or have a vector of them already setup? not sure)
        //     // Call the correct function based on message type
        //     // Modify the Peer object to get passed the Logger object so it can do data logging stuff
        //     // Modify Peer class to get passed the Vector of clients
        //   }
        //
        }
      }
      else {
        System.out.println("There must be exactly one argument");
      }

    }

    public static  ArrayList<RemotePeerInfo> getInterestedPeers(){
        return interestedPeers;
    }

    public static void checkFinish(Logger logger) throws IOException{
    	for(RemotePeerInfo remotePeerInfo:peerProcess.peerInfoVector){
    		if(remotePeerInfo.pieceIndex.size()<peerProcess.maxPieces) {
    			return;
    		}
    	}
      // TODO: Close clients and server {Donald}

      // TODO: Write log to file {Yu-Peng}
      logger.writeToFile();
    	System.exit(0);
    }

    // private static int convertByteArrayToInt(byte[] data) {
    //   if (data == null || data.length != 4) return 0x0;
    //   // ----------
    //   return (int)( // NOTE: type cast not necessary for int
    //           (0xff & data[0]) << 24  |
    //           (0xff & data[1]) << 16  |
    //           (0xff & data[2]) << 8   |
    //           (0xff & data[3]) << 0
    //   );
    // }
}
