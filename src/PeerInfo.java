package src;

public class PeerInfo {
    private int peerID;
    // IP address of the peer
    private String hostName;
    private int listeningPort;
    private int hasFileOrNot;

    public PeerInfo() {

    }

    public PeerInfo(int peerID, String hostName, int port, int hasFileOrNot) {
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
