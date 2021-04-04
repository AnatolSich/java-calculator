package com.epam.testtools.calculator;

import com.epam.testtools.readers.ReadWriteExpressionFromFile;

import java.math.BigInteger;
import java.util.*;

import static com.epam.testtools.Main.CONSOLE;
import static com.epam.testtools.Main.FILES;

public class Calculator {
    public final static String INCORRECT_EXPRESSION = "Expression is incorrect. Expression: ";

    public final static int LEFT_ASSOCIATION = 0;
    public final static int RIGHT_ASSOCIATION = 1;

    public final static String ADD = "+";
    public final static String SUBTRACT = "-";
    public final static String MULTIPLY = "*";
    public final static String DIVIDE = "/";
    public final static String LEFT_BRACKET = "(";
    public final static String RIGHT_BRACKET = ")";

    public final static String EXPRESSION_SPLIT_REGEXP = "((?<=\\*)|(?=\\*)|(?<=/)|(?=/)|(?<=\\+)|(?=\\+)|(?<=-)|(?=-)|(?<=\\()|(?=\\)))";
    public final static String CHECK_SYMBOLS_REGEXP = "(?!(?:(([+-/*0-9()])+))$).*";
    public final static String CHECK_DOUBLES_REGEXP = "([0-9]+(\\.|,){1}[0-9]+)(([+-/*0-9()])+)";
    public final static List<String> REGEXPS_ARRAY = Arrays.asList(CHECK_SYMBOLS_REGEXP, CHECK_DOUBLES_REGEXP);

    public final static LinkedHashMap<String, Integer[]> OPERATORS_ARRAY = new LinkedHashMap<String, Integer[]>();

    static {
        OPERATORS_ARRAY.put(ADD, new Integer[]{0, LEFT_ASSOCIATION});
        OPERATORS_ARRAY.put(SUBTRACT, new Integer[]{0, LEFT_ASSOCIATION});
        OPERATORS_ARRAY.put(MULTIPLY, new Integer[]{5, LEFT_ASSOCIATION});
        OPERATORS_ARRAY.put(DIVIDE, new Integer[]{5, LEFT_ASSOCIATION});
    }

    public final String mode;
    public final ReadWriteExpressionFromFile readWriteExpressionFromFile;

    public Calculator(String mode, ReadWriteExpressionFromFile readWriteExpressionFromFile) {
        this.mode = mode;
        if (FILES.equals(mode)) {
            this.readWriteExpressionFromFile = readWriteExpressionFromFile;
        } else this.readWriteExpressionFromFile = null;
    }

    boolean isOperator(final String item) {
        return OPERATORS_ARRAY.containsKey(item);
    }

    boolean isAssociative(final String item, final int type) {
        if (!isOperator(item)) {
            throw new IllegalArgumentException("Invalid item: ${item}");
        }
        return OPERATORS_ARRAY.get(item)[1] == type;
    }

    int comparePriority(final String item1, final String item2) {
        if (!isOperator(item1) || !isOperator(item2)) {
            throw new IllegalArgumentException("Invalid items: ${item1} and/or ${item2}");
        }
        return OPERATORS_ARRAY.get(item1)[0] - OPERATORS_ARRAY.get(item2)[0];
    }

    String[] convertToReversePolishNotation(final String[] inputItemsArray) {
        final List<String> outputList = new ArrayList<String>();
        final Stack<String> stack = new Stack<String>();

        for (String item : inputItemsArray
        ) {
            if (isOperator(item)) {
                while (!stack.empty() && isOperator(stack.peek())) {
                    if ((isAssociative(item, LEFT_ASSOCIATION) && comparePriority(item, stack.peek()) <= 0) ||
                            (isAssociative(item, RIGHT_ASSOCIATION) && comparePriority(item, stack.peek()) < 0)) {
                        outputList.add(stack.pop());
                        continue;
                    }
                    break;
                }
                stack.push(item);
            } else if (item.equals(LEFT_BRACKET)) {
                stack.push(item);
            } else if (item.equals(RIGHT_BRACKET)) {
                while (!stack.empty() && !stack.peek().equals(LEFT_BRACKET)) {
                    outputList.add(stack.pop());
                }
                stack.pop();
            } else {
                outputList.add(item);
            }
        }

        while (!stack.empty()) {
            outputList.add(stack.pop());
        }
        final String[] output = new String[outputList.size()];
        return outputList.toArray(output);
    }

    double resolveReversePolishNotation(final String[] items) {
        final Stack<String> stack = new Stack<String>();

        for (String item : items) {
            if (!isOperator(item)) {
                stack.push(item);
            } else {
                final BigInteger b = new BigInteger(stack.pop());
                final BigInteger a = new BigInteger(stack.pop());

                switch (item) {
                    case ADD:
                        stack.push(a.add(b).toString());
                        writeOperationResult(ADD, a, b);
                        break;
                    case SUBTRACT:
                        stack.push(a.subtract(b).toString());
                        writeOperationResult(SUBTRACT, a, b);
                        break;
                    case MULTIPLY:
                        stack.push(a.multiply(b).toString());
                        writeOperationResult(MULTIPLY, a, b);
                        break;
                    case DIVIDE:
                        stack.push(a.divide(b).toString());
                        writeOperationResult(DIVIDE, a, b);
                        break;
                }
            }
        }
        return Double.parseDouble(stack.pop());
    }

    private void writeOperationResult(String operation, BigInteger arg1, BigInteger arg2) {
        if (FILES.equals(mode)) {
            readWriteExpressionFromFile.writeResultToFile(String.format("Operation: %s | arg1: %s | arg2: %s%n", operation, arg1, arg2));
        } else if (CONSOLE.equals(mode)) {
            System.out.printf("Operation: %s | arg1: %s | arg2: %s%n", operation, arg1, arg2);
        }
    }

    private void writeResult(String result) {
        if (FILES.equals(mode)) {
            readWriteExpressionFromFile.writeResultToFile("Result: " + result + "\n");
        } else if (CONSOLE.equals(mode)) {
            System.out.println("Result: " + result);
        }
    }

    void validateExpression(final String expression) {
        if (expression == null || expression.isEmpty()) {
            throw new IllegalArgumentException(INCORRECT_EXPRESSION + expression);
        }
        for (String item : REGEXPS_ARRAY
        ) {
            if (expression.matches(item)) {
                throw new IllegalArgumentException(INCORRECT_EXPRESSION + expression);
            }
        }
    }


    public double calculate(final String expression) {
        try {
            validateExpression(expression);
        } catch (IllegalArgumentException e) {
            writeResult(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }

        final String[] splitExpression = expression.split(EXPRESSION_SPLIT_REGEXP);
        final String[] reversePolishNotation = convertToReversePolishNotation(splitExpression);
        final double result = resolveReversePolishNotation(reversePolishNotation);
        writeResult(String.valueOf(result));

        return result;
    }

}
