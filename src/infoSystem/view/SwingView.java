package infoSystem.view;

import infoSystem.model.*;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class SwingView extends JFrame {

    private Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final int PORT = 4004;

    private Model model = new TransportModel();
    private JTable table;

    /* Очередь команд, ожидающих отправки на сервер */
    private ArrayDeque<String> commandQueue = new ArrayDeque<>();

    /* Список номеров поездов, полученных с сервера (нужен для корректного редактирования номера поезда) */
    private ArrayList<Integer> oldIndexes;

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

        /* Кнопка "Сохранить изменения" */
        JButton saveButton = createButton("Сохранить изменения");
        saveButton.addActionListener(action -> pushCommandsToServer());
        southPanel.add(saveButton);

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
                if (table.getSelectedRow() == -1)
                    return;
                int index = (int) table.getValueAt(table.getSelectedRow(), 0);
                commandQueue.addLast("rm " + index);
                model.removeTransport(index);
                oldIndexes.remove((Object) index);
                table.updateUI();
            }
        };
    }

    /* Событие: нажали на кнопку "Добавить" */
    private ActionListener addButtonPressed() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                commandQueue.addLast("addNull");
                Route route = Route.builder().departure("").destination("").build();
                model.addTransport(Train.builder().index(-1).route(route).departureTime("").travelTime("").build());
                oldIndexes.add(-1);
                table.updateUI();
            }
        };
    }

    /* Событие: нажали на кнопку "Обновить" или "Сортировать */
    private ActionListener updateOrSortButtonPressed(String command) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    System.out.println("На сервер отправлена команда: " + command);

                    /* Если обнавляем данные, то не сохраняем внесенные ранее изменения */
                    if (command.equals("show"))
                        commandQueue.clear();

                    pushCommandsToServer();
                    out.writeObject(command);
                    out.flush();

                    /* Получаем список с сервера, собираем модель */
                    java.util.List<Transport> transportList = (java.util.List<Transport>) in.readObject();
                    oldIndexes = new ArrayList<>();
                    for (Transport transport : transportList) {
                        oldIndexes.add(transport.getIndex());
                    }
                    model = new TransportModel(transportList);
                    model.addTableModelListener(setTransportListener());

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

    /* Сохранить изменения на сервер */
    private void pushCommandsToServer() {
        while (!commandQueue.isEmpty()) {
            try {
                String command = commandQueue.removeFirst();
                System.out.println("-------------------------------------------------------");
                System.out.println("На сервер отправлена команда: " + command);
                out.writeObject(command);
                out.flush();
                Object answer = in.readObject();
                System.out.println("Ответ сервера: " + answer);
            } catch (IOException | ClassNotFoundException ex) {
                System.out.println(ex.getMessage());
            }
        }
        System.out.println("-------------------------------------------------------");
    }

    /* Событие: изменили значение ячейки таблицы */
    private TableModelListener setTransportListener() {
        return new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {

                int row = table.getEditingRow();
                int column = table.getEditingColumn();
                System.out.println(String.format("Table changed at (%d,%d)", row, column));

                /* запрос к серверу на изменение транспорта */
                int index = oldIndexes.get(row);
                commandQueue.addLast("set " + index);

                /* Запрос серверу, что конкретно меняем в выбранном транспорте */
                switch (column) {
                    case 0:
                        commandQueue.addLast("index " + table.getValueAt(row, column));
                        oldIndexes.set(row, (Integer) table.getValueAt(row, column));
                        break;
                    case 1:
                        commandQueue.addLast("route " + table.getValueAt(row, column));
                        break;
                    case 2:
                        commandQueue.addLast("dTime " + table.getValueAt(row, column));
                        break;
                    case 3:
                        commandQueue.addLast("tTime " + table.getValueAt(row, column));
                        break;
                }

                /* Запрос серверу на выход из редактирования транспорта */
                commandQueue.addLast("return");
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