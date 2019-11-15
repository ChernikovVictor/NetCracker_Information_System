package infoSystem;

import java.util.List;

public interface Model
{
    void addTransport(Transport transport);
    void removeTransport(int index);
    Transport getTransport(int index);
    void setTransport(int index, Transport transport);
    List<Transport> getTransports();
}
