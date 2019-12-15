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
}
