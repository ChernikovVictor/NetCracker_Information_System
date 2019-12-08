package infoSystem.model;

import java.io.Serializable;
import java.util.Comparator;

public interface Transport extends Serializable
{
    int getIndex();
    void setIndex(int value);

    Route getRoute();
    void setRoute(Route route);

    String getDepartureTime();
    void setDepartureTime(String departureTime);

    String getTravelTime();
    void setTravelTime(String travelTime);

    Comparator<Transport> departureTimeComparator = new Comparator<Transport>() {
        @Override
        public int compare(Transport o1, Transport o2) {
            return o1.getDepartureTime().compareTo(o2.getDepartureTime());
        }
    };
}
