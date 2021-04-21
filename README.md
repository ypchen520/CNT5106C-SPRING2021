# CNT5106C-SPRING2021
Group members: Yu-Peng Chen, Donald Honeycutt, Futing Shan

A P2P file sharing system.

## Instructions

* run ```cd src/``` to change to the working directory.
* run ```make``` to compile.
* run ```make clean``` to remove the compiled .class files.


## Partial credit information
* Core problem: messages are sometimes sent but not read from the buffer, which once it occurs stops any further messages

* Peer connection through socket: fully implemented and working (when messages are properly read)
  * Creates a socket and two clients that share input/output streams
  * Processes handshake between the two peers and then keeps input/output streams open until process termination
* Interpretation of received actual messages: fully implemented and working (when messages are properly read from the buffer)
  * Splits message into message length, message type, and message payload.
* Sending of messages: fully implemented and working
  * Sends through a data output stream
  * Is properly received by peer, is processed
* Reading of messages: inconsistently working
  * A buffered input stream is used to read the most recently received messages
    * When it works properly, it interprets the message length then reads the remaining bytes from the input buffer to get the full messages
    * Received message is made into an ActualMessage object which keeps track of length, type, and the payload
    * If applicable, sends a reply message based on type
  * ISSUE: sometimes, a message is recorded as being sent from one of the peers but is never received by the peer it was sent to.
    * After this occurs once, any further reading from the message input buffer ceases to work
    * Messages that we observed being accurately received: CHOKE/UNCHOKE/INTERESTED/BITFIELD/REQUEST/PIECE
      * Messages that only occur further into the process (e.g., Request/Piece) were less likely to be accurately received since once it fails no further messages can be read (which also stops the sending of messages from the sending peer since it is permanently waiting for the response message)
* Message types:
  * Handshake: fully implemented and working (when messages are properly read)
    * For peers that are earlier than it in the CFG, handshake messages are sent from peerProcess.java to a Server object, which is continually listening for new connections as a ServerSocket using a while(true) loop
    * Processes that the message is a handshake message based on the first 4 bytes of the message
    * Reads in the rest of the message and creates a socket, alongside a Client object that has input/output streams corresponding to the socket
    * If the handshake was generated by the initial connection to the Server instance, the receiving peer sends a handshake back that creates a new client object using the same socket (so the input/output streams are connected)
    * If the handshake was generated in response to an initial handshake, the handshake is received and processed but no additional handshake is performed since both peers have sent a handshake at this point
    * After the handshake is completed, each peer will send a BITFIELD message
  * Bitfield: fully implemented and working (when messages are properly read)
    * First takes out piece index that the peer has, and converts those to bytes
    * Due to the implementation being in JAVA, all 1s is not equal to a byte of all 1s, it is equal to -1. So the byte we pass only uses 7 pieces.
      * This results in pieces of size 7 instead of 8
      * This functionality was tested externally to the message sending issue and worked fine, pieces are encoded/decoded based on this definition of a piece so we do not think that this difference is the cause of the issue that we had
  * Request: fully implemented and working (when messages are properly read)
    * Sends the piece index as the payload
    * Sends a Piece message back (typically) corresponding to the piece index
    * Special case: when a peer sends a request it ideally knows it is unchoked. However, due to the timer setup for the unchoking/rechoking process, a request message may be sent while unchoked, but received while choked.
      * To deal with this, when a request is received it first checks if the sending peer it was sent from is currently unchoked or not. If it is unchoked, the receiving peer sends a Piece message back. If it is choked, no reply message is sent.
  * Piece: Fully implemented but unable to confirm if it works or not since we could not get the system to the point where a Piece message completed before the message buffer reading broke
    * Accesses the payload from a FileHandler object to extract the piece (byte array) from a 2D byte array in the file handler using the requested index
      * A peer that has file pieces stores them in a 2D byte array (either initialized from reading in the file or filled out based on received pieces)
    * Sends the payload
    * Once a piece message is received, the peer downloads the piece from the input buffer and stores it in the previously mentioned 2D array based on the piece index that is parsed from the payload
    * To avoid repeated request messages, a boolean value is stored for each client that stores whether the corresponding peer has requested an unsent piece or not, which is set to false once the piece message is received by the peer or a choke message is received by the peer.
    * Once the request flag has been set to false, new request message to that peer can be made if applicable
  * Choke and unchoke
    * Since the number of simultaneous connections that are transmitting piece data is limited, each peer keeps a list of k most preferred neighbors, and one optimistically unchoked neighbor
    * The preferred neighbors are updated every <unchoking interval> seconds
      * If the peer has the complete file already, it randomly selects at most k peers that do not have the complete file
      * If the peer does not have the complete file yet, it chooses the k peers with the highest current download rate
        * Every time a piece is received, the download rate will increment up (so download rate is equal to number of received pieces since the last neighbor selection)
        * Once new preferred neighbors have been selected, the download rate will be reset to 0
      * Each peer has a list of which peers have it choked or unchoked, as well as a list of which peers it has choked or unchoked
        * If one of the preferred neighbors is both interested in the current peer's data and was also choked by the current peer, it updates the list to unchoke that peer and sends an unchoke message
        * If the optimistically unchoked neighbor is currently choked, update the list to be unchoked and send an unchoke message
        * If a peer is not in the list of preferred neighbors nor the optimistically unchoked neighbor and is currently unchoked, it updates the list to choke that peer and sends a choke messages
        * A variable stores the previous status of choked/unchoked peers, so once a message is sent it updates that variable to the state before the change
      * Upon receiving a choke message, the peer's list of neighbors that have it choked is updated to include the message sender
      * Upon receiving an unchoke message, the peer's list of neighbors that have it unchoked is updated to include the message sender
        * When the state is updated to unchoked, a request message is sent to the corresponding peer
  * Interested and not interested
    * Each peer stores a list of peers that are interested in it
    * After receiving an interested or not interested message, it will check if the peer is in the list or not
      * If a newly interested peer is not in the list, it will be added to the list
      * If a newly not interested peer is in the list, it will be removed from the list
    * Whenever a piece is received, the peer will check all of the peers it is interested in to see whether it should still be interested in them
      * If the remote peer no longer has any pieces that the current peer does not have, it will send a not interested message
  * Have
    * Each peer has a list of which piece indexes it has, as well as which pieces each remote peer has
      * When receiving a have message, the index is added to the piece index list for the remote peer
      * If the current peer does not contain that piece, and the current peer is not interested in the remote peer, it will update the list to be interested in that peer and sends an interested message
    * After receiving a have message, the current peer process checks whether the process should be terminated (i.e., all peers have the complete file)

* Classes/file
  * ActualMessage.java: stores the length, type, and payload of a message
    * Has an enumerator for message type
    * Has a function that concatenates the type and payload, then calculates the message length and appends it to the beginning to create the final byte[] message that will be sent
  * Client.java: stores the connections between peers
    * Has the socket and input/output streams to send messages between
    * Stores the information that is available to the peer that the client corresponds to
    * Each peer process keeps a vector of Clients, with one client for each peer that it is connected to
    * Has a connect() function that is passed a socket which is called when the connection is first established
      * Sets the socket value and the corresponding input and output buffers
      * Sends a handshake message to the remote peer that the client connects to
    * Has a sendMessage() function that takes an ActualMessage object and writes the message that it creates to the output stream
    * Has a readMessage() function that reads the bytes from the input buffer
      * First reads 4 bytes to identify message length (or if the message is a handshake), then reads in the remaining bytes by length
      * Processes the message type and performs the corresponding operation with the payload
      * If it is a handshake message, it directly performs the handshake operation, otherwise it creates ActualMessage objects to pass to MessageHandler functions
    * Runs a timer to constantly read in from the buffer
    * For any individual read that we tested this process worked fine, but during the back-and-forth sending of messages it breaks somewhere along the line
      * We tested different methods of when to call the read function (calling it when it is expected based on the last received message and calling it on a timer)
        * Neither option solved the problem, behavior was slightly different with each but the same overall error was occuring
        * Unsure if this is an issue with the readMessage() function or something about the MessageHandler functions that sends messages that are not able to be processed correctly
          * We performed extensive bug testing on both the readMessage() function and the MessageHandler functions to attempt to identify where the problems were occurring. We fixed several errors, but not enough to make the problem go away entirely
  * CommonUtil.java: container for the values read in from Common.cfg
  * FileHandler.java: Keeps track of the pieces that a peer has
    * Reads from the original file and also creates byte[] arrays to be sent by the client
    * On being initialized, it creates the subdirectory for each peer (if such a subdirectory does not already exist), then calculates the maximum number of pieces to divide the 2D byte array into pieces
    * For the peers that start with the full file, this has a function readFromFile() that reads the file into a 2D array (byte arrays for each piece, then an array of those pieces)
    * When the process finishes, the saveToSubDirectory() function writes the file to the subdirectory using the received pieces
  * HandshakeMessaage.java: is a basic container for the parts of the handshake message
  * Logger.java: has functions to generate the logs for each operation
    * Has functions to generate the string for the log with the appropriate timestamp
    * The logger object keeps track of a list of the log strings and prints each log to the terminal for testing/demonstration purposes
    * Has a function that writes the list of logs to a text file on process termination
    * Functions are called in all files at the times where logs are supposed to be written
  * MessageHandler.java: contains handler functions for sending and receiving different message types
    * UnchokedPeers.run() - A function that is called every <unchoked interval from configuration file> seconds that updates the preferred neighbors to be choked or unchoked
    * OptUnchokedPeers.run() - A function that is called every <optimistic unchoked interval from configuration file> seconds that updates the optimistically unchoked neighbor
    * startUnchoking() and startOptUnchoking() - Functions that start the timers to call the associated previous functions
    * receive<MessageType>Msg() - Functions that process the payload and update peer information upon receiving each message type
    * send<MessageType>Msg() - Functions that process the associated information for each message type and send using the given client
  * PeerInfoLocal.cfg: a testing cfg file that uses localhost. This was supposed to not be used by the submitted code, but due to submitting the project under time constraints this was mistakenly used. The line of code that references it is line 83 of peerProcess.java, which should have been commented out and been replaced by uncommenting line 80 of peerProcess.java to make it use PeerInfo.cfg. The code was tested on the CISE servers and functioned the exact same, but it was swapped back to localhost while trying to solve the message read issue and did not get switched back before submission
  * peerProcess.java: main process that connects all of the components
    * Stores vectors of information about remote peers (RemotePeerInfo objects, sockets, client objects, input and output streams) as well as information about itself (maximum number of pieces, what pieces it has, interested peers etc.) as described in previous classes
    * Has a function getConfiguration() that reads in from the peerInfo.cfg file (or in the case of the mistaken submission due to the wrong line being commented out, peerInfoLocal.cfg)
    * Has a function getCommon() that reads in from Common.cfg
    * Main function:
      * Initializes and stores values about itself and the remote peers from the configuration files
      * Initializes a vector of Client objects (before making the connection with a socket) for each remote peer
      * Generates and sends handshake messages to each remote peer that occurs prior to the current peer in the configuration file
      * Starts a Server instance that listens for new connections repeatedly
      * Has a function that checks whether the process should terminate or not, and writes both the file (from the pieces) and the log file to the associated directory
  * RemotePeerInfo.java: container for information about remote peers {provided by website}
  * Server.java: handles initial connections between peers
    * Stores information from the connection and peerProcess that are necessary to create the clients
    * Has startListening() function that creates a ServerSocket with associated input/output streams to read handshakes from remote peers that have not yet connected to the associated peer
    * Has keepListening() function that accepts a socket connection
      * Calls the Connect() function with the socket for the appropriate client based on the peerID of the handshake sender
      * Sends the bitfield message that needs to be sent after that handshake
      * Is called in a while(!finished) loop in peerProcess so that the server waits for an external client to connect, then once it does it pairs it with the current client so that they share input and output streams
  * Utils.java: has functions to convert between data types
    * Converts an integer to a byte array
    * Converts a byte array to an integer
    * Converts a piece set to a byte array
    * Converts a byte array to a piece set

  * Unnecessary files: again due to submitting the project under time constraints since we tried to keep fixing/improving the program for as long as we could, several files (.class files, some testing files, etc.) were mistakenly not removed. The necessary files that would have been included have been discussed in this document (except for makefile and startRemotePeers.java). run ```make clean``` to remove the compiled .class files if necessary.


* Team Member Contributions
  * All team members contributed to overall design and protocol integration in group meetings
  * All team members contributed to debugging (predominantly the message errors that resulted in the project not being fully working) in group meetings
  * All team members edited files that they did not primarily write (sometimes extensively, particularly for MessageHandler.java, peerProcess.java, and remotePeerInfo.java)
  * All team members participated in getting the individual implementation for all of the files put together
  * Each team member performed the original implementation and primary writing of the following message types/files (Note: This does not mean that those message types/files were entirely written by only the associated team member in this list, just that the initial code was written by those members and were then edited based on differing functionality requirements when integrating with other files or bug fixes):
    * Donald Honeycutt:
      * HANDSHAKE
      * Client.java
      * peerProcess.java
      * Server.java

    * Yu-Peng Chen:
      * PIECE
      * REQUEST (partial)
      * ActualMessage.java
      * CommonUtil.java
      * Utils.java (partial)
      * FileHandler.java
      * Logger.java

    * Futing Shan:
      * BITFIELD
      * REQUEST (partial)
      * CHOKE/UNCHOKE
      * INTERESTED/NOT INTERESTED
      * HAVE
      * MessageHandler.java
