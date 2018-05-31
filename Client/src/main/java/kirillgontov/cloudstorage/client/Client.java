package kirillgontov.cloudstorage.client;
import kirillgontov.cloudstorage.client.util.Configuration;
import kirillgontov.cloudstorage.common.Message;
import kirillgontov.cloudstorage.common.MessageService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;


public class Client {
    private SocketChannel clientChannel;


    public Client() throws IOException {
        connect();
    }

    private void connect() throws IOException {
        clientChannel = SocketChannel.open();
        clientChannel.configureBlocking(false);
        clientChannel.connect(new InetSocketAddress(Configuration.SERVER_HOST, Configuration.SERVER_PORT));
    }

    public void sendMessage (Message message) throws IOException {
        MessageService.sendMessage(message,clientChannel);
    }

    public Message receiveMessage () throws IOException, ClassNotFoundException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        return MessageService.receiveMessage(byteBuffer, clientChannel);
    }

    public void finishConnection() {
        try {
            clientChannel.finishConnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
