package com.example;


import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class Calculator {

    private static final Logger LOGGER = LogManager.getLogger(Calculator.class);

    public static void main(String[] args) {
        String logLevel = System.getProperty("logLevel");
        if(StringUtils.equalsAny(logLevel, "INFO", "ERROR", "DEBUG")){
            LOGGER.setLevel(Level.toLevel(logLevel));
        }
        if(args.length == 0 || StringUtils.isBlank(args[0])){
            LOGGER.error("Expression is not passed");
            System.out.println("Pass the exression as parameter!");
        }else{
            System.out.println(calculator(args[0]));
        }

    }

    private static Map<String, Integer> varMap = new HashMap<>();

    public static String calculator(String input){
        if(!isValidExp(input)){
            return "Invalid expression";
        }
        LOGGER.info("Expression being evaluated is: " + input);

        input = input.replaceAll(" ", "").replaceAll("add\\(", "+\\(").replaceAll("sub\\(", "-\\(")
                .replaceAll("mult\\(", "*\\(").replaceAll("div\\(", "/\\(").replaceAll("let\\(", "#\\(");

        int result = 0;
        try {
            result = evaluate(input);
        } catch (NumberFormatException e) {
            LOGGER.error("Error while converting String to Integer: " + e.getMessage());
        }
        return String.valueOf(result);
    }

    private static Map<Integer, Integer> getParanthesisMap(String input) {
        Deque<Integer> stack = new ArrayDeque<>();
        Map<Integer, Integer> map = new HashMap<>();
        for(int i=0; i<input.length(); i++){
            if(input.charAt(i) == '('){
                stack.push(i);
            }else if(input.charAt(i) == ')'){
                map.put(stack.pop(), i);
            }
        }
        return map;
    }

    public static int evaluate(String input){
        Map<Integer, Integer> paranthesisMap = getParanthesisMap(input);
        char[] chArr = input.toCharArray();
        int result = 0;
        int i = input.indexOf('(');
        Character operation = chArr[i-1];
        result = Integer.parseInt(applyOp(operation, input.substring(i+1, paranthesisMap.get(i))));
        return result;
    }

    public static String applyOp(Character op, String expression){
        String expressionCopy = new String(expression);
        Map<Integer, Integer> paranthesisMap = getParanthesisMap(expressionCopy);
        if(op == '#'){
            LOGGER.info("Apply Operation: Let");
            String[] commaSplit = expressionCopy.split(",");
            while(commaSplit[1].contains("(")){
                int i = expressionCopy.indexOf('(');
                String res = applyOp(expressionCopy.charAt(i-1), expressionCopy.substring(i+1, paranthesisMap.get(i)));
                expressionCopy = expressionCopy.substring(0, i-1) + res + expressionCopy.substring(paranthesisMap.get(i)+1);
                paranthesisMap = getParanthesisMap(expressionCopy);
                commaSplit = expressionCopy.split(",");
            }
            if(StringUtils.isNumeric(commaSplit[1])){
                    LOGGER.info("Setting variable: " + commaSplit[0]);
                    varMap.put(commaSplit[0], Integer.parseInt(commaSplit[1]));
            }else{
                    LOGGER.info("Setting variable: " + commaSplit[0]);
                    varMap.put(commaSplit[0], varMap.get(commaSplit[1]));
            }
        }
        while(expressionCopy.contains("(")){
            int i = expressionCopy.indexOf('(');
            String res = applyOp(expressionCopy.charAt(i-1), expressionCopy.substring(i+1, paranthesisMap.get(i)));
            expressionCopy = expressionCopy.substring(0, i-1) + res + expressionCopy.substring(paranthesisMap.get(i)+1);
            paranthesisMap = getParanthesisMap(expressionCopy);
        }
        String[] split = expressionCopy.split(",");
        if(split.length == 2){
            int num1, num2;
            if(StringUtils.isNumeric(split[0])){
                num1 = Integer.parseInt(split[0]);
            }else{
                num1 = varMap.get(split[0]);
            }

            if(StringUtils.isNumeric(split[1])){
                num2 = Integer.parseInt(split[1]);
            }else{
                num2 = varMap.get(split[1]);
            }
            if(op == '+'){
                LOGGER.info("Apply Operation ADD");
                LOGGER.debug("Apply Operation ADD on " + num1 + ", " + num2);
                return String.valueOf(num1 + num2);
            }else if(op == '-'){
                LOGGER.info("Apply Operation SUB");
                LOGGER.debug("Apply Operation SUB on " + num1 + ", " + num2);
                return String.valueOf(num1 - num2);
            }else if(op == '*'){
                LOGGER.info("Apply Operation MULT");
                LOGGER.debug("Apply Operation MULT on " + num1 + ", " + num2);
                return String.valueOf(num1 * num2);
            }else if(op == '/'){
                LOGGER.info("Apply Operation DIV");
                LOGGER.debug("Apply Operation DIV on" + num1 + ", " + num2);
                return String.valueOf(num1/num2);
            }
        }
        if(split.length == 3 && op == '#'){
            return split[2];
        }
        LOGGER.error("Applying Operation " + op + " on expression failed: " + expression);
        return expression;
    }

    public static boolean isValidExp(String expression){
        LOGGER.info("Expression is being validated");
        int counter = 0;
        for(int i=0; i<expression.length(); i++){
            if(expression.charAt(i) == '('){
                counter++;
            }else if(expression.charAt(i) == ')'){
                counter--;
                if(counter < 0){
                    return false;
                }
            }
        }
        if(counter != 0){
            return false;
        }
        return true;
    }
}
