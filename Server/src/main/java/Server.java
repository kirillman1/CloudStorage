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
    private final int port;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector; // бегает по каналам и проеверяет нет ли там событий
    private ByteBuffer byteBuffer = ByteBuffer.allocate(1000000);

    Server(int port) throws IOException, SQLException, ClassNotFoundException {
        SQLHandler.connect();
        this.port = port;
        this.serverSocketChannel = ServerSocketChannel.open(); // создание сервер сокета
        this.serverSocketChannel.socket().bind(new InetSocketAddress(port)); // привязываем локальный адрес
        this.serverSocketChannel.configureBlocking(false); // настраиваем неблокирующие операции
        this.selector = Selector.open(); // создаем селектор

        // сервер регистрируется на селекторе;
        // сервер говорит селектору, чтобы он реагировал на сообщения OP-ACCEPT (клиент подключился) со стороны сервера
        this.serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    //public boolean isAuthorized = SQLHandler.checkUsernamePassword("", "");

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

                /*if (key.isAcceptable()) this.handleAccept(key); // проверяем что за событие и как его обрабатывать
                if (key.isReadable()) this.handleRead(key);*/
            }
        }
    } catch (IOException e) {
        System.out.println("IOException, server of port " + this.port + " terminating. Stack trace:");
        e.printStackTrace();
        }
    }




    public static void main(String[] args) throws IOException {
        Server server = null;
        try {
            server = new Server(8189);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        (new Thread(server)).start();
    }
}
