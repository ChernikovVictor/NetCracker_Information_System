package infoSystem.model;

import infoSystem.util.JsonLoader;

import java.util.List;

public class JsonTransportModel extends TransportModel
{
    public JsonTransportModel() { super(); }

    public JsonTransportModel(List<Transport> transports) {
        super(transports);
    }

    @Override
    public void downloadTransports(String filename) {
        transports = JsonLoader.getFromJson(filename);
    }

    @Override
    public void saveTransports(String filename) {
        JsonLoader.saveAsJson(transports, filename);
    }
}
