package ru.bluewater.externaldataconsumer.export;

import ru.bluewater.integration.message.ITPDataMessage;

public interface ITPDataExporter {
    void exportITPData(String itpId, ITPDataMessage itpDataMessage);
}
