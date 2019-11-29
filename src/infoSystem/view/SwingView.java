package infoSystem.view;

import infoSystem.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class SwingView extends JFrame {

    private Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final int PORT = 4004;

    private Model model = new TransportModel();

    public SwingView() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Справочная система");

        /* подключаемся к серверу */
        try {
            clientSocket = new Socket("localhost", PORT);
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
            System.out.println("Клиент подключился к серверу");
        } catch (IOException e) {
            System.out.println("Не удалось подключиться к серверу");
            System.out.println(e.getMessage());
        }

        /* Добавим пустую таблицу */
        JTable table = createJTable();

        /* Поместим таблицу на панель */
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(0, 50, 1400, 500);

        /* Создадим кнопку получения данных с сервера */
        JButton updateButton = new JButton("Обновить данные с сервера");
        updateButton.setSize(50, 25);
        updateButton.setFont(new Font( "Arial" , Font.BOLD, 15 ) );
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    out.writeObject("show");
                    out.flush();
                    model = (Model) in.readObject();
                    table.setModel(model);
                    System.out.println("Список транспортов, полученный с сервера");
                    (new ConsoleView()).showAllTransports(model);
                    repaint();
                } catch (ClassNotFoundException | IOException ex) {
                    System.out.println("Ошибка\n" + ex.getMessage());
                }
            }
        });

        /* Добавим панель для кнопки */
        JPanel panel = new JPanel();
        panel.add(updateButton);
        panel.setBounds(100, 100, 300, 100);

        /* Отключаемся от сервера при закрытии окна */
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) { }

            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    out.writeObject("exit");
                    out.flush();
                    in.close();
                    out.close();
                    clientSocket.close();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                } finally {
                    System.out.println("Клиент закончил работу");
                }
            }

            @Override
            public void windowClosed(WindowEvent e) { }

            @Override
            public void windowIconified(WindowEvent e) { }

            @Override
            public void windowDeiconified(WindowEvent e) { }

            @Override
            public void windowActivated(WindowEvent e) { }

            @Override
            public void windowDeactivated(WindowEvent e) { }
        });

        /* Добавляем панели во фрейм */
        getContentPane().add(scrollPane);
        getContentPane().add(panel);

        /* Устанавливаем размеры окна, делаем его видимым */
        setPreferredSize(new Dimension(1400, 800));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JTable createJTable() {

        JTable table = new JTable(model);

        /* Установим центровку и шрифт */
        DefaultTableCellRenderer centerRend = new DefaultTableCellRenderer();
        centerRend.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, centerRend);
        table.setDefaultRenderer(Integer.class, centerRend);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font( "Arial" , Font.BOLD, 15 ));
        table.setFont(new Font("Serif", Font.PLAIN, 25));

        return table;
    }
}
