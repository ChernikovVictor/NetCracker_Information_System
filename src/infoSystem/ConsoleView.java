package infoSystem;

public class ConsoleView implements View
{
    @Override
    public void showTransport(Transport transport)
    {
        System.out.println(String.format("Поезд номер %d: %s. Время отправления: " +
                        "%s Время в пути: %s", transport.getIndex(), transport.getRoute(),
                transport.getDepartureTime(), transport.getTravelTime()));
    }

    @Override
    public void showTransport(int index, Model model) {
        showTransport(model.getTransport(index));
    }

    @Override
    public void showAllTransports(Model model)
    {
        int count = model.count();
        for (int i = 0; i < count; i++)
            showTransport(i, model);
    }
}
