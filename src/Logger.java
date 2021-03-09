import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

public class Logger {
    private int peerId;
    private List<String> log = new ArrayList<>();
    private String filePrefix = "log_peer_";
    private String fileExtenstion = ".log";

    public Logger(int peerId){
        this.peerId = peerId;
    }

    private String formatDate(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss a zzz");
        return dateFormat.format(date);
    }

    private void writeToLog(String content) throws IOException{
        log.add(content);
    }

    private void writeToFile() throws IOException{
        FileWriter fileWriter = new FileWriter(filePrefix + peerId + fileExtenstion);
        // PrintWriter printWriter = new PrintWriter(fileWriter);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        PrintWriter printWriter = new PrintWriter(bufferedWriter);
        for(String s : log){
            // System.out.println(s);
            printWriter.println(s);
        }
        printWriter.close();
    }

    public void logTcpConnection(int peerId2, String tcpType) throws IOException{
        
        switch (tcpType) {
        case "to":
            writeToLog(formatDate(new Date()) + "Peer " + peerId + " makes a connection to Peer " + peerId2 + ".");
            break;
        case "from":
            writeToLog(formatDate(new Date()) + "Peer " + peerId + " is connected from Peer " + peerId2 + ".");
            break;
        }
    }
    
    
}