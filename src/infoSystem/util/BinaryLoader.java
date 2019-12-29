package infoSystem.util;

import java.io.*;
import java.util.List;

public class BinaryLoader {

    /* Сериализовать список траспортов в файл */
    public static <T> void serializeList(List<T> items, String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(items);
            System.out.println("Данные успешно сохранены в " + filename);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /* Десериализовать список траспортов из файла */
    public static <T> List<T> deserializeList(String filename) {
        return deserializeList(new File(filename));
    }

    /* Десериализовать список траспортов из файла */
    public static <T> List<T> deserializeList(File file) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            List<T> list = (List<T>) in.readObject();
            System.out.println("Данные успешно загружены из " + file.getName());
            return list;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
