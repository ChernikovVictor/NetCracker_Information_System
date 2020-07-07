package infoSystem.util;

import infoSystem.model.*;
import lombok.AllArgsConstructor;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/* Класс-DTO для сохранения списка транспортов в XML файл */
@AllArgsConstructor
@XmlRootElement
@XmlSeeAlso({Train.class, Route.class})
public class TransportListXmlDTO {
    private List<Transport> transports;

    public TransportListXmlDTO() {
        transports = new ArrayList<>();
    }

    @XmlElementWrapper(name = "transports")
    @XmlElements({
            @XmlElement(name = "train", type = Train.class)
    })
    public List<Transport> getTransports() {
        return transports;
    }
}
