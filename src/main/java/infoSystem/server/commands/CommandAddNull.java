package infoSystem.server.commands;

import infoSystem.TransportController;
import infoSystem.model.Route;
import infoSystem.model.Train;
import infoSystem.server.ServerCommands;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandAddNull extends Command {

    public CommandAddNull(String parameter) {
        setCommandID(ServerCommands.ADDNULL);
        setParameter(parameter);
    }

    @Override
    public Object execute(TransportController controller) {
        Route route = Route.builder().departure("").destination("").build();
        controller.addTransport(Train.builder().index(-1).route(route).departureTime("").travelTime("").build());
        log.info("Поезд без информации добавлен в систему");
        return "Поезд без информации добавлен в систему";
    }
}
