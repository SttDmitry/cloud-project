package my.cloud.server.service.impl.command;

import io.netty.channel.Channel;
import my.cloud.common.Common;
import my.cloud.server.service.CommandService;

import java.sql.*;

public class AuthCommand implements CommandService {

    private static final String url = "jdbc:postgresql://localhost:5435/cloud";
    private static final String user = "postgres";
    private static final String pass = "postgrespass";

    @Override
    public String processCommand(String command, Channel channel) {
        final int requirementCountCommandParts = 3;

        String[] actualCommandParts = command.split("\\s", 3);
        if (actualCommandParts.length != requirementCountCommandParts) {
            throw new IllegalArgumentException("Command \"" + getCommand() + "\" is not correct");
        }

        try {
            Connection connection = DriverManager.getConnection(url, user, pass);
            System.out.println("Successfully connected");
            PreparedStatement prepStmt = connection.prepareStatement("SELECT login, password, username FROM auth_data WHERE login = ? AND password = ? ;");
            prepStmt.setString(1, actualCommandParts[1]);
            prepStmt.setString(2, actualCommandParts[2]);
            ResultSet rs = prepStmt.executeQuery();
            while (rs.next()) {
                prepStmt.close();
                connection.close();
                return Common.AUTH_SUCCESS + " " + rs.getString("username");
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return Common.AUTH_FAIL.toString();
    }

    @Override
    public String getCommand() {
        return Common.AUTH.toString();
    }
}
