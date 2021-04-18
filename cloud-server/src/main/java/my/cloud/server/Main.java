package my.cloud.server;

import my.cloud.server.Factory.Factory;

public class Main {
    public static void main(String[] args) throws Exception {
        Factory.getServerService().startServer();
        //Мои вопросы:
        //1. Как работать сразу с двумя типами данных, ведь я должен отправлять и измененный список объектов на сервере (если произошел upload) и должен отправлять файл, если происходит download этого файла со стороны клиента
    }
}
