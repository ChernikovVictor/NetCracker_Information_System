package infoSystem.server;

import infoSystem.server.commands.Command;
import infoSystem.TransportController;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.io.*;
import java.net.Socket;

/* Поток для работы с очередным клиентом */
@Slf4j
public class ServerThread extends Thread {

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private TransportController controller;

    private static final String FILENAME_LOG = Server.class.getSimpleName();

    private final TransportController binaryController;
    private final TransportController xmlController;

    public ServerThread(Socket socket, TransportController binaryController, TransportController xmlController)
            throws IOException, ClassNotFoundException, DisableServerException {

        this.socket = socket;
        this.binaryController = binaryController;
        this.xmlController = xmlController;
        controller = xmlController;     // по умолчанию
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        Command clientCommand = CommandFactory.createCommand((String) in.readObject());
        if (clientCommand.getCommandID() == ServerCommands.DISABLE) {
            disconnect();
            throw new DisableServerException();
        }

        start();
    }

    @Override
    public void run() {
        setLogFileName();   // определям файл для логов

        Command clientCommand;
        do {
            String stringCommand;
            try {
                stringCommand = getStringFromClient();
            } catch (IOException | ClassNotFoundException e) {
                log.error(e.getMessage(), e);
                stringCommand = "exit";
            }

            clientCommand = CommandFactory.createCommand(stringCommand);

            try {
                executeCommand(clientCommand);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                clientCommand.setCommandID(ServerCommands.EXIT);
            }

        } while (clientCommand.getCommandID() != ServerCommands.EXIT);

        disconnect();
    }

    /* Выполнить команду, отправить результат клиенту */
    private void executeCommand(Command clientCommand) throws IOException {
        try {
            switch (clientCommand.getCommandID()) {
                case ADDNULL: case GET: case RM: case SEARCH: case SHOW: case SORT:
                    out.writeObject(clientCommand.execute(controller));
                    break;
                case ADD: case MERGE: case SET:
                    out.writeObject(clientCommand.execute(in, out, controller));
                    break;
                case SWITCH:
                    controller = clientCommand.execute(binaryController, xmlController);
                    out.writeObject("Файл данных изменен");
                    break;
                case HELP:
                    out.writeObject(clientCommand.execute());
                    break;
                case EXIT:
                    break;
                default:
                    log.error("Некорректная команда");
                    out.writeObject("Некорректная команда");
            }
        } catch (CommandExecutionException e) {
            out.writeObject(e.getMessage());
        } finally {
            out.flush();
            out.reset();    // удалить хеши объектов, переданных в поток ранее
        }
    }

    /* Получить команду в текстовой форме */
    private String getStringFromClient() throws IOException, ClassNotFoundException {
        Object clientCommand;
        do {
            clientCommand = in.readObject();
            if (clientCommand instanceof String) {
                return (String) clientCommand;
            }
            out.writeObject("Некорректные данные");
            out.flush();
        } while (true);
    }

    /* Отключить клиента */
    private void disconnect() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /* Определить файл логирования для текущего потока */
    private static void setLogFileName() {
        MDC.clear();
        MDC.put("logFileName", FILENAME_LOG);
    }
}
