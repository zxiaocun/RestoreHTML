import java.util.Vector;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class ExtractTcpData {

	private String pcapDir;
	private Vector<String> tcpFiles;
	private static final byte[] DIVISION = new byte[]{ 0X0d,0X0a };

	public ExtractTcpData ( String pcapDir ) {
		this.pcapDir = pcapDir;
		tcpFiles = new Vector<String>();
	}

	public void extract () {
		if ( pcapDir == null || pcapDir.length() == 0 )
			return;
		File file = new File( pcapDir );
		String[] pcapFiles = file.list();
		for ( int i = 0; i < pcapFiles.length; i++ ) {
			pcapFiles[i] = file.getPath() + File.separator + pcapFiles[i];
		}
		pickTcpFiles( pcapFiles );
		oprateTcpFiles();
	}

	private void pickTcpFiles ( String[] pcapFiles ) {
		for ( int i = 0; i < pcapFiles.length; i++ ) {
		File file = new File( pcapFiles[i] );
			String fileName = file.getName();
			if ( fileName.toUpperCase().startsWith("TCP") ) {
				tcpFiles.add( pcapFiles[i] );
			}
		}		
	}

	private void oprateTcpFiles () {
		Iterator itr = tcpFiles.iterator();
		while ( itr.hasNext() ) {
			String tcpFilePath = (String) itr.next();
			File file = new File( tcpFilePath );
			String savedName = file.getName() + ".txt";
			extractData( tcpFilePath, savedName );
		}
	}

	private void extractData ( String inPath, String savedName ) {
		File dir = new File( "./Ëã·¨2Êä³ö");
		String savedPath = "";
		try {
			if ( !dir.exists() ) { dir.mkdir(); }
			savedPath = dir.getPath() + File.separator + savedName;
			File file = new File( savedPath );
			if ( !file.exists() ) { file.createNewFile(); }
			FileInputStream in = new FileInputStream( inPath );
			FileOutputStream out = new FileOutputStream( file );
			in.skip( 24 );
			byte[] packetHeader = new byte[16];
			String bfSrc = null, curSrc = null;
			int index = 0;
			while ( in.read( packetHeader ) != -1 ) {
				Util.reverseByteArray( packetHeader, 8, 4 );
				int packetDataLen = (int)Util.byteArrayToLong( packetHeader, 8 );
				byte[] packetData = new byte[ packetDataLen ];
				in.read( packetData );
				curSrc = calSrc( packetData );
				byte[] tcpSeg = takeSeg( packetData );
				if ( bfSrc != null  && !curSrc.equals(bfSrc) && tcpSeg.length > 0 ) { out.write( DIVISION ); }
				out.write( tcpSeg );
				bfSrc = curSrc;
			}			
		} catch ( IOException ex ) { }
	}

	private String calSrc ( byte[] bts ) {
		return unsignedByte(bts[26]) + "." + unsignedByte(bts[27]) + "." + unsignedByte(bts[28]) + "." + unsignedByte(bts[29]);
	}

	private byte[] takeSeg ( byte[] bts ) {
		int ethHeaderLen = 14, ipHeaderLen, tcpHeaderLen, segLen, ipLen;
		ipHeaderLen = (bts[14] & 0x0F) * 4;
		tcpHeaderLen = ((bts[ 14 + ipHeaderLen + 12 ]&0xf0)>>4) * 4;
		ipLen = Util.byteArrayToInt( bts, 16 );
		segLen = ipLen - ipHeaderLen - tcpHeaderLen;
		int skip = ethHeaderLen + ipHeaderLen + tcpHeaderLen;
		byte[] result = new byte[segLen];
		for ( int i = 0; i < segLen; i++ ) {
			result[i] = bts[i+skip];
		}
		return result;
	}

	private int unsignedByte( byte b ) {
		return b & 0x000000FF;
	}
}