package infoSystem.server.commands;

import infoSystem.model.Route;
import infoSystem.model.Transport;
import infoSystem.server.CommandExecutionException;
import infoSystem.server.ServerCommands;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandRoute extends Command {

    public CommandRoute(String parameter) {
        setCommandID(ServerCommands.ROUTE);
        setParameter(parameter);
    }

    @Override
    public Object execute(Transport transport) throws CommandExecutionException {
        try {
            int posDash = getParameter().indexOf('-');
            Route route = Route.builder().departure(getParameter().substring(0, posDash - 1))
                    .destination(getParameter().substring(posDash + 2)).build();
            transport.setRoute(route);
            log.info("Маршрут изменен");
            return "Маршрут изменен";
        } catch (ClassCastException | IndexOutOfBoundsException e) {
            log.error("Некорректный маршрут", e);
            throw new CommandExecutionException("Некорректный маршрут");
        }
    }
}
