package org.apache.solr.handler.dataimport;

import java.util.List;
import java.util.Map;

public interface IVariableResolver {
    String FUNCTIONS_NAMESPACE = "dataimporter.functions.";
    String FUNCTIONS_NAMESPACE_SHORT = "dih.functions.";

    /**
     * Resolves a given value with a name
     *
     * @param name
     *          the String to be resolved
     * @return an Object which is the result of evaluation of given name
     */
    Object resolve(String name);

    Object resolveEvaluator(String namespace, String name);

    /**
     * Given a String with place holders, replace them with the value tokens.
     *
     * @return the string with the placeholders replaced with their values
     */
    String replaceTokens(String template);

    IVariableResolver addNamespace(String name, Map<String, Object> newMap);

    List<String> getVariables(String expr);

    CurrentLevel currentLevelMap(String[] keyParts,
                                 Map<String, Object> currentLevel, boolean includeLastLevel);

    IVariableResolver removeNamespace(String name);

    IVariableResolver setEvaluators(Map<String, Evaluator> evaluators);

    public static class CurrentLevel {
      final Map<String,Object> map;
      final int level;
      CurrentLevel(int level, Map<String,Object> map) {
        this.level = level;
        this.map = map;
      }
    }
}
