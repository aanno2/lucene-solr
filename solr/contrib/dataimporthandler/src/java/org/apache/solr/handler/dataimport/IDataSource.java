package org.apache.solr.handler.dataimport;

import java.util.Properties;

public interface IDataSource<T> {
    /**
     * Initializes the DataSource with the <code>Context</code> and
     * initialization properties.
     * <p>
     * This is invoked by the <code>DataImporter</code> after creating an
     * instance of this class.
     */
    void init(Context context, Properties initProps);

    /**
     * Get records for the given query.The return type depends on the
     * implementation .
     *
     * @param query The query string. It can be a SQL for JdbcDataSource or a URL
     *              for HttpDataSource or a file location for FileDataSource or a custom
     *              format for your own custom DataSource.
     * @return Depends on the implementation. For instance JdbcDataSource returns
     *         an Iterator&lt;Map &lt;String,Object&gt;&gt;
     */
    T getData(String query);

    /**
     * Cleans up resources of this DataSource after use.
     */
    void close();
}
