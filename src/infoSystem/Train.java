package infoSystem;

public class Train implements Transport
{
    private int index;  // номер поезда
    private Route route;
    private String departureTime;   // время отправления
    private String travelTime;      // время в пути

    public Train()
    {
        this(-1, new Route(), "", "");
    }

    public Train(int index, Route route, String departureTime, String travelTime)
    {
        this.index = index;
        this.route = route;
        this.departureTime = departureTime;
        this.travelTime = travelTime;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public void setIndex(int value) {
        System.out.println("Отработал метод setIndex");
        index = value;
    }

    @Override
    public Route getRoute() {
        return route;
    }

    @Override
    public void setRoute(Route route) {
        System.out.println("Отработал метод setRoute");
        this.route = route;
    }

    @Override
    public String getDepartureTime() {
        return departureTime;
    }

    @Override
    public void setDepartureTime(String departureTime) {
        System.out.println("Отработал метод setDepartureTime");
        this.departureTime = departureTime;
    }

    @Override
    public String getTravelTime() {
        return travelTime;
    }

    @Override
    public void setTravelTime(String travelTime) {
        System.out.println("Отработал метод setTravelTime");
        this.travelTime = travelTime;
    }
}
