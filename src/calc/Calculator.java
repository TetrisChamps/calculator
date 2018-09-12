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
        fixMultiplication(tokens);
        List<String> postfix = infix2Postfix(tokens);
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
                // Is operator
                while (operatorStack.size() > 0 && OPERATORS.contains(operatorStack.peek())) {
                    if (getPrecedence(operatorStack.peek()) >= getPrecedence(token) && !operatorStack.peek().equals("^")) {
                        postfix.add(operatorStack.pop());
                    } else {
                        break;
                    }
                }
                operatorStack.push(token);
            } else if (token.equals("(")) {
                // Left parenthesis
                operatorStack.push("(");
            } else if (token.equals(")")) {
                // Right parenthesis
                while (operatorStack.size() > 0 && !operatorStack.peek().equals("(")) {
                    postfix.add(operatorStack.pop());
                }
                if (operatorStack.size() > 0) {
                    operatorStack.pop();
                }
            } else {
                // Number
                postfix.add(token);
            }
        }
        while (operatorStack.size() > 0) {
            postfix.add(operatorStack.pop());
        }
        return postfix;
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

    void fixMultiplication(List<String> tokens) {
        int i = 0;
        while (i < tokens.size() - 1) {
            if (!isOperator(tokens.get(i)) && !tokens.get(i).contains("(") && !isOperator(tokens.get(i + 1)) && !tokens.get(i + 1).contains(")")) {
                tokens.add(i + 1, "*");
            }
            ++i;
        }
    }
}
