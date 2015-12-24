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
	//public Vector<Packet> packets;
	public MyList packets;

	public class MyList {
		class Node {
			Packet data;
			Node next, prev;

			public Node ( Packet pkt ) {
				data = pkt;
				next = prev = null;
			}
		}
		private int size;
		private Node front, rear;

		public MyList () {
			size = 0;
			front = rear = null;
		}

		public void add ( Packet pkt ) {
			Node nn = new Node( pkt );
			// list is empty
			if ( size == 0 ) {
				front = rear = nn;
				size ++;
				return;
			}
			if ( pkt.protocal == Protocal.type.TCP ) {
				addTcpPacket( nn );
				}
			else if ( pkt.protocal == Protocal.type.UDP ) {
				addUdpPacket( nn );
			}
		}

		public Iterator<Packet> iterator() {
			return new MyIterator<Packet>();
		}

		private void addUdpPacket ( Node nn ) {
			// add the new to the rear
			if ( nn.data.ts_sec >= rear.data.ts_sec ) {
				rear.next = nn;
				nn.prev = rear;
				rear = nn;
				size ++;
				return;
			}

			// add the new to the front
			if ( nn.data.ts_sec <= front.data.ts_sec ) {
				nn.next = front;	
				front.prev = nn;
				front = nn;
				size ++;
				return;
			}

			Node cur = rear;
			// the new node will insert before cur
			for ( int i = 0; i < size && cur.data.ts_sec > nn.data.ts_sec; i++ )
				cur = cur.prev;
			nn.next = cur.next;
			nn.next.prev = nn;
			nn.prev = cur;
			cur.next = nn;
			size ++;
		}

		private void addTcpPacket ( Node nn ) {
			// add the new to the rear
			if ( (rear.data.source.equals(nn.data.source) && rear.data.seq<=nn.data.seq) || (rear.data.source.equals(nn.data.destination) && rear.data.seq<nn.data.ack) ) {
				rear.next = nn;
				nn.prev = rear;
				rear = nn;
				size ++;
				return;
			}

			// add the new to the front
			if ( (front.data.source.equals(nn.data.source) && front.data.seq>=nn.data.seq) || (front.data.source.equals(nn.data.destination) && front.data.seq>=nn.data.ack) ) {
				nn.next = front;	
				front.prev = nn;
				front = nn;
				size ++;
				return;
			}

			Node cur = rear;
			// the new node will insert before cur
			for ( int i = 0; i < size; i++ ) {
				if ( cur.data.source.equals(nn.data.source) && cur.data.seq <= nn.data.seq ) {
					break;
				} 
				// contrary direction
				else if ( cur.data.source.equals(nn.data.destination) && cur.data.seq < nn.data.ack ){
					break;
				}
				cur = cur.prev;
			}
			nn.next = cur.next;
			nn.next.prev = nn;
			nn.prev = cur;
			cur.next = nn;
			size ++;
		}

		class MyIterator<T> implements Iterator<T> {
			Node cur = front;
			public T next () {
				T v = (T)cur.data;
				cur = cur.next;
				return v;
			}
			public boolean hasNext() {
				return cur != null;
			}
			public void remove() {}
		}
	}

	public Session ( String ip1, String ip2, long port1, long port2, Protocal.type protocal) {
		this.ip1 = ip1;
		this.ip2 = ip2;
		this.port1 = port1;
		this.port2 = port2;
		this.protocal = protocal;

		//packets = new Vector<Packet>( 50 );
		//data = new LinkedList<Packet>();
		packets = new MyList();
	}

	public boolean equals ( Object obj ) {
		Session s = ( Session )obj;
		return s.protocal == protocal && s.port1 == port1 && s.port2 == port2 && s.ip1.equals(ip1) && s.ip2.equals(ip2);
	}

	public void addPacket ( Packet pkt ) {
		packets.add( pkt );
	}

	// private void addTcpPacket ( Packet nn ) {
	// 	Iterator itr = packets.iterator();
	// 	int index = 0;
	// 	Packet n;
	// 	while ( itr.hasNext() ) {
	// 		n = (Packet)itr.next();
	// 		// same direction
	// 		if ( n.source.equals(nn.source) && n.seq > nn.seq ) {
	// 			break;
	// 		} 
	// 		// contrary direction
	// 		else if ( n.source.equals(nn.destination) && n.seq >= nn.ack ){
	// 			break;
	// 		}

	// 		index ++;
	// 	}

	// 	packets.add( index, nn );
	// }

	// private void addUdpPacket ( Packet nn ) {
	// 	Iterator itr = packets.iterator();
	// 	Packet pkt;
	// 	int index = 0;
	// 	while ( itr.hasNext() ) {
	// 		pkt = (Packet)itr.next();
	// 		if ( pkt.ts_sec > nn.ts_sec ) {
	// 			break;
	// 		}
	// 		index ++;
	// 	}
	// 	packets.add( index, nn );
	// }

	public String toString () {
		return String.format( "%s  %d  %s  %d  %s\n", ip1, port1, ip2, port2, protocal );
	}
}