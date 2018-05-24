import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.Iterator;


public class Server implements Runnable {
    private final String host = Configuration.SERVER_HOST;
    private final int port = Configuration.SERVER_PORT;
    private ServerSocketChannel serverSocket;
    private Selector selector; // бегает по каналам и проеверяет нет ли там событий

    private ByteBuffer byteBuffer = ByteBuffer.allocate(256);

    Server() throws IOException, SQLException {
        SQLHandler.connect();
        this.serverSocket = ServerSocketChannel.open(); // создание сервер сокета
        this.serverSocket.socket().bind(new InetSocketAddress(host, port)); // привязываем локальный адрес
        this.serverSocket.configureBlocking(false); // настраиваем неблокирующие операции
        this.selector = Selector.open(); // создаем селектор

        // сервер регистрируется на селекторе;
        // сервер говорит селектору, чтобы он реагировал на сообщения OP-ACCEPT (клиент подключился) со стороны сервера
        this.serverSocket.register(selector, SelectionKey.OP_ACCEPT);
    }

    //public boolean isAuthorized = SQLHandler.checkUsernamePassword("", "");

    public void run() {
        try {
            System.out.println("Server starting on port " + this.port);

            Iterator<SelectionKey> iter;
            SelectionKey key; // SelectionKey = информация о канале и о событии
            while (this.serverSocket.isOpen()) { // до тех пор пока работает сервер
                selector.select(); // блокирующая операция, пока событий нет - ждет
                iter = this.selector.selectedKeys().iterator(); // запрашиваем ссылку на итератор, selectedKeys = все необработанные события на сервере
                while (iter.hasNext()) { // перебираем события
                    key = iter.next(); // событие
                    if (key.isAcceptable()) this.handleAccept(serverSocket, selector); // проверяем что за событие и как его обрабатывать
                    if (key.isReadable()) this.handleRead(key);
                    iter.remove(); // выкидываем событие, чтобы не обработать его несколько раз
                }
            }
        } catch (IOException e) {
            System.out.println("IOException, server of port " + this.port + " terminating. Stack trace:");
            e.printStackTrace();
        } finally {
            try {
                selector.close();
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            SQLHandler.disconnect();
        }
    }

    private void handleAccept(ServerSocketChannel serverSocket, Selector selector) throws IOException {
        SocketChannel clientSocket = serverSocket.accept(); // получаем ссылку на сокет канал клиента (сокет каналы постоянно не создаются, мы просто берем ссылку)
        String address = (new StringBuilder(clientSocket.socket().getInetAddress().toString())).append(":").append(clientSocket.socket().getPort()).toString(); // запрашиваем из сокета полный IP-адрес клиента
        clientSocket.configureBlocking(false); // будем работать с этим клиентом не в режиме блокировки
        clientSocket.register(selector, SelectionKey.OP_READ, address);   // клиентский сокет канал регистрируется на селекторе, говорит ему, чтобы реагировал на события READ с этого канала,
        // даем имя этому каналу
        System.out.println("accepted connection from: " + address);
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel clientSocket = (SocketChannel) key.channel();
        StringBuilder sb = new StringBuilder();

        byteBuffer.clear();
        int read = 0;
        while ((read = clientSocket.read(byteBuffer)) > 0) {
            byteBuffer.flip();
            byte[] bytes = new byte[byteBuffer.limit()];
            byteBuffer.get(bytes);
            sb.append(new String(bytes));
            byteBuffer.clear();
        }
        String msg;
        if (read < 0) {
            msg = key.attachment() + " left the chat.\n";
            clientSocket.close();
        } else {
            msg = key.attachment() + ": " + sb.toString();
        }

        System.out.println(msg);
//        broadcast(msg);
    }

    public static void main (String[] args) throws IOException, SQLException {
        Server server = new Server();
        (new Thread(server)).start();
    }
}
