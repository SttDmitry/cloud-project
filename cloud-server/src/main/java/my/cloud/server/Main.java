package my.cloud.server;

import my.cloud.server.factory.Factory;
import org.flywaydb.core.Flyway;

public class Main {
    public static void main(String[] args) throws Exception {
        Flyway flyway = Flyway.configure().dataSource("jdbc:postgresql://localhost:5435/cloud", "postgres", "postgrespass").load();
        flyway.migrate();
        Factory.getServerService().startServer();
        //Мои вопросы:
        //1.
    }
}
