package infoSystem.server;

import infoSystem.util.BinaryLoader;
import infoSystem.TransportController;
import infoSystem.util.XmlLoader;
import infoSystem.model.*;
import infoSystem.view.ConsoleView;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.io.*;
import java.net.Socket;
import java.util.List;

/* Поток для работы с очередным клиентом */
@Slf4j
public class ServerThread extends Thread {

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private TransportController controller;
    private Model model;

    private static final String FILENAME_LOG = Server.class.getSimpleName();

    private final TransportController binaryController;
    private final TransportController xmlController;

    public ServerThread(Socket socket, TransportController binaryController, TransportController xmlController)
            throws IOException, ClassNotFoundException, DisableServerException {

        this.socket = socket;
        this.binaryController = binaryController;
        this.xmlController = xmlController;
        controller = xmlController;
        model = controller.getModel();
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        String command = (String) in.readObject();
        log.info("Получена команда: {}", command);
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
        setLogFileName();   // определям файл для логов
        try {
            do {
                try {
                    command = (String) in.readObject();
                    log.info("Получена команда: {}", command);
                } catch (IOException | ClassNotFoundException e) {
                    log.error(e.getMessage(), e);
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
                        log.info("Файл данных изменен");
                        out.writeObject("Файл данных изменен");
                        break;
                    case "get":
                        try {
                            int index = Integer.parseInt(command.substring(posSpace + 1));
                            Transport transport = controller.getTransport(index);
                            if (transport == null) {
                                log.info("Ответ клиенту: Поезда с таким номером не существует");
                                out.writeObject("Поезда с таким номером не существует");
                            } else {
                                log.info("Ответ клиенту: {}", (new ConsoleView()).getTransportInfo(transport));
                                out.writeObject(transport);
                            }
                        } catch (ClassCastException | IndexOutOfBoundsException e) {
                            log.error("Ответ клиенту: Некорректный индекс");
                            out.writeObject("Некорректный индекс");
                        }
                        break;
                    case "set":
                        try {
                            int index = Integer.parseInt(command.substring(posSpace + 1));
                            Transport transport = controller.getTransport(index);
                            if (transport == null) {
                                log.info("Ответ клиенту: Поезда с таким номером не существует");
                                out.writeObject("Поезда с таким номером не существует");
                            } else {
                                log.info("Поезд до изменения\n{}", (new ConsoleView()).getTransportInfo(transport));
                                changeTransportInfo(transport);
                                log.info("Поезд после изменения\n{}", (new ConsoleView()).getTransportInfo(transport));
                                out.writeObject("Поезд изменен");
                            }
                        } catch (ClassCastException | IndexOutOfBoundsException e) {
                            log.error("Ответ клиенту: Некорректный индекс");
                            out.writeObject("Некорректный индекс");
                        } catch (ClassNotFoundException e) {
                            log.error("Ответ клиенту: Некорректные данные");
                            out.writeObject("Некорректные данные");
                        }
                        break;
                    case "add":
                        Transport transport = createTransport();
                        if (transport != null) {
                            controller.addTransport(transport);
                            log.info("Ответ клиенту: Поезд добавлен в систему");
                            out.writeObject("Поезд добавлен в систему");
                        } else {
                            log.info("Ответ клиенту: Ошибка. Поезд не добавлен в систему");
                            out.writeObject("Ошибка. Поезд не добавлен в систему");
                        }
                        break;
                    case "addNull":
                        Route route = Route.builder().departure("").destination("").build();
                        controller.addTransport(Train.builder().index(-1).route(route).departureTime("").travelTime("").build());
                        out.writeObject("Поезд без информации добавлен в систему");
                        log.info("Ответ клиенту: Поезд без информации добавлен в систему");
                        break;
                    case "rm":
                        try {
                            int index = Integer.parseInt(command.substring(posSpace + 1));
                            if (controller.getTransport(index) == null) {
                                out.writeObject("Поезда с таким номером не существует");
                                log.info("Ответ клиенту: Поезда с таким номером не существует");
                            } else {
                                controller.removeTransport(index);
                                out.writeObject("Поезд с номером " + index + " удален из системы");
                                log.info("Ответ клиенту: Поезд с номером {} удален из системы", index);
                            }
                        } catch (ClassCastException | IndexOutOfBoundsException e) {
                            out.writeObject("Некорректный индекс");
                            log.error("Ответ клиенту: Некорректный индекс");
                        }
                        break;
                    case "show":
                        out.writeObject(model.getTransports());
                        log.info("Список, переданный клиенту:\n{}", (new ConsoleView()).getAllTransportsInfo(model));
                        break;
                    case "sort":
                        controller.sortByDepartureTime();
                        out.writeObject(model.getTransports());
                        log.info("Список, переданный клиенту:\n{}", (new ConsoleView()).getAllTransportsInfo(model));
                        break;
                    case "search":
                        String regex = command.substring(posSpace + 1);
                        Model patternModel = controller.getModelByPattern(regex);
                        out.writeObject(patternModel.getTransports());
                        log.info("Список, переданный клиенту:\n{}", (new ConsoleView()).getAllTransportsInfo(patternModel));
                        break;
                    case "merge":
                        File file;
                        try {
                            file = (File) in.readObject();
                        } catch (ClassNotFoundException e) {
                            log.error(e.getMessage(), e);
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
                        log.info("Ответ клиенту: {}", answer);
                        try {
                            out.writeObject(answer);
                        } catch (IOException e) {
                            log.error(e.getMessage(), e);
                        }

                        break;
                    case "exit":
                        break;
                    case "help":
                        out.writeObject(helpInfoSystem());
                        break;
                    default:
                        out.writeObject("Некорректные данные");
                        log.info("Некорректные данные");
                        break;
                }
                if (command.equals("exit"))
                    break;
                out.flush();
                out.reset();    // удалить хеши объектов, переданных в поток ранее
            } while (true);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /* изменить информацию о транспорте */
    private void changeTransportInfo(Transport transport) throws IOException, ClassNotFoundException {
        String buffer;
        out.writeObject(transport);
        out.flush();
        out.reset();    // удалить хеши объектов, переданных в поток ранее
        do {
            buffer = (String) in.readObject();
            log.info("Получена команда {}", buffer);
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
                        log.info("Номер изменен");
                    } catch (ClassCastException | IndexOutOfBoundsException e) {
                        out.writeObject("Некорректный индекс");
                        log.error("Некорректный индекс");
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
                        log.info("Маршрут изменен");
                    } catch (ClassCastException | IndexOutOfBoundsException e) {
                        out.writeObject("Некорректный маршрут");
                        log.error("Некорректный маршрут");
                    }
                    break;
                case "dTime":
                    try {
                        transport.setDepartureTime(buffer.substring(posSpace + 1));
                        out.writeObject("Время отправления изменено");
                        log.info("Время отправления изменено");
                    } catch (IndexOutOfBoundsException e) {
                        out.writeObject("Некорректные данные");
                        log.error("Некорректные данные");
                    }
                    break;
                case "tTime":
                    try {
                        transport.setTravelTime(buffer.substring(posSpace + 1));
                        out.writeObject("Время в пути изменено");
                        log.info("Время отправления изменено");
                    } catch (IndexOutOfBoundsException e) {
                        out.writeObject("Некорректные данные");
                        log.error("Некорректные данные");
                    }
                    break;
                case "return":
                    return;
                case "help":
                    out.writeObject(helpChangeTransport());
                    break;
                default:
                    out.writeObject("Некорректные данные");
                    log.error("Некорректные данные");
                    break;
            }
            out.flush();
            out.reset();    // удалить хеши объектов, переданных в поток ранее
        } while (!buffer.equals("return"));
    }

    /* создать новый транспорт */
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
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /* справка по командам */
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

    /* справка по изменению данных о транспорте */
    private String helpChangeTransport() {
        return "Изменить номер поезда: index \"index\"\n" +
                "Информация о транспорте: get\n" +
                "Изменить маршрут: route \"start - finish\"\n" +
                "Изменить время отправления: dTime \"time\"\n" +
                "Изменить путевое время: tTime \"time\"\n" +
                "Выход: return";
    }

    /* Определить файл логирования для текущего потока и производных от него потоков */
    private static void setLogFileName() {
        MDC.clear();
        MDC.put("logFileName", FILENAME_LOG);
    }
}
