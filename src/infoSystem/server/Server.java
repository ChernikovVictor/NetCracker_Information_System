package infoSystem.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import infoSystem.SaveData;
import infoSystem.TransportController;
import infoSystem.model.*;
import infoSystem.view.ConsoleView;

public class Server {

    private static LinkedList<ServerThread> serverThreads = new LinkedList<>(); // список всех нитей
    private static ServerSocket server;
    private static final int PORT = 4004;
    private static final String FILENAME = "FILE.xml";

    public static void main(String[] args) {
        Model model = null;
        TransportController controller;
        try {
            server = new ServerSocket(PORT);
            System.out.println("Сервер запущен");

            /* Получаем данные из файла */
            model = new TransportModel(SaveData.getFromXML(FILENAME));
            controller = new TransportController(model);
            System.out.println("Считали из файла список транспортов");
            (new ConsoleView()).showAllTransports(model);

            while (true) {
                Socket clientSocket = server.accept();
                System.out.println("К серверу подключился клиент");
                serverThreads.add(new ServerThread(clientSocket, model, controller));
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (DisableServerException e) {
            System.out.println("Получена команда завершения работы сервера");
        } finally {
            System.out.println("Сервер завершил работу");
            SaveData.saveAsXML(model.getTransports(), FILENAME);
            try {
                server.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}