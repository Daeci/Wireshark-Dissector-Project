import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;

public class UDPServer {
    public static void main (String[] args) {

        System.out.println("Server running.");

        int okPacketTotal = 0, corruptedPacketTotal = 0;
        try {
            File file = new File("output.txt");
            FileWriter fw = new FileWriter(file);
    
            DatagramSocket dsock = new DatagramSocket(25352);
    
            byte[] message = new byte[1024];
            DatagramPacket packet = new DatagramPacket(message, message.length);
    
            boolean running = true;
            while (running) {
                
                dsock.receive(packet);
                ByteBuffer bb = ByteBuffer.allocate(packet.getLength());
                bb.put(packet.getData(), 0, packet.getLength());
                bb.rewind();
        
                short blockNum = bb.getShort();
                if (blockNum == 0) break;
                System.out.printf("Received blockNum %d\n", blockNum);

                byte[] buf = new byte[packet.getLength() - Long.BYTES];
                bb.rewind();
                bb.get(buf, 0, packet.getLength() - Long.BYTES);

                CRC32 crc = new CRC32();
                crc.update(buf);
                
                long crcFromPacket = bb.getLong();

                if (crc.getValue() == crcFromPacket) {
                    System.out.printf("Packet %d is ok.\n", blockNum);

                    ByteBuffer ok = ByteBuffer.allocate(2);
                    ok.putShort((short)(1));
                    DatagramPacket okPacket = new DatagramPacket(ok.array(), ok.position());

                    okPacket.setAddress(packet.getAddress());
                    okPacket.setPort(packet.getPort());
                    dsock.send(okPacket);

                    for (int i = Short.BYTES; i < packet.getLength() - Long.BYTES; ++i) {
                        fw.write(message[i]);
                    }

                    okPacketTotal++;
                }
                else {
                    System.out.printf("Packet %d is corrupted.\n", blockNum);

                    ByteBuffer corrupted = ByteBuffer.allocate(2);
                    corrupted.putShort((short)(2));
                    DatagramPacket corruptedPacket = new DatagramPacket(corrupted.array(), corrupted.position());

                    corruptedPacket.setAddress(packet.getAddress());
                    corruptedPacket.setPort(packet.getPort());
                    dsock.send(corruptedPacket);

                    corruptedPacketTotal++;
                }
        
            }
        
            byte[] response = "Success!".getBytes();
            DatagramPacket responsePacket = new DatagramPacket(response, response.length);
            responsePacket.setAddress(packet.getAddress());
            responsePacket.setPort(packet.getPort());
            dsock.send(responsePacket);

            fw.close();
            dsock.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Server done.");
        System.out.printf("Statistics: Ok packets=%d, Corrupted packets=%d\n", okPacketTotal, corruptedPacketTotal);
    }
}