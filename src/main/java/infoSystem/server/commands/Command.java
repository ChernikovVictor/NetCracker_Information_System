package infoSystem.server.commands;

import infoSystem.TransportController;
import infoSystem.model.Transport;
import infoSystem.server.CommandExecutionException;
import infoSystem.server.ServerCommands;
import lombok.Data;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@Data
public abstract class Command {
    private ServerCommands commandID;
    private String parameter;

    public Object execute() throws CommandExecutionException {
        throw new UnsupportedOperationException();
    }

    public Object execute(Transport transport) throws CommandExecutionException {
        throw new UnsupportedOperationException();
    }

    public Object execute(TransportController controller) throws CommandExecutionException {
        throw new UnsupportedOperationException();
    }

    public Object execute(ObjectInputStream in, ObjectOutputStream out, TransportController controller)
            throws CommandExecutionException {
        throw new UnsupportedOperationException();
    }

    public TransportController execute(TransportController binaryController, TransportController xmlController,
                              TransportController jsonController) throws CommandExecutionException {
        throw new UnsupportedOperationException();
    }
}
