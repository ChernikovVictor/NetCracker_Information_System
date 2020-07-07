package infoSystem.server.commands;

import infoSystem.TransportController;
import infoSystem.model.*;
import infoSystem.server.CommandExecutionException;
import infoSystem.server.ServerCommands;
import infoSystem.util.DataForCommandDTO;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class CommandAdd extends Command {

    public CommandAdd(String parameter) {
        setCommandID(ServerCommands.ADD);
        setParameter(parameter);
    }

    @Override
    public Object execute(DataForCommandDTO data) throws CommandExecutionException {

        ObjectInputStream in = data.getInputStream();
        ObjectOutputStream out = data.getOutputStream();
        TransportController controller = data.getController();

        try {
            Transport transport = createTransport(in, out);
            controller.addTransport(transport);
            log.info("Поезд добавлен в систему");
            return "Поезд добавлен в систему";
        } catch (Exception e) {
           log.error("Ошибка создания транспорта", e);
           throw new CommandExecutionException("Ошибка. Поезд не добавлен в систему");
        }
    }

    /* создать новый транспорт */
    private Transport createTransport(ObjectInputStream in, ObjectOutputStream out)
            throws IOException, ClassCastException, ClassNotFoundException {

        out.writeObject("Введите номер поезда");
        out.flush();
        int index = (Integer.parseInt((String) in.readObject()));

        out.writeObject("Введите маршрут (Например: Самара - Москва)");
        out.flush();
        String s = (String) in.readObject();
        int pos = s.indexOf('-');
        Route route = Route.builder().departure(s.substring(0, pos - 1)).destination(s.substring(pos + 2)).build();

        out.writeObject("Введите время отправления");
        out.flush();
        String departureTime = (String) in.readObject();

        out.writeObject("Введите время в пути");
        out.flush();
        String travelTime = (String) in.readObject();

        return Train.builder().index(index).route(route).
                departureTime(departureTime).travelTime(travelTime).build();
    }
}
