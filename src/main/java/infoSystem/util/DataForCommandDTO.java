package infoSystem.util;

import infoSystem.TransportController;
import infoSystem.model.Transport;
import lombok.Builder;
import lombok.Data;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/* класс-DTO для передачи интерфейсу Command информации, необходимой для её выполнения */
@Builder
@Data
public class DataForCommandDTO {
    private TransportController controller;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private ControllersDTO controllersDTO;
    private Transport transport;
}
