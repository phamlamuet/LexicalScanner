import java.util.Map;

public class Main {
    public static void main(String[] args) {
       LexicalScanner lexicalScanner = new LexicalScanner();
      //  System.out.println(lexicalScanner.getNextState(4,' '));
        // int x = lexicalScanner.inputData.length();
       while (lexicalScanner.forwardPointer!=lexicalScanner.inputData.length()){
           lexicalScanner.scan();
       }
    }
}
