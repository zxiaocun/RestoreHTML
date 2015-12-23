import java.util.Vector;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

// represent each session
public class Session {
	public String ip1;
	public String ip2;
	public long port1;
	public long port2;
	public Protocal.type protocal;
	//public List<Packet> data;
	public Vector<Packet> packets;

	public Session ( String ip1, String ip2, long port1, long port2, Protocal.type protocal) {
		this.ip1 = ip1;
		this.ip2 = ip2;
		this.port1 = port1;
		this.port2 = port2;
		this.protocal = protocal;

		packets = new Vector<Packet>( 50 );
		//data = new LinkedList<Packet>();
	}

	public boolean equals ( Object obj ) {
		Session s = ( Session )obj;
		return s.protocal == protocal && s.port1 == port1 && s.port2 == port2 && s.ip1.equals(ip1) && s.ip2.equals(ip2);
	}

	public void addPacket ( Packet pkt ) {
		if ( pkt.protocal == Protocal.type.TCP ) {
			addTcpPacket( pkt );
		}
		else if ( pkt.protocal == Protocal.type.UDP ) {
			addUdpPacket( pkt );
		}
	}

	private void addTcpPacket ( Packet nn ) {
		Iterator itr = packets.iterator();
		int index = 0;
		Packet n;
		while ( itr.hasNext() ) {
			n = (Packet)itr.next();
			// same direction
			if ( n.source.equals(nn.source) && n.seq > nn.seq ) {
				break;
			} 
			// contrary direction
			else if ( n.source.equals(nn.destination) && n.seq >= nn.ack ){
				break;
			}

			index ++;
		}

		packets.add( index, nn );
	}

	private void addUdpPacket ( Packet nn ) {
		Iterator itr = packets.iterator();
		Packet pkt;
		int index = 0;
		while ( itr.hasNext() ) {
			pkt = (Packet)itr.next();
			if ( pkt.ts_sec > nn.ts_sec ) {
				break;
			}
			index ++;
		}
		packets.add( index, nn );
	}

	public String toString () {
		return String.format( "%s  %d  %s  %d  %s\n", ip1, port1, ip2, port2, protocal );
	}
}