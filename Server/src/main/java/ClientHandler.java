import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ClientHandler {

    private Selector selector; // бегает по каналам и проеверяет нет ли там событий
    private ByteBuffer byteBuffer = ByteBuffer.allocate(1000000);


    public ClientHandler() {
    }

    private final ByteBuffer welcomeBuf = ByteBuffer.wrap("Welcome to NioChat!\n".getBytes());

    private void handleAccept(SelectionKey key) throws IOException {
        SocketChannel sc = ((ServerSocketChannel) key.channel()).accept(); // получаем ссылку на сокет канал клиента (сокет каналы постоянно не создаются, мы просто берем ссылку)
        String address = (new StringBuilder(sc.socket().getInetAddress().toString())).append(":").append(sc.socket().getPort()).toString(); // запрашиваем из сокета полный IP-адрес клиента
        sc.configureBlocking(false); // будем работать с этим клиентом не в режиме блокировки
        sc.register(selector, SelectionKey.OP_READ, address);   // клиентский сокет канал регистрируется на сокете, говорит ему, чтобы реагировал на сообщения READ с этого канала,
        // даем имя этому каналу
        sc.write(welcomeBuf); // посылаем в этот канал сообщение
        welcomeBuf.rewind(); // перекидываем курсор в начало, чтобы иметь возможность отправить это сообщение следующему клиенту
        System.out.println("accepted connection from: " + address);
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel ch = (SocketChannel) key.channel();
        StringBuilder sb = new StringBuilder();

        byteBuffer.clear();
        int read = 0;
        while ((read = ch.read(byteBuffer)) > 0) {
            byteBuffer.flip();
            byte[] bytes = new byte[byteBuffer.limit()];
            byteBuffer.get(bytes);
            sb.append(new String(bytes));
            byteBuffer.clear();
        }
        String msg;
        if (read < 0) {
            msg = key.attachment() + " left the chat.\n";
            ch.close();
        } else {
            msg = key.attachment() + ": " + sb.toString();
        }

        System.out.println(msg);
//        broadcast(msg);
    }
}
