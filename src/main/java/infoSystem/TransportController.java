package infoSystem;

import infoSystem.model.*;
import infoSystem.util.DepartureTimeComparator;

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
    public synchronized List<Transport> getTransportsByPattern(String regex) {
        regex = regex.replaceAll("\\*", ".*");
        regex = regex.replaceAll("\\?", ".?");
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

        List<Transport> transports = new ArrayList<>();
        model.getTransports().forEach(transport -> {
            Matcher matcher = pattern.matcher(transport.toString());
            if (matcher.find()) {
                transports.add(transport);
            }
        });

        return transports;
    }

    /* Добавить в модель данные, исключая дубликаты */
    public synchronized void merge(List<Transport> transports) {
        if (transports == null) { return; }
        transports.removeIf(obj -> model.getTransports().contains(obj));
        model.getTransports().addAll(transports);
    }

    public synchronized void downloadTransports(String filename) {
        model.downloadTransports(filename);
    }

    public synchronized void saveTransports(String filename) {
        model.saveTransports(filename);
    }
}
