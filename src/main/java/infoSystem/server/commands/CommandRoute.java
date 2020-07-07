package infoSystem.server.commands;

import infoSystem.model.Route;
import infoSystem.server.CommandExecutionException;
import infoSystem.server.ServerCommands;
import infoSystem.util.DataForCommandDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandRoute extends Command {

    public CommandRoute(String parameter) {
        setCommandID(ServerCommands.ROUTE);
        setParameter(parameter);
    }

    @Override
    public Object execute(DataForCommandDTO data) throws CommandExecutionException {
        try {
            int posDash = getParameter().indexOf('-');
            Route route = Route.builder().departure(getParameter().substring(0, posDash - 1))
                    .destination(getParameter().substring(posDash + 2)).build();
            data.getTransport().setRoute(route);
            log.info("Маршрут изменен");
            return "Маршрут изменен";
        } catch (ClassCastException | IndexOutOfBoundsException e) {
            log.error("Некорректный маршрут", e);
            throw new CommandExecutionException("Некорректный маршрут");
        }
    }
}
