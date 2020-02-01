package infoSystem.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect
@XmlRootElement(name = "train")
@XmlType(propOrder = {"index", "route", "departureTime", "travelTime"})
public class Train implements Transport, Serializable
{
    private int index;  // номер поезда
    private Route route;
    private String departureTime;   // время отправления
    private String travelTime;      // время в пути

    @Override
    public String toString() {
        return String.format("%d %s %s %s", index, route.toString(), departureTime, travelTime);
    }

    @Override
    public boolean equals(Object object) {
        if (object == this)
            return true;
        if (!(object instanceof Train))
            return false;
        return this.toString().equals(object.toString());
    }
}
