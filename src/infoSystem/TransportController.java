package infoSystem;

import java.io.*;
import java.util.List;
import java.util.Scanner;

public class TransportController
{
    private static Model model;
    private static View view;
    private static final String FILENAME = "FILE.bin";

    public static void main(String[] args)
    {
        model = new TransportModel(deserializeTransports(FILENAME)); // получаем данные из файла
        view = new ConsoleView();
        String buffer = "1";
        Scanner scanner = new Scanner(System.in);
        while (!buffer.equals("exit"))
        {
            System.out.println("\tСправочная система (подсказки: help)");
            System.out.println("Введите команду:");
            buffer = scanner.nextLine();
            System.out.println("---------------------------------------");
            int posSpace = buffer.indexOf(' ');
            if (posSpace == -1)
                posSpace = buffer.length();
            switch (buffer.substring(0, posSpace)) {
                case "get":
                    try {
                        int index = Integer.parseInt(buffer.substring(posSpace + 1));
                        view.showTransport(index, model);
                    } catch (NumberFormatException | IndexOutOfBoundsException e) {
                        System.out.println("Некорректный индекс");
                    }
                    break;
                case "set":
                    try {
                        int index = Integer.parseInt(buffer.substring(posSpace + 1));
                        Transport transport = model.getTransport(index);
                        if (transport == null)
                            System.out.println("Поезда с таким номером не существует");
                        else
                            changeTransportInfo(transport);
                    } catch (NumberFormatException | IndexOutOfBoundsException e) {
                        System.out.println("Некорректный индекс");
                    }
                    break;
                case "add":
                    model.addTransport(createTransport());
                    System.out.println("Поезд добавлен в систему");
                    break;
                case "rm":
                    try {
                        int index = Integer.parseInt(buffer.substring(posSpace + 1));
                        model.removeTransport(index);
                        System.out.println("Поезд с номером " + index + " удален из системы");
                    } catch (NumberFormatException | IndexOutOfBoundsException e) {
                        System.out.println("Некорректный индекс");
                    }
                    break;
                case "show":
                    System.out.println("Доступные поезда:");
                    view.showAllTransports(model);
                    break;
                case "exit":
                    break;
                case "help":
                    helpInfoSystem();
                    break;
                default:
                    System.out.println("Некорректные данные");
                    break;
            }
            System.out.println("---------------------------------------");
        }

        // сохраняем информацию
        serializeTransports(model.getTransports(), FILENAME);
    }

    // создать транспорт через консоль
    private static Transport createTransport()
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите номер поезда");
        int index = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Введите маршрут (Например: Самара - Москва)");
        String s = scanner.nextLine();
        int pos = s.indexOf('-');
        Route route = Route.builder().departure(s.substring(0, pos - 1)).destination(s.substring(pos + 2)).build();

        System.out.println("Введите время отправления");
        String time1 = scanner.nextLine();

        System.out.println("Введите время в пути");
        String time2 = scanner.nextLine();

        return Train.builder().index(index).route(route).departureTime(time1).travelTime(time2).build();
    }

    // изменить информацию о транспорте через консоль
    private static void changeTransportInfo(Transport transport)
    {
        String buffer = "1";
        Scanner scanner = new Scanner(System.in);
        while (!buffer.equals("exit")) {
            System.out.println(".....................................");
            view.showTransport(transport);
            System.out.println("\tВыберите действие (подсказки: help)");
            buffer = scanner.nextLine();
            int posSpace = buffer.indexOf(' ');
            if (posSpace == -1)
                posSpace = buffer.length();
            System.out.println(".....................................");
            switch (buffer.substring(0, posSpace)) {
                case "index":
                    try {
                        int index = Integer.parseInt(buffer.substring(posSpace + 1));
                        transport.setIndex(index);
                    } catch (NumberFormatException | IndexOutOfBoundsException e) {
                        System.out.println("Некорректный индекс");
                    }
                    break;
                case "route":
                    try {
                        String s = buffer.substring(posSpace + 1);
                        int pos = s.indexOf('-');
                        Route route = Route.builder().departure(s.substring(0, pos - 1))
                                .destination(s.substring(pos + 2)).build();
                        transport.setRoute(route);
                    } catch (NumberFormatException | IndexOutOfBoundsException e) {
                        System.out.println("Некорректный маршрут");
                    }
                    break;
                case "dTime":
                    try {
                        transport.setDepartureTime(buffer.substring(posSpace + 1));
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("Некорректные данные");
                    }
                    break;
                case "tTime":
                    try {
                        transport.setTravelTime(buffer.substring(posSpace + 1));
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("Некорректные данные");
                    }
                    break;
                case "exit":
                    break;
                case "help":
                    helpCreateTransport();
                    break;
                default:
                    System.out.println("Некорректные данные");
                    break;
            }
        }
    }

    // сериализовать модель в файл
    private static void serializeTransports(List<Transport> transports, String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(transports);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    // десериализовать модель из файла
    private static List<Transport> deserializeTransports(String filename)
    {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (List<Transport>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private static void helpInfoSystem() {
        System.out.println("Показать информацию о поезде: get \"index\"\n" +
                "Изменить информацию о поезде: set \"index\"\n" +
                "Добавить поезд: add\n" +
                "Удалить поезд: rm \"index\"\n" +
                "Показать все поезда: show\n" +
                "Выход: exit");
    }

    private static void helpCreateTransport() {
        System.out.println("Изменить номер поезда: index \"index\"\n" +
                "Изменить маршрут: route \"start - finish\"\n" +
                "Изменить время отправления: dTime \"time\"\n" +
                "Изменить путевое время: tTime \"time\"\n" +
                "Выход: exit");
    }
}
