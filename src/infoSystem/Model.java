package infoSystem;

public interface Model
{
    void addTransport(Transport transport);
    void removeTransport(int index);
    Transport getTransport(int index);
    void setTransport(int index, Transport transport);
    int count(); // кол-во траспортов в списке модели
}
