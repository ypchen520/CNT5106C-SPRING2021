import java.io.File;

public class FileHandler {

    private int peerID;

    public FileHandler(int peerID){
        this.peerID = peerID;
    }

    private void createSubDirectory(){
        try{
            String dirName = "peer_" + String.valueOf(peerID);
            File file = new File(dirName);
            file.mkdir();
        }
        catch (Exception e){
            System.out.println("[FileHandler] " + e);
        }
    }

    private int getNumPieces(){
        int numPieces = 0;
        // TODO
        return numPieces;
    }
    
    public void saveToSubDirectory(){
        // TODO
    }

    public String getFilePath(){
        String piecePath = "";
        // TODO
        return piecePath;
    }

    public void divideIntoPieces(){
        // TODO
    }

}