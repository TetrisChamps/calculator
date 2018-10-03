package calc;

import java.util.*;

import static java.lang.Double.NaN;
import static java.lang.Math.pow;


/*
 *   A calculator for rather simple arithmetic expressions
 *
 *   This is not the program, it's a class declaration (with methods) in it's
 *   own file (which must be named Calculator.java)
 *
 *   NOTE:
 *   - No negative numbers implemented
 */
class Calculator {

    // Here are the only allowed instance variables!
    // Error messages (more on static later)
    final static String MISSING_OPERAND = "Missing or bad operand";
    final static String DIV_BY_ZERO = "Division with 0";
    final static String MISSING_OPERATOR = "Missing operator or parenthesis";
    final static String OP_NOT_FOUND = "Operator not found";

    // Definition of operators
    final static String OPERATORS = "+-*/^";

    // Method used in REPL
    double eval(String expr) {
        if (expr.length() == 0) {
            return NaN;
        }
        List<String> tokens = tokenize(expr);
        List<String> multiplicationFix = fixMultiplication(tokens);
        List<String> postfix = infix2Postfix(multiplicationFix);
        double result = evalPostfix(postfix);
        return result; // result;
    }

    // ------  Evaluate RPN expression -------------------

    // TODO Eval methods

    double evalPostfix(List<String> postfix) {
        Stack<String> stack = new Stack<>();
        for (String token : postfix) {
            if (isOperator(token)) {
                if (stack.size() < 2) {
                    throw new IllegalArgumentException(MISSING_OPERAND);
                }
                try {
                    double left = Double.parseDouble(stack.pop());
                    double right = Double.parseDouble(stack.pop());
                    double result = applyOperator(token, left, right);
                    stack.push(Double.toString(result));
                } catch (Exception e) {
                    throw e;
                }
            } else {
                stack.push(token);
            }
        }
        if (stack.size() != 1) {
            throw new IllegalArgumentException(MISSING_OPERATOR);
        }
        return Double.parseDouble(stack.pop());
    }

    double applyOperator(String op, double d1, double d2) {
        switch (op) {
            case "+":
                return d1 + d2;
            case "-":
                return d2 - d1;
            case "*":
                return d1 * d2;
            case "/":
                if (d1 == 0) {
                    throw new IllegalArgumentException(DIV_BY_ZERO);
                }
                return d2 / d1;
            case "^":
                return pow(d2, d1);
        }
        throw new RuntimeException(OP_NOT_FOUND);
    }

    // ------- Infix 2 Postfix ------------------------

    // TODO Methods

    List<String> infix2Postfix(List<String> tokens) {
        List<String> postfix = new LinkedList<>();
        Stack<String> operatorStack = new Stack<>();
        for (String token : tokens) {
            if (isOperator(token)) {
                // Operator
                while (!operatorStack.empty() && !operatorStack.peek().equals("(") && shouldStackBePopped(operatorStack.peek(), token)) {
                    postfix.add(operatorStack.pop());
                }
                operatorStack.push(token);
            } else if (token.equals("(")) {
                // Left parenthesis
                operatorStack.push("(");
            } else if (token.equals(")")) {
                // Right parenthesis
                while (!operatorStack.empty() && !operatorStack.peek().equals("(")) {
                    postfix.add(operatorStack.pop());

                    // If size is 0 after pop and now left parentheses is found,
                    // the expression is missing an operator.
                    if (operatorStack.empty()) {
                        throw new IllegalArgumentException(MISSING_OPERATOR);
                    }
                }
                if (!operatorStack.empty()) {
                    operatorStack.pop();
                }
            } else {
                // Number
                if (!isNumber(token)) {
                    throw new IllegalArgumentException(MISSING_OPERAND);
                }
                postfix.add(token);
            }
        }
        while (!operatorStack.empty()) {
            postfix.add(operatorStack.pop());
        }
        return postfix;
    }

    boolean shouldStackBePopped(String operator, String token) {
        if (getPrecedence(operator) > getPrecedence(token) ||
                (getPrecedence(operator) == getPrecedence(token) &&
                        getAssociativity(operator) == Assoc.LEFT)) {
            return true;
        }
        return false;
    }

    boolean isOperator(String token) {
        return OPERATORS.contains(token);
    }

    int getPrecedence(String op) {
        if ("+-".contains(op)) {
            return 2;
        } else if ("*/".contains(op)) {
            return 3;
        } else if ("^".contains(op)) {
            return 4;
        } else {
            throw new RuntimeException(OP_NOT_FOUND);
        }
    }

    enum Assoc {
        LEFT,
        RIGHT
    }

    Assoc getAssociativity(String op) {
        if ("+-*/".contains(op)) {
            return Assoc.LEFT;
        } else if ("^".contains(op)) {
            return Assoc.RIGHT;
        } else {
            throw new RuntimeException(OP_NOT_FOUND);
        }
    }


    // ---------- Tokenize -----------------------

    // TODO Methods to tokenize

    List<String> tokenize(String expression) {
        List<String> tokens = new LinkedList<>();
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < expression.length(); ++i) {
            if (expression.charAt(i) == ' ') {
                if (token.length() > 0) {
                    tokens.add(token.toString());
                    token.setLength(0);
                }
            } else if ((OPERATORS + "()").indexOf(expression.charAt(i)) >= 0) {
                if (token.length() > 0) {
                    tokens.add(token.toString());
                    token.setLength(0);
                }
                tokens.add(Character.toString(expression.charAt(i)));
            } else {
                token.append(expression.charAt(i));
            }
        }
        if (token.length() > 0) {
            tokens.add(token.toString());
        }
        return tokens;
    }

    List<String> fixMultiplication(List<String> inTokens) {
        List<String> outTokens = new LinkedList<>();
        boolean firstIteration = true;
        String previous = "";
        for (String token : inTokens) {
            if (firstIteration) {
                outTokens.add(token);
                previous = token;
                firstIteration = false;
            } else {
                if (((isNumber(previous) || previous.contains(")")) && token.contains("(")) ||
                        ((isNumber(token) || token.contains("(")) && previous.contains(")"))) {
                    outTokens.add("*");
                }
                outTokens.add(token);
                previous = token;
            }

        }
        return outTokens;
    }

    private boolean isNumber(String string) {
        try {
            Double.parseDouble(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
