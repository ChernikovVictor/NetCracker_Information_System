package infoSystem.model;

import javax.swing.table.AbstractTableModel;
import java.io.Serializable;
import java.util.*;

public class TransportModel extends AbstractTableModel implements Model, Serializable
{
    private List<Transport> transports;

    public TransportModel() {
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

    /* Методы интерфейса TableModel */
    @Override
    public int getRowCount() {
        return transports.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "Номер";
            case 1:
                return "Маршрут";
            case 2:
                return "Время отправления";
            case 3:
                return "Время в пути";
            default:
                return "";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return (columnIndex == 0) ? Integer.class : String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Transport transport = transports.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return transport.getIndex();
            case 1:
                return transport.getRoute();
            case 2:
                return transport.getDepartureTime();
            case 3:
                return transport.getTravelTime();
            default:
                return "";
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Transport transport = transports.get(rowIndex);
        switch (columnIndex) {
            case 0:
                transport.setIndex((int) aValue);
                break;
            case 1:
                String s = (String) aValue;
                int pos = s.indexOf('-');
                if (pos == -1)  // Если данные некорректные, то не изменять ничего
                    return;
                Route route = Route.builder().departure(s.substring(0, pos - 1)).destination(s.substring(pos + 2)).build();
                transport.setRoute(route);
                break;
            case 2:
                transport.setDepartureTime((String) aValue);
                break;
            case 3:
                transport.setTravelTime((String) aValue);
                break;
        }
        fireTableCellUpdated(rowIndex, columnIndex); // Сообщить слушателям об изменении в модели
    }
}
