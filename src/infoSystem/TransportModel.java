package infoSystem;

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

    @Override
    public int count() { return transports.size(); }

    @Override
    public void addTransport(Transport transport) {
        transports.add(transport);
    }

    @Override
    public void removeTransport(int index) {
        transports.remove(index);
    }

    @Override
    public Transport getTransport(int index) {
        return transports.get(index);
    }

    @Override
    public void setTransport(int index, Transport transport) {
        transports.set(index, transport);
    }
}
