package infoSystem.server.commands;

import infoSystem.server.ServerCommands;
import infoSystem.util.DataForCommandDTO;

public class CommandDisable extends Command {

    public CommandDisable(String parameter) {
        setCommandID(ServerCommands.DISABLE);
        setParameter(parameter);
    }

    @Override
    public Object execute(DataForCommandDTO data) {
        return null;
    }
}