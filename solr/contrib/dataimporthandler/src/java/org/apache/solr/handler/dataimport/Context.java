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

import org.apache.solr.core.SolrCore;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * This abstract class gives access to all available objects. So any
 * component implemented by a user can have the full power of DataImportHandler
 * </p>
 * <p>
 * Refer to <a
 * href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache.org/solr/DataImportHandler</a>
 * for more details.
 * </p>
 * <p>
 * <b>This API is experimental and subject to change</b>
 *
 * @since solr 1.3
 */
public abstract class Context implements IContext {

  @Override
  public Object clone() {
    try {
      Context clone = (Context) super.clone();
      return clone;
    } catch (CloneNotSupportedException e) {
      throw new IllegalStateException(e);
    }
  }

}
