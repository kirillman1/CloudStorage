package kirillgontov.cloudstorage.common;

import java.io.Serializable;

public enum Command implements Serializable {
    REGISTER, LOGIN,
    REGISTER_SUCCESS, USERNAME_EXISTS, USERNAME_EMPTY, PASSWORD_INCORRECT, LOGIN_SUCCESS,
    UPLOAD, DOWNLOAD, DELETE;


}
