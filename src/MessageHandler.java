// TODO: rename to MessageHandler (filename, classname, and constructor) {Futing}

// TODO: Implement Bitfield, Request, and Piece

// TODO: sender functions

// TODO: pass in Vector<client>

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.io.*;
import java.util.*;

public class MessageHandler {
	private static int peerID;
    private static CommonUtil comUtil;
	private static List<RemotePeerInfo> unchokedPeers = new ArrayList<>();
    private static Logger logger;
	private static FileHandler fileHandler;

	public MessageHandler(CommonUtil comUtil, int peerID, Logger logger, FileHandler fh) {
		MessageHandler.comUtil = comUtil;
        MessageHandler.peerID = peerID;
        MessageHandler.logger = logger;
		MessageHandler.fileHandler = fh;
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
					if (!peerProcess.peerInfoVector.get(peerProcess.indexID).hascompletefile()) {

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

					// ArrayList<RemotePeerInfo> logPreferredNeighborList = new ArrayList<>();
					logger.logPreferredNeighborsChange(preferredNeighborList);

					Map<Integer, Client> clientMap = new HashMap<>();
					for(Client client:peerProcess.clients) {
						clientMap.put(client.getServerID(), client);
					}
					for (RemotePeerInfo tempPeer : peerProcess.peerInfoVector) {
						// if prefer and choke, send unchoke
						if (preferredNeighborList.contains(tempPeer.getPeerID()) && tempPeer.choke) {
							Client client = clientMap.get(tempPeer.getPeerID());
							ActualMessage actualMessage = new ActualMessage(ActualMessage.MessageType.UNCHOKE, null);
							client.sendMessage(actualMessage);
							tempPeer.choke = false;
						} else if (!preferredNeighborList.contains(tempPeer.getPeerID()) && !tempPeer.choke
								&& optimisticUnchokeNeighbor != tempPeer.getPeerID()) {
							// if not prefer, not choke, not opt, send choke
							Client client = clientMap.get(tempPeer.getPeerID());
							ActualMessage actualMessage = new ActualMessage(ActualMessage.MessageType.CHOKE, null);
							client.sendMessage(actualMessage);

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
					Map<Integer, Client> clientMap = new HashMap<>();
					for(Client client:peerProcess.clients) {
						clientMap.put(client.getServerID(), client);
					}

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
						peerProcess.peerInfoVector.get(chockedPeerList.get(index)).choke=false;
						Client client = clientMap.get(peerProcess.peerInfoVector.get(chockedPeerList.get(index)).getPeerID());
						ActualMessage actualMessage = new ActualMessage(ActualMessage.MessageType.UNCHOKE, null);
						client.sendMessage(actualMessage);
						logger.logOptimisticallyUnchokedNeighborChange(optimisticUnchokeNeighbor);
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
			logger.logReceivingMessages(client.serverID, "interested");
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
			logger.logReceivingMessages(client.serverID, "not interested");
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
			logger.logReceivingMessages(client.serverID,"have");
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

		try {
			peerProcess.checkFinish(logger);
		}
		catch (Exception e) {
			System.out.print("Error checking if finished.");
			e.printStackTrace();
			System.out.println(e);
		}

	}


    public static void receiveRequestMsg(ActualMessage m, Client client){
		//no need to log
        //logger.logReceivingMessages(id,"receive");
        int pieceIndex = Utils.convertByteArrayToInt(m.getPayload());
        // DONALD: I'm adding these so the program will compile, I don't know about their usage so I'm guessing this is just not fully finished yet
        // TODO
		byte[] piece = null;
        //if unchoked:
        try {
          sendPieceMsg(pieceIndex, piece, client);
        }
        catch (Exception e) {
          System.out.print("Error sending piece.");
          e.printStackTrace();
          System.out.println(e);
        }

    }

    public static void receivePieceMsg(ActualMessage m, Client client) throws IOException {
		byte[] payload = m.getPayload();
		byte[] pieceIndexRaw = Arrays.copyOfRange(payload, 0, 4);
		int pieceIndex = Utils.convertByteArrayToInt(pieceIndexRaw);
		byte[] piece = Arrays.copyOfRange(payload, 4, payload.length);
		Set<Integer> pieces = peerProcess.peerInfoVector.get(peerProcess.indexID).pieceIndex;
		if(!pieces.contains(pieceIndex)){
			pieces.add(pieceIndex);
		}
		logger.logDownloadingPiece(client.serverID, pieceIndex, pieces.size());
		fileHandler.downloadPiece(piece, pieceIndex);
		//send have message?
		sendHaveMsg(client, pieceIndex);
	}

	public static void sendPieceMsg(int pieceIndex, byte[] piece, Client client) throws Exception{
		ByteArrayOutputStream msg = new ByteArrayOutputStream();
		msg.write(Utils.convertIntToByteArray(pieceIndex));
		msg.write(piece);
		byte[] payload = msg.toByteArray();
		ActualMessage pieceMsg = new ActualMessage(ActualMessage.MessageType.PIECE, payload);
		// byte[] pieceMsg = pieceMsgCreator.createMessage();
		client.sendMessage(pieceMsg);
	}

	public void sendInterestedMsg(Client client) {
		ActualMessage actualMessage = new ActualMessage(ActualMessage.MessageType.INTERESTED, null);		
		client.sendMessage(actualMessage);
	}

	public void sendNotInterestedMsg(Client client) {
		ActualMessage actualMessage = new ActualMessage(ActualMessage.MessageType.NOT_INTERESTED, null);
		client.sendMessage(actualMessage);
	}

	public static void sendHaveMsg(Client client,int index) {
		byte[] payload = Utils.convertIntToByteArray(index);
		ActualMessage actualMessage = new ActualMessage(ActualMessage.MessageType.HAVE, payload);
		client.sendMessage(actualMessage);
	}

	public void sendBitfieldMsg(Client client) {
		byte[] payload = Utils.convertPieceSetToByteArr(peerProcess.peerInfoVector.get(peerProcess.indexID).pieceIndex);
		ActualMessage actualMessage = new ActualMessage(ActualMessage.MessageType.BITFIELD, payload);
		client.sendMessage(actualMessage);
	}

	public void receiveBitfieldMsg(ActualMessage m,Client client) {
		byte[] bytes = m.getPayload();
		Set<Integer> convertSet = new HashSet<>();
		convertSet = Utils.convertByteArrToPieceSet(bytes);
		for(RemotePeerInfo p:peerProcess.peerInfoVector) {
			if(client.serverID==p.getPeerID()) {
				p.pieceIndex.addAll(convertSet);
				return;
			}
		}
	}

	public void receiveUnchokeMsg(ActualMessage m,Client client) throws IOException{
		logger.logTitForTat(client.serverID, "unchoke");
		this.sendRequestMsg(m,client);
	}

	private void sendRequestMsg(ActualMessage m, Client client) {
		//TODO:log{Yu-peng}
		//no need to log
		RemotePeerInfo clientPeer = new RemotePeerInfo();
		for(RemotePeerInfo p:peerProcess.peerInfoVector) {
			if(p.getPeerID()==client.serverID) {
				clientPeer = p;
			}
		}
		ArrayList<Integer> requiredPieces = new ArrayList<>();
		for(int i = 0;i<peerProcess.maxPieces-1;i++) {
			if(!peerProcess.peerInfoVector.get(peerProcess.indexID).pieceIndex.contains(i)&&clientPeer.pieceIndex.contains(i)&&!peerProcess.requestedPieces.contains(i)) {
				requiredPieces.add(i);
			}
		}
		Collections.shuffle(requiredPieces);
		byte[] payload = Utils.convertIntToByteArray(requiredPieces.get(0));
		ActualMessage actualMessage = new ActualMessage(ActualMessage.MessageType.REQUEST, payload);
		client.sendMessage(actualMessage);
	}

	public void receiveChokeMsg(ActualMessage m,Client client) throws IOException {
		logger.logTitForTat(client.serverID, "choke");
		return;
	}

}
