package infoSystem.server.commands;

import infoSystem.server.CommandExecutionException;
import infoSystem.server.ServerCommands;
import infoSystem.util.ControllersDTO;
import infoSystem.util.DataForCommandDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandSwitch extends Command {

    public CommandSwitch(String parameter) {
        setCommandID(ServerCommands.SWITCH);
        setParameter(parameter);
    }

    @Override
    public Object execute(DataForCommandDTO data) throws CommandExecutionException {
        ControllersDTO controllersDTO = data.getControllersDTO();
        switch (getParameter()) {
            case "bin":
                log.info("Файл данных изменен");
                return controllersDTO.getBinaryController();
            case "xml":
                log.info("Файл данных изменен");
                return controllersDTO.getXmlController();
            case "json":
                log.info("Файл данных изменен");
                return controllersDTO.getJsonController();
            default:
                log.error("Некорректные данные");
                throw new CommandExecutionException("Некорректные данные");
        }
    }
}