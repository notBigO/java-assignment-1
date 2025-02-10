package org.example;

import java.util.Scanner;
import java.util.Stack;

public class Main {

    public static double evaluateExpression(String expression) {
        Stack<Double> values = new Stack<>();
        Stack<Character> operators = new Stack<>();
        char[] tokens = expression.toCharArray();

        for (int i = 0; i < tokens.length; i++) {
            char current = tokens[i];


            if (current == ' ') continue;


            if (Character.isDigit(current) || current == '.') {
                StringBuilder number = new StringBuilder();
                while (i < tokens.length && (Character.isDigit(tokens[i]) || tokens[i] == '.')) {
                    number.append(tokens[i++]);
                }
                values.push(Double.parseDouble(number.toString()));
                i--;
            } else if (current == '(') {
                operators.push(current);
            } else if (current == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                }
                if (operators.isEmpty() || operators.pop() != '(') {
                    throw new IllegalArgumentException("Mismatched parentheses detected.");
                }
            } else if ("+-*/".indexOf(current) != -1) {
                while (!operators.isEmpty() && hasPrecedence(current, operators.peek())) {
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                }
                operators.push(current);
            } else {
                throw new IllegalArgumentException("Invalid character encountered: " + current);
            }
        }

        while (!operators.isEmpty()) {
            values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
        }

        if (values.size() != 1) {
            throw new IllegalArgumentException("Invalid expression format.");
        }

        return values.pop();
    }

    private static boolean hasPrecedence(char operator1, char operator2) {
        if (operator2 == '(' || operator2 == ')') return false;

        if ((operator1 == '*' || operator1 == '/') && (operator2 == '+' || operator2 == '-')) {
            return false;
        }
        return true;
    }

    private static double applyOperator(char operator, double b, double a) {
        return switch (operator) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> {
                if (b == 0) throw new ArithmeticException("Cannot divide by zero.");
                yield a / b;
            }
            default -> throw new IllegalArgumentException("Invalid operator: " + operator);
        };
    }

    private static boolean isValidExpression(String expression) {
        if (expression == null || expression.isBlank()) return false;

        int openParentheses = 0;
        char prevChar = '\0';

        for (char c : expression.toCharArray()) {
            if ("0123456789.+-*/() ".indexOf(c) == -1) return false;

            if (c == '(') openParentheses++;
            if (c == ')') {
                openParentheses--;
                if (openParentheses < 0) return false;
            }

            if ("+-*/".indexOf(c) != -1 && "+-*/".indexOf(prevChar) != -1) return false;

            prevChar = c;
        }

        return openParentheses == 0;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try (scanner) {
            System.out.println("Welcome to the Mathematical Expression Evaluator!");
            System.out.print("Enter a mathematical expression (e.g., (2+3)*4/5): ");
            String expression = scanner.nextLine();


            if (!isValidExpression(expression)) {
                System.out.println("Error: The entered expression is invalid. Please check the syntax.");
                return;
            }
            
            double result = evaluateExpression(expression);
            System.out.printf("Result: %.2f%n", result);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}