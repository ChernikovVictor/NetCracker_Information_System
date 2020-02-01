package infoSystem.server.commands;

import infoSystem.TransportController;
import infoSystem.server.CommandExecutionException;
import infoSystem.server.ServerCommands;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandSwitch extends Command {

    public CommandSwitch(String parameter) {
        setCommandID(ServerCommands.SWITCH);
        setParameter(parameter);
    }

    @Override
    public TransportController execute(TransportController binaryController,
           TransportController xmlController, TransportController jsonController) throws CommandExecutionException {
        switch (getParameter()) {
            case "bin":
                log.info("Файл данных изменен");
                return binaryController;
            case "xml":
                log.info("Файл данных изменен");
                return xmlController;
            case "json":
                log.info("Файл данных изменен");
                return jsonController;
            default:
                log.error("Некорректные данные");
                throw new CommandExecutionException("Некорректные данные");
        }
    }
}