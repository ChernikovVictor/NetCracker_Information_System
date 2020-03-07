package infoSystem.server.commands;

import infoSystem.server.ServerCommands;
import infoSystem.util.DataForCommandDTO;

public class CommandReturn extends Command {

    public CommandReturn(String parameter) {
        setCommandID(ServerCommands.RETURN);
        setParameter(parameter);
    }

    @Override
    public Object execute(DataForCommandDTO data) {
        return null;
    }
}
