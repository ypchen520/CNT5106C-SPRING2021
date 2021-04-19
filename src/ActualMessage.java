import java.io.ByteArrayOutputStream;

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

    public ActualMessage(int length, byte type, byte[] payload) {
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

    public byte [] createPieceMessage(byte[] data, String pieceIndex) throws Exception {
        // Piece messages have a payload which consists of a 4-byte piece index field and the content of the piece.
        ByteArrayOutputStream msg = new ByteArrayOutputStream();
        ByteArrayOutputStream msgWithLen = new ByteArrayOutputStream();
        msg.write(TYPE_PIECE);
        // Payload
        msg.write(pieceIndex.getBytes());
        msg.write(data);
        byte[] msgLen = intToBytes(msg.toByteArray().length);
        msgWithLen.write(msgLen);
        msgWithLen.write(msg.toByteArray());
        
        return msgWithLen.toByteArray();
    }

    private byte[] intToBytes(final int data){
        return new byte[] {
            (byte)((data >> 24) & 0xff),
            (byte)((data >> 16) & 0xff),
            (byte)((data >> 8) & 0xff),
            (byte)((data >> 0) & 0xff),
        };
    }
}
