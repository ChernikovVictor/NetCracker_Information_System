package infoSystem.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TransportModel implements Model, Serializable
{
    private List<Transport> transports;

    public TransportModel()
    {
        transports = new ArrayList<>();
    }

    public TransportModel(List<Transport> transports) {
        this.transports = (transports == null) ? new ArrayList<>() : transports;
    }

    public List<Transport> getTransports() {
        return transports;
    }

    @Override
    public void addTransport(Transport transport) {
        transports.add(transport);
    }

    @Override
    public void removeTransport(int index) {
        for (Transport transport : transports) {
            if (transport.getIndex() == index) {
                transports.remove(transport);
                return;
            }
        }
    }

    @Override
    public Transport getTransport(int index) {
        for (Transport transport : transports) {
            if (transport.getIndex() == index) {
                return transport;
            }
        }
        return null;
    }

    @Override
    public void setTransport(int index, Transport transport) {
        for (int i = 0; i < transports.size(); i++) {
            if (transports.get(i).getIndex() == index) {
                transports.set(i, transport);
                return;
            }
        }
    }
}
