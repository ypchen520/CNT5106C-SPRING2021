package src;

public class HandshakeMessage {
    private final String HANDSHAKE_HEADER = "P2PFILESHARINGPROJ";
    private final String ZERO_BITS = "0000000000";
    private final int peerID;

    public HandshakeMessage(int peerId) {
        this.peerID = peerId;
    }

    public int getPeerId(){
        return this.peerID;
    }
}
