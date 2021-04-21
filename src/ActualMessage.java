import java.io.ByteArrayOutputStream;

// package src;

public class ActualMessage {

    public enum MessageType
    {
        CHOKE((byte)0),
        UNCHOKE((byte)1),
        INTERESTED((byte)2),
        NOT_INTERESTED((byte)3),
        HAVE((byte)4),
        BITFIELD((byte)5),
        REQUEST((byte)6),
        PIECE((byte)7);

        private final byte type;

        public byte getType()
        {
            return this.type;
        }

        private MessageType(byte type)
        {
            this.type = type;
        }
    }

    private int messageLength; // message length not including the length of the length field itself
    private MessageType messageType;
    private byte[] messagePayload; // payload given when the object is created.
    private byte[] messageWithoutLen; // message not including the length field

    // public static final byte TYPE_CHOKE = 0;
    // public static final byte TYPE_UNCHOKE = 1;
    // public static final byte TYPE_INTERESTED = 2;
    // public static final byte TYPE_NOT_INTERESTED = 3;
    // public static final byte TYPE_HAVE = 4;
    // public static final byte TYPE_BITFIELD = 5;
    // public static final byte TYPE_REQUEST = 6;
    // public static final byte TYPE_PIECE = 7;

    public ActualMessage() {
    }

    public ActualMessage(MessageType type, byte[] payload) {
        // this.messageLength=length;
        this.messageType = type;
        this.messagePayload = payload;
    }

    public void setMessageLength(int length){
        this.messageLength = length;
    }

    public int getMessageLength() {
        return messageLength;
    }

    public void setMessageType(MessageType type){
        this.messageType = type;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setPayload(byte[] payload){
        this.messagePayload = payload;
    }

    public byte[] getPayload() {
        return messagePayload;
    }

    public byte [] createMessage() throws Exception {
        // Piece messages have a payload which consists of a 4-byte piece index field and the content of the piece.

        ByteArrayOutputStream msgWithLen = new ByteArrayOutputStream();
        calculateLength();
        System.out.println("MessageLength = " + this.messageLength);
        byte[] msgLen = new byte[4];
        msgLen = Utils.convertIntToByteArray(this.messageLength);
        msgWithLen.write(msgLen);
        msgWithLen.write(messageWithoutLen);
        return msgWithLen.toByteArray();
    }

    // public byte [] createPieceMessage(String pieceIndex) throws Exception {
    //     // Piece messages have a payload which consists of a 4-byte piece index field and the content of the piece.
    //     ByteArrayOutputStream msgWithLen = new ByteArrayOutputStream();
    //     calculateLength(pieceIndex.getBytes());
    //     byte[] msgLen = intToBytes(this.messageLength);
    //     msgWithLen.write(msgLen);
    //     msgWithLen.write(messageWithoutLen);
    //     return msgWithLen.toByteArray();
    // }

    // public byte [] createRequestMessage(){

    // }

    private void calculateLength() throws Exception {
        ByteArrayOutputStream msg = new ByteArrayOutputStream();
        msg.write(messageType.getType());
        // msg.write(payload);
        if (messagePayload != null) {
          msg.write(messagePayload);
        }
        this.messageWithoutLen = msg.toByteArray();
        this.messageLength = messageWithoutLen.length;
    }

    // private byte[] intToBytes(final int data){
    //     return new byte[] {
    //         (byte)((data >> 24) & 0xff),
    //         (byte)((data >> 16) & 0xff),
    //         (byte)((data >> 8) & 0xff),
    //         (byte)((data >> 0) & 0xff),
    //     };
    // }
}
