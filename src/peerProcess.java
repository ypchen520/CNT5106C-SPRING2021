// package src;

import java.util.Scanner;
import java.io.*;
import java.util.*;
// import src.RemotePeerInfo;
import java.net.Socket;
import java.net.*;

// TODO: Add logging
public class peerProcess{
    // getConfiguration method reads in the PeerInfo cfg file.
    // Original code from the StartRemotePeers.java file provided on the course website
    static Vector<RemotePeerInfo> peerInfoVector = new Vector<RemotePeerInfo>();
    static Vector<Socket> clientSockets = new Vector<Socket>();
    static Vector<DataOutputStream> clientOutstreams = new Vector<DataOutputStream>();
    static Vector<BufferedReader> clientInstreams = new Vector<BufferedReader>();

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
        // BufferedReader in = new BufferedReader(new FileReader("PeerInfo.cfg"));

        // Alternative configuration file for local testing, leave one of them commented out
        BufferedReader in = new BufferedReader(new FileReader("PeerInfoLocal.cfg"));

        while((st = in.readLine()) != null) {

           String[] tokens = st.split("\\s+");
           int peerID = Integer.parseInt(tokens[0]);
           String hostname = tokens[1];
           int port = Integer.parseInt(tokens[2]);
           int hasFile = Integer.parseInt(tokens[3]);
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

    public static void main (String[] args){

      int peerID;
      // Check command line arguments
      // Should be one and only one argument containing the peerID
      if (args.length == 1) {
        peerID = Integer.parseInt(args[0]);

        // Read in PeerInfo.cfg using a modified function provided on the course website
        getConfiguration();

        // Find position of the peerID from the command line arguments in peerInfoVector
        // TODO: Check for duplicat IDs in the config file?
        int selfPos = -1;
        int pos = 0;

        while (pos < peerInfoVector.size() && selfPos == -1) {
          if (peerID == peerInfoVector.get(pos).getPeerID()) {
              selfPos = pos;
          }
          pos++;
        }
        System.out.print("Self listening port: " + peerInfoVector.elementAt(selfPos).getListeningPort());
        System.out.print("\n");

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
                …and also type and payload……
                ActualMessage actualmessage = new Actualmessage(messageLength,messageType,messagePayload);

                MessageHandler.onReceivePayload(actualMessage)

        */

        // Connect to peers (testing if statement, hostname, and port numbers, will need to change to work based on the cfg file)
        // Will need to loop to handshake with all previous peers in the cfg file, among other things
        if (selfPos != 0) {
          // Should change this to be an overloaded constructor to avoid all these unnecessary lines but when I tried it something broke so I just did this for testing and to be able to move on


          int newPeerID = peerInfoVector.elementAt(selfPos).getPeerID();
          int newServerPort = peerInfoVector.elementAt(selfPos - 1).getListeningPort();
          String newMessage = String.valueOf(peerInfoVector.elementAt(selfPos).getPeerID());
          String newServerName = peerInfoVector.elementAt(selfPos - 1).getHostName();
          int newServerID = peerInfoVector.elementAt(selfPos - 1).getPeerID();
          Client newClient = new Client(newMessage, newServerName, newPeerID, newServerID, newServerPort);
          newClient.connect();

        }


        // Connect to previous peers
        // Will need to be setup as a loop, currently goes to previous

        // Listen on port
        int thisPort = (peerInfoVector.elementAt(selfPos).getListeningPort());
        int thisPeerID = (peerInfoVector.elementAt(selfPos).getPeerID());
        int numPrevPeers = (selfPos);
        Server listenServer = new Server(thisPeerID, thisPort, numPrevPeers);

        listenServer.startListening();



        // try {
        //   String inText;
        //   String outText;
        //
        //   ServerSocket welcomeSocket = new ServerSocket(peerInfoVector.elementAt(selfPos).getListeningPort());
        //
        //   while (true) {
        //     Socket connectionSocket = welcomeSocket.accept();
        //
        //     BufferedReader clientIn = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        //
        //     DataOutputStream clientOut = new DataOutputStream(connectionSocket.getOutputStream());
        //
        //     inText = clientIn.readLine();
        //
        //     outText = inText.toUpperCase() + "\n";
        //
        //     clientOut.writeBytes(outText);
        //   }
        // }
        // catch (Exception e) {
        //   System.out.print("Error listening on port");
        //   e.printStackTrace();
        //   System.out.println(e);
        // }

        // Run server file
        // try {
        //   Runtime.getRuntime().exec("java peerServer.java");
        // }
        // catch (Exception e) {
        //   System.out.print("Error running server file");
        // }
        // // Run client file multiple times
        // try {
        //   Runtime.getRuntime().exec("java peerClient.java");
        // }
        // catch (Exception e) {
        //   System.out.print("Error running client file");
        // }








        // BELOW IS FAILED CODE: delete if next attempt works, leaving it for potential reference later
        // try (
        //   // Initiate listening on port
        //   ServerSocket peerServer = new ServerSocket(1337);
        //   // Wait for client to connect
        //   Socket peerClient = peerServer.accept();
        //   //
        //   PrintWriter outStream = new PrintWriter(peerClient.getOutputStream(), true);
        //   BufferedReader inStream = new BufferedReader (new InputStreamReader(peerClient.getInputStream()));
        // ) {
        //   // Initiate conversation with client
        //   System.out.println("here");
        //   // while (true) {
        //   //   String inText = "";
        //   //   try {
        //   //     inText = inStream.readLine();
        //   //     System.out.println(inText);
        //   //   }
        //   //   catch (IOException e) {
        //   //     System.out.println("inStream read error");
        //   //   }
        //   // }
        //   // outputLine = kkp.processInput(null);
        //   // out.println(outputLine);
        //   //
        //   // while ((inputLine = in.readLine()) != null) {
        //   //     outputLine = kkp.processInput(inputLine);
        //   //     out.println(outputLine);
        //   //     if (outputLine.equals("Bye."))
        //   //         break;
        //   // }
        // }
        // catch (Exception e) {
        //   System.out.println("Error listening on or connecting to socket");
        // }
        // // Initiate connection to each peer that is before it in peerInfoVector
        // int tempPeer = selfPos - 1;
        // while (tempPeer > 0) {
        //   // TODO: Connect to peer at position tempPeer
        //
        // }
      }
      else {
        System.out.println("There must be exactly one argument");
      }

    }

}
