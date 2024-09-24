package org.example;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Klient_Main {
    private Socket tcpSocket;
    JFrame okno;

    JPanel paragonyPanel;
    List<Paragon> paragons_waiting = new ArrayList<>();

    public Klient_Main(Socket tcp) {
        this.tcpSocket = tcp;
    }

    public void start() {
        okno = new JFrame();
        okno.setTitle("Paragony");
        okno.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        okno.setLayout(new GridLayout());
        //okno.setUndecorated(true); // Usunięcie ramki okna
        okno.setExtendedState(JFrame.MAXIMIZED_BOTH); // Maksymalizacja na pełny ekran
        // Tworzymy panel z GridLayout, który ma 6 kolumn i dynamiczną ilość wierszy
        paragonyPanel = new JPanel();
        paragonyPanel.setLayout(new GridLayout(0, 6, 10, 10)); // 0 - dynamiczna liczba wierszy, 6 kolumn


        // Dodajemy scrollowanie
        JScrollPane scrollPane = new JScrollPane(paragonyPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Tylko pionowe przewijanie

        // Dodajemy scrollPane do okna
        okno.add(scrollPane);

        // Wyświetlamy okno
        okno.setVisible(true);
    }

    public void addParagon(Paragon p) {

        paragons_waiting.add(p);
        paragonyPanel.removeAll();

        paragons_waiting.sort(Comparator.comparingInt(Paragon::getId));


        //na okno obok
        for (Paragon paragon : paragons_waiting) {
            JPanel paragonPanel = createParagonPanel(paragon, paragonyPanel);
            paragonyPanel.add(paragonPanel);
        }
        paragonyPanel.revalidate();
        paragonyPanel.repaint();
    }

    public void deleteProduct(int id, Produkt_na_paragonie p)
    {
        for(Paragon paragon : paragons_waiting)
        {
            if(paragon.getId() == id)
            {
                List<Produkt_na_paragonie> products = paragon.getProducts();
                for(Produkt_na_paragonie to_change : products)
                {
                    if(to_change.getNumer_na_paragonie() == p.getNumer_na_paragonie())
                    {
                        LocalTime currentTime = LocalTime.now();

                        // Ustalenie formatu HH:mm
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

                        // Sformatowanie czasu do stringa
                        String formattedTime = currentTime.format(formatter);
                        to_change.setCzas_wydania(formattedTime);
                    }
                }
            }
        }

        paragonyPanel.removeAll();

        for (Paragon paragon : paragons_waiting) {
            JPanel paragonPanel = createParagonPanel(paragon, paragonyPanel);
            paragonyPanel.add(paragonPanel);
        }
        paragonyPanel.revalidate();
        paragonyPanel.repaint();
    }

    public void deleteParagon(int id)
    {
        for(Paragon p : paragons_waiting)
        {
            if(p.getId() == id)
            {
                paragons_waiting.remove(p);
                break;
            }
        }
        paragonyPanel.removeAll();

        //na okno obok
        for (Paragon paragon : paragons_waiting) {
            JPanel paragonPanel = createParagonPanel(paragon, paragonyPanel);
            paragonyPanel.add(paragonPanel);
        }
        paragonyPanel.revalidate();
        paragonyPanel.repaint();

    }
    private JPanel createParagonPanel(Paragon paragon, JPanel parentPanel) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Utworzenie obramowania z większą czcionką
        Font titleFont = new Font("Arial", Font.BOLD, 28);
        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Stolik: " + paragon.getNumer_stolika() + paragon.getKelner());
        border.setTitleFont(titleFont);
        panel.setBorder(border);

        // Panel dla produktów
        JPanel productsPanel = new JPanel();
        productsPanel.setLayout(new GridLayout(paragon.getProducts().size(), 1)); // GridLayout dla produktów

        // Dodanie produktów do panelu
        for (Produkt_na_paragonie produkt : paragon.getProducts()) {
            JPanel productPanel = new JPanel();
            productPanel.setLayout(new BorderLayout());

            // Tworzenie tekstu dla produktu
            String productInfo = produkt.getNazwa() + " - Ilość: " + produkt.getIlosc() + "\n" + produkt.getNotatka();
            JTextArea productTextArea = new JTextArea(productInfo);
            productTextArea.setFont(new Font("Arial", Font.PLAIN, 20));
            productTextArea.setEditable(false);
            productTextArea.setWrapStyleWord(true);
            productTextArea.setLineWrap(true);
            productTextArea.setBackground(Color.WHITE);

            if(produkt.getCzas_wydania() != null)
            {
                productTextArea.setForeground(Color.WHITE);
                productTextArea.setBackground(Color.RED);
                productTextArea.append(produkt.getCzas_wydania());
            }


            // Dodanie komponentów do panelu produktu
            productPanel.add(productTextArea, BorderLayout.CENTER);

            productsPanel.add(productPanel);
        }

        // Dodanie produktów do głównego panelu
        panel.add(productsPanel, BorderLayout.CENTER);


        return panel;
    }
}
