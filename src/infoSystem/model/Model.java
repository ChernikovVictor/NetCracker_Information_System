package infoSystem.model;

import javax.swing.table.TableModel;
import java.io.Serializable;
import java.util.List;

public interface Model extends Serializable, TableModel
{
    void addTransport(Transport transport);
    void removeTransport(int index);
    Transport getTransport(int index);
    void setTransport(int index, Transport transport);
    List<Transport> getTransports();
}
