package infoSystem;

import infoSystem.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TransportController
{
    private Model model;

    public Model getModel() { return model; }

    public TransportController(Model model) {
        this.model = model;
    }

    public synchronized Transport getTransport(int index) {
        return model.getTransport(index);
    }

    public synchronized void setTransport(int index, Transport transport) {
        model.setTransport(index, transport);
    }

    public synchronized void addTransport(Transport transport) {
        model.addTransport(transport);
    }

    public synchronized void removeTransport(int index) {
        model.removeTransport(index);
    }

    public synchronized void sortByDepartureTime() {
        model.getTransports().sort(new DepartureTimeComparator());
    }

    /* Получить модель с транспортами, соответствующими шаблону */
    public synchronized Model getModelByPattern(String regex) {
        List<Transport> transports = new ArrayList<>();
        regex = regex.replaceAll("\\*", ".*");
        regex = regex.replaceAll("\\?", ".?");
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

        for (Transport transport : model.getTransports()) {
            Matcher matcher = pattern.matcher(transport.toString());
            if (matcher.find()) {
                transports.add(transport);
            }
        }

        return new TransportModel(transports);
    }
}
