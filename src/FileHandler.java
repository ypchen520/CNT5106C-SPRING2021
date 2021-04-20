import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class FileHandler {

    private int peerID;
    private String fileName;
    private int fileSize;
    private int pieceSize;
    private int maxPieces;
    private byte[][] thisData;
    private int hasOriginalFile;
    private String subDirName;


    public FileHandler(int peerID, String fileName, int fileSize, int pieceSize, int hasFile){
        this.peerID = peerID;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.pieceSize = pieceSize;
        this.hasOriginalFile = hasFile;
        this.subDirName = "peer_" + String.valueOf(peerID);
        if(hasFile == 0) createSubDirectory();
        calculateMaxPieces();
        divideIntoPieces();
    }

    private void createSubDirectory(){
        try{
            File dir = new File(subDirName);
            if(!dir.exists()){
                dir.mkdir();
            }
        }
        catch (Exception e){
            System.out.println("[FileHandler] " + e);
        }
    }

    private void calculateMaxPieces(){
        // int numPieces = 0;
        this.maxPieces = (int)Math.ceil(fileSize*1.0f/pieceSize);
        // return numPieces;
    }

    public int getMaxPieces(){
        return maxPieces;
    }

    public byte[] getData(int index){
        return thisData[index];
    }

    public void divideIntoPieces(){
        thisData = new byte[maxPieces][];
    }

    public void readFromFile(){
        try{
            // String dirName = "peer_" + String.valueOf(peerID);
            String filePath = subDirName+fileName;
            File f = new File(filePath);
            if(!f.exists() && hasOriginalFile == 1){
                byte[] data = Files.readAllBytes(Paths.get(filePath));
                for(int i = 0; i < maxPieces; i++){
                    if(i != maxPieces-1){
                        thisData[i] = Arrays.copyOfRange(data, pieceSize*i, pieceSize*(i+1));
                    }
                    else{
                        thisData[i] = Arrays.copyOfRange(data, pieceSize*i, maxPieces);
                    }
                }
            }
        }
        catch (Exception e){
            System.out.println("[FileHandler] " + e);
        }
    }

    public void downloadPiece(byte[] piece, int pieceIndex){
        thisData[pieceIndex] = piece.clone();
    }
    
    public void saveToSubDirectory(){
        try {
            FileOutputStream stream = new FileOutputStream(subDirName+fileName, true);
            for(int i = 0; i < maxPieces; i++){
                stream.write(thisData[i]);
            }
            stream.close();
        }
        catch (Exception e){
            System.out.println("[FileHandler] " + e);
        }
    }

}