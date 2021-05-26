package chat.client;

import chat.network.Connection;
import chat.network.ConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Client extends JFrame implements ActionListener, ConnectionListener {
    private static final String IP_ADDR = "79.139.182.61";
    private static final int PORT = 8189;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;

    public static void main (String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Client();
            }
        });
    }

    private final JTextArea log = new JTextArea();
    private final JTextField fieldNickname = new JTextField("имя");
    private final JTextField fieldInput = new JTextField("сообщение");
    private final JScrollPane scrollPane = new JScrollPane(log);
    private Connection connection;

    private Client() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        log.setEditable(false);
        log.setLineWrap(true);
        fieldInput.addActionListener(this);
        add(fieldInput, BorderLayout.SOUTH);
        add(fieldNickname, BorderLayout.NORTH);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane);
        setVisible(true);
        try {
            connection = new Connection(this, IP_ADDR, PORT);
        } catch (IOException e) {
            printMag("Connection exception: " + e);
        }
    }
    // В этом конструкторе настраиваем само окно

    @Override
    public void actionPerformed(ActionEvent e) {
        String mag = fieldInput.getText();
        if(mag.equals("")) return;
        fieldInput.setText(null);
        connection.sendString(fieldNickname.getText() + ": " + mag);
    }
    // Метод отправляющий сообщение нажатием на Enter

    @Override
    public void onConnectionReady(Connection connection) {
        printMag("Connection ready");
    }
    // Реализуем метод; вывод сообщения о готовности соединения

    @Override
    public void onReceiveString(Connection connection, String value) {
        printMag(value);
    }
    // Реализуем метод; вывод принимаемой строки

    @Override
    public void onDisconnect(Connection connection) {
        printMag("Connection close");
    }
    // Реализуем метод; вывод сообщения о прерывании соединения

    @Override
    public void onException(Connection connection, Exception e) {
        printMag("Connection exception: " + e);
    }
    // Реализуем метод; вывод сообщение об ошибке

    private synchronized void printMag(String mag) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(mag + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }
    // Метод пишущий сообщения
}
