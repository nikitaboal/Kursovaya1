package chat.server;

import chat.network.Connection;
import chat.network.ConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server implements ConnectionListener {

    public static void main (String[] args) {
        new Server();
    }
    private final ArrayList<Connection> connections = new ArrayList<>();
    // Список соединений

    private Server(){
        System.out.println("Server running...");
        try(ServerSocket serverSocket = new ServerSocket(8189)) {
            while (true){
                try {
                    new Connection(this, serverSocket.accept());
                }catch (IOException e){
                    System.out.println("Connection exception: " + e);
                }
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    // В конструкторе "слушаем" порт и принмаем входящие соединение, обрабатывая исключения

    @Override
    public synchronized void onConnectionReady(Connection connection) {
        connections.add(connection);
        sendToAllConnections("Client connected" + connection);
    }
    // Реализуем метод; Соединяет клиента с сервером и выводит информацию об этом

    @Override
    public synchronized void onReceiveString(Connection connection, String value) {
        sendToAllConnections(value);
    }
    // Реализуем метод; Рассылает принятую строку всем соединениям

    @Override
    public synchronized void onDisconnect(Connection connection) {
        connections.remove(connection);
        sendToAllConnections("Client disconnected" + connection);
    }
    // Реализуем метод; Прерывает соединение и выводит информацию об этом

    @Override
    public synchronized void onException(Connection connection, Exception e) {
        System.out.println("Connection exception: " + e);
    }
    // Реализуем метод; Вывод информации об исключении

    private void sendToAllConnections(String value){
        System.out.println(value);
        final int cnt = connections.size();
        for (int i = 0; i < connections.size(); i++) connections.get(i).sendString(value);
    }
    // Метод, рассылающий сообщение всем соединениям
}
