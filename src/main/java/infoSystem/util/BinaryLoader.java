package infoSystem.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.List;

@Slf4j
public class BinaryLoader {

    /* Сериализовать список в файл */
    public static <T extends Serializable> void serializeList(List<T> items, String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(items);
            log.info("Данные успешно сохранены в {}", filename);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /* Десериализовать список из файла */
    public static <T extends Serializable> List<T> deserializeList(String filename) {
        return deserializeList(new File(filename));
    }

    /* Десериализовать список из файла */
    public static <T extends Serializable> List<T> deserializeList(File file) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            List<T> list = (List<T>) in.readObject();
            log.info("Данные успешно загружены из {}", file.getName());
            return list;
        } catch (IOException | ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
