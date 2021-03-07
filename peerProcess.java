package src;

import java.util.Scanner;
import java.io.*;
import java.util.*;
import src.RemotePeerInfo;

// TODO: Add logging
public class peerProcess{
    // getConfiguration method reads in the PeerInfo cfg file.
    // Original code from the StartRemotePeers.java file provided on the course website
    static Vector<RemotePeerInfo> peerInfoVector = new Vector<RemotePeerInfo>();
    public static void getConfiguration()
    {
      String st;
      int i1;
      try {
        BufferedReader in = new BufferedReader(new FileReader("src/PeerInfo.cfg"));
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

        // Read in PeerInfo.cfg using a modified (overloaded to return the vector) function provided on the course website
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
        //If not in CFG output an error
        if (selfPos == -1) {
          System.out.print("Error: ID not in configuration file");
          System.exit(0);
        }

        // Start listening to port

        // Initiate connection to each peer that is before it in peerInfoVector

      }
      else {
        System.out.println("There must be exactly one argument");
      }

    }

}
