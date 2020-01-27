package infoSystem;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import infoSystem.model.*;
import infoSystem.view.*;

public class ConsoleClient {

    private static Socket clientSocket;
    private static ObjectInputStream in;
    private static ObjectOutputStream out;
    private static final int PORT = 4004;

    public static void main(String[] args) {

        System.out.println("Клиент начал работу");
        View view = new ConsoleView();
        Scanner scanner = new Scanner(System.in);

        try {
            clientSocket = new Socket("localhost", PORT);
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());

            if (isDisableServerCommand(scanner)) {
                return;
            }

            System.out.println("Введите сообщение серверу");
            do {
                String command = scanner.nextLine();
                System.out.println("---------------------------------------");
                out.writeObject(command);
                out.flush();

                if (command.equals("exit")) {
                    break;
                }

                Object serverAnswer = in.readObject();
                if (serverAnswer instanceof String) {
                    System.out.println(serverAnswer);
                } else if (serverAnswer instanceof Transport) {
                    view.showTransport((Transport) serverAnswer);
                } else if (serverAnswer instanceof List<?>) {
                    view.showAllTransports(new XmlTransportModel((List<Transport>) serverAnswer));
                }
                System.out.println("---------------------------------------");
            } while (true);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    /* Отключиться от сервера */
    private static void disconnect() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Клиент закончил работу");
    }

    /* Возможность выключить сервер */
    private static boolean isDisableServerCommand(Scanner scanner) throws IOException {
        System.out.println("Отключить сервер: \"disable\"\n" +
                "Продолжить работу: \"something_else\"");
        String command = scanner.nextLine();
        out.writeObject(command);
        out.flush();
        return command.equals("disable");
    }
}
