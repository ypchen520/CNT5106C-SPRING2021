//package src;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class MessageHandler {

    //TODO test this module
    //judge if the message is handshake message

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

    }

    public void UNCHOKEReceived()
    {
        //TODO:need to be implement

    }

    public void BITFIELDReceived()
    {
        //TODO:need to be implement

    }

    public void INTERESTEDReceived()
    {
        //TODO:need to be implement

    }

    public void NOTINTERESTEDReceived()
    {
        //TODO:need to be implement

    }

    public void HAVEReceived()
    {
        //TODO:need to be implement

    }

    public void REQUESTReceived()
    {
        //TODO:need to be implement

    }

    public void PIECEReceived()
    {
        //TODO:need to be implement

    }
}
