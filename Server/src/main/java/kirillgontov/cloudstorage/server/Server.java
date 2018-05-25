package kirillgontov.cloudstorage.server;
import kirillgontov.cloudstorage.common.Command;

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

    Server() throws IOException, SQLException, ClassNotFoundException {
        SQLHandler.connect();
        this.serverSocket = ServerSocketChannel.open(); // создание сервер сокета
        this.serverSocket.socket().bind(new InetSocketAddress(host, port)); // привязываем локальный адрес
        this.serverSocket.configureBlocking(false); // настраиваем неблокирующие операции
        this.selector = Selector.open(); // создаем селектор

        // сервер регистрируется на селекторе;
        // сервер говорит селектору, чтобы он реагировал на сообщения OP-ACCEPT (клиент подключился) со стороны сервера
        this.serverSocket.register(selector, SelectionKey.OP_ACCEPT);
    }

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
            byteBuffer = null;
            SQLHandler.disconnect();
            System.out.println("Everything is closed");
        }
    }

    private void handleAccept(ServerSocketChannel serverSocket, Selector selector) throws IOException {

        SocketChannel clientSocket = serverSocket.accept(); // получаем ссылку на сокет канал клиента (сокет каналы постоянно не создаются, мы просто берем ссылку)
        String address = (new StringBuilder(clientSocket.socket().getInetAddress().toString())).append(":").append(clientSocket.socket().getPort()).toString(); // запрашиваем из сокета полный IP-адрес клиента
        clientSocket.configureBlocking(false); // будем работать с этим клиентом не в режиме блокировки
        clientSocket.register(selector, SelectionKey.OP_READ, address); // клиентский сокет канал регистрируется на селекторе, говорит ему, чтобы реагировал на события READ с этого канала,
        // даем имя этому каналу
        System.out.println("accepted connection from: " + address);
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel clientSocket = (SocketChannel) key.channel();
        String address = (new StringBuilder(clientSocket.socket().getInetAddress().toString())).append(":").append(clientSocket.socket().getPort()).toString();
        byteBuffer.clear();
        clientSocket.read(byteBuffer);
        String request =  new String(byteBuffer.array()).trim();
        System.out.println(address + ": " + request);
        byteBuffer.clear();

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
        System.out.println("email = " + email + " password = " + password);
        /*try {
            System.out.println(SQLHandler.checkUsername(email));
        } catch (SQLException e) {
            byteBuffer.put(Command.USERNAME_EMPTY.bytes());
            byteBuffer.flip();
            clientSocket.write(byteBuffer);
            byteBuffer.clear();
            return;
        }
        try {
            System.out.println(SQLHandler.checkPassword(email, password));
        } catch (SQLException e) {
            byteBuffer.put(Command.PASSWORD_INCORRECT.bytes());
            byteBuffer.flip();
            clientSocket.write(byteBuffer);
            byteBuffer.clear();
            return;
        }*/
        byteBuffer.put(Command.LOGIN_SUCCESS.getText().getBytes());
        byteBuffer.flip();

        clientSocket.write(byteBuffer);
        byteBuffer.clear();
        // подгрузить папку
    }

    private void register (String request, SocketChannel clientSocket) throws IOException{
        String[] regTokens = request.split(" ");
        String firstName = regTokens[1];
        String lastName = regTokens[2];
        String emailReg = regTokens[3];
        String passwordReg = regTokens[4];
        try {
            SQLHandler.addNewUser(firstName,lastName,emailReg,passwordReg);
        } catch (SQLException e) {
            byteBuffer.put(Command.USERNAME_EXISTS.bytes());
            byteBuffer.flip();
            clientSocket.write(byteBuffer);
            byteBuffer.clear();
            return;
        }
        byteBuffer.put(Command.REGISTER_SUCCESS.bytes());
        byteBuffer.flip();
        clientSocket.write(byteBuffer);
        byteBuffer.clear();
        //создать папку FileService
    }

    public static void main (String[] args)  {
        Server server = null;
        try {
            server = new Server();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        (new Thread(server)).start();
    }
}
