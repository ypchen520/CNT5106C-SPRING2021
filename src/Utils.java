import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Utils {
    public Utils(){}
    public static byte[] convertIntToByteArray(final int data){
        return new byte[] {
            (byte)((data >> 24) & 0xff),
            (byte)((data >> 16) & 0xff),
            (byte)((data >> 8) & 0xff),
            (byte)((data >> 0) & 0xff),
        };
    }
    public static int convertByteArrayToInt(byte[] data) {
        if (data == null || data.length != 4) return 0x0;
        // ----------
        return (int)( // NOTE: type cast not necessary for int
                (0xff & data[0]) << 24  |
                (0xff & data[1]) << 16  |
                (0xff & data[2]) << 8   |
                (0xff & data[3]) << 0
        );
    }
    
    public static byte[] convertPieceSetToByteArr(Set<Integer> pieceIndexes) {
    	byte[] bytes = new byte[peerProcess.maxPieces];
    	for(int pieceIndex:pieceIndexes) {
    		bytes[pieceIndex] = 1;
    	}
    	List<Byte> resArr = new ArrayList<Byte>();
    	StringBuilder stringBuilder = new StringBuilder();
    	for(int i = 0; i < bytes.length; i++) {
    		stringBuilder.append(bytes[i]);
    		if(i%8==7) {
    			resArr.add(Byte.valueOf(stringBuilder.toString(),2));
    			stringBuilder.setLength(0);
    		}
    	}
    	if(stringBuilder.length()!=0) {
    		for(int i = 0;i<8-stringBuilder.length();i++) {
    			stringBuilder.append("0");
    		}
    		resArr.add(Byte.valueOf(stringBuilder.toString(),2));
    	}
    	byte[] resBytes = new byte[resArr.size()];
    	for(int i = 0; i < resArr.size(); i++) {
    		resBytes[i]=resArr.get(i);
    	}
    	return resBytes;
    }
    
    public static Set<Integer> convertByteArrToPieceSet(byte[] payload){
    	StringBuilder stringBuilder = new StringBuilder();
    	for (int i=0;i<payload.length;i++) {
    		String temp = Integer.toBinaryString(Integer.parseInt(Byte.toString(payload[i])));
    		for(int j = 0;j<8-temp.length();i++) {
    			stringBuilder.append("0");
    		}
    		stringBuilder.append(temp);
    	}
    	Set<Integer> resSet = new HashSet<>();
    	for(int i = 0; i < stringBuilder.length(); i++) {
    		if(stringBuilder.charAt(i)=='1') {
    			resSet.add(i);
    		}
    	}
    	return resSet;
    }
}
