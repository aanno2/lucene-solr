package org.apache.solr.handler.dataimport;

import java.util.List;
import java.util.regex.Pattern;

public interface IEvaluator {
    Pattern IN_SINGLE_QUOTES = Pattern.compile("^'(.*?)'$");
    String DATE_FORMAT_EVALUATOR = "formatDate";
    String URL_ENCODE_EVALUATOR = "encodeUrl";
    String ESCAPE_SOLR_QUERY_CHARS = "escapeQueryChars";
    String SQL_ESCAPE_EVALUATOR = "escapeSql";

    /**
     * Return a String after processing an expression and a {@link VariableResolver}
     *
     * @see VariableResolver
     * @param expression string to be evaluated
     * @param context instance
     * @return the value of the given expression evaluated using the resolver
     */
    String evaluate(String expression, Context context);

    /**
     * Parses a string of expression into separate params. The values are separated by commas. each value will be
     * translated into one of the following:
     * &lt;ol&gt;
     * &lt;li&gt;If it is in single quotes the value will be translated to a String&lt;/li&gt;
     * &lt;li&gt;If is is not in quotes and is a number a it will be translated into a Double&lt;/li&gt;
     * &lt;li&gt;else it is a variable which can be resolved and it will be put in as an instance of VariableWrapper&lt;/li&gt;
     * &lt;/ol&gt;
     *
     * @param expression the expression to be parsed
     * @param vr the VariableResolver instance for resolving variables
     *
     * @return a List of objects which can either be a string, number or a variable wrapper
     */
    List<Object> parseParams(String expression, VariableResolver vr);

    VariableWrapper getVariableWrapper(String s, VariableResolver vr);

    public static class VariableWrapper {
      public final String varName;
      public final VariableResolver vr;

      public VariableWrapper(String s, VariableResolver vr) {
        this.varName = s;
        this.vr = vr;
      }

      public Object resolve() {
        return vr.resolve(varName);
      }

      @Override
      public String toString() {
        Object o = vr.resolve(varName);
        return o == null ? null : o.toString();
      }
    }
}
