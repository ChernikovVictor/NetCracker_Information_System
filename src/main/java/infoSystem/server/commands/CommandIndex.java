package infoSystem.server.commands;

import infoSystem.model.Transport;
import infoSystem.server.CommandExecutionException;
import infoSystem.server.ServerCommands;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandIndex extends Command {

    public CommandIndex(String parameter) {
        setCommandID(ServerCommands.INDEX);
        setParameter(parameter);
    }

    @Override
    public Object execute(Transport transport) throws CommandExecutionException {
        try {
            int index = Integer.parseInt(getParameter());
            transport.setIndex(index);
            log.info("Номер изменен");
            return "Номер изменен";
        } catch (ClassCastException e) {
            log.error("Некорректный индекс");
            throw new CommandExecutionException("Некорректный индекс");
        }
    }
}
