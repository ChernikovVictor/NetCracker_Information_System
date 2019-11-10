package infoSystem;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
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
