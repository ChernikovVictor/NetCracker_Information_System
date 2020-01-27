package infoSystem.server.commands;

import infoSystem.server.ServerCommands;

public class CommandReturn extends Command {

    public CommandReturn(String parameter) {
        setCommandID(ServerCommands.RETURN);
        setParameter(parameter);
    }
}
