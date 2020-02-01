package infoSystem.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import infoSystem.model.Train;
import infoSystem.model.Transport;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/* Класс-DTO для сохранения списка транспортов в JSON файл */
@AllArgsConstructor
@JsonAutoDetect
public class TransportListJsonDTO {
    private List<Transport> transports;

    public TransportListJsonDTO() {
        transports = new ArrayList<>();
    }

    @JsonDeserialize(as = ArrayList.class, contentAs = Train.class)
    public List<Transport> getTransports() {
        return transports;
    }
}
