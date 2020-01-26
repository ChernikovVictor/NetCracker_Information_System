package infoSystem.view;

import infoSystem.model.*;
import lombok.extern.slf4j.Slf4j;

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

@Slf4j
public class SwingView extends JFrame {

    private Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final int PORT = 4004;

    private Model model = new XmlTransportModel();
    private JTable table;
    private TextField searchTextField;

    /* Очередь команд, ожидающих отправки на сервер */
    private ArrayDeque<String> commandQueue = new ArrayDeque<>();

    /* Список номеров поездов, полученных с сервера (нужен для корректного редактирования номера поезда) */
    private ArrayList<Integer> oldIndexes;

    public SwingView() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Справочная система");
        this.addWindowListener(windowListener());
        this.setLayout(new BorderLayout(10, 10));

        /* подключаемся к серверу */
        try {
            clientSocket = new Socket("localhost", PORT);
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
            log.info("Клиент подключился к серверу");

            /* Отправим команду, что хотим работать, а не выключить сервер */
            out.writeObject("work");
            out.flush();
        } catch (IOException  e) {
            log.error("Не удалось подключиться к серверу", e);
        }

        /* Создаем меню */
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);

        /* Создаем пустую таблицу */
        table = createJTable();

        /* Поместим таблицу на панель */
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(0, 50, 1400, 500);

        /* Создадим кнопку получения данных с сервера */
        JButton updateButton = createButton("Обновить данные с сервера");
        updateButton.addActionListener(updateButtonPressed());

        /* Создадим строку поиска и кнопку для инициации поиска */
        searchTextField = new TextField();
        searchTextField.setPreferredSize(new Dimension(400, 30));
        searchTextField.setFont(new Font("Arial", Font.PLAIN, 15));
        JButton searchButton = createButton("Поиск");
        searchButton.addActionListener(searchButtonPressed());
        JPanel searchPanel = new JPanel();
        searchPanel.add(searchTextField);
        searchPanel.add(searchButton);

        /* Добавим панель для кнопки обновления и панели поиска */
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BorderLayout());
        northPanel.add(searchPanel, BorderLayout.CENTER);
        northPanel.add(updateButton, BorderLayout.LINE_END);

        /* Добавим панель для кнопок снизу таблицы */
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new FlowLayout());

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
        sortButton.addActionListener(sortButtonPressed());
        southPanel.add(sortButton);

        /* Кнопка "Сохранить изменения" */
        JButton saveButton = createButton("Сохранить изменения");
        saveButton.addActionListener(action -> pushCommandsToServer());
        southPanel.add(saveButton);

        /* Добавляем панели во фрейм */
        getContentPane().add(southPanel, BorderLayout.PAGE_END);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(northPanel, BorderLayout.PAGE_START);

        /* Устанавливаем размеры окна, делаем его видимым */
        setPreferredSize(new Dimension(1400, 700));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        /* Пункт меню для выбора моделей на сервере */
        JMenu modelTypeMenu = new JMenu("Загрузить из...");
        modelTypeMenu.setFont(new Font("Arial", Font.PLAIN, 15));
        menuBar.add(modelTypeMenu);

        JRadioButtonMenuItem binaryType = new JRadioButtonMenuItem("Двоичного файла");
        binaryType.setFont(new Font("Arial", Font.PLAIN, 15));
        modelTypeMenu.add(binaryType);
        binaryType.addActionListener(listener -> {
            log.info("Нажали на пункт меню \"Двоичный файл\"");
            try {
                commandQueue.clear();
                out.writeObject("switch bin");
                out.flush();
                log.info((String) in.readObject());
            } catch (IOException | ClassNotFoundException ex) {
                log.error(ex.getMessage(), ex);
            }
        });

        JRadioButtonMenuItem xmlType = new JRadioButtonMenuItem("Файла xml");
        xmlType.setFont(new Font("Arial", Font.PLAIN, 15));
        xmlType.setSelected(true);
        modelTypeMenu.add(xmlType);
        xmlType.addActionListener(listener -> {
            log.info("Нажали на пункт меню \"xml файл\"");
            try {
                commandQueue.clear();
                out.writeObject("switch xml");
                out.flush();
                log.info((String) in.readObject());
            } catch (IOException | ClassNotFoundException ex) {
                log.error(ex.getMessage(), ex);
            }
        });

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(binaryType);
        buttonGroup.add(xmlType);

        /* Пункт меню для загрузки данных на сервер */
        JMenu addTransportsMenu = new JMenu("Добавить на сервер");
        addTransportsMenu.setFont(new Font("Arial", Font.PLAIN, 15));
        menuBar.add(addTransportsMenu);

        JMenuItem fromFile = new JMenuItem("Из файла...");
        fromFile.setFont(new Font("Arial", Font.PLAIN, 15));
        addTransportsMenu.add(fromFile);
        fromFile.addActionListener(listener -> {
            /* Выбираем файл и отправляем на сервер */
            JFileChooser fileChooser = new JFileChooser(new File("src\\main\\resources"));
            fileChooser.setDialogTitle("Выберите файл");
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                try {
                    pushCommandsToServer();
                    out.writeObject("merge");
                    out.writeObject(fileChooser.getSelectedFile());
                    out.flush();
                    JOptionPane.showMessageDialog(null, in.readObject());
                } catch (ClassNotFoundException | IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        });

        return menuBar;
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
                log.info("Нажали на кнопку \"Удалить\"");
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
                log.info("Нажали на кнопку \"Добавить\"");
                commandQueue.addLast("addNull");
                Route route = Route.builder().departure("").destination("").build();
                model.addTransport(Train.builder().index(-1).route(route).departureTime("").travelTime("").build());
                oldIndexes.add(-1);
                table.updateUI();
            }
        };
    }

    /* Событие: нажали на кнопку "Поиск" */
    private ActionListener searchButtonPressed() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("Нажали на кнопку \"Поиск\"");
                if (searchTextField.getText().equals("")) {
                    return;
                }
                try {
                    pushCommandsToServer();
                    out.writeObject("search " + searchTextField.getText());
                    out.flush();
                    getTransportsFromServer();
                } catch (ClassNotFoundException | IOException ex) {
                    log.error("Ошибка", ex);
                }
            }
        };
    }

    /* Событие: нажали на кнопку "Обновить данные с сервера" */
    private ActionListener updateButtonPressed() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("Нажали на кнопку \"Обновить данные с сервера\"");
                try {
                    commandQueue.clear();
                    out.writeObject("show");
                    out.flush();
                    getTransportsFromServer();
                } catch (ClassNotFoundException | IOException ex) {
                    log.error("Ошибка", ex);
                }
            }
        };
    }

    /* Событие: нажали на кнопку "Сортировать" */
    private ActionListener sortButtonPressed() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("Нажали на кнопку \"Обновить данные с сервера\"");
                try {
                    pushCommandsToServer();
                    out.writeObject("sort");
                    out.flush();
                    getTransportsFromServer();
                } catch (ClassNotFoundException | IOException ex) {
                    log.error("Ошибка", ex);
                }
            }
        };
    }

    /* Получаем список с сервера, собираем модель */
    private void getTransportsFromServer() throws IOException, ClassNotFoundException {
        java.util.List<Transport> transportList = (java.util.List<Transport>) in.readObject();
        oldIndexes = new ArrayList<>();
        for (Transport transport : transportList) {
            oldIndexes.add(transport.getIndex());
        }
        model = new XmlTransportModel(transportList);
        model.addTableModelListener(setTransportListener());

        table.setModel(model);
        table.updateUI();
        log.info("Список транспортов, полученный с сервера\n{}", (new ConsoleView()).getAllTransportsInfo(model));
    }

    /* Сохранить изменения на сервер */
    private void pushCommandsToServer() {
        while (!commandQueue.isEmpty()) {
            try {
                String command = commandQueue.removeFirst();
                log.info("-------------------------------------------------------");
                log.info("На сервер отправлена команда: {}", command);
                out.writeObject(command);
                out.flush();
                Object answer = in.readObject();
                log.info("Ответ сервера: {}", answer);
            } catch (IOException | ClassNotFoundException ex) {
                log.error(ex.getMessage(), ex);
            }
        }
        log.info("-------------------------------------------------------");
    }

    /* Событие: изменили значение ячейки таблицы */
    private TableModelListener setTransportListener() {
        return new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {

                int row = table.getEditingRow();
                int column = table.getEditingColumn();
                log.info("Table changed at ({},{})", row, column);

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
                    log.error(ex.getMessage(), ex);
                } finally {
                    log.info("Клиент закончил работу");
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
