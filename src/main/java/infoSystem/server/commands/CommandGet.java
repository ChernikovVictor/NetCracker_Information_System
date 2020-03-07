package infoSystem.server.commands;

import infoSystem.model.Transport;
import infoSystem.server.CommandExecutionException;
import infoSystem.server.ServerCommands;
import infoSystem.util.DataForCommandDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandGet extends Command {

    public CommandGet(String parameter) {
        setCommandID(ServerCommands.GET);
        setParameter(parameter);
    }

    @Override
    public Object execute(DataForCommandDTO data) throws CommandExecutionException {
        int index;
        try {
            index = Integer.parseInt(getParameter());
        } catch (ClassCastException e) {
            log.error("Некорректный индекс");
            throw new CommandExecutionException("Некорректный индекс");
        }

        Transport transport = data.getController().getTransport(index);
        if (transport == null) {
            log.info("Поезда с таким номером не существует");
            return "Поезда с таким номером не существует";
        } else {
            log.info(transport.toString());
            return transport;
        }
    }
}
