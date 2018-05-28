package kirillgontov.cloudstorage.common;

public enum Command {
    REGISTER("/register"), LOGIN("/login"),
    REGISTER_SUCCESS("/register_success"), USERNAME_EXISTS("/username_exists"), USERNAME_EMPTY("/username_empty"), PASSWORD_INCORRECT("/password_incorrect"), LOGIN_SUCCESS("/login_success"),
    UPLOAD("/upload"), DOWNLOAD("/download"), DELETE("/delete");

    private final String text;

    public final String getText() {
        return text;
    }

    Command(final String text){
        this.text = text;
    }
}
