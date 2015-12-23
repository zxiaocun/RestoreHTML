// represent each Packet in the pcap file
public class Packet {
	public String source;
	public String destination;
	public long srcPort;
	public long dstPort;
	public Protocal.type protocal;
	public long seq;
	public long ack;
	public int pcapStart;
	public int pcapLen;
	public long ts_sec;

	public String toString () {
		return protocal == Protocal.type.OTHER ? "" : String.format( "%s  %s  %d  %d  %s  %d  %d  %d  %d  %d",
			source, destination, srcPort, dstPort, protocal, seq, ack, pcapStart, pcapLen, ts_sec );
	}
}