package infoSystem;

import infoSystem.model.*;

public class TransportController
{
    private Model model;

    public Model getModel() { return model; }

    public TransportController(Model model) {
        this.model = model;
    }

    public Transport getTransport(int index) {
        return model.getTransport(index);
    }

    public void setTransport(int index, Transport transport) {
        model.setTransport(index, transport);
    }

    public void addTransport(Transport transport) {
        model.addTransport(transport);
    }

    public void removeTransport(int index) {
        model.removeTransport(index);
    }
}
