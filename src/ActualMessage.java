// package src;

public class ActualMessage {
    private final int messageLength;
    private final byte messageType;
    private final byte[] messagePayload;

    public static final byte TYPE_CHOKE = 0;
    public static final byte TYPE_UNCHOKE = 1;
    public static final byte TYPE_INTERESTED = 2;
    public static final byte TYPE_NOT_INTERESTED = 3;
    public static final byte TYPE_HAVE = 4;
    public static final byte TYPE_BITFIELD = 5;
    public static final byte TYPE_REQUEST = 6;
    public static final byte TYPE_PIECE = 7;

    public ActualMessage(int length,byte type, byte[] payload) {
        this.messageLength=length;
        this.messageType = type;
        this.messagePayload = payload;
    }

    public int getMessageLength() {
        return messageLength;
    }

    public int getMessageType() {
        return messageType;
    }

    public byte[] getPayload() {
        return messagePayload;
    }
}
