import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

public class Logger {
    private static int peerID;
    private static List<String> log = new ArrayList<>();
    private String filePrefix = "log_peer_";
    private String fileExtenstion = ".log";

    public Logger(int peerID){
        Logger.peerID = peerID;
    }

    public List<String> getLog() {
      return log;
    }

    private static String formatDate(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss a zzz");
        return dateFormat.format(date) + ": ";
    }

    private static void writeToLog(String content) throws IOException{
        log.add(content);
        System.out.println(content);
    }

    public void writeToFile() throws IOException{
        String filename = filePrefix + peerID + fileExtenstion;
        FileWriter fileWriter = new FileWriter(filename, true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        PrintWriter printWriter = new PrintWriter(bufferedWriter);
        for(String s : log){
            // System.out.println(s);
            printWriter.println(s);
        }
        printWriter.close();
    }

    public void logTcpConnection(int peerID2, String messageType) throws IOException{
        switch (messageType) {
            case "to":
                writeToLog(formatDate(new Date()) + "Peer " + peerID + " makes a connection to Peer " + peerID2 + ".");
                break;
            case "from":
                writeToLog(formatDate(new Date()) + "Peer " + peerID + " is connected from Peer " + peerID2 + ".");
                break;
            default:
                System.out.println("[logTcpConnection] Unknown message type");
        }
    }

    public void logPreferredNeighborsChange(ArrayList<Integer> preferredNeighbors) throws IOException{
        String preferredNeighborIDs = "";
        int n = preferredNeighbors.size();
        for(int i = 0; i < n; i++){
            // preferredNeighborIDs += String.valueOf(preferredNeighbors.get(i).getPeerID());
            preferredNeighborIDs += String.valueOf(preferredNeighbors.get(i));
            if(i != n-1)
                preferredNeighborIDs += ", ";
        }
        writeToLog(formatDate(new Date()) + "Peer " + peerID + " has the preferred neighbors " + preferredNeighborIDs + ".");
    }

    public void logTitForTat(int peerID2, String messageType) throws IOException{
        switch (messageType) {
            case "optUnchoke":
                writeToLog(formatDate(new Date()) + "Peer " + peerID + " has the optimistically unchoked neighbor " + peerID2 + ".");
                break;
            case "unchoke":
                writeToLog(formatDate(new Date()) + "Peer " + peerID + " is unchoked by " + peerID2 + ".");
                break;
            case "choke":
                writeToLog(formatDate(new Date()) + "Peer " + peerID + " is choked by " + peerID2 + ".");
                break;
            default:
                System.out.println("[logTitForTat] Unknown message type");
        }
    }

    public void logReceivingMessages(int peerID2, String messageType) throws IOException{
        final String[] setValues = new String[] { "have", "interested", "not interested" };
        final Set<String> msgTypeSet = new HashSet<>(Arrays.asList(setValues));
        if(!msgTypeSet.contains(messageType)){
            System.out.println("[logReceivingMessages] Unknown message type");
        }else{
            writeToLog(formatDate(new Date()) + "Peer " + peerID + " received the '" + messageType + "' message from " + peerID2 + ".");
        }
    }

    public void logDownloadingPiece(int peerID2, int pieceIndex, int numPieces) throws IOException{
        writeToLog(formatDate(new Date()) + "Peer " + peerID + " has downloaded the piece " + pieceIndex + " from " + peerID2 + "." +
                   " Now the number of pieces it has is " + numPieces + ".");
    }

    public void logCompleteDownloading() throws IOException{
        writeToLog(formatDate(new Date()) + "Peer " + peerID + " has downloaded the complete file.");
    }

    public void logOptimisticallyUnchokedNeighborChange(int peerID2) throws IOException{
        writeToLog(formatDate(new Date()) + "Peer " + peerID + " has the optimistically unchoked neighbor " + peerID2 + ".");
    }
}
