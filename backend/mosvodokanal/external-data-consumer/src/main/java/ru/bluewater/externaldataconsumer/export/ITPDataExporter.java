package ru.bluewater.externaldataconsumer.export;

import ru.bluewater.externaldataconsumer.model.ITPData;

public interface ITPDataExporter {
    void exportITPData(String itpId, ITPData itpData);
}
