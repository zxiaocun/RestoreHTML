import java.util.Vector;
import java.util.Iterator;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.File;

public class ExtractTcpUdpFromPcap {
	// represent all the sessions
	private String pcapFilePath;
	private Vector<Session> sessions = new Vector<Session>( 100 );
	private byte[] pcapHeader = new byte[24];

	public ExtractTcpUdpFromPcap ( String pcapPath ) {
		pcapFilePath = pcapPath;
	}

	public void extract() {
		byte[] packetHeader = new byte[16];
		FileInputStream in = null;
		int index = 0;
		try {
			in = new FileInputStream( pcapFilePath );
		} catch ( FileNotFoundException ex ) { }
		try {
			in.read( pcapHeader );
			index += pcapHeader.length;
			while (	in.read(packetHeader) != -1) {	
				Packet pkt = new Packet();
				pkt.pcapStart = index;
				resolvePacketHeader( packetHeader, pkt );
				index += pkt.pcapLen;
				byte[] packetData = new byte[ (int)pkt.pcapLen-16 ];
				in.read( packetData );
				resolvePacketData( packetData, pkt );
				if ( pkt.protocal == Protocal.type.OTHER ) {
					continue;
				}
				Session session = createSession( packetData );
				int i = sessions.indexOf( session );
				if ( i == -1 ) {
					sessions.add( session );
				} else {
					session = sessions.get( i );
				}
				session.addPacket( pkt );
			}

			writeResult();
		} catch ( IOException ex ) { }
	}

	private void resolvePacketHeader ( byte[] bts, Packet pkt ) {
		Util.reverseByteArray( bts, 0, 4 );
		pkt.ts_sec = Util.byteArrayToLong( bts, 0 );
		Util.reverseByteArray( bts, 8, 4 );
		pkt.pcapLen = (int)Util.byteArrayToLong( bts, 8 ) + 16;
	}

	private void resolvePacketData ( byte[] bts, Packet pkt ) {
		int pro = bts[ 23 ];
		if ( pro == 6 )
			pkt.protocal = Protocal.type.TCP;
		else if ( pro == 17 )
			pkt.protocal = Protocal.type.UDP;
		else {
			pkt.protocal = Protocal.type.OTHER;
			return;
		}

		pkt.source = unsignedByte(bts[26]) + "." + unsignedByte(bts[27]) + "." + unsignedByte(bts[28]) + "." + unsignedByte(bts[29]);
		pkt.destination = unsignedByte(bts[30]) + "." + unsignedByte(bts[31]) + "." + unsignedByte(bts[32]) + "." + unsignedByte(bts[33]);
		pkt.srcPort = Util.byteArrayToInt( bts, 34 );
		pkt.dstPort = Util.byteArrayToInt( bts, 36 );
		pkt.seq = Util.byteArrayToLong( bts, 38 );
		pkt.ack = Util.byteArrayToLong( bts, 42 );
	}

	private Session createSession ( byte[] bts ) {
		Protocal.type pro = bts[ 23 ] == 6 ? Protocal.type.TCP : bts[ 23 ] == 17 ? Protocal.type.UDP : Protocal.type.OTHER;

		if (pro == Protocal.type.OTHER)
			return null;

		long ip1 = Util.byteArrayToLong( bts, 26 );
		long ip2 = Util.byteArrayToLong( bts, 30 );
		String ip1Str = unsignedByte(bts[26]) + "." + unsignedByte(bts[27]) + "." + unsignedByte(bts[28]) + "." + unsignedByte(bts[29]);
		String ip2Str = unsignedByte(bts[30]) + "." + unsignedByte(bts[31]) + "." + unsignedByte(bts[32]) + "." + unsignedByte(bts[33]);
		int port1 = Util.byteArrayToInt( bts, 34 );
		int port2 = Util.byteArrayToInt( bts, 36 );

		if ( ip1 > ip2 ) {
			String ipTmp = ip1Str;
			int portTmp = port1;
			ip1Str = ip2Str;
			ip2Str = ipTmp;
			port1 = port2;
			port2 = portTmp;
		}

		return new Session( ip1Str, ip2Str, port1, port2, pro );
	}

	private int unsignedByte( byte b ) {
		return b & 0x000000FF;
	}

	private void writeResult () {
		String outDir = "./Ëã·¨1Êä³ö";
		File file = new File( outDir );
		if ( !file.exists() ) {
			file.mkdirs();
		}
		Iterator itr = sessions.iterator();
		Session session;
		String filePath;
		while ( itr.hasNext() ) {
			session = (Session)itr.next();
			filePath = outDir + File.	separator + session.protocal + "[" + session.ip1  + "][" + session.port1 + "][" + session.ip2 + "][" + session.port2 + "].pcap";
			writePcap( session, filePath );
		}
	}

	private void writePcap ( Session session, String filePath ) {
		File file = new File( filePath );
		try {
			if ( !file.exists() ) {
				file.createNewFile();
			}
			FileOutputStream out = new FileOutputStream( file );
			out.write( pcapHeader );
			Iterator itr = session.packets.iterator();
			Packet pkt;
			while ( itr.hasNext() ) {
				pkt = (Packet)itr.next();
				FileInputStream in = new FileInputStream( pcapFilePath );
				byte[] bts = new byte[ pkt.pcapLen ];
				in.skip( pkt.pcapStart );
				in.read( bts );
				out.write( bts );
			}
		} catch ( IOException ex ) {}	
	}
}