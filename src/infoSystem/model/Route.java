package infoSystem.model;

import lombok.*;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "route")
@XmlType(propOrder = {"departure", "destination"})
public class Route implements Serializable
{
    private String departure;   // начальная станция
    private String destination;     // конечная станция

    @Override
    public String toString()
    {
        return departure + " - " + destination;
    }
}
