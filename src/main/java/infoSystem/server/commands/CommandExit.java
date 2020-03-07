package infoSystem.server.commands;

import infoSystem.server.ServerCommands;
import infoSystem.util.DataForCommandDTO;

public class CommandExit extends Command {

    public CommandExit(String parameter) {
        setCommandID(ServerCommands.EXIT);
        setParameter(parameter);
    }

    @Override
    public Object execute(DataForCommandDTO data) {
        return null;
    }
}
