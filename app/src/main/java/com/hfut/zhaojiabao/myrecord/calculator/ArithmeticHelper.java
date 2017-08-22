package com.hfut.zhaojiabao.myrecord.calculator;

import java.util.ArrayList;
import java.util.Stack;

/**
 * @author zhaojiabao 2017/5/18
 */

public class ArithmeticHelper {

    /**
     * 调度场算法
     *
     * @param infix infix 输入的字符串(中缀),未做非法检查
     * @return 后缀表达式
     */
    private static ArrayList<Element> getSuffix(String infix) {
        Stack<Element> stack = new Stack<>();
        ArrayList<Element> output = new ArrayList<>();

        ArrayList<Element> elements = getElements(infix);

        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            if (element.isNum) {
                output.add(element);
                continue;
            }
            if (stack.isEmpty()) {
                stack.push(element);
                continue;
            }
            while (!stack.isEmpty()) {
                if (!comparePriority(element.operator, stack.peek().operator)) {
                    output.add(stack.pop());
                    if (stack.isEmpty()) {
                        stack.push(element);
                        break;
                    }
                } else {
                    stack.push(element);
                    break;
                }
            }
        }

        while (!stack.isEmpty()) {
            output.add(stack.pop());
        }

        return output;
    }

    public static double calculate(String infix) {
        ArrayList<Element> output = getSuffix(infix);

        Stack<Element> stack = new Stack<>();

        for (int i = 0; i < output.size(); i++) {
            Element element = output.get(i);
            if (element.isNum) {
                stack.push(element);
            } else {
                double value1 = stack.pop().num;
                double value2 = stack.pop().num;
                double result = calculate(value2, value1, element.operator);
                stack.push(new Element(true, result, '?'));
            }
        }

        return stack.pop().num;
    }

    private static double calculate(double value1, double value2, char operator) {
        switch (operator) {
            case '+':
                return value1 + value2;
            case '-':
                return value1 - value2;
            case '*':
                return value1 * value2;
            case '/':
                return value1 / value2;
            default:
                return -1;
        }
    }

    private static boolean isOperator(char a) {
        return a == '+' || a == '-' || a == '*' || a == '/';
    }

    /**
     * 判断运算符a的优先级是否大于b
     *
     * @param a 运算符
     * @param b 运算符
     * @return a>b
     */
    private static boolean comparePriority(char a, char b) {
        return !(!isOperator(a) || !isOperator(b)) && getPriority(a) > getPriority(b);
    }

    private static int getPriority(char a) {
        if (!isOperator(a)) {
            return -1;
        }
        switch (a) {
            case '+':
            case '-':
                return 0;
            case '*':
            case '/':
                return 1;
            default:
                return -1;
        }
    }

    //TODO 这个方法要做修改
    private static ArrayList<Element> getElements(String infix) {
        ArrayList<Element> elements = new ArrayList<>();
        String str = "";
        for (int i = 0; i < infix.length(); i++) {
            char a = infix.charAt(i);
            if (!isOperator(a)) {
                str += a;
                continue;
            }
            if (!str.equals("")) {
                elements.add(new Element(true, Double.parseDouble(str), '?'));
                str = "";
            }
            elements.add(new Element(false, -1, a));

        }
        if (!str.equals("")) {
            elements.add(new Element(true, Double.parseDouble(str), '?'));
        }

        return elements;
    }

    private static class Element {
        double num;
        char operator;
        boolean isNum;

        Element(boolean isNum, double num, char operator) {
            this.num = num;
            this.isNum = isNum;
            this.operator = operator;
        }

        @Override
        public String toString() {
            return "num: " + num + " operator: " + operator + " isNum: " + isNum;
        }
    }
}
