import java.util.*;

public class Parser {
    static class Grammar {
        String[] rules = {
                "E -> E + T",
                "E -> T",
                "T -> T * F",
                "T -> F",
                "F -> ( E )",
                "F -> id"
        };
    }


    static class TopDownParser {
        private final String input;
        private int pos = 0;

        public TopDownParser(String input) {
            this.input = input.replaceAll("\\s+", "") + "$";
        }

        public boolean parse() {
            return E() && match('$');
        }

        private boolean E() {
            int savedPos = pos;
            if (T() && EPrime()) return true;
            pos = savedPos;
            return false;
        }

        private boolean EPrime() {
            int savedPos = pos;
            if (match('+') && T() && EPrime()) return true;
            pos = savedPos;
            return true;
        }

        private boolean T() {
            int savedPos = pos;
            if (F() && TPrime()) return true;
            pos = savedPos;
            return false;
        }

        private boolean TPrime() {
            int savedPos = pos;
            if (match('*') && F() && TPrime()) return true;
            pos = savedPos;
            return true;
        }

        private boolean F() {
            int savedPos = pos;
            if (match('(') && E() && match(')')) return true;
            pos = savedPos;
            if (match('i') && match('d')) return true;
            pos = savedPos;
            return false;
        }

        private boolean match(char expected) {
            if (pos < input.length() && input.charAt(pos) == expected) {
                pos++;
                return true;
            }
            return false;
        }
    }


    static class BottomUpParser {
        private final String input;

        public BottomUpParser(String input) {
            this.input = input.replaceAll("\\s+", "") + "$";
        }

        public boolean parse() {
            Stack<String> stack = new Stack<>();
            String[] tokens = input.split("");
            int pos = 0;

            while (pos < tokens.length) {
                stack.push(tokens[pos]);
                pos++;
                while (canReduce(stack)) {
                    reduce(stack);
                }
            }

            return stack.size() == 1 && stack.peek().equals("E");
        }

        private boolean canReduce(Stack<String> stack) {
            String top = stack.toString();


            return top.contains("id") ||
                    top.contains("( E )") ||
                    top.contains("T * F") ||
                    top.contains("E + T");
        }

        private void reduce(Stack<String> stack) {
            String top = String.join(" ", stack);

            if (top.contains("( E )")) {
                stack.pop();
                stack.pop();
                stack.pop();
                stack.push("F");
            } else if (top.contains("id")) {
                stack.pop();
                stack.push("F");
            } else if (top.contains("T * F")) {
                stack.pop();
                stack.pop();
                stack.pop();
                stack.push("T");
            } else if (top.contains("E + T")) {
                stack.pop();
                stack.pop();
                stack.pop();
                stack.push("E");
            } else if (top.contains("F")) {
                stack.pop();
                stack.push("T");
            }
        }
    }


    public static void main(String[] args) {
        String input = "id + id * id";


        TopDownParser topDownParser = new TopDownParser(input);
        System.out.println("Top-Down Parsing Result: " + (topDownParser.parse() ? "Accepted" : "Rejected"));


        BottomUpParser bottomUpParser = new BottomUpParser(input);
        System.out.println("Bottom-Up Parsing Result: " + (bottomUpParser.parse() ? "Accepted" : "Rejected"));
    }
}
