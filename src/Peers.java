import java.net.*;
import java.io.*;
import java.util.*;

public class Peers {
    private static CommonUtil comUtil;
    private static List<RemotePeerInfo> unchokedPeers = new ArrayList<>();

    public Peers(CommonUtil comUtil){
        Peers.comUtil = comUtil;
    }

    public static class UnchokedPeers extends TimerTask{
        @Override
        public void run(){
            try{
                //TODO
            }catch(Exception e){
                System.out.println(e);
            }
        }
        
    }

    public static class OptUnchokedPeers extends TimerTask{
        @Override
        public void run(){
            try{
                //TODO
            }catch(Exception e){
                System.out.println(e);
            }
        }
        
    }

    public void startUnchoking(){
        Timer timer = new Timer();
        int unchockingInterval = comUtil.getUnchockingInterval()*1000;
        timer.scheduleAtFixedRate(new UnchokedPeers(), 0, unchockingInterval);
    }

    public void startOptUnchoking(){
        Timer timer = new Timer();
        int optUnchockingInterval = comUtil.getOptUnchockingInterval()*1000;
        timer.scheduleAtFixedRate(new OptUnchokedPeers(), 0, optUnchockingInterval);
    }

    public static void receiveInterestedMsg(){
        //TODO
    }

    public static void receiveNotInterestedMsg(){
        //TODO
    }

    private static void sendUnchokeMsg(){
        //TODO
    }

}