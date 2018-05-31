package kirillgontov.cloudstorage.common;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class MessageService {

    public static void sendMessage (Message message, SocketChannel clientChannel) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(message);
        objectOutputStream.flush();
        clientChannel.write(ByteBuffer.wrap(byteArrayOutputStream.toByteArray()));
    }

    public static Message receiveMessage (ByteBuffer byteBuffer, SocketChannel clientChannel) throws IOException, ClassNotFoundException {
        clientChannel.read(byteBuffer);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteBuffer.array());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        return  (Message) objectInputStream.readObject();
    }
}
