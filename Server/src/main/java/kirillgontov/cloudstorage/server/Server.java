package kirillgontov.cloudstorage.server;
import kirillgontov.cloudstorage.common.Command;
import kirillgontov.cloudstorage.server.service.SQLHandler;
import kirillgontov.cloudstorage.server.util.Configuration;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;


public class Server implements Runnable{
    private final String host = Configuration.SERVER_HOST;
    private final int port = Configuration.SERVER_PORT;
    private ServerSocketChannel serverSocket;
    private Selector selector; // бегает по каналам и проеверяет нет ли там событий
    private ByteBuffer byteBuffer = ByteBuffer.allocate(256);



    Server() throws IOException, SQLException, ClassNotFoundException {
        SQLHandler.connect();
        serverSocket = ServerSocketChannel.open(); // создание сервер сокета
        serverSocket.socket().bind(new InetSocketAddress(host, port)); // привязываем локальный адрес
        serverSocket.configureBlocking(false); // настраиваем неблокирующие операции
        selector = Selector.open(); // создаем селектор

        // сервер регистрируется на селекторе;
        // сервер говорит селектору, чтобы он реагировал на сообщения OP-ACCEPT (клиент подключился) со стороны сервера
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public void run() {
        try {
            System.out.println("Server starting on port " + port);

            SelectionKey key; // SelectionKey = информация о канале и о событии
            while (serverSocket.isOpen()) { // до тех пор пока работает сервер
                selector.select(); // блокирующая операция, пока событий нет - ждет
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator(); // запрашиваем ссылку на итератор, selectedKeys = все необработанные события на сервере
                while (iterator.hasNext()) { // перебираем события
                    key = iterator.next(); // событие
                    if (key.isAcceptable()) handleAccept(key); // проверяем что за событие и как его обрабатывать
                    if (key.isReadable()) handleRead(key);
                    iterator.remove(); // выкидываем событие, чтобы не обработать его несколько раз
                }
            }
        } catch (IOException e) {
            System.out.println("IOException, server of port " + port + " terminating.");
            e.printStackTrace();
        } finally {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {

                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            byteBuffer = null;
            SQLHandler.disconnect();
            System.out.println("Everything is closed");
        }
    }

    private void handleAccept(SelectionKey key) throws IOException {

        SocketChannel clientSocket = ((ServerSocketChannel) key.channel()).accept(); // получаем ссылку на сокет канал клиента (сокет каналы постоянно не создаются, мы просто берем ссылку)
        String address = clientSocket.socket().getRemoteSocketAddress().toString(); // запрашиваем из сокета полный IP-адрес клиента
        clientSocket.configureBlocking(false); // будем работать с этим клиентом не в режиме блокировки
        clientSocket.register(selector, SelectionKey.OP_READ, address); // клиентский сокет канал регистрируется на селекторе, говорит ему, чтобы реагировал на события READ с этого канала,
        System.out.println("accepted connection from: " + address);
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel clientSocket = (SocketChannel) key.channel();
        String address = clientSocket.socket().getRemoteSocketAddress().toString();

        byteBuffer.clear();
        clientSocket.read(byteBuffer);
        String request = new String(byteBuffer.array()).trim();
        System.out.println(address + ": " + request);

        if (request.startsWith(Command.LOGIN.getText())) {
            login(request, clientSocket);
        }
        if (request.startsWith(Command.REGISTER.getText())) {
            register(request, clientSocket);
        }
    }

    private void login (String request, SocketChannel clientSocket) throws IOException{
        String[] authTokens = request.split(" ");
        String email = authTokens[1];
        String password = authTokens[2];

        try {
            System.out.println(email + " " + SQLHandler.checkUsername(email));
        } catch (SQLException e) {
            byteBuffer.clear();
            byteBuffer.put(Command.USERNAME_EMPTY.getText().getBytes());
            byteBuffer.flip();
            clientSocket.write(byteBuffer);
            return;
        }

        try {
            System.out.println(SQLHandler.checkPassword(email, password));
        } catch (SQLException e) {
            byteBuffer.clear();
            byteBuffer.put(Command.PASSWORD_INCORRECT.getText().getBytes());
            byteBuffer.flip();
            clientSocket.write(byteBuffer);
            return;
        }

        byteBuffer.clear();
        byteBuffer.put((Command.LOGIN_SUCCESS.getText()).getBytes());
        byteBuffer.flip();
        clientSocket.write(byteBuffer);

        // подгрузить папку
    }

    private void register (String request, SocketChannel clientSocket) throws IOException{
        String[] regTokens = request.split(" ");
        String firstName = regTokens[1];
        String lastName = regTokens[2];
        String email = regTokens[3];
        String passwordHash = regTokens[4];
        try {
            SQLHandler.addNewUser(firstName,lastName,email,passwordHash);
        } catch (SQLException e) {
            byteBuffer.put(Command.USERNAME_EXISTS.getText().getBytes());
            byteBuffer.flip();
            clientSocket.write(byteBuffer);
            byteBuffer.clear();
            return;
        }

        byteBuffer.clear();
        byteBuffer.put(Command.REGISTER_SUCCESS.getText().getBytes());
        byteBuffer.flip();
        clientSocket.write(byteBuffer);

        //создать папку FileService
    }

    public static void main (String[] args)  {
        Server server = null;
        try {
            server = new Server();
        } catch (IOException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        new Thread(server).start();
    }
}
