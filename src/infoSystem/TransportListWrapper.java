package infoSystem;

import infoSystem.model.*;
import lombok.AllArgsConstructor;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/* Класс-обертка для сохранения списка транспортов в XML файл */
@AllArgsConstructor
@XmlRootElement
@XmlSeeAlso({Train.class, Route.class})
public class TransportListWrapper {
    private List<Transport> transports;

    public TransportListWrapper() {
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