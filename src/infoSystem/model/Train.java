package infoSystem.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Train implements Transport, Serializable
{
    private int index;  // номер поезда
    private Route route;
    private String departureTime;   // время отправления
    private String travelTime;      // время в пути
}
