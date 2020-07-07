package infoSystem.server.commands;

import infoSystem.TransportController;
import infoSystem.model.BinaryTransportModel;
import infoSystem.model.Transport;
import infoSystem.server.ServerCommands;
import infoSystem.util.DataForCommandDTO;
import infoSystem.view.ConsoleView;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class CommandSearch extends Command {

    public CommandSearch(String parameter) {
        setCommandID(ServerCommands.SEARCH);
        setParameter(parameter);
    }

    @Override
    public Object execute(DataForCommandDTO data) {
        TransportController controller = data.getController();
        List<Transport> transports = controller.getTransportsByPattern(getParameter());
        log.info("Список, переданный клиенту:\n{}", (new ConsoleView()).
                getAllTransportsInfo(new BinaryTransportModel(transports)));
        return transports;
    }
}