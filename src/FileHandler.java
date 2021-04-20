import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class FileHandler {

    private int peerID;
    private String fileName;
    private int fileSize;
    private int pieceSize;
    private int maxPieces;


    public FileHandler(int peerID, String fileName, int fileSize, int pieceSize){
        this.peerID = peerID;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.pieceSize = pieceSize;
        getNumPieces();
    }

    private void createSubDirectory(){
        try{
            String dirName = "peer_" + String.valueOf(peerID);
            File dir = new File(dirName);
            if(!dir.exists()){
                dir.mkdir();
            }
        }
        catch (Exception e){
            System.out.println("[FileHandler] " + e);
        }
    }

    private int getNumPieces(){
        int numPieces = 0;
        this.maxPieces = (int)Math.ceil(fileSize/pieceSize);
        return numPieces;
    }
    
    public void saveToSubDirectory(){
        FileWriter fileWriter = new FileWriter(fileName, true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        PrintWriter printWriter = new PrintWriter(bufferedWriter);
        for(String s : log){
            // System.out.println(s);
            printWriter.println(s);
        }
        printWriter.close();
    }

    public String getFilePath(){
        String piecePath = "";
        // TODO
        return piecePath;
    }

    public int divideIntoPieces(){
        // TODO

        return 
    }

}