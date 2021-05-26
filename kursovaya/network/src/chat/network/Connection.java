package chat.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class Connection {
    private final Socket socket;
    private final Thread rxThread;
    private final ConnectionListener eventListener;
    private final BufferedReader in;
    private final BufferedWriter out;

    public Connection(ConnectionListener eventListener, String ipAddr, int port) throws IOException {
        this(eventListener, new Socket(ipAddr, port));
    }
    // Конструктор расчитан на то, что сокет будет создаваться "внутри"
    // Методы этого конструктора могут генерировать исключения
    // В него передается экземпляр интерфейса, IP адрес и порт
    // В нем вызываем другой конструктор

    public Connection(ConnectionListener eventListener, Socket socket) throws IOException {
        // Методы этого конструктора могут генерировать исключения
        // В него передается сам сокет и экземпляр интерфейса
        // Конструктор расчитан на то, что кто-то "снаружи" создаст сокет
        this.eventListener = eventListener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        // Получаем входящий поток, принимающий байты и задаем конкретную кодировку
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        // Поток для передачи байтов(вписывать) и задаем конкретную кодировку
        rxThread = new Thread(new Runnable() {
            // Поток, который "слушает" входящие соединения; Экземпляр класса Thread реализующий интерфейс Runnable
            // Анонимный класс
            @Override
            public void run() {
                // Этот метод "слушает" входящие соединения
                try {
                    // "Ловит" и обрабатывет исключение
                    eventListener.onConnectionReady(Connection.this);
                    // Принимает экземпляр обрамляющего класса
                    while (!rxThread.isInterrupted()){
                        // Бесконечный цикл для получения строк
                        eventListener.onReceiveString(Connection.this, in.readLine());
                    }
                } catch(IOException e){
                    eventListener.onException(Connection.this, e);
                    }
                 finally{
                    // В случае ошибки закрывает сокет
                    eventListener.onDisconnect(Connection.this);
                    }

                }

        });
        rxThread.start();
    }
    public synchronized void sendString(String value){
        // Метод для отправки сообщений; принимает строку, которую хотим отправить
        // Синхронизация для обращения к нему из разных потоков
        try {
            out.write(value + "\r\n");
            out.flush();
            // "flush" сбрасывает буфер
        } catch (IOException e) {
            eventListener.onException(Connection.this, e);
            disconnect();
        }
        // Вписывает строку в поток вывода и обрабатывает исключение, прерывает соединение

    }

    public synchronized void disconnect() {
        // Метод для обрыва соединения
        // Синхронизация для обращения к нему из разных потоков
        rxThread.interrupt();
        // Прерываем поток
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(Connection.this, e);
        }
        // Закрывает сокет, обрабатывая исключение

    }

    @Override
    public String toString() {
        return "Connection: " + socket.getInetAddress() + ": " + socket.getPort();

    }
    // Переопределяем метод; возвращает данные подключившегося клиента
}
