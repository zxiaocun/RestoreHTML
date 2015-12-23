public class Util {
	public static long byteArrayToLong(byte[] b,int offset){
		long value=0;
		for(int i=0;i<4;i++){
			int shift=(4-1-i)*8;
			value+=(b[i+offset]&0x000000FFl)<<shift;
		}
		return value;
	}
	
	public static int byteArrayToInt(byte[]b,int offset){
		int value=0;
		for(int i=0;i<2;i++){
			int shift=(2-1-i)*8;
			value+=(b[i+offset]&0x000000FF)<<shift;
		}

		return value;
	}

	public static void reverseByteArray ( byte[] bts, int start, int len ) {
		byte temp;
		for ( int i = 0; i < len / 2; i++ ) {
			temp = bts[ start+i ];
			bts[ start+i ] = bts[ start+len-i-1 ];
			bts[ start+len-i-1 ] = temp;
		}
	}
}