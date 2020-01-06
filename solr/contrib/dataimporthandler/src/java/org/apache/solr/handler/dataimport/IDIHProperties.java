package org.apache.solr.handler.dataimport;

import java.util.Date;
import java.util.Map;

public interface IDIHProperties {
    void init(DataImporter dataImporter, Map<String, String> initParams);

    boolean isWritable();

    void persist(Map<String, Object> props);

    Map<String, Object> readIndexerProperties();

    String convertDateToString(Date d);

    Date getCurrentTimestamp();
}
