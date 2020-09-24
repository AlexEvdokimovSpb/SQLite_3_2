package server;

import java.sql.*;

public class SimpleAuthService implements AuthService {

    private static Connection connection;
    private static Statement stmt;
    private static PreparedStatement psInsert;

    private class UserData {
        String login;
        String password;
        String nickname;

        public UserData(String login, String password, String nickname) {
            this.login = login;
            this.password = password;
            this.nickname = nickname;
        }
    }

    public SimpleAuthService() {
        try {
            connect();
            prepareAllStatement();
            System.out.println("DB подключена");
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password){
        try {
            return getNicknameByLogin(login, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean registration(String login, String password, String nickname) {
        try {
            addUser(login, password, nickname);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:mainDB_2.db");
        stmt = connection.createStatement();
    }

    @Override
    public void disconnect() {
        try {
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            psInsert.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void prepareAllStatement() throws SQLException {
        psInsert = connection.prepareStatement("INSERT INTO users (login, password, nickname) VALUES (?, ?, ?) ;");
    }

    public String getNicknameByLogin(String login, String password) throws SQLException {
        ResultSet rs = stmt.executeQuery("SELECT * FROM users");
        String nick = null;
        while (rs.next()) {
            if (rs.getString("login").equals(login)&&rs.getString("password").equals(password)){
                nick=rs.getString("nickname");
            }
        }
        rs.close();
        return nick;
    }

    public void addUser(String login, String password, String nickname) throws SQLException {
        connection.setAutoCommit(false);
        psInsert.setString(1, login);
        psInsert.setString(2, password);
        psInsert.setString(3, nickname);
        psInsert.addBatch();
        psInsert.executeBatch();
        connection.setAutoCommit(true);
    }

    private boolean checkIsNickname(String nickname) throws SQLException {
        boolean check = false;
        ResultSet rs = stmt.executeQuery("SELECT nickname FROM users");
        while (rs.next()) {
            if (rs.getString("nickname").equals(nickname)){
                check=true;
            }
        }
        rs.close();
        return check;
    }

    private boolean checkIsLogin(String login) throws SQLException {
        boolean check = false;
        ResultSet rs = stmt.executeQuery("SELECT login FROM users");
        while (rs.next()) {
            if (rs.getString("login").equals(login)){
                check=true;
            }
        }
        rs.close();
        return check;
    }

    private static void clearTable() throws SQLException {
        stmt.executeUpdate("DELETE FROM users; ");
    }

    private void createTableEx() {
        String s = "CREATE TABLE users (\n" +
                "    id    INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    login   TEXT,\n" +
                "    password TEXT,\n" +
                "    nickname TEXT\n" +
                ");";
    }
}

