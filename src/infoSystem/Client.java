package infoSystem;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import infoSystem.model.*;
import infoSystem.view.*;

public class Client {

    private static Socket clientSocket;
    private static ObjectInputStream in;
    private static ObjectOutputStream out;
    private static final int PORT = 4004;

    public static void main(String[] args) {

        System.out.println("Клиент начал работу");
        View view = new ConsoleView();

        try {
            clientSocket = new Socket("localhost", PORT);
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
            Scanner scanner = new Scanner(System.in);

            System.out.println("Введите сообщение серверу");
            String command;
            do {
                command = scanner.nextLine();
                System.out.println("---------------------------------------");
                out.writeObject(command);
                out.flush();

                if (!command.equals("exit")) {
                    Object answer = in.readObject();
                    if (answer instanceof String) {
                        System.out.println(answer);
                    } else if (answer instanceof Transport) {
                        view.showTransport((Transport) answer);
                    } else if (answer instanceof Model) {
                        view.showAllTransports((Model) answer);
                    }
                }

                System.out.println("---------------------------------------");
            } while (!command.equals("exit"));

            System.out.println("Клиент отсоединился от сервера");

        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            System.out.println("Клиент закончил работу");
        }
    }
}
