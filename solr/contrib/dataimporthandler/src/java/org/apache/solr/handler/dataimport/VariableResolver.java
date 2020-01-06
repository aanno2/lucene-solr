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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.solr.common.util.Cache;
import org.apache.solr.common.util.MapBackedCache;
import org.apache.solr.update.processor.TemplateUpdateProcessorFactory;

import static org.apache.solr.update.processor.TemplateUpdateProcessorFactory.Resolved;

/**
 * <p>
 * A set of nested maps that can resolve variables by namespaces. Variables are
 * enclosed with a dollar sign then an opening curly brace, ending with a
 * closing curly brace. Namespaces are delimited with '.' (period).
 * </p>
 * <p>
 * This class also has special logic to resolve evaluator calls by recognizing
 * the reserved function namespace: dataimporter.functions.xxx
 * </p>
 * <p>
 * This class caches strings that have already been resolved from the current
 * dih import.
 * </p>
 * <b>This API is experimental and may change in the future.</b>
 *
 * <p>
 * Instances of VariableResolver are immutable.
 * </p>
 * 
 * @since solr 1.3
 */
public class VariableResolver implements IVariableResolver {
  
  private static final Pattern DOT_PATTERN = Pattern.compile("[.]");
  private static final Pattern EVALUATOR_FORMAT_PATTERN = Pattern
      .compile("^(\\w*?)\\((.*?)\\)$");
  private Map<String,Object> rootNamespace;
  private Map<String,Evaluator> evaluators;
  private Cache<String,Resolved> cache = new MapBackedCache<>(new WeakHashMap<>());
  private Function<String,Object> fun = this::resolve;

  public VariableResolver() {
    rootNamespace = new HashMap<>();
  }
  
  public VariableResolver(Properties defaults) {
    rootNamespace = new HashMap<>();
    for (Map.Entry<Object,Object> entry : defaults.entrySet()) {
      rootNamespace.put(entry.getKey().toString(), entry.getValue());
    }
  }
  
  public VariableResolver(Map<String,Object> defaults) {
    rootNamespace = new HashMap<>(defaults);
  }

  VariableResolver(VariableResolver toCopy) {
    this(toCopy.rootNamespace);
    if (toCopy.evaluators != null) {
      this.evaluators = new HashMap<>(toCopy.evaluators);
    }
    this.cache = toCopy.cache;
    this.fun = toCopy.fun;
  }
  
  /**
   * Resolves a given value with a name
   * 
   * @param name
   *          the String to be resolved
   * @return an Object which is the result of evaluation of given name
   */
  @Override
  public Object resolve(String name) {
    Object r = null;
    if (name != null) {
      String[] nameParts = DOT_PATTERN.split(name);
      CurrentLevel cr = currentLevelMap(nameParts,
          rootNamespace, false);
      Map<String,Object> currentLevel = cr.map;
      r = currentLevel.get(nameParts[nameParts.length - 1]);
      if (r == null && name.startsWith(FUNCTIONS_NAMESPACE)
          && name.length() > FUNCTIONS_NAMESPACE.length()) {
        return resolveEvaluator(FUNCTIONS_NAMESPACE, name);
      }
      if (r == null && name.startsWith(FUNCTIONS_NAMESPACE_SHORT)
          && name.length() > FUNCTIONS_NAMESPACE_SHORT.length()) {
        return resolveEvaluator(FUNCTIONS_NAMESPACE_SHORT, name);
      }
      if (r == null) {
        StringBuilder sb = new StringBuilder();
        for(int i=cr.level ; i<nameParts.length ; i++) {
          if(sb.length()>0) {
            sb.append(".");
          }
          sb.append(nameParts[i]);
        }
        r = cr.map.get(sb.toString());
      }      
      if (r == null) {
        r = System.getProperty(name);
      }
    }
    return r == null ? "" : r;
  }

  @Override
  public Object resolveEvaluator(String namespace, String name) {
    if (evaluators == null) {
      return "";
    }
    Matcher m = EVALUATOR_FORMAT_PATTERN.matcher(name
        .substring(namespace.length()));
    if (m.find()) {
      String fname = m.group(1);
      Evaluator evaluator = evaluators.get(fname);
      if (evaluator == null) return "";
      ContextImpl ctx = new ContextImpl(null, this, null, null, null, null,
          null);
      String g2 = m.group(2);
      return evaluator.evaluate(g2, ctx);
    } else {
      return "";
    }
  }

  /**
   * Given a String with place holders, replace them with the value tokens.
   * 
   * @return the string with the placeholders replaced with their values
   */
  @Override
  public String replaceTokens(String template) {
    return TemplateUpdateProcessorFactory.replaceTokens(template, cache, fun, TemplateUpdateProcessorFactory.DOLLAR_BRACES_PLACEHOLDER_PATTERN);
  }

  @Override
  public IVariableResolver addNamespace(String name, Map<String, Object> newMap) {
    VariableResolver result = new VariableResolver(this);
    result._addNamespace(name, newMap);
    return result;
  }

  // Modifying!
  private void _addNamespace(String name, Map<String, Object> newMap) {
    if (newMap != null) {
      if (name != null) {
        String[] nameParts = DOT_PATTERN.split(name);
        Map<String,Object> nameResolveLevel = currentLevelMap(nameParts,
            rootNamespace, false).map;
        nameResolveLevel.put(nameParts[nameParts.length - 1], newMap);
      } else {
        for (Map.Entry<String,Object> entry : newMap.entrySet()) {
          String[] keyParts = DOT_PATTERN.split(entry.getKey());
          Map<String,Object> currentLevel = rootNamespace;
          currentLevel = currentLevelMap(keyParts, currentLevel, false).map;
          currentLevel.put(keyParts[keyParts.length - 1], entry.getValue());
        }
      }
    }
  }

  @Override
  public List<String> getVariables(String expr) {
    return TemplateUpdateProcessorFactory.getVariables(expr, cache, TemplateUpdateProcessorFactory.DOLLAR_BRACES_PLACEHOLDER_PATTERN);
  }

  @Override
  public CurrentLevel currentLevelMap(String[] keyParts,
                                      Map<String, Object> currentLevel, boolean includeLastLevel) {
    int j = includeLastLevel ? keyParts.length : keyParts.length - 1;
    for (int i = 0; i < j; i++) {
      Object o = currentLevel.get(keyParts[i]);
      if (o == null) {
        if(i == j-1) {
          Map<String,Object> nextLevel = new HashMap<>();
          currentLevel.put(keyParts[i], nextLevel);
          currentLevel = nextLevel;
        } else {
          return new CurrentLevel(i, currentLevel);
        }
      } else if (o instanceof Map<?,?>) {
        @SuppressWarnings("unchecked")
        Map<String,Object> nextLevel = (Map<String,Object>) o;
        currentLevel = nextLevel;
      } else {
        throw new AssertionError(
            "Non-leaf nodes should be of type java.util.Map");
      }
    }
    return new CurrentLevel(j-1, currentLevel);
  }

  @Override
  public IVariableResolver removeNamespace(String name) {
    VariableResolver result = new VariableResolver(this);
    result.rootNamespace.remove(name);
    return result;
  }
  
  @Override
  public IVariableResolver setEvaluators(Map<String, Evaluator> evaluators) {
    VariableResolver result = new VariableResolver(this);
    result.evaluators = evaluators;
    return result;
  }
}
