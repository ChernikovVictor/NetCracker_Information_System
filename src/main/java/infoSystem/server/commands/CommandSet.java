package infoSystem.server.commands;

import infoSystem.TransportController;
import infoSystem.model.Transport;
import infoSystem.server.*;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class CommandSet extends Command {

    public CommandSet(String parameter) {
        setCommandID(ServerCommands.SET);
        setParameter(parameter);
    }

    @Override
    public Object execute(ObjectInputStream in, ObjectOutputStream out, TransportController controller)
            throws CommandExecutionException {
        int index;
        try {
            index = Integer.parseInt(getParameter());
        } catch (ClassCastException e) {
            log.error("Некорректный индекс", e);
            throw new CommandExecutionException("Некорректный индекс");
        }

        Transport transport = controller.getTransport(index);
        if (transport == null) {
            log.info("Поезда с таким номером не существует");
            return "Поезда с таким номером не существует";
        }

        try {
            changeTransportInfo(in, out, transport);
        } catch (IOException | ClassNotFoundException e) {
            log.error("Некорректные данные", e);
            throw new CommandExecutionException("Некорректные данные");
        }

        log.info("Поезд изменен");
        return "Поезд изменен";
    }

    /* изменить информацию о транспорте */
    private void changeTransportInfo(ObjectInputStream in, ObjectOutputStream out, Transport transport)
            throws IOException, ClassNotFoundException {

        out.writeObject(transport);
        out.flush();
        out.reset();    // удалить хеши объектов, переданных в поток ранее

        Command clientCommand;
        do {
            clientCommand = CommandFactory.createCommand((String) in.readObject());
            try {
                switch (clientCommand.getCommandID()) {
                    case DTIME: case INDEX: case ROUTE: case TTIME:
                        out.writeObject(clientCommand.execute(transport));
                        break;
                    case HELP:
                        out.writeObject(clientCommand.execute());
                        break;
                    case RETURN:
                        break;
                    default:
                        log.info("Некорректная операция");
                        out.writeObject("Некорректная операция");
                }
            } catch (CommandExecutionException e) {
                out.writeObject(e.getMessage());
            } finally {
                out.flush();
            }
        } while (clientCommand.getCommandID() != ServerCommands.RETURN);
    }
}