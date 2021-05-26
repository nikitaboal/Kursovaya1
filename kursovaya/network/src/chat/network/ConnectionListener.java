package chat.network;

public interface ConnectionListener {
    // Создаем интерфейс для того, чтобы использовать одни и те же методы и в серверной, и в клиентской частях
    void onConnectionReady (Connection connection);
    // Соединение готово; передается экземпляр соединения
    void onReceiveString(Connection connection, String mag);
    // Принимается строка; передается экземпляр соединения и сама строка
    void onDisconnect (Connection connection);
    // Отключение соединения; передается экземпляр соединения
    void onException (Connection connection, Exception e);
    // Исключение; передается экземпляр соединения и объект исключения
}
