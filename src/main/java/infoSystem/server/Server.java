package infoSystem.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import infoSystem.TransportController;
import infoSystem.model.*;
import infoSystem.view.ConsoleView;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
public class Server {

    private static LinkedList<ServerThread> serverThreads = new LinkedList<>(); // список всех нитей
    private static ServerSocket server;
    private static final int PORT = 4004;
    private static final String FILENAME_XML = "src\\main\\resources\\FILE.xml";
    private static final String FILENAME_JSON = "src\\main\\resources\\FILE.json";
    private static final String FILENAME_BIN = "src\\main\\resources\\FILE.bin";
    private static final String FILENAME_LOG = Server.class.getSimpleName();

    public static void main(String[] args) {
        setLogFileName();   // определяем файл для логов
        XmlTransportModel xmlTransportModel = new XmlTransportModel();
        TransportController xmlController = new TransportController(xmlTransportModel);
        BinaryTransportModel binaryTransportModel = new BinaryTransportModel();
        TransportController binaryController = new TransportController(binaryTransportModel);
        JsonTransportModel jsonTransportModel = new JsonTransportModel();
        TransportController jsonController = new TransportController(jsonTransportModel);
        try {
            server = new ServerSocket(PORT);
            log.info("Сервер запущен");

            /* Получаем данные из файлов */
            readModel(binaryController, FILENAME_BIN);
            readModel(xmlController, FILENAME_XML);
            readModel(jsonController, FILENAME_JSON);

            /* Принимаем клиентов, пока не поступит команда завершения работы сервера */
            while (true) {
                Socket clientSocket = server.accept();
                log.info("К серверу подключился клиент");
                serverThreads.add(new ServerThread(clientSocket, binaryController, xmlController, jsonController));
            }

        } catch (IOException | ClassNotFoundException e) {
            log.error(e.getMessage(), e);
        } catch (DisableServerException e) {
            log.info("Получена команда завершения работы сервера\n");
        } finally {
            binaryController.saveTransports(FILENAME_BIN);
            xmlController.saveTransports(FILENAME_XML);
            jsonController.saveTransports(FILENAME_JSON);
            try {
                server.close();
                log.info("Сервер завершил работу");
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /* Определить файл логирования для текущего потока */
    private static void setLogFileName() {
        MDC.clear();
        MDC.put("logFileName", FILENAME_LOG);
    }

    /* Считать данные из файла */
    private static void readModel(TransportController controller, String filename) {
        controller.downloadTransports(filename);
        log.info("Считали из файла список транспортов\n{}",
                (new ConsoleView()).getAllTransportsInfo(controller.getModel()));
    }
}