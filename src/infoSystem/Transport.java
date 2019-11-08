package infoSystem;

public interface Transport
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
