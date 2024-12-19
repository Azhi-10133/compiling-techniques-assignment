import java.util.List;

public class App {
    public static void main(String[] args) {
        Lexer lexer = new Lexer("id + id * ( id )");
        List<Token> tokens = lexer.tokenize();
        for (Token t : tokens) {
            System.out.println(t);
        }
    }
}
