package backend.academy;

public class StringConstants {
    public static final String API = "api";
    public static final String V1 = "v1";
    public static final String API_V1 = "/" + API + "/" + V1 + "/";
    public static final String LINKS = "links";
    public static final String LINKS_V1 = API_V1 + LINKS;
    public static final String TG_CHAT = "tg-chat";
    public static final String TG_CHAT_V1 = API_V1 + TG_CHAT;
    public static final String UPDATES = "updates";
    public static final String UPDATES_V1 = API_V1 + UPDATES;

    public static String cantFindLink(String link) {
        return "Не удалось найти ссылку " + link;
    }

    public static String cantFindLinkInChat(String link, Long chat) {
        return String.format("Чат %s не содержит ссылки %s", chat, link);
    }

    public static String unknownIpAddress() {
        return "неизвестный ip адресс";
    }
}
