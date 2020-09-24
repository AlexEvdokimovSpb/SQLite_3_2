package server;

import java.sql.SQLException;

public interface AuthService {
    /**
     * @return nickname если пользователь есть
     * @return null если пользоватаеля нет
     * */
    String getNicknameByLoginAndPassword(String login, String password) throws SQLException;

    boolean registration(String login, String password, String nickname);

    void disconnect();
}
