package infoSystem.model;

import java.io.Serializable;
import java.util.List;

public interface Model extends Serializable
{
    void addTransport(Transport transport);
    void removeTransport(int index);
    Transport getTransport(int index);
    void setTransport(int index, Transport transport);
    List<Transport> getTransports();
}
