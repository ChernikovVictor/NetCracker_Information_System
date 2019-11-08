package infoSystem;

import java.util.Scanner;

public class TransportController
{
    private static Transport transport;
    private static View view;

    public static void main(String[] args)
    {
        transport = new Train();
        view = new ConsoleView();
        String buffer = "1";
        Scanner scanner = new Scanner(System.in);
        while (!buffer.equals("0"))
        {
            System.out.println("Справочная система\n" +
                    "1. Показать информацию о поезде\n" +
                    "2. Изменить номер поезда\n" +
                    "3. Изменить маршрут\n" +
                    "4. Изменить время отправления\n" +
                    "5. Изменить путевое время\n" +
                    "0. Выход");
            buffer = scanner.nextLine();
            switch (buffer)
            {
                case "1":
                    view.showTransport(transport);
                    break;
                case "2":
                    transport.setIndex(scanner.nextInt());
                    scanner.nextLine();
                    break;
                case "3":
                    Route route = new Route();
                    System.out.println("Введите начальную станцию");
                    route.setDeparture(scanner.nextLine());
                    System.out.println("Введите конечную станцию");
                    route.setDestination(scanner.nextLine());
                    transport.setRoute(route);
                    break;
                case "4":
                    transport.setDepartureTime(scanner.nextLine());
                    break;
                case "5":
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
}
