package infoSystem.server.commands;

import infoSystem.server.CommandExecutionException;
import infoSystem.server.ServerCommands;
import infoSystem.util.DataForCommandDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandIllegalCommand extends Command {

    public CommandIllegalCommand(String parameter) {
        setCommandID(ServerCommands.ILLEGAL_COMMAND);
        setParameter(parameter);
    }

    @Override
    public Object execute(DataForCommandDTO data) throws CommandExecutionException {
        log.error("Некорректная команда");
        throw new CommandExecutionException("Некорректная команда");
    }
}