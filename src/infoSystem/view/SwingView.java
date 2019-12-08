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
    private JTable table;

    public SwingView() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Справочная система");
        this.addWindowListener(windowListener());

        /* подключаемся к серверу */
        try {
            clientSocket = new Socket("localhost", PORT);
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
            System.out.println("Клиент подключился к серверу");

            /* Отправим команду, что хотим работать, а не выключить сервер */
            out.writeObject("work");
            out.flush();
        } catch (IOException e) {
            System.out.println("Не удалось подключиться к серверу");
            System.out.println(e.getMessage());
        }

        /* Создаем пустую таблицу */
        table = createJTable();

        /*KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        table.getInputMap().put(enter, "enter");
        table.getActionMap().put("enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("нажали enter");
            }
        });*/

        /* Поместим таблицу на панель */
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(0, 50, 1400, 500);

        /* Создадим кнопку получения данных с сервера */
        JButton updateButton = createButton("Обновить данные с сервера");
        updateButton.addActionListener(updateOrSortButtonPressed("show"));

        /* Добавим панель для кнопки обновления */
        JPanel northPanel = new JPanel();
        northPanel.add(updateButton);
        northPanel.setBounds(100, 100, 300, 100);

        /* Добавим панель для кнопок снизу таблицы */
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new FlowLayout());
        southPanel.setBounds(900, 550, 400, 100);

        /* Кнопка "Добавить" */
        JButton addButton = createButton("Добавить");
        addButton.addActionListener(addButtonPressed());
        southPanel.add(addButton);

        /* Кнопка "Удалить" */
        JButton removeButton = createButton("Удалить");
        removeButton.addActionListener(removeButtonPressed());
        southPanel.add(removeButton);

        /* Кнопка "Сортировать" */
        JButton sortButton = createButton("Сортировать");
        sortButton.addActionListener(updateOrSortButtonPressed("sort"));
        southPanel.add(sortButton);

        /* Добавляем панели во фрейм */
        getContentPane().add(southPanel);
        getContentPane().add(scrollPane);
        getContentPane().add(northPanel);

        /* Устанавливаем размеры окна, делаем его видимым */
        setPreferredSize(new Dimension(1400, 700));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JButton createButton(String text) {
        JButton jButton = new JButton(text);
        jButton.setSize(50, 25);
        jButton.setFont(new Font("Arial", Font.BOLD, 15));
        return jButton;
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

    /* Событие: нажали на кнопку "Удалить" */
    private ActionListener removeButtonPressed() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (table.getSelectedRow() == -1)
                        return;

                    /* Номер удаляемого поезда */
                    int index = (int) table.getValueAt(table.getSelectedRow(), 0);

                    out.writeObject("rm " + index);
                    out.flush();
                    model.removeTransport(index);
                    table.updateUI();
                    System.out.println((String) in.readObject());
                } catch (ClassNotFoundException | IOException ex) {
                    System.out.println("Ошибка\n" + ex.getMessage());
                }
            }
        };
    }

    /* Событие: нажали на кнопку "Добавить" */
    private ActionListener addButtonPressed() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    out.writeObject("addNull");
                    out.flush();
                    Route route = Route.builder().departure("").destination("").build();
                    model.addTransport(Train.builder().index(-1).route(route).departureTime("").travelTime("").build());
                    table.updateUI();
                    System.out.println((String) in.readObject());
                } catch (ClassNotFoundException | IOException ex) {
                    System.out.println("Ошибка\n" + ex.getMessage());
                }
            }
        };
    }

    /* Событие: нажали на кнопку "Обновить" или "Сортировать */
    private ActionListener updateOrSortButtonPressed(String command) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    out.writeObject(command);
                    out.flush();
                    model = new TransportModel((java.util.List<Transport>) in.readObject());
                    table.setModel(model);
                    table.updateUI();
                    System.out.println("Список транспортов, полученный с сервера");
                    (new ConsoleView()).showAllTransports(model);
                } catch (ClassNotFoundException | IOException ex) {
                    System.out.println("Ошибка\n" + ex.getMessage());
                }
            }
        };
    }

    /* Отключаемся от сервера при закрытии окна */
    private WindowListener windowListener() {
        return new WindowListener() {
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
        };
    }
}
