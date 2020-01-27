package infoSystem.server.commands;

import infoSystem.model.Transport;
import infoSystem.server.ServerCommands;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandTTime extends Command {

    public CommandTTime(String parameter) {
        setCommandID(ServerCommands.TTIME);
        setParameter(parameter);
    }

    @Override
    public Object execute(Transport transport) {
        transport.setTravelTime(getParameter());
        log.info("Время в пути изменено");
        return "Время в пути изменено";
    }
}
