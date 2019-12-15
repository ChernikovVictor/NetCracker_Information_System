package infoSystem.model;

import lombok.*;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "train")
@XmlType(propOrder = {"index", "route", "departureTime", "travelTime"})
public class Train implements Transport, Serializable
{
    private int index;  // номер поезда
    private Route route;
    private String departureTime;   // время отправления
    private String travelTime;      // время в пути
}
