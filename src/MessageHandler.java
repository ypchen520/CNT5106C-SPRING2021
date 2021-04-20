// TODO: rename to MessageHandler (filename, classname, and constructor) {Futing}

// TODO: Implement Bitfield, Request, and Piece

// TODO: sender functions

// TODO: pass in Vector<client>

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.io.*;
import java.util.*;

import javax.print.attribute.HashAttributeSet;

import sun.misc.Cleaner;

public class MessageHandler {
	private static int peerID;
    private static CommonUtil comUtil;
	private static List<RemotePeerInfo> unchokedPeers = new ArrayList<>();
    private Logger logger;

	public MessageHandler(CommonUtil comUtil, int peerID) {
		MessageHandler.comUtil = comUtil;
        MessageHandler.peerID = peerID;
        this.logger = new Logger(peerID);
	}

	// An arraylist stores the preferred neighbors
	public static ArrayList<Integer> preferredNeighborList = new ArrayList<Integer>();
	public static int optimisticUnchokeNeighbor = -1;

	public static class UnchokedPeers extends TimerTask {
		@Override
		public void run() {
			synchronized (preferredNeighborList) {
				try {
					// reset the neighbors
					preferredNeighborList.clear();

					ArrayList<RemotePeerInfo> downloadingRateList = new ArrayList<>();
					// waiting for peer process add the global var IDIndex
					if (peerProcess.peerInfoVector.get(peerProcess.indexID).hascompletefile()) {

						// add all the interested peers to the rate list
						for (RemotePeerInfo tempPeer : peerProcess.getInterestedPeers()) {
							downloadingRateList.add(tempPeer);
						}

						// sort the downloadingRateList according to the downloading rate from high to
						// low
						downloadingRateList.sort(new Comparator<RemotePeerInfo>() {
							@Override
							public int compare(RemotePeerInfo remotePeerInfo, RemotePeerInfo t1) {
								return t1.downloadingRatePiece - remotePeerInfo.downloadingRatePiece;
							}
						});

						for (RemotePeerInfo tempPeer : downloadingRateList) {
							if (preferredNeighborList.size() < comUtil.getNumNeighbors()) {
								preferredNeighborList.add(tempPeer.getPeerID());
							}
						}

						// reset the downloading rate to 0;
						// request peerProcess add getPeerInfoVector
						for (RemotePeerInfo tempPeer : peerProcess.peerInfoVector) {
							tempPeer.resetDownloadingRatePiece();
						}

					} else {
						// get interested Peers from peerprocess it change when get bitfield and
						// have messages;
						ArrayList<RemotePeerInfo> interestedPeers = peerProcess.getInterestedPeers();
						Collections.shuffle(interestedPeers);
						for (RemotePeerInfo tempPeer : interestedPeers) {
							if (preferredNeighborList.size() < comUtil.getNumNeighbors()) {
								preferredNeighborList.add(tempPeer.getPeerID());
							}
						}
					}

					//TODO logger change prefer neighbors
					ArrayList<RemotePeerInfo> logPreferredNeighborList = new ArrayList<>();
					// Logger.logPreferredNeighborsChange(preferredNeighborList);

					Map<Integer, Client> clientMap = new HashMap<>();
					for(Client client:peerProcess.clients) {
						clientMap.put(client.getServerID(), client);
					}
					for (RemotePeerInfo tempPeer : peerProcess.peerInfoVector) {
						// if prefer and choke, send unchoke
						if (preferredNeighborList.contains(tempPeer.getPeerID()) && tempPeer.choke) {
							// TODO send unchoke message
							Client client = clientMap.get(tempPeer.getPeerID());
							tempPeer.choke = false;
						} else if (!preferredNeighborList.contains(tempPeer.getPeerID()) && !tempPeer.choke
								&& optimisticUnchokeNeighbor != tempPeer.getPeerID()) {
							// if not prefer, not choke, not opt, send choke
							// TODO send choke message
							tempPeer.choke = true;
						}
					}

				} catch (Exception e) {
					System.out.println(e);
				}
			}
		}
	}

	public static class OptUnchokedPeers extends TimerTask {
		@Override
		public void run() {
			synchronized (preferredNeighborList) {
				try {

					ArrayList<Integer> chockedPeerList = new ArrayList<>();
					Set<Integer> interestedSet = new HashSet<>();
					for (RemotePeerInfo tempPeer : peerProcess.getInterestedPeers()) {
						interestedSet.add(tempPeer.getPeerID());
					}
					for (RemotePeerInfo tempPeer : peerProcess.peerInfoVector) {

						if (interestedSet.contains(tempPeer.getPeerID()) && tempPeer.choke) {
							chockedPeerList.add(tempPeer.index);
						}
					}

					if (!chockedPeerList.isEmpty()) {
						int index = (int) (Math.random() * chockedPeerList.size());
						optimisticUnchokeNeighbor = peerProcess.peerInfoVector.get(chockedPeerList.get(index))
								.getPeerID();
						// TODO:sendmessage

						// LOGGER
						new Logger(peerProcess.peerInfoVector.get(peerProcess.indexID).getPeerID())
								.logOptimisticallyUnchokedNeighborChange(optimisticUnchokeNeighbor);
					}

				} catch (Exception e) {
					System.out.println(e);
				}
			}
		}

	}

	public void startUnchoking() {
		Timer timer = new Timer();
		int unchockingInterval = comUtil.getUnchockingInterval() * 1000;
		timer.scheduleAtFixedRate(new UnchokedPeers(), 0, unchockingInterval);
	}

	public void startOptUnchoking() {
		Timer timer = new Timer();
		int optUnchockingInterval = comUtil.getOptUnchockingInterval() * 1000;
		timer.scheduleAtFixedRate(new OptUnchokedPeers(), 0, optUnchockingInterval);
	}

	// receive Msg from Id
	public static void receiveInterestedMsg(ActualMessage m, Client client) {
		try {
			new Logger(peerProcess.peerInfoVector.get(peerProcess.indexID).getPeerID()).logReceivingMessages(client.serverID,
					"interested");
		} catch (IOException e) {
			e.printStackTrace();
		}
		Set<Integer> interestedSet = new HashSet<>();
		ArrayList<RemotePeerInfo> interestedPeers = peerProcess.getInterestedPeers();
		for (RemotePeerInfo tempPeer : peerProcess.getInterestedPeers()) {
			interestedSet.add(tempPeer.getPeerID());
		}

		if (!interestedSet.contains(client.serverID)) {
			RemotePeerInfo containedPeer = new RemotePeerInfo();
			for (RemotePeerInfo tempPeer : peerProcess.peerInfoVector) {
				if (tempPeer.getPeerID() == client.serverID) {
					containedPeer = tempPeer;
					break;
				}
			}
			interestedPeers.add(containedPeer);
		}
	}

	public static void receiveNotInterestedMsg(ActualMessage m, Client client) {
		try {
			new Logger(peerProcess.peerInfoVector.get(peerProcess.indexID).getPeerID()).logReceivingMessages(client.serverID,
					"not interested");
		} catch (IOException e) {
			e.printStackTrace();
		}
		Set<Integer> interestedSet = new HashSet<>();
		ArrayList<RemotePeerInfo> interestedPeers = peerProcess.getInterestedPeers();
		for (RemotePeerInfo tempPeer : peerProcess.getInterestedPeers()) {
			interestedSet.add(tempPeer.getPeerID());
		}
		RemotePeerInfo containedPeer = new RemotePeerInfo();
		for (RemotePeerInfo tempPeer : peerProcess.peerInfoVector) {
			if (tempPeer.getPeerID() == client.serverID) {
				containedPeer = tempPeer;
				break;
			}
		}
		interestedPeers.remove(containedPeer);

	}

	public static void receiveHaveMsg(ActualMessage m, Client client) {
		int fileIndex = ByteBuffer.wrap(m.getPayload()).getInt();
		try {
			new Logger(peerProcess.peerInfoVector.get(peerProcess.indexID).getPeerID()).logReceivingMessages(client.serverID,"have");
		} catch (IOException e) {
			e.printStackTrace();
		}

		RemotePeerInfo containedPeer = new RemotePeerInfo();
		for (RemotePeerInfo tempPeer : peerProcess.peerInfoVector) {
			if (tempPeer.getPeerID() == client.serverID) {
				containedPeer = tempPeer;
				break;
			}
		}

		containedPeer.pieceIndex.add(fileIndex);

		RemotePeerInfo remotePeerInfo = peerProcess.peerInfoVector.get(peerProcess.indexID);
		// i don't have fileIndex piece && i'm not interested in you now
		if (!remotePeerInfo.pieceIndex.contains(fileIndex) && !peerProcess.getInterestedPeers().contains(client.serverID)) {
			// TODO send intersted message to id
		}

		peerProcess.checkFinish();
	}

    public static void receiveRequestMsg(ActualMessage m, Client client){
		//no need to log
        //logger.logReceivingMessages(id,"receive");
        String pieceIndex = new String(m.getPayload(), StandardCharsets.UTF_8);
        //if unchoked:
        //sendPieceMsg(pieceIndex);
    }
   
    public static void receivePieceMsg(ActualMessage m, Client client) {}

	private static void sendUnchokeMsg() {
		
    	
    	
		return;
	}

}
