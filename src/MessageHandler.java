//package src;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class MessageHandler {

    //TODO test this module
    //judge if the message is handshake message

    // Define message types
    public static final byte TYPE_CHOKE = 0;
    public static final byte TYPE_UNCHOKE = 1;
    public static final byte TYPE_INTERESTED = 2;
    public static final byte TYPE_NOT_INTERESTED = 3;
    public static final byte TYPE_HAVE = 4;
    public static final byte TYPE_BITFIELD = 5;
    public static final byte TYPE_REQUEST = 6;
    public static final byte TYPE_PIECE = 7;


    public void onReceivePayload(ActualMessage actualMessage) {
        switch(actualMessage.getMessageType()) {

            case TYPE_CHOKE:
                CHOKEReceived();
                break;
            case TYPE_UNCHOKE:
                UNCHOKEReceived();
                break;
            case TYPE_INTERESTED:
                INTERESTEDReceived();
                break;
            case TYPE_NOT_INTERESTED:
                NOTINTERESTEDReceived();
                break;
            case TYPE_HAVE:
                HAVEReceived(actualMessage.getPayload());
                break;
            case TYPE_BITFIELD:
                BITFIELDReceived(actualMessage.getPayload());
                break;
            case TYPE_REQUEST:
                REQUESTReceived(actualMessage.getPayload());
                break;
            case TYPE_PIECE:
                PIECEReceived(actualMessage.getPayload());
                break;
            default:
                System.out.println("Wrong type");
        }
    }

    public String getHandshakeHeader(){
        return "P2PFILESHARINGPROJ";
    }

    public String getZeroBits(){
        return "0000000000";
    }

    public int getPeerId(byte[] bytes){
        return ByteBuffer.wrap(bytes).getInt();
    }

    public int getMessageLength(byte[] bytes){
        return ByteBuffer.wrap(bytes).getInt();
    }

    public int getMessageType(byte[] bytes){
        return ByteBuffer.wrap(bytes).getInt();
    }

    public byte[] getMessagePayload(byte[] bytes){
        return bytes;
    }

    public void CHOKEReceived()
    {
        //TODO:need to be implement
        return;
    }

    public void UNCHOKEReceived()
    {
        //TODO:need to be implement
        return;
    }

    public void BITFIELDReceived(byte[] payload)
    {
        //TODO:need to be implement
        return;
    }

    public void INTERESTEDReceived()
    {
        //TODO:need to be implement
        return;
    }

    public void NOTINTERESTEDReceived()
    {
        //TODO:need to be implement
        return;
    }

    public void HAVEReceived(byte[] payload)
    {
        //TODO:need to be implement
        return;
    }

    public void REQUESTReceived(byte[] payload)
    {
        //TODO:need to be implement
        return;
    }

    public void PIECEReceived(byte[] payload)
    {
        //TODO:need to be implement
        return;
    }
}
