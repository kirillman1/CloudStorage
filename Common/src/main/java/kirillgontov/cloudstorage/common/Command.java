package kirillgontov.cloudstorage.common;

public enum Command {
    REGISTER("/register"), LOGIN("/login"),
    REGISTER_SUCCESS("/register_success"), USERNAME_EXISTS("/username_exists"), USERNAME_EMPTY("/username_empty"), PASSWORD_INCORRECT("/password_incorrect"), LOGIN_SUCCESS("/login_success"),
    UPLOAD("/upload"), DOWNLOAD("/download"), DELETE("/delete");

    private String text;
    private byte[] bytes;

    public byte[] bytes(){
        return bytes;
    }
    public String getText() {
        return text;
    }

    Command(String text){
        this.text = text;
        this.bytes = this.getText().getBytes();
    }
}
