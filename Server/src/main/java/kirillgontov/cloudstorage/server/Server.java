package kirillgontov.cloudstorage.server;

import kirillgontov.cloudstorage.common.Command;
import kirillgontov.cloudstorage.common.Message;
import kirillgontov.cloudstorage.common.MessageService;
import kirillgontov.cloudstorage.server.service.FileService;
import kirillgontov.cloudstorage.server.service.SQLHandler;
import kirillgontov.cloudstorage.server.util.Configuration;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;


public class Server implements Runnable{
    private final String host = Configuration.SERVER_HOST;
    private final int port = Configuration.SERVER_PORT;
    private ServerSocketChannel serverSocket;
    private Selector selector;

    Server() throws IOException, SQLException, ClassNotFoundException {
        SQLHandler.connect();
        selector = Selector.open();
        serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(host, port));
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public void run() {
        try {
            while (true) {
                selector.select(); // блокирующая операция, пока событий нет - ждет
                // selectedKeys = все необработанные события на сервере
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove(); // выкидываем событие, чтобы не обработать его несколько раз
                    if (key.isAcceptable()) handleAccept(key);
                    if (key.isReadable()) handleRead(key);
                }
            }
        } catch (IOException  e) {
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
            SQLHandler.disconnect();
            System.out.println("Everything is closed");
        }
    }

    private void handleAccept(SelectionKey key) {
        try {//сокет каналы постоянно не создаются, мы просто берем ссылку
            SocketChannel client = ((ServerSocketChannel) key.channel()).accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void handleRead(SelectionKey key) {
        try {
            SocketChannel clientChannel = (SocketChannel) key.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            Message message = MessageService.receiveMessage(byteBuffer,clientChannel);
            switch (message.getCommand()) {
                case LOGIN:
                    login(message, clientChannel);
                    break;
                case REGISTER:
                    register(message, clientChannel);
                    break;
                case UPLOAD:
                    uploadFile(message, clientChannel);
                    break;
                case DELETE:
                    deleteFile(message,clientChannel);
                    break;
                case DOWNLOAD:
                    downloadFile(message,clientChannel);
                    break;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
//            TODO clientChannel.close();
        }
    }

    private void login (Message message, SocketChannel clientChannel) throws IOException{
        String username = message.getUsername();
        int passwordHash = message.getPasswordHash();
        try {
            if (!SQLHandler.checkExist(username))
                MessageService.sendMessage(new Message.MessageBuilder().setCommand(Command.USERNAME_EMPTY)
                                                                        .create(),clientChannel);
            else if (!SQLHandler.checkPassword(username,passwordHash))
                MessageService.sendMessage(new Message.MessageBuilder().setCommand(Command.PASSWORD_INCORRECT)
                                                                        .create(),clientChannel);
            else {
                List<Path> fileList = FileService.getFileList(username);
                MessageService.sendMessage(new Message.MessageBuilder().setCommand(Command.LOGIN_SUCCESS)
                                                                        .setFileList(fileList)
                                                                        .create(),clientChannel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void register (Message message, SocketChannel clientChannel) throws IOException{
        String firstName = message.getFirstName();
        String lastName = message.getLastName();
        String email = message.getEmail();
        String username = message.getUsername();
        int passwordHash = message.getPasswordHash();
        try {
            SQLHandler.addNewUser(firstName, lastName, email, username, passwordHash);
        } catch (SQLException e) {
            //TODO убрать из эксепшн
            try {
                if (!SQLHandler.checkExist(email))
                    MessageService.sendMessage(new Message.MessageBuilder().setCommand(Command.EMAIL_EXISTS)
                                                                            .create(), clientChannel);
                else if (!SQLHandler.checkExist(username))
                    MessageService.sendMessage(new Message.MessageBuilder().setCommand(Command.USERNAME_EXISTS)
                                                                            .create(), clientChannel);
            } catch (SQLException e1) {
               e1.printStackTrace();
            }
            return;
        }
        FileService.createUserFolder(username);
        MessageService.sendMessage(new Message.MessageBuilder().setCommand(Command.REGISTER_SUCCESS)
                                                                .create(), clientChannel);
    }

    private void uploadFile(Message message, SocketChannel clientChannel) throws IOException {
        String folderName = message.getUsername();
        String fileName = message.getFileName();
        byte[] fileBytes = message.getFileBytes();

        try {
            FileService.uploadFile(folderName, fileName, fileBytes);
        } catch (IOException e){
            e.printStackTrace();
            MessageService.sendMessage(new Message.MessageBuilder().setCommand(Command.UPLOAD_FAILED)
                                                                    .create(),clientChannel);
        }
        List<Path> fileList = FileService.getFileList(folderName);
        MessageService.sendMessage(new Message.MessageBuilder().setCommand(Command.UPLOAD_SUCCESS)
                                                                .setFileList(fileList)
                                                                .create(),clientChannel);
    }
    private void deleteFile(Message message, SocketChannel clientChannel) throws IOException {
        String folderName = message.getUsername();
        String fileName = message.getFileName();

        try {
            FileService.deleteFile(folderName, fileName);
        } catch (IOException e){
            e.printStackTrace();
            MessageService.sendMessage(new Message.MessageBuilder().setCommand(Command.DELETE_FAILED)
                                                                    .create(),clientChannel);
        }
        List<Path> fileList = FileService.getFileList(folderName);
        MessageService.sendMessage(new Message.MessageBuilder().setCommand(Command.DELETE_SUCCESS)
                                                                .setFileList(fileList)
                                                                .create(),clientChannel);
    }

    private void downloadFile(Message message, SocketChannel clientChannel) throws IOException {
        String folderName = message.getUsername();
        String fileName = message.getFileName();
        byte[] fileBytes = FileService.downloadFile(folderName,fileName);
        MessageService.sendMessage(new Message.MessageBuilder()
                                            .setCommand(Command.DOWNLOAD_SUCCESS)
                                            .setFileName(fileName)
                                            .setFileBytes(fileBytes).create(),clientChannel);
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
