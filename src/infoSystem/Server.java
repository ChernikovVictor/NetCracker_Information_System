package infoSystem;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import infoSystem.model.*;
import infoSystem.view.ConsoleView;

public class Server {

    private static Socket clientSocket;
    private static ServerSocket server;
    private static ObjectInputStream in;
    private static ObjectOutputStream out;
    private static final int PORT = 4004;

    private static final String FILENAME = "FILE.bin";

    public static void main(String[] args) {
        Model model = null;
        TransportController controller;
        try {
            server = new ServerSocket(PORT);
            System.out.println("Сервер запущен");

            /* Получаем данные из файла */
            model = new TransportModel(SaveData.deserializeTransports(FILENAME));
            controller = new TransportController(model);
            System.out.println("Считали из файла список транспортов");
            (new ConsoleView()).showAllTransports(model);

            clientSocket = server.accept();

            try {
                System.out.println("К серверу подключился клиент");

                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());

                String command;
                do {
                    try {
                        command = (String) in.readObject();
                        System.out.println("Получена команда " + command);
                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println(e.getMessage());
                        out.writeObject("Error");
                        out.flush();
                        command = "";
                        continue;
                    }

                    int posSpace = command.indexOf(' ');
                    if (posSpace == -1)
                        posSpace = command.length();
                    switch (command.substring(0, posSpace)) {
                        case "get":
                            try {
                                int index = Integer.parseInt(command.substring(posSpace + 1));
                                Transport transport = controller.getTransport(index);
                                if (transport == null) {
                                    out.writeObject("Поезда с таким номером не существует");
                                } else {
                                    System.out.println("Ответ клиенту:");
                                    (new ConsoleView()).showTransport(transport);
                                    out.writeObject(transport);
                                }
                            } catch (ClassCastException | IndexOutOfBoundsException e) {
                                out.writeObject("Некорректный индекс");
                            }
                            break;
                        case "set":
                            try {
                                int index = Integer.parseInt(command.substring(posSpace + 1));
                                Transport transport = controller.getTransport(index);
                                if (transport == null) {
                                    out.writeObject("Поезда с таким номером не существует");
                                } else {
                                    System.out.println("Поезд до изменения");
                                    (new ConsoleView()).showTransport(transport);
                                    changeTransportInfo(transport);
                                    System.out.println("Поезд после изменения");
                                    (new ConsoleView()).showTransport(transport);
                                    out.writeObject("Поезд изменен");
                                }
                            } catch (ClassCastException | IndexOutOfBoundsException e) {
                                out.writeObject("Некорректный индекс");
                            } catch (ClassNotFoundException e) {
                                out.writeObject("Некорректные данные");
                            }
                            break;
                        case "add":
                            Transport transport = createTransport();
                            if (transport != null) {
                                controller.addTransport(transport);
                                out.writeObject("Поезд добавлен в систему");
                            } else {
                                out.writeObject("Ошибка. Поезд не добавлен в систему");
                            }
                            break;
                        case "rm":
                            try {
                                int index = Integer.parseInt(command.substring(posSpace + 1));
                                if (controller.getTransport(index) == null) {
                                    out.writeObject("Поезда с таким номером не существует");
                                } else {
                                    controller.removeTransport(index);
                                    out.writeObject("Поезд с номером " + index + " удален из системы");
                                }
                            } catch (ClassCastException | IndexOutOfBoundsException e) {
                                out.writeObject("Некорректный индекс");
                            }
                            break;
                        case "show":
                            out.writeObject(model.getTransports());
                            System.out.println("Список, переданный клиенту:");
                            (new ConsoleView()).showAllTransports(model);
                            break;
                        case "exit":
                            break;
                        case "help":
                            out.writeObject(helpInfoSystem());
                            break;
                        default:
                            out.writeObject("Некорректные данные");
                            break;
                    }
                    if (command.equals("exit"))
                        break;
                    out.flush();
                    out.reset();    // удалить хеши объектов, переданных в поток ранее
                } while (true);
            } finally {
                System.out.println("Клиент отсоединился");
                in.close();
                out.close();
                clientSocket.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            System.out.println("Сервер завершил работу");
            SaveData.serializeTransports(model.getTransports(), FILENAME);
            try {
                server.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // изменить информацию о транспорте
    private static void changeTransportInfo(Transport transport) throws IOException, ClassNotFoundException {
        String buffer;
        (new ConsoleView()).showTransport(transport);
        out.writeObject(transport);
        out.flush();
        out.reset();    // удалить хеши объектов, переданных в поток ранее
        do {
            buffer = (String) in.readObject();
            System.out.println("Получена команда " + buffer);
            int posSpace = buffer.indexOf(' ');
            if (posSpace == -1)
                posSpace = buffer.length();
            switch (buffer.substring(0, posSpace)) {
                case "get":
                    out.writeObject(transport);
                    break;
                case "index":
                    try {
                        int index = Integer.parseInt(buffer.substring(posSpace + 1));
                        transport.setIndex(index);
                        out.writeObject("Номер изменен");
                    } catch (ClassCastException | IndexOutOfBoundsException e) {
                        out.writeObject("Некорректный индекс");
                    }
                    break;
                case "route":
                    try {
                        String s = buffer.substring(posSpace + 1);
                        int pos = s.indexOf('-');
                        Route route = Route.builder().departure(s.substring(0, pos - 1))
                                .destination(s.substring(pos + 2)).build();
                        transport.setRoute(route);
                        out.writeObject("Маршрут изменен");
                    } catch (ClassCastException | IndexOutOfBoundsException e) {
                        out.writeObject("Некорректный маршрут");
                    }
                    break;
                case "dTime":
                    try {
                        transport.setDepartureTime(buffer.substring(posSpace + 1));
                        out.writeObject("Время отпрапления изменено");
                    } catch (IndexOutOfBoundsException e) {
                        out.writeObject("Некорректные данные");
                    }
                    break;
                case "tTime":
                    try {
                        transport.setTravelTime(buffer.substring(posSpace + 1));
                        out.writeObject("Время в пути изменено");
                    } catch (IndexOutOfBoundsException e) {
                        out.writeObject("Некорректные данные");
                    }
                    break;
                case "return":
                    return;
                case "help":
                    out.writeObject(helpChangeTransport());
                    break;
                default:
                    out.writeObject("Некорректные данные");
                    break;
            }
            out.flush();
            out.reset();    // удалить хеши объектов, переданных в поток ранее
        } while (!buffer.equals("return"));
    }

    // создать новый транспорт
    private static Transport createTransport() {
        try {
            out.writeObject("Введите номер поезда");
            out.flush();
            int index = (Integer.parseInt((String) in.readObject()));

            out.writeObject("Введите маршрут (Например: Самара - Москва)");
            out.flush();
            String s = (String) in.readObject();
            int pos = s.indexOf('-');
            Route route = Route.builder().departure(s.substring(0, pos - 1)).destination(s.substring(pos + 2)).build();

            out.writeObject("Введите время отправления");
            out.flush();
            String time1 = (String) in.readObject();

            out.writeObject("Введите время в пути");
            out.flush();
            String time2 = (String) in.readObject();

            return Train.builder().index(index).route(route).departureTime(time1).travelTime(time2).build();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            System.out.println("Поезд не добавлен\n" + e.getMessage());
            return null;
        }
    }

    private static String helpInfoSystem() {
        return "Показать информацию о поезде: get \"index\"\n" +
                "Изменить информацию о поезде: set \"index\"\n" +
                "Добавить поезд: add\n" +
                "Удалить поезд: rm \"index\"\n" +
                "Показать все поезда: show\n" +
                "Выход: exit";
    }

    private static String helpChangeTransport() {
        return "Изменить номер поезда: index \"index\"\n" +
                "Информация о транспорте: get\n" +
                "Изменить маршрут: route \"start - finish\"\n" +
                "Изменить время отправления: dTime \"time\"\n" +
                "Изменить путевое время: tTime \"time\"\n" +
                "Выход: return";
    }
}
