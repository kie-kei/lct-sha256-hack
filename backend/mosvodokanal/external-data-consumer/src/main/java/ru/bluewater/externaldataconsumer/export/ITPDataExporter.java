package ru.bluewater.externaldataconsumer.export;

import ru.bluewater.integration.model.ITPData;

public interface ITPDataExporter {
    void exportITPData(String itpId, ITPData itpData);
}
