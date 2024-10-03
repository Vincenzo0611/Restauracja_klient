package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class Main extends JFrame {
    private JTextArea textArea;
    private Socket tcpSocket;
    private PrintWriter out;
    private Klient_Main klient;

    public Main() {
        // Ustawienia okna
        setTitle("Klient Broadcast");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Pole tekstowe do wyświetlania logów
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        // Przycisk "Start"
        JButton startButton = new JButton("Start");
        add(startButton, BorderLayout.SOUTH);

        // Akcja po kliknięciu przycisku "Start"
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Uruchomienie w osobnym wątku, aby nie blokować GUI
                new Thread(() -> startBroadcastClient()).start();
            }
        });
    }

    private void startBroadcastClient() {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true); // Włącza tryb broadcast

            // Wiadomość do wysłania
            String message = "DISCOVER_SERVER";
            byte[] sendData = message.getBytes();

            // Broadcast na wszystkie komputery w sieci lokalnej (255.255.255.255)
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 8888);
            socket.send(sendPacket);
            logMessage("Wysłano broadcast: " + message);

            // Oczekiwanie na odpowiedź od serwera
            byte[] buffer = new byte[256];
            DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(receivePacket);

            String serverIP = new String(receivePacket.getData()).trim();
            logMessage("Znaleziono serwer! Adres IP: " + serverIP);

            // Teraz możemy połączyć się z serwerem TCP przy użyciu znalezionego adresu IP
            tcpSocket = new Socket(serverIP, 12345);
            logMessage("Połączono z serwerem TCP.");

            // Uruchamiamy nasłuchiwanie wiadomości od serwera
            new Thread(() -> listenForMessages()).start();

            // Uruchamiamy interfejs Klient_Main
            dispose(); // Zamykamy okno broadcastu
            klient = new Klient_Main(tcpSocket);
            klient.start();

        } catch (IOException e) {
            logMessage("Błąd: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void listenForMessages() {
        try (InputStream inputStream = tcpSocket.getInputStream();
             ObjectInputStream in = new ObjectInputStream(inputStream)) {

            while (true) {
                try {
                    String type = in.readUTF(); // Odczytujemy typ danych

                    if ("STRING".equals(type)) {
                        String message = in.readUTF(); // Odczytujemy string
                        System.out.println("Odebrano stringa: " + message);
                    } else if ("ADD_PARAGON".equals(type)) {
                        Paragon p = (Paragon) in.readObject(); // Deserializacja obiektu
                        klient.addParagon(p);
                    } else if ("DELETE_PARAGON".equals(type)) {
                        int id = (int) in.readObject(); // Deserializacja obiektu
                        klient.deleteParagon(id);
                    } else if ("DELETE_PRODUCT".equals(type)) {
                        Paragon p = (Paragon) in.readObject(); // Deserializacja obiektu
                        klient.deleteProduct(p);
                    } else {
                        break; // Przerywamy pętlę w przypadku nieznanego typu
                    }
                } catch (EOFException e) {
                    System.out.println("Serwer zamknął połączenie: " + e.getMessage());
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    // Metoda do logowania wiadomości w JTextArea
    private void logMessage(String message) {
        SwingUtilities.invokeLater(() -> textArea.append(message + "\n"));
    }

    public static void main(String[] args) {
        // Uruchomienie GUI w wątku Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            Main clientGUI = new Main();
            clientGUI.setVisible(true);
        });
    }
}
