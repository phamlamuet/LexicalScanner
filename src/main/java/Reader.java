import java.io.*;
import java.nio.charset.Charset;

public class Reader {
    BufferedReader reader;
    {
        try {
            reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream("D:\\learnSpring\\dragonbook-compiler\\java-compiler\\src\\com\\shady\\java\\compiler\\lexer\\input.vc"),
                            Charset.forName("UTF-8")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public char nextChar() {
        int c = 0;
        while(true) {
            try {
                if (!((c = reader.read()) != -1)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            char character = (char) c;
            return character;
        }
        return ' ';
    }
}
