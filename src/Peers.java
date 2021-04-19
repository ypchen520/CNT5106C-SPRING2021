import java.net.*;
import java.nio.ByteBuffer;
import java.rmi.Remote;
import java.io.*;
import java.util.*;

public class Peers {
	private static CommonUtil comUtil;
	private static List<RemotePeerInfo> unchokedPeers = new ArrayList<>();

	public Peers(CommonUtil comUtil) {
		Peers.comUtil = comUtil;
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
					if (peerProcess.peerInfoVector.get(peerProcess.indexID).completeFile) {

						// add all the interested peers to the rate list
						for (RemotePeerInfo tempPeer : peerProcess.getInterestedPeers()) {
							// peerId_ChunkCounts.add(new PeerId_ChunkCount(peerId,
							// PeerProcess.peers.get(PeerProcess.getIndex(peerId)).getChunkCount()));
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
							tempPeer.resetDownlowdingRatePiece();
						}

					} else {
						// TODO:get interested Peers from peerprocess it change when get bitfield and
						// have messages;
						ArrayList<RemotePeerInfo> interestedPeers = peerProcess.getInterestedPeers();
						Collections.shuffle(interestedPeers);
						for (RemotePeerInfo tempPeer : interestedPeers) {
							if (preferredNeighborList.size() < comUtil.getNumNeighbors()) {
								preferredNeighborList.add(tempPeer.getPeerID());
							}
						}
					}

					// logger change prefer neighbors
					// TODO change the data stucture
					ArrayList<RemotePeerInfo> logPreferredNeighborList = new ArrayList<>();
					// Logger.logPreferredNeighborsChange(preferredNeighborList);

					for (RemotePeerInfo tempPeer : peerProcess.peerInfoVector) {
						// if prefer and choke, send unchoke
						if (preferredNeighborList.contains(tempPeer.getPeerID()) && tempPeer.choke) {
							// TODO send unchoke message
							tempPeer.choke = false;
						} else if (!preferredNeighborList.contains(tempPeer.getPeerID()) && !tempPeer.choke
								&& optimisticUnchokeNeighbor != tempPeer.getPeerID()) {
							// if not prefer, not choke, not opt, send choke
							// PeerProcess.write("Choking peer " + p.peerId);
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
	public static void receiveInterestedMsg(ActualMessage m, int id) {
		try {
			new Logger(peerProcess.peerInfoVector.get(peerProcess.indexID).getPeerID()).logReceivingMessages(id,
					"interested");
		} catch (IOException e) {
			e.printStackTrace();
		}
		Set<Integer> interestedSet = new HashSet<>();
		ArrayList<RemotePeerInfo> interestedPeers = peerProcess.getInterestedPeers();
		for (RemotePeerInfo tempPeer : peerProcess.getInterestedPeers()) {
			interestedSet.add(tempPeer.getPeerID());
		}

		if (!interestedSet.contains(id)) {
			RemotePeerInfo containedPeer = new RemotePeerInfo();
			for (RemotePeerInfo tempPeer : peerProcess.peerInfoVector) {
				if (tempPeer.getPeerID() == id) {
					containedPeer = tempPeer;
					break;
				}
			}
			interestedPeers.add(containedPeer);
		}
	}

	public static void receiveNotInterestedMsg(ActualMessage m, int id) {
		try {
			new Logger(peerProcess.peerInfoVector.get(peerProcess.indexID).getPeerID()).logReceivingMessages(id,
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
			if (tempPeer.getPeerID() == id) {
				containedPeer = tempPeer;
				break;
			}
		}
		interestedPeers.remove(containedPeer);

	}
	
	public static void receiveHaveMsg(ActualMessage m, int id) {
		int fileIndex = ByteBuffer.wrap(m.getPayload()).getInt();
		try {
			new Logger(peerProcess.peerInfoVector.get(peerProcess.indexID).getPeerID()).logReceivingMessages(id,"have");
		} catch (IOException e) {
			e.printStackTrace();
		}

		RemotePeerInfo containedPeer = new RemotePeerInfo();
		for (RemotePeerInfo tempPeer : peerProcess.peerInfoVector) {
			if (tempPeer.getPeerID() == id) {
				containedPeer = tempPeer;
				break;
			}
		}

		containedPeer.pieceIndex.add(fileIndex);

		RemotePeerInfo remotePeerInfo = peerProcess.peerInfoVector.get(peerProcess.indexID);
		// i don't have fileIndex piece && i'm not interested in you now
		if (!remotePeerInfo.pieceIndex.contains(fileIndex) && !peerProcess.getInterestedPeers().contains(id)) {
			// TODO send intersted message to id
		}

		peerProcess.checkFinish();
	}

    public static void receiveRequestMsg(ActualMessage m, ){

    }

	private static void sendUnchokeMsg() {
		// TODO
		return;
	}

}