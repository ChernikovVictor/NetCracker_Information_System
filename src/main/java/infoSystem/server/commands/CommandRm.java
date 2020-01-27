package infoSystem.server.commands;

import infoSystem.TransportController;
import infoSystem.server.CommandExecutionException;
import infoSystem.server.ServerCommands;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandRm extends Command {

    public CommandRm(String parameter) {
        setCommandID(ServerCommands.RM);
        setParameter(parameter);
    }

    @Override
    public Object execute(TransportController controller) throws CommandExecutionException {
        int index;
        try {
            index = Integer.parseInt(getParameter());
        } catch (ClassCastException e) {
            log.error("Некорректный индекс");
            throw new CommandExecutionException("Некорректный индекс");
        }

        if (controller.getTransport(index) == null) {
            log.info("Поезда с таким номером не существует");
            return "Поезда с таким номером не существует";
        } else {
            controller.removeTransport(index);
            log.info("Поезд с номером {} удален из системы", index);
            return "Поезд с номером " + index + " удален из системы";
        }
    }
}