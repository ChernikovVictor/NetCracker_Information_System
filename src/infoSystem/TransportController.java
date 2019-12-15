package infoSystem;

import infoSystem.model.*;

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
}
