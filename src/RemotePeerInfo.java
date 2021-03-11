// package src;
/*
 *                     CEN5501C Project2
 * This is the program starting remote processes.
 * This program was only tested on CISE SunOS environment.
 * If you use another environment, for example, linux environment in CISE
 * or other environments not in CISE, it is not guaranteed to work properly.
 * It is your responsibility to adapt this program to your running environment.
 */


public class RemotePeerInfo {
    private int peerID;
    // IP address of the peer
    private String hostName;
    private int listeningPort;
    private int hasFileOrNot;

    public RemotePeerInfo() {

    }

    // Constructor
    public RemotePeerInfo(int peerID, String hostName, int port, int hasFileOrNot) {
        this.peerID = peerID;
        this.hostName = hostName;
        this.listeningPort = port;
        this.hasFileOrNot = hasFileOrNot;
    }

    public int getPeerID() {
        return peerID;
    }

    public String getHostName() {
        return hostName;
    }

    public int getListeningPort() {
        return listeningPort;
    }

    public int getHasFileOrNot() {
        return hasFileOrNot;
    }

    public void setPeerID(int peerID) {
        this.peerID = peerID;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setListeningPort(int listeningPort) {
        this.listeningPort = listeningPort;
    }

    public void setHasFileOrNot(int hasFileOrNot) {
        this.hasFileOrNot = hasFileOrNot;
    }
}
