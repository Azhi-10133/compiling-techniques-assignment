import java.util.*;

public class App {
    public static void main(String[] args) {
        String input = "a + a";


        TopDownParser topDown = new TopDownParser(input);
        boolean topDownResult = topDown.parse();
        System.out.println("Top-Down Parser result for \"" + input + "\": " + topDownResult);

        BottomUpParser bottomUp = new BottomUpParser(input);
        boolean bottomUpResult = bottomUp.parse();
        System.out.println("Bottom-Up Parser result for \"" + input + "\": " + bottomUpResult);
    }
}