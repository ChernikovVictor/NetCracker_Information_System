package infoSystem;

public class Route
{
    private String departure;   // начальная станция
    private String destination;     // конечная станция

    public Route(){ this("", ""); }

    public Route(String departure, String destination)
    {
        this.departure = departure;
        this.destination = destination;
    }

    public String getDeparture() { return departure; }
    public String getDestination() { return destination; }
    public void setDeparture(String value) {
        departure = value;
        System.out.println("Отработал метод setDeparture");
    }
    public void setDestination(String value) {
        destination = value;
        System.out.println("Отработал метод setDestination");
    }

    @Override
    public String toString()
    {
        return departure + " - " + destination;
    }
}
