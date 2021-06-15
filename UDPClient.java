import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.zip.CRC32;

public class UDPClient {

    public static int PACKETLEN = 64;
    public static void main(String[] args) {

        if (args.length != 1) return;
        try {
            File file = new File(args[0]);
            Path filePath = file.toPath();
            byte[] fileData = Files.readAllBytes(filePath);
            DatagramSocket dsock = new DatagramSocket();

            int fileDataBlocks = fileData.length / PACKETLEN + 1;
            System.out.printf("%d blocks to send\n", fileDataBlocks);

            int fileDataOffset = 0;
            for (int i = 0; i < fileDataBlocks; ++i) {
                System.out.printf("Sending block %d\n", i+1);

                ByteBuffer message = ByteBuffer.allocate(Short.BYTES + PACKETLEN + Long.BYTES); // packet num + message + checksum
                message.putShort((short)(i+1));

                int len = Math.min(64, fileData.length - fileDataOffset);
                message.put(fileData, fileDataOffset, len);

                CRC32 crc = new CRC32();
                crc.update(message.array(), 0, message.position()); // calcs crc from packet num + message
                message.putLong(crc.getValue());

                /* send built packet to server */
                DatagramPacket packet = new DatagramPacket(message.array(), message.position());
                packet.setAddress(InetAddress.getLocalHost());
                packet.setPort(25352);
                dsock.send(packet);

                short respNum;
                do {
                    /* receive response from server after packet sent */
                    byte[] response = new byte[Short.BYTES];
                    DatagramPacket responsePacket = new DatagramPacket(response, response.length);
                    dsock.receive(responsePacket);

                    ByteBuffer respBuf = ByteBuffer.allocate(responsePacket.getLength());
                    respBuf.put(responsePacket.getData(), 0, responsePacket.getLength());
                    respBuf.rewind();

                    respNum = respBuf.getShort();
                    if (respNum == 2) {
                        dsock.send(packet);
                    }
                } while (respNum != 1);
                
                fileDataOffset += PACKETLEN;
            }

            System.out.println("Sending eof");

            byte[] eof = new byte[Short.BYTES];
            DatagramPacket eofPacket = new DatagramPacket(eof, eof.length);
            eofPacket.setAddress(InetAddress.getLocalHost());
            eofPacket.setPort(25352);
            dsock.send(eofPacket);
            
            byte[] response = new byte[10];
            DatagramPacket responsePacket = new DatagramPacket(response, response.length);
            dsock.receive(responsePacket);

            System.out.println(new String(response, StandardCharsets.UTF_8));

            dsock.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}