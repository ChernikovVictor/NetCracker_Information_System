package infoSystem.server;

import infoSystem.server.commands.*;
import infoSystem.server.commands.Command;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandFactory {

    public static Command createCommand(String userCommand) {
        if (userCommand == null)
            throw new NullPointerException();
        log.info("Получена команда: {}", userCommand);

        /* Определить тип команды и её параметр */
        int posSpace = userCommand.indexOf(' ');
        String action = (posSpace == -1) ? userCommand : userCommand.substring(0, posSpace);
        String parameter = (posSpace == -1) ? null : userCommand.substring(posSpace + 1);
        ServerCommands commandID;
        try {
            commandID = ServerCommands.valueOf(action.toUpperCase());
        } catch (IllegalArgumentException e) {
            commandID = ServerCommands.ILLEGAL_COMMAND;
        }

        switch (commandID) {
            case ADD:
                return new CommandAdd(parameter);
            case ADDNULL:
                return new CommandAddNull(parameter);
            case DISABLE:
                return new CommandDisable(parameter);
            case DTIME:
                return new CommandDTime(parameter);
            case EXIT:
                return new CommandExit(parameter);
            case GET:
                return new CommandGet(parameter);
            case HELP:
                return new CommandHelp(parameter);
            case INDEX:
                return new CommandIndex(parameter);
            case MERGE:
                return new CommandMerge(parameter);
            case RETURN:
                return new CommandReturn(parameter);
            case RM:
                return new CommandRm(parameter);
            case ROUTE:
                return new CommandRoute(parameter);
            case SEARCH:
                return new CommandSearch(parameter);
            case SET:
                return new CommandSet(parameter);
            case SHOW:
                return new CommandShow(parameter);
            case SORT:
                return new CommandSort(parameter);
            case SWITCH:
                return new CommandSwitch(parameter);
            case TTIME:
                return new CommandTTime(parameter);
            default:
                return new CommandIllegalCommand(parameter);
        }
    }
}
