package infoSystem.view;

import infoSystem.model.Model;
import infoSystem.model.Transport;


public class ConsoleView implements View
{
    @Override
    public void showTransport(Transport transport) {
        if (transport == null) {
            System.out.println("Поезда с таким номером не существует");
        }
        else {
            System.out.println(String.format("Поезд номер %d: %s. Время отправления: " +
                            "%s Время в пути: %s", transport.getIndex(), transport.getRoute(),
                    transport.getDepartureTime(), transport.getTravelTime()));
        }
    }

    @Override
    public void showAllTransports(Model model) {
        model.getTransports().forEach(this::showTransport);
    }

    @Override
    public String getTransportInfo(Transport transport) {
        return transport.toString();
    }

    @Override
    public String getAllTransportsInfo(Model model) {
        StringBuilder result = new StringBuilder();
        model.getTransports().forEach(transport -> result.append(getTransportInfo(transport)).append('\n'));
        return result.toString();
    }
}
