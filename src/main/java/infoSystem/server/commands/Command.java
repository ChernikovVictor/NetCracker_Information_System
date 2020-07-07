package infoSystem.server.commands;

import infoSystem.server.CommandExecutionException;
import infoSystem.server.ServerCommands;
import infoSystem.util.DataForCommandDTO;
import lombok.Data;

@Data
public abstract class Command {
    private ServerCommands commandID;
    private String parameter;

    public abstract Object execute(DataForCommandDTO data) throws CommandExecutionException;
}
