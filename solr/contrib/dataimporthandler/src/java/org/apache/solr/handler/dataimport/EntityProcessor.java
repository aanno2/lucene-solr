/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.solr.handler.dataimport;

import java.util.Map;

/**
 * <p>
 * An instance of entity processor serves an entity. It is reused throughout the
 * import process.
 * </p>
 * <p>
 * Implementations of this abstract class must provide a public no-args constructor.
 * </p>
 * <p>
 * Refer to <a
 * href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache.org/solr/DataImportHandler</a>
 * for more details.
 * </p>
 * <p>
 * <b>This API is experimental and may change in the future.</b>
 *
 * @since solr 1.3
 */
public abstract class EntityProcessor implements IEntityProcessor {

  /**
   * Invoked after the transformers are invoked. EntityProcessors can add, remove or modify values
   * added by Transformers in this method.
   *
   * @param r The transformed row
   * @since solr 1.4
   */
  @Override
  public void postTransform(Map<String, Object> r) {
  }

  /**
   * Invoked when the Entity processor is destroyed towards the end of import.
   *
   * @since solr 1.4
   */
  @Override
  public void close() {
    //no-op
  }

  @Override
  public Object clone() {
    try {
      EntityProcessor clone = (EntityProcessor) super.clone();
      return clone;
    } catch (CloneNotSupportedException e) {
      throw new IllegalStateException(e);
    }
  }
}
