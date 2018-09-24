package calc;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

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

    final static String[] OPERATORSARRAY = {"+", "-", "*", "^", "(", ")"};
    // Definition of operators
    final static String OPERATORS = "+-*/^";

    // Method used in REPL
    double eval(String expr) {
        if (expr.length() == 0) {
            return NaN;
        }
        List<String> tokens = tokenize(expr);
        List<String> postfix = infix2Postfix(tokens);
        double result = evalPostfix(postfix);
        return result;
    }

    // ------  Evaluate RPN expression -------------------


    public double evalPostfix(List<String> postfix){
        Stack<Double> output = new Stack<>();
        for(String p : postfix){
            if (p.matches("[0-9]*(\\.[0-9]*)?")) {
                output.push(Double.parseDouble(p));
            } else if (p.matches("([+*^/\\-])")) {
                if(output.size() < 2)
                    throw new IllegalArgumentException(MISSING_OPERAND);
                output.push(applyOperator(p, output.pop(), output.pop()));
            }
        }
        if(output.size() != 1)
            throw new IllegalArgumentException(MISSING_OPERATOR);
        return output.pop();
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



    public List<String> infix2Postfix(List<String> expression) {
        LinkedList<String> outputQueue = new LinkedList<>();
        Stack<String> operatorStack = new Stack<>();
        for (String s : expression) {
            if (s.matches("[0-9]*(\\.[0-9]*)?")) {
                outputQueue.addLast(s);
            } else if (s.matches("([+*^/\\-])")) {
                while (!operatorStack.empty() && shouldOperatorStackBePopped(s, operatorStack.peek())) {
                    outputQueue.addLast(operatorStack.pop());
                }
                operatorStack.push(s);


            } else if (s.equals("(")) {
                operatorStack.push(s);
            } else if (s.equals(")")){
                while(!operatorStack.empty() && !operatorStack.peek().equals("(")){
                    outputQueue.addLast(operatorStack.pop());
                }
                if(operatorStack.empty()){
                    throw new IllegalArgumentException(MISSING_OPERATOR);
                }
                operatorStack.pop();

            }
        }
        while(!operatorStack.empty()){
            outputQueue.addLast(operatorStack.pop());
        }
        return outputQueue;
    }

    public boolean shouldOperatorStackBePopped(String expressionOperator, String stackOperator) {
        return !stackOperator.equals("(") &&
                (hasEOLowerPrecedence(expressionOperator, stackOperator) ||
                        (isEqualPrecendece(expressionOperator, stackOperator) && isLeftAssociative(stackOperator)));
    }

    public boolean hasEOLowerPrecedence(String expressionOperator, String stackOperator) {
        return getPrecedence(expressionOperator) < getPrecedence(stackOperator);
    }

    public boolean isEqualPrecendece(String expressionOperator, String stackOperator){
        return getPrecedence(expressionOperator) == getPrecedence(stackOperator);
    }

    public boolean isLeftAssociative(String operator){
        return getAssociativity(operator) == Assoc.LEFT;
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

    public List<String> tokenize(String expression) {
        expression = expression.replaceAll("([+*^/()\\-])", " $1 ").trim();
        return Arrays.asList(expression.split("\\s+"));
    }
}
