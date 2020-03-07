package infoSystem.server.commands;

import infoSystem.server.ServerCommands;
import infoSystem.util.DataForCommandDTO;

public class CommandHelp extends Command {

    public CommandHelp(String parameter) {
        setCommandID(ServerCommands.HELP);
        setParameter(parameter);
    }

    /* справка по командам */
    @Override
    public Object execute(DataForCommandDTO data) {
        return "Показать информацию о поезде: get \"index\"\n" +
                "Изменить информацию о поезде: set \"index\"\n" +
                "Добавить поезд: add\n" +
                "Добавить поезд без информации: addNull\n" +
                "Удалить поезд: rm \"index\"\n" +
                "Показать все поезда: show\n" +
                "Сортировать по времени отправления: sort\n" +
                "Поиск по шаблону: search \"regex\"\n" +
                "Выбрать файл для загрузки данных: switch \"bin\" switch \"xml\" switch \"json\"\n" +
                "Выход: exit\n" +
                "\tКоманды изменения информации о поезде:\n" +
                "Изменить номер поезда: index \"index\"\n" +
                "Изменить маршрут: route \"start - finish\"\n" +
                "Изменить время отправления: dTime \"time\"\n" +
                "Изменить путевое время: tTime \"time\"\n" +
                "Вернуться назад: return";
    }
}