package infoSystem;

public class ConsoleView implements View
{
    @Override
    public void showTransport(Transport transport)
    {
        System.out.println(String.format("Поезд номер %d: %s. Время отправления: " +
                        "%s Время в пути: %s",
                transport.getIndex(), transport.getRoute(), transport.getDepartureTime(),
                transport.getTravelTime()));
    }
}
