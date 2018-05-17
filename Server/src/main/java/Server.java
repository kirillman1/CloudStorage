import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Server implements Runnable {
    private final int port;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector; // бегает по каналам и проеверяет нет ли там событий
    private ByteBuffer byteBuffer = ByteBuffer.allocate(1000000);

    Server(int port) throws IOException {
        this.port = port;
        this.serverSocketChannel = ServerSocketChannel.open(); // создание сервер сокета
        this.serverSocketChannel.socket().bind(new InetSocketAddress(port)); // привязываем локальный адрес
        this.serverSocketChannel.configureBlocking(false); // настраиваем неблокирующие операции
        this.selector = Selector.open(); // создаем селектор

        // сервер регистрируется на селекторе;
        // сервер говорит селектору, чтобы он реагировал на сообщения OP-ACCEPT (клиент подключился) со стороны сервера
        this.serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }
    public void run() {
        try {
        System.out.println("Server starting on port " + this.port);

        Iterator<SelectionKey> iter;
        SelectionKey key; // SelectionKey = информация о канале и о событии
        while (this.serverSocketChannel.isOpen()) { // до тех пор пока работает сервер
            selector.select(); // блокирующая операция, пока событий нет - ждет
            iter = this.selector.selectedKeys().iterator(); // запрашиваем ссылку на итератор, selectedKeys = все необработанные события на сервере
            while (iter.hasNext()) { // перебираем события
                key = iter.next(); // событие
                iter.remove(); // выкидываем событие, чтобы не обработать его несколько раз

                if (key.isAcceptable()) this.handleAccept(key); // проверяем что за событие и как его обрабатывать
                if (key.isReadable()) this.handleRead(key);
            }
        }
    } catch (IOException e) {
        System.out.println("IOException, server of port " + this.port + " terminating. Stack trace:");
        e.printStackTrace();
    }
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
        broadcast(msg);
    }

    private void broadcast(String msg) throws IOException {
        ByteBuffer msgBuf = ByteBuffer.wrap(msg.getBytes());
        for (SelectionKey key : selector.keys()) {
            if (key.isValid() && key.channel() instanceof SocketChannel) {
                SocketChannel sch = (SocketChannel) key.channel();
                sch.write(msgBuf);
                msgBuf.rewind();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(8189);
        (new Thread(server)).start();
    }
}
