package org.apache.solr.handler.dataimport;

import java.util.Map;

public interface IEntityProcessor extends AutoCloseable, Cloneable {
    /**
     * This method is called when it starts processing an entity. When it comes
     * back to the entity it is called again. So it can reset anything at that point.
     * For a rootmost entity this is called only once for an ingestion. For sub-entities , this
     * is called multiple once for each row from its parent entity
     *
     * @param context The current context
     */
    void init(Context context);

    /**
     * This method helps streaming the data for each row . The implementation
     * would fetch as many rows as needed and gives one 'row' at a time. Only this
     * method is used during a full import
     *
     * @return A 'row'.  The 'key' for the map is the column name and the 'value'
     *         is the value of that column. If there are no more rows to be
     *         returned, return 'null'
     */
    Map<String, Object> nextRow();

    /**
     * This is used for delta-import. It gives the pks of the changed rows in this
     * entity
     *
     * @return the pk vs value of all changed rows
     */
    Map<String, Object> nextModifiedRowKey();

    /**
     * This is used during delta-import. It gives the primary keys of the rows
     * that are deleted from this entity. If this entity is the root entity, solr
     * document is deleted. If this is a sub-entity, the Solr document is
     * considered as 'changed' and will be recreated
     *
     * @return the pk vs value of all changed rows
     */
    Map<String, Object> nextDeletedRowKey();

    /**
     * This is used during delta-import. This gives the primary keys and their
     * values of all the rows changed in a parent entity due to changes in this
     * entity.
     *
     * @return the pk vs value of all changed rows in the parent entity
     */
    Map<String, Object> nextModifiedParentRowKey();

    /**
     * Invoked for each entity at the very end of the import to do any needed cleanup tasks.
     *
     */
    void destroy();

    /**
     * Invoked after the transformers are invoked. EntityProcessors can add, remove or modify values
     * added by Transformers in this method.
     *
     * @param r The transformed row
     * @since solr 1.4
     */
    void postTransform(Map<String, Object> r);

    /**
     * Invoked when the Entity processor is destroyed towards the end of import.
     *
     * @since solr 1.4
     */
    @Override
    void close();

    Object clone();
}
