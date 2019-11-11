package infoSystem;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
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
    public void removeTransport(int index)
    {
        for (Transport transport : transports)
        {
            if (transport.getIndex() == index) {
                transports.remove(transport);
                return;
            }
        }
    }

    @Override
    public Transport getTransport(int index)
    {
        for (Transport transport : transports)
        {
            if (transport.getIndex() == index)
                return transport;
        }
        return null;
    }

    @Override
    public void setTransport(int index, Transport transport)
    {
        for (int i = 0; i < count(); i++)
        {
            if (transports.get(i).getIndex() == index)
            {
                transports.set(i, transport);
                return;
            }
        }
    }

    // итератор по транспортам
    @Override
    public Iterator<Transport> iterator() {
        return new transportIterator(this);
    }

    // класс итератора по траспортам
    private class transportIterator implements Iterator<Transport>
    {
        private TransportModel model;
        private int index;
        public transportIterator(TransportModel model)
        {
            this.model = model;
            index = -1;
        }

        @Override
        public boolean hasNext()
        {
            return index + 1 < model.count() ? true : false;
        }

        @Override
        public Transport next()
        {
            index++;
            return model.transports.get(index);
        }
    }
}
