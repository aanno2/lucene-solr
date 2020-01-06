package org.apache.solr.handler.dataimport;

import java.util.Map;

public interface ITransformer {
    /**
     * The input is a row of data and the output has to be a new row.
     *
     * @param context The current context
     * @param row     A row of data
     * @return The changed data. It must be a {@link Map}&lt;{@link String}, {@link Object}&gt; if it returns
     *         only one row or if there are multiple rows to be returned it must
     *         be a {@link java.util.List}&lt;{@link Map}&lt;{@link String}, {@link Object}&gt;&gt;
     */
    Object transformRow(Map<String, Object> row, IContext context);
}
