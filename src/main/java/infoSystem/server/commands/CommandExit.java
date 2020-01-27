package infoSystem.server.commands;

import infoSystem.server.ServerCommands;

public class CommandExit extends Command {

    public CommandExit(String parameter) {
        setCommandID(ServerCommands.EXIT);
        setParameter(parameter);
    }
}
