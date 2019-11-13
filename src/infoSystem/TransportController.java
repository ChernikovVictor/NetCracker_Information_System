package infoSystem;

import java.io.*;
import java.util.Scanner;

public class TransportController
{
    private static Model model;
    private static View view;
    private static final String FILENAME = "FILE.bin";

    public static void main(String[] args)
    {
        model = deserializeModel(FILENAME); // получаем данные из файла
        view = new ConsoleView();
        String buffer = "1";
        Scanner scanner = new Scanner(System.in);
        while (!buffer.equals("0"))
        {
            System.out.println("\tСправочная система\n" +
                    "1. Показать информацию о поезде\n" +
                    "2. Изменить информацию о поезде\n" +
                    "3. Добавить поезд\n" +
                    "4. Удалить поезд\n" +
                    "5. Показать все поезда\n" +
                    "0. Выход");
            System.out.print("Выберите действие: ");
            buffer = scanner.nextLine();
            System.out.println("---------------------------------------");
            switch (buffer) {
                case "1":
                    System.out.println("Введите номер поезда");
                    view.showTransport(scanner.nextInt(), model);
                    scanner.nextLine();
                    break;
                case "2":
                    System.out.println("Введите номер поезда");
                    Transport transport = model.getTransport(scanner.nextInt());
                    scanner.nextLine();
                    if (transport == null)
                        System.out.println("Поезда с таким номером не существует");
                    else
                        changeTransportInfo(transport);
                    break;
                case "3":
                    model.addTransport(createTransport());
                    System.out.println("Поезд добавлен в систему");
                    break;
                case "4":
                    System.out.println("Введите номер поезда");
                    model.removeTransport(scanner.nextInt());
                    scanner.nextLine();
                    break;
                case "5":
                    System.out.println("Доступные поезда:");
                    view.showAllTransports(model);
                    break;
                case "0":
                    break;
                default:
                    System.out.println("Некорректные данные");
                    break;
            }
            System.out.println("---------------------------------------");
        }

        // сохраняем информацию
        serializeModel(model, FILENAME);
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
        while (!buffer.equals("0")) {
            System.out.println(".....................................");
            view.showTransport(transport);
            System.out.println("\tВыберите действие:\n" +
                    "1. Изменить номер поезда\n" +
                    "2. Изменить маршрут\n" +
                    "3. Изменить время отправления\n" +
                    "4. Изменить путевое время\n" +
                    "0. Выход");
            buffer = scanner.nextLine();
            System.out.println(".....................................");
            switch (buffer) {
                case "1":
                    System.out.println("Введите новый номер поезда");
                    transport.setIndex(scanner.nextInt());
                    scanner.nextLine();
                    break;
                case "2":
                    Route route = Route.builder().build();
                    System.out.println("Введите новую начальную станцию");
                    route.setDeparture(scanner.nextLine());
                    System.out.println("Введите новую конечную станцию");
                    route.setDestination(scanner.nextLine());
                    transport.setRoute(route);
                    break;
                case "3":
                    System.out.println("Введите новое время отправления");
                    transport.setDepartureTime(scanner.nextLine());
                    break;
                case "4":
                    System.out.println("Введите новое время в пути");
                    transport.setTravelTime(scanner.nextLine());
                    break;
                case "0":
                    break;
                default:
                    System.out.println("Некорректные данные");
                    break;
            }
        }
    }

    // сериализовать модель в файл
    private static void serializeModel(Model model, String filename)
    {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename)))
        {
            out.writeObject(model);
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }

    // десериализовать модель из файла
    private static Model deserializeModel(String filename)
    {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename)))
        {
            return (Model) in.readObject();
        }
        catch (IOException | ClassNotFoundException e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
