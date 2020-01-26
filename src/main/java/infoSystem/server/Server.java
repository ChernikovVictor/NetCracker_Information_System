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
    private static final String FILENAME_BIN = "src\\main\\resources\\FILE.bin";
    private static final String FILENAME_LOG = Server.class.getSimpleName();

    public static void main(String[] args) {
        setLogFileName();   // определям файл для логов
        XmlTransportModel xmlTransportModel = new XmlTransportModel();
        BinaryTransportModel binaryTransportModel = new BinaryTransportModel();
        TransportController xmlController, binaryController;
        try {
            server = new ServerSocket(PORT);
            log.info("Сервер запущен");

            /* Получаем данные из xml-файла */
            xmlTransportModel.downloadTransports(FILENAME_XML);
            xmlController = new TransportController(xmlTransportModel);
            log.info("Считали из файла список транспортов\n{}",
                    (new ConsoleView()).getAllTransportsInfo(xmlTransportModel));

            /* Получаем данные из bin-файла */
            binaryTransportModel.downloadTransports(FILENAME_BIN);
            binaryController = new TransportController(binaryTransportModel);
            log.info("Считали из файла список транспортов\n{}",
                    (new ConsoleView()).getAllTransportsInfo(binaryTransportModel));

            /* Принимаем клиентов, пока не поступит команда завершения работы сервера */
            while (true) {
                Socket clientSocket = server.accept();
                log.info("К серверу подключился клиент");
                serverThreads.add(new ServerThread(clientSocket, binaryController, xmlController));
            }

        } catch (IOException | ClassNotFoundException e) {
            log.error(e.getMessage(), e);
        } catch (DisableServerException e) {
            log.info("Получена команда завершения работы сервера\n");
        } finally {
            binaryTransportModel.saveTransports(FILENAME_BIN);
            xmlTransportModel.saveTransports(FILENAME_XML);
            try {
                server.close();
                log.info("Сервер завершил работу");
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /* Определить файл логирования для текущего потока и производных от него потоков */
    private static void setLogFileName() {
        MDC.clear();
        MDC.put("logFileName", FILENAME_LOG);
    }
}