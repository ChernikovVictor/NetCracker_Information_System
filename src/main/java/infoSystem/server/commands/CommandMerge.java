package infoSystem.server.commands;

import infoSystem.TransportController;
import infoSystem.model.Transport;
import infoSystem.server.CommandExecutionException;
import infoSystem.server.ServerCommands;
import infoSystem.util.BinaryLoader;
import infoSystem.util.JsonLoader;
import infoSystem.util.XmlLoader;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.List;

@Slf4j
public class CommandMerge extends Command {

    public CommandMerge(String parameter) {
        setCommandID(ServerCommands.MERGE);
        setParameter(parameter);
    }

    @Override
    public Object execute(ObjectInputStream in, ObjectOutputStream out,
                          TransportController controller) throws CommandExecutionException {
        File file;
        try {
            file = (File) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            throw new CommandExecutionException("Некорректные данные о файле");
        }

        String extension = file.getPath().substring(file.getPath().lastIndexOf("."));
        List<Transport> transportList = null;
        switch (extension) {
            case ".bin":
                transportList = BinaryLoader.deserializeList(file);
                break;
            case ".xml":
                transportList = XmlLoader.getFromXML(file);
                break;
            case ".json":
                transportList = JsonLoader.getFromJson(file);
                break;
        }
        controller.merge(transportList);

        String result = (transportList == null) ? "Ошибка. Проверьте содержимое файла" : "Данные успешно добавлены на сервер";
        log.info(result);
        return result;
    }
}