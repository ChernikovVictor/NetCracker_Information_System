package infoSystem.server;

import infoSystem.util.BinaryLoader;
import infoSystem.TransportController;
import infoSystem.util.XmlLoader;
import infoSystem.model.*;
import infoSystem.view.ConsoleView;

import java.io.*;
import java.net.Socket;
import java.util.List;

/* Поток для работы с очередным клиентом */
public class ServerThread extends Thread {

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private TransportController controller = null;
    private Model model = null;

    private final TransportController binaryController;
    private final TransportController xmlController;

    public ServerThread(Socket socket, TransportController binaryController, TransportController xmlController)
            throws IOException, ClassNotFoundException, DisableServerException {
        this.socket = socket;
        this.binaryController = binaryController;
        this.xmlController = xmlController;
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        String command = (String) in.readObject();
        System.out.println("Получена команда: " + command);
        if (command.equals("disable")) {
            in.close();
            out.close();
            socket.close();
            throw new DisableServerException();
        }

        start();
    }

    @Override
    public void run() {
        String command;
        try {
            do {
                try {
                    command = (String) in.readObject();
                    System.out.println("Получена команда " + command);
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println(e.getMessage());
                    out.writeObject("Error");
                    out.flush();
                    continue;
                }

                int posSpace = command.indexOf(' ');
                if (posSpace == -1)
                    posSpace = command.length();
                switch (command.substring(0, posSpace)) {
                    case "switch":
                        String modelType = command.substring(posSpace + 1);
                        controller = (modelType.equals("bin")) ? binaryController : xmlController;
                        model = controller.getModel();
                        System.out.println("Файл данных изменен");
                        out.writeObject("Файл данных изменен");
                        break;
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
                    case "addNull":
                        Route route = Route.builder().departure("").destination("").build();
                        controller.addTransport(Train.builder().index(-1).route(route).departureTime("").travelTime("").build());
                        out.writeObject("Поезд без информации добавлен в систему");
                        System.out.println("Поезд без информации добавлен в систему");
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
                    case "sort":
                        controller.sortByDepartureTime();
                        out.writeObject(model.getTransports());
                        System.out.println("Список, переданный клиенту:");
                        (new ConsoleView()).showAllTransports(model);
                        break;
                    case "search":
                        String regex = command.substring(posSpace + 1);
                        Model patternModel = controller.getModelByPattern(regex);
                        out.writeObject(patternModel.getTransports());
                        System.out.println("Список, переданный клиенту:");
                        (new ConsoleView()).showAllTransports(patternModel);
                        break;
                    case "merge":
                        File file;
                        try {
                            file = (File) in.readObject();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                            break;
                        }

                        String extension = file.getPath().substring(file.getPath().lastIndexOf("."));
                        List<Transport> transportList = null;
                        switch (extension) {
                            case ".bin":
                                transportList = BinaryLoader.deserializeList(file);
                                break;
                            case ".xml":
                                transportList = XmlLoader.getFromXML(file);
                                break;
                        }
                        controller.merge(transportList);

                        String answer = (transportList == null) ? "Считать не удалось. Проверьте содержимое файла"
                                                                : "Данные успешно добавлены на сервер";
                        System.out.println(answer);
                        try {
                            out.writeObject(answer);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

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
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // изменить информацию о транспорте
    private void changeTransportInfo(Transport transport) throws IOException, ClassNotFoundException {
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
    private Transport createTransport() {
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

    private String helpInfoSystem() {
        return "Показать информацию о поезде: get \"index\"\n" +
                "Изменить информацию о поезде: set \"index\"\n" +
                "Добавить поезд: add\n" +
                "Добавить поезд без информации: addNull\n" +
                "Удалить поезд: rm \"index\"\n" +
                "Показать все поезда: show\n" +
                "Сортировать по времени отправления: sort\n" +
                "Поиск по шаблону: search \"regex\"\n" +
                "Выбрать файл для загрузки данных: switch \"bin\" switch \"xml\"\n" +
                "Выход: exit";
    }

    private String helpChangeTransport() {
        return "Изменить номер поезда: index \"index\"\n" +
                "Информация о транспорте: get\n" +
                "Изменить маршрут: route \"start - finish\"\n" +
                "Изменить время отправления: dTime \"time\"\n" +
                "Изменить путевое время: tTime \"time\"\n" +
                "Выход: return";
    }
}
