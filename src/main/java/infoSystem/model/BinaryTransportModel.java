package infoSystem.model;

import infoSystem.util.BinaryLoader;

import java.util.List;

public class BinaryTransportModel extends TransportModel
{
    public BinaryTransportModel() {
        super();
    }

    public BinaryTransportModel(List<Transport> transports) {
        super(transports);
    }

    @Override
    public void downloadTransports(String filename) {
        transports = BinaryLoader.deserializeList(filename);
    }

    @Override
    public void saveTransports(String filename) {
        BinaryLoader.serializeList(transports, filename);
    }
}
