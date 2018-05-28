package kirillgontov.cloudstorage.client;
import kirillgontov.cloudstorage.client.util.Configuration;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;


public class ClientNIO {
    private SocketChannel clientChannel;
    private ByteBuffer byteBuffer;

    public ClientNIO() throws IOException {
        clientChannel = SocketChannel.open();
        clientChannel.connect(new InetSocketAddress(Configuration.SERVER_HOST, Configuration.SERVER_PORT));
        byteBuffer = ByteBuffer.allocate(128);
    }


    public String sendRequest (String request){
        System.out.println("request -> " + request);
        byteBuffer.put(request.getBytes());
        byteBuffer.flip();
        String response = null;
        try {
            clientChannel.write(byteBuffer);
            byteBuffer.clear();///////////////////////////////
            clientChannel.read(byteBuffer);
            response = new String(byteBuffer.array()).trim();
            System.out.println("response <- " + response);
            byteBuffer.clear();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                clientChannel.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            byteBuffer = null;
        }
        return response;
    }
}
