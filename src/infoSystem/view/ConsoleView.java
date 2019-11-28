package infoSystem.view;

import infoSystem.model.Model;
import infoSystem.model.Transport;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

public class ConsoleView implements View
{
    @Override
    public void showTransport(Transport transport)
    {
        if (transport == null)
        {
            System.out.println("Поезда с таким номером не существует");
        }
        else {
            System.out.println(String.format("Поезд номер %d: %s. Время отправления: " +
                            "%s Время в пути: %s", transport.getIndex(), transport.getRoute(),
                    transport.getDepartureTime(), transport.getTravelTime()));
        }
    }

    @Override
    public void showTransport(int index, Model model) {
        showTransport(model.getTransport(index));
    }

    @Override
    public void showAllTransports(Model model)
    {
        List<Transport> transports = model.getTransports();
        for (Transport transport : transports)
            showTransport(transport);
    }
}
