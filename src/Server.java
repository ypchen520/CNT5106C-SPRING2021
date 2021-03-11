import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Server {
  int listeningPort;
  ServerSocket listener;

  // Constructor
  public void Server() {

  }

  // Get function for port number
  public int getListeningPort() {
    return listeningPort;
  }

  // Set function for port number
  public void setListeningPort(int portNumber) {
    listeningPort = portNumber;
  }

  // Start listening to the port number
  public void startListening() {
    try {
      // TODO: Data logging

      // Call constructor
      listener = new ServerSocket(listeningPort);

      // Start listening for multiple clients via infinite loop
      while (true) {
        try {
          // Receive incoming client request
          Socket socket = listener.accept();

          // Obtain the I/O streams

          // Close the socket once finished
          socket.close();
        }
        catch (Exception e) {
          // TODO: exception handling

        }
      }
    }
    catch (Exception e) {
      //TODO: exception handling

    }
  }

}
