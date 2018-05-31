package kirillgontov.cloudstorage.common;

import java.io.*;

public class Message implements Serializable {

    private Command command;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private int passwordHash;
    private String fileName;
    private byte[] fileBytes;



    public static class MessageBuilder{
        private Command command;
        private String firstName;
        private String lastName;
        private String email;
        private String username;
        private int passwordHash;
        private String fileName;
        private byte[] fileBytes;

        public Message create(){
            return new Message(command,firstName,lastName,email,username,passwordHash,fileName,fileBytes);
        }

        public MessageBuilder setCommand(Command command) {
            this.command = command;
            return this;
        }

        public MessageBuilder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public MessageBuilder setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public MessageBuilder setEmail(String email) {
            this.email = email;
            return this;
        }

        public MessageBuilder setUsername(String username) {
            this.username = username;
            return this;
        }

        public MessageBuilder setPasswordHash(int passwordHash) {
            this.passwordHash = passwordHash;
            return this;
        }

        public MessageBuilder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public MessageBuilder setFileBytes(byte[] fileBytes) {
            this.fileBytes = fileBytes;
            return this;
        }

        public MessageBuilder() {
        }
    }


    private Message(Command command, String firstName, String lastName, String email, String username, int passwordHash, String fileName, byte[] fileBytes) {
        this.command = command;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fileName = fileName;
        this.fileBytes = fileBytes;
    }

    public Command getCommand() {
        return command;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public int getPasswordHash() {
        return passwordHash;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }
}
