public class CommonUtil {
    private int numNeighbors;
    private int unchockingInterval;
    private int optUnchockingInterval;
    private String fileName;
    private int fileSize;
    private int pieceSize;


    public CommonUtil(int numNeighbors, int unchockingInterval, int optUnchockingInterval, String fileName, int fileSize, int pieceSize){
        this.numNeighbors = numNeighbors;
        this.unchockingInterval = unchockingInterval;
        this.optUnchockingInterval = optUnchockingInterval;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.pieceSize = pieceSize;
    }

    public int getNumNeighbors(){
        return numNeighbors;
    }

    public int getOptUnchockingInterval(){
        return optUnchockingInterval;
    }
    
    public int getUnchockingInterval(){
        return unchockingInterval;
    }

    public String getFileName(){
        return fileName;
    }

    public int getfileSize(){
        return fileSize;
    }

    public int getpieceSize(){
        return pieceSize;
    }
}