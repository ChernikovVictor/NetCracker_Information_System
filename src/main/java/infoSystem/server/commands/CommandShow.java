package infoSystem.server.commands;

import infoSystem.TransportController;
import infoSystem.model.Model;
import infoSystem.server.ServerCommands;
import infoSystem.util.DataForCommandDTO;
import infoSystem.view.ConsoleView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandShow extends Command {

    public CommandShow(String parameter) {
        setCommandID(ServerCommands.SHOW);
        setParameter(parameter);
    }

    @Override
    public Object execute(DataForCommandDTO data) {
        TransportController controller = data.getController();
        Model model = controller.getModel();
        log.info("Список, переданный клиенту:\n{}", (new ConsoleView()).getAllTransportsInfo(model));
        return model.getTransports();
    }
}