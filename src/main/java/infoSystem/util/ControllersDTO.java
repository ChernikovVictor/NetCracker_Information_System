package infoSystem.util;

import infoSystem.TransportController;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/* класс-DTO для передачи доступных контролеров очередной нити сервера */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ControllersDTO {
    private TransportController binaryController;
    private TransportController xmlController;
    private TransportController jsonController;
}
