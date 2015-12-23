import java.util.Vector;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class ExtractTcpData {

	private String pcapDir;
	private Vector<String> tcpFiles;

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
		File dir = new File( "./tcpData");
		String savedPath = "";
		
		try {
			if ( !dir.exists() ) {
				dir.mkdir();
			}
			savedPath = dir.getPath() + File.separator + savedName;
			File file = new File( savedPath );
			if ( !file.exists() ) {
				file.createNewFile();
			}
			FileInputStream in = new FileInputStream( inPath );
			FileOutputStream out = new FileOutputStream( file );
			byte[] fileHeader = new byte[24];
			in.read( fileHeader );
			byte[] packetHeader = new byte[16];
			while ( in.read( packetHeader ) != -1 ) {
				Util.reverseByteArray( packetHeader, 8, 4 );
				int packetDataLen = (int)Util.byteArrayToLong( packetHeader, 8 );
				byte[] packetData = new byte[ packetDataLen ];
				in.read( packetData );
				out.write( packetData );
			}			
		} catch ( IOException ex ) { }
	}
}