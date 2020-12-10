package fiabilitate;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class GeneratePredicate {

    private static ScriptEngineManager factory = new ScriptEngineManager();
    private static ScriptEngine engine = factory.getEngineByName("JavaScript");

    public static void main(String[] args) throws ScriptException {
        String[] variablesList = {"a", "b", "c", "d"};
        String expression = "(a ∨ b) ∧ (c ∨ d)";
        evaluateExpresionPossibilities(variablesList, expression, variablesList.length);

        System.out.println();

        variablesList = new String[]{"a", "b", "c"};
        expression = "(¬a ∧ ¬b) ∨ (a ∧ ¬c) ∨ (¬a ∧ c)";
        evaluateExpresionPossibilities(variablesList, expression, variablesList.length);

        System.out.println();

        variablesList = new String[]{"a", "b", "c", "d"};
        expression = "a ∨ b ∨ (c ∧ d)";
        evaluateExpresionPossibilities(variablesList, expression, variablesList.length);

        System.out.println();

        variablesList = new String[]{"a", "b", "c"};
        expression = "(a ∧ b) ∨ (b ∧ c) ∨ (a ∧ c)";
        evaluateExpresionPossibilities(variablesList, expression, variablesList.length);


    }

    private static void evaluateExpresionPossibilities(String[] variablesList, String expression, int n) throws ScriptException {
        System.out.println(expression);
        for (int i = 0; i < Math.pow(2, n); i++) {
            String combination = generateCombination(i, n);
            evaluateExpression(expression, variablesList, combination);
        }
    }


    public static void evaluateExpression(String expression, String[] variablesList, String combination) throws ScriptException {
        for (int variableIndex = 0; variableIndex < variablesList.length; variableIndex++) {
            String variable = combination.charAt(variableIndex) == '0' ? "false" : "true";
            expression = expression.replace(variablesList[variableIndex], variable);
        }
        expression = expression.replace("∧", "&&");
        expression = expression.replace("∨", "||");
        expression = expression.replace("¬", "!");


        System.out.println(expression + " = " + engine.eval(expression));
    }

    public static String generateCombination(int i, int n) {
        String bin = Integer.toBinaryString(i);
        while (bin.length() < n)
            bin = "0" + bin;
//        System.out.println(bin);
        return bin;
    }
}
