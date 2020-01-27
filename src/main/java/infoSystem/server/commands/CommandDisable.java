package infoSystem.server.commands;

import infoSystem.server.ServerCommands;

public class CommandDisable extends Command {

    public CommandDisable(String parameter) {
        setCommandID(ServerCommands.DISABLE);
        setParameter(parameter);
    }
}