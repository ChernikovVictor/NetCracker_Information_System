package infoSystem.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import infoSystem.model.Transport;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.List;

@Slf4j
public class JsonLoader {

    /* Записать список траспортов в JSON файл */
    public static void saveAsJson(List<Transport> transports, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            TransportListJsonDTO jsonDTO = new TransportListJsonDTO(transports);
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);  // сделать JSON файл более читабельным
            mapper.writeValue(writer, jsonDTO);
            log.info("Данные успешно сохранены в {}", filename);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /* Получить список траспортов из JSON файла */
    public static List<Transport> getFromJson(File file) {
        try (FileReader reader = new FileReader(file)) {
            ObjectMapper mapper = new ObjectMapper();
            TransportListJsonDTO dto = mapper.readValue(reader, TransportListJsonDTO.class);
            log.info("Данные успешно загружены из {}", file.getName());
            return dto.getTransports();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /* Получить список траспортов из JSON файла */
    public static List<Transport> getFromJson(String filename) {
        return getFromJson(new File(filename));
    }
}
