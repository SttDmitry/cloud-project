package my.cloud.server;

import my.cloud.server.Factory.Factory;

public class Main {
    public static void main(String[] args) {
        Factory.getServerService().startServer();
        //Мои вопросы:
        //1. Т.к. не знакомился ещё с Netty - то первоочередно, можно ли будет взаимодействовать с файловой системой через неё (т.е. выводить через неё файловую систему, изменять и т.д.)?
        //2. Посмотрел доп. материалы по Docker-у, понял речь о возможнностях Flyway и прочее. Возник такой вопрос - как они будут взаимодействовать друг с другом? Ведь докер создает в volume уже бд, которую можно будет сохранить. В таком случае для чего флайвей?
        //3. Правильно ли я понимаю, что "облако" будет расположено локально? В случае реализации авторизации, для каждого пользователя для изначального пути "облака" будут добавляться папки с именами пользователей и там уже храниться их файлы?
    }
}
