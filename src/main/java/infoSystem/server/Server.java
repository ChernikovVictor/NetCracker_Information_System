package infoSystem.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import infoSystem.TransportController;
import infoSystem.model.*;
import infoSystem.util.ControllersDTO;
import infoSystem.view.ConsoleView;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
public class Server {

    private static LinkedList<ServerThread> serverThreads = new LinkedList<>(); // список всех нитей
    private static ServerSocket server;

    private static ControllersDTO controllersDTO;   // доступные контроллеры

    private static final int PORT = 4004;
    private static final String FILENAME_XML = "src\\main\\resources\\FILE.xml";
    private static final String FILENAME_JSON = "src\\main\\resources\\FILE.json";
    private static final String FILENAME_BIN = "src\\main\\resources\\FILE.bin";
    private static final String FILENAME_LOG = Server.class.getSimpleName();

    public static void main(String[] args) {
        setLogFileName();   // определяем файл для логов
        controllersDTO = initControllers();
        try {
            server = new ServerSocket(PORT);
            log.info("Сервер запущен");

            /* Принимаем клиентов, пока не поступит команда завершения работы сервера */
            while (true) {
                Socket clientSocket = server.accept();
                log.info("К серверу подключился клиент");
                serverThreads.add(new ServerThread(clientSocket, controllersDTO));
            }
        } catch (IOException | ClassNotFoundException e) {
            log.error(e.getMessage(), e);
        } catch (DisableServerException e) {
            log.info("Получена команда завершения работы сервера\n");
        } finally {
            disableServer();
        }
    }

    /* Определить файл логирования для текущего потока */
    private static void setLogFileName() {
        MDC.clear();
        MDC.put("logFileName", FILENAME_LOG);
    }

    /* Инициализация контроллеров */
    private static ControllersDTO initControllers() {
        XmlTransportModel xmlTransportModel = new XmlTransportModel();
        TransportController xmlController = new TransportController(xmlTransportModel);
        readModel(xmlController, FILENAME_XML);

        BinaryTransportModel binaryTransportModel = new BinaryTransportModel();
        TransportController binaryController = new TransportController(binaryTransportModel);
        readModel(binaryController, FILENAME_BIN);

        JsonTransportModel jsonTransportModel = new JsonTransportModel();
        TransportController jsonController = new TransportController(jsonTransportModel);
        readModel(jsonController, FILENAME_JSON);

        return new ControllersDTO(binaryController, xmlController, jsonController);
    }

    /* Считать данные из файла */
    private static void readModel(TransportController controller, String filename) {
        controller.downloadTransports(filename);
        log.info("Считали из файла список транспортов\n{}",
                (new ConsoleView()).getAllTransportsInfo(controller.getModel()));
    }

    /* Сохранить данные, выключить сервер */
    private static void disableServer() {
        controllersDTO.getBinaryController().saveTransports(FILENAME_BIN);
        controllersDTO.getXmlController().saveTransports(FILENAME_XML);
        controllersDTO.getJsonController().saveTransports(FILENAME_JSON);
        try {
            server.close();
            log.info("Сервер завершил работу");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}