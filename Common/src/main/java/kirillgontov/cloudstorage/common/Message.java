package kirillgontov.cloudstorage.common;

import java.io.Serializable;

public class Message implements Serializable {

    private static final long serialVersionUID = -11054103631430122L;
    private Command command;
    private String email;
    private String password;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public Message(Command command){
        this.command = command;
    }

    public Message(Command command, String email, String password) {
        this.command = command;
        this.email = email;
        this.password = password;
    }
}
