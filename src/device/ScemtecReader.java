package device;

import java.util.ArrayList;

import com.fazecast.jSerialComm.SerialPort;

public class ScemtecReader {

	byte STX = 0x2;
	byte ETX = 0x3;

	SerialPort serialPort;

	public ScemtecReader() {
		System.out.println(SerialPort.getCommPorts());

		serialPort = SerialPort.getCommPort("COM20");
		serialPort.setComPortParameters(9600, 8, 1, SerialPort.NO_PARITY);
		serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
		serialPort.openPort();
		System.out.println("ScemtecReader initialized");
	}

	public ArrayList<String> getTags() {
		ArrayList<String> tags = new ArrayList<>();

		// Inventory erstellen
		send("6C20s"); // Response length: 14 byte
		System.out.println("Response HEX: " + read(14));

		// Inventory lesen (Alle TAG Ids) // Response with 1 TAG ID: 28 byte, e.g:
		// <ACK><STX>6C210001563FC34C000104E0<ETX>q
		// <ACK><STX> 6C21 0001 563FC34C000104E0 <ETX> q
		// ACK STX CMD #COUNT DSFID-Byte-TAG-ID ETX CHECKSUM
		// ID = 16 byte (64bit)
		send("6C21");

		// read first 10 byte to know how much data is sent to us
		// answer looks like: <ACK><STX>6C210001
		String data = read(10);

		// 17th byte is our number we need (this is the number which indicates how many
		// tags were found)
		char cCount = data.charAt(17);

		int iCount = Integer.valueOf(cCount);

		System.out.println("Number of tags found: " + iCount);

		// int offset = 18;

		// read all tag ids
		for (int i = 0; i < iCount; i++) {
			// read first found tag
			data = read(16);
			// String tag = data.substring(offset, offset + 16);
			tags.add(data);
			// offset += offset + 16;
		}

		return tags;

	}

	/**
	 * Sends data to the reader
	 * 
	 * @param data
	 */
	public void send(String cmd) {
		byte[] fullCmd = calcScemtecFullCmd(cmd.getBytes());
		serialPort.writeBytes(fullCmd, fullCmd.length);

		System.out.println("Send CMD (HEX): " + cmdToHexString(fullCmd));

	}

	/**
	 * Blocking read function
	 * 
	 * @return byte[] response from the reader
	 */
	public String read(int length) {
		System.out.println("Waiting for answer...");
		while (true) {
			if (serialPort.bytesAvailable() > length - 1) {
				byte[] readBuffer = new byte[serialPort.bytesAvailable()];
				System.out.println("Bytes Available: " + readBuffer.length);
				serialPort.readBytes(readBuffer, readBuffer.length);
				System.out.println("ASCII: " + cmdToASCIIString(readBuffer));
				// return cmdToHexString(readBuffer);
				return cmdToASCIIString(readBuffer);

			}
		}

	}

	/**
	 * Calculate CRC for scemtec SHL-2001
	 * 
	 * @param bArr
	 *            payload
	 * 
	 * @return crc for scemtec SHL-2001
	 */
	public byte calcScemtecCRC(byte[] bArr) {
		byte crc = 0x0; // initialize CRC
		for (int i = 0; i < bArr.length; i++) {
			crc ^= bArr[i]; // XOR
		}
		return crc;
	}

	/**
	 * Calculate full command with STX, ETX and CRC from a comand for scemtec
	 * SHL-2001
	 * 
	 * @author Ralf S. Mayer, june 2015
	 * 
	 * @param cmd
	 *            payload only (w.o. STX and ETX)
	 * @return full command string for scemtec SHL-2001 including STX, cmd, ETX, CRC
	 */
	public byte[] calcScemtecFullCmd(byte[] cmd) {
		byte bArr[] = new byte[cmd.length + 2]; // STX, cmd, ETX
		bArr[0] = STX; // start with STX
		for (int i = 0; i < cmd.length; i++) {
			bArr[i + 1] = cmd[i]; // fill after STX
		}
		bArr[cmd.length + 1] = ETX; // end with ETX
		byte crc = calcScemtecCRC(bArr); // get CRC
		// new array with CRC
		byte bArr2[] = new byte[bArr.length + 1]; // STX, cmd, ETX, CRC
		for (int i = 0; i < bArr.length; i++) {
			bArr2[i] = bArr[i]; // copy
		}
		bArr2[bArr.length] = crc;
		return bArr2;
	}

	/**
	 * Output command array as decimals
	 * 
	 * @author Ralf S. Mayer, june 2015
	 * @param cmd
	 * @return
	 */
	public String cmdToDecString(byte[] cmd) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < cmd.length - 1; i++) {
			buf.append(String.format("%03d", cmd[i]) + ",");
		}
		buf.append(String.format("%03d", cmd[cmd.length - 1]));
		return buf.toString();
	}

	/**
	 * Output command array as hex
	 * 
	 * @author Ralf S. Mayer, june 2015
	 * @param cmd
	 * @return
	 */
	public String cmdToHexString(byte[] cmd) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < cmd.length - 1; i++) {
			buf.append(String.format("%02X", cmd[i]) + ",");
		}
		buf.append(String.format("%02X", cmd[cmd.length - 1]));
		return buf.toString();
	}

	public String cmdToASCIIString(byte[] cmd) {
		String tmp = "";
		char c;
		for (byte b : cmd) {
			if (b == 0x2) {
				tmp += "<STX>";
			} else if (b == 0x3) {
				tmp += "<ETX>";
			} else if (b == 0x6) {
				tmp += "<ACK>";
			} else {
				c = (char) b;
				tmp += c;
			}

		}
		return tmp;
	}
}
