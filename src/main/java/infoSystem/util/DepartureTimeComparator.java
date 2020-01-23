package infoSystem.util;

import infoSystem.model.Transport;

import java.util.Comparator;

public class DepartureTimeComparator implements Comparator<Transport> {
    @Override
    public int compare(Transport o1, Transport o2) {
        return o1.getDepartureTime().compareTo(o2.getDepartureTime());
    }
}
