package infoSystem.server.commands;

import infoSystem.model.Transport;
import infoSystem.server.ServerCommands;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandDTime extends Command {

    public CommandDTime(String parameter) {
        setCommandID(ServerCommands.DTIME);
        setParameter(parameter);
    }

    @Override
    public Object execute(Transport transport) {
        transport.setDepartureTime(getParameter());
        log.info("Время отправления изменено");
        return "Время отправления изменено";
    }
}
