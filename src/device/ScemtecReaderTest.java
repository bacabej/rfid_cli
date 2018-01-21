package device;

public class ScemtecReaderTest {

	static ScemtecReader sr;

	public static void main(String[] args) {
		sr = new ScemtecReader();

		System.out.println(" ");

		// Inventory erstellen
		sr.send("6C20s"); // Response length: 14 byte
		System.out.println("Response HEX: " + sr.read(14));

		System.out.println(" ");

		// Inventory lesen (Alle TAG Ids) // Response with 1 TAG ID: 28 byte, e.g:
		// <ACK><STX>6C210001563FC34C000104E0<ETX>q
		// <ACK><STX> 6C21 0001 563FC34C000104E0 <ETX> q
		// ACK STX CMD #COUNT DSFID-Byte-TAG-ID ETX CHECKSUM
		// ID = 16 byte (64bit)
		sr.send("6C21");

		// read first 10 byte to know how much data is sent to us
		// answer looks like: <ACK><STX> 6C21 0001
		String data = sr.read(10);

		System.out.println("Response HEX: " + sr.read(28));
	}

	public static void sendTest(String cmd) {
		sr.send(cmd);
	}

}
