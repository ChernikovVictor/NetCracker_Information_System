package infoSystem.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import infoSystem.TransportController;
import infoSystem.model.*;
import infoSystem.view.ConsoleView;

public class Server {

    private static LinkedList<ServerThread> serverThreads = new LinkedList<>(); // список всех нитей
    private static ServerSocket server;
    private static final int PORT = 4004;
    private static final String FILENAME_XML = "src\\main\\resources\\FILE.xml";
    private static final String FILENAME_BIN = "src\\main\\resources\\FILE.bin";

    public static void main(String[] args) {
        XmlTransportModel xmlTransportModel = new XmlTransportModel();
        BinaryTransportModel binaryTransportModel = new BinaryTransportModel();
        TransportController xmlController, binaryController;
        try {
            server = new ServerSocket(PORT);
            System.out.println("Сервер запущен");

            /* Получаем данные из xml-файла */
            xmlTransportModel.downloadTransports(FILENAME_XML);
            xmlController = new TransportController(xmlTransportModel);
            System.out.println("Считали из файла список транспортов");
            (new ConsoleView()).showAllTransports(xmlTransportModel);

            /* Получаем данные из bin-файла */
            binaryTransportModel.downloadTransports(FILENAME_BIN);
            binaryController = new TransportController(binaryTransportModel);
            System.out.println("Считали из файла список транспортов");
            (new ConsoleView()).showAllTransports(binaryTransportModel);

            while (true) {
                Socket clientSocket = server.accept();
                System.out.println("К серверу подключился клиент");
                serverThreads.add(new ServerThread(clientSocket, binaryController, xmlController));
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (DisableServerException e) {
            System.out.println("Получена команда завершения работы сервера");
        } finally {
            System.out.println("Сервер завершил работу");
            binaryTransportModel.saveTransports(FILENAME_BIN);
            xmlTransportModel.saveTransports(FILENAME_XML);
            try {
                server.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}