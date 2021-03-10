import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Server {
  int listeningPort;
  ServerSocket listener;

  public int getListeningPort() {
    return listeningPort;
  }

  public void setListeningPort(int portNumber) {
    listeningPort = portNumber;
  }

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
          socket.close();
        }
        catch (Exception e) {

        }
      }
    }
    catch (Exception e) {

    }
  }

}
