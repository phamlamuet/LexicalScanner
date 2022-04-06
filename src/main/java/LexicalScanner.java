import com.google.common.collect.HashBasedTable;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class LexicalScanner {

    Integer state = 0;
    public static int line = 1;  //starting line
    char peek = ' '; //which char is in process
    Reader reader = new Reader();
    HashMap<String, Word> reservedWord = new HashMap<>();
    HashBasedTable<Integer, InputType, Integer> stateTable = HashBasedTable.create();

    public void reserveWord(Word word) {
        reservedWord.put(word.mLexeme, word);
    }

    public void initReservedWord() {
        reserveWord(new Word("boolean"));
        reserveWord(new Word("break"));
        reserveWord(new Word("continue"));
        reserveWord(new Word("else"));
        reserveWord(new Word("for"));
        reserveWord(new Word("float"));
        reserveWord(new Word("if"));
        reserveWord(new Word("int"));
        reserveWord(new Word("void"));
        reserveWord(new Word("while"));
        reserveWord(new Word("char"));

    }

    public void initStateTable() {
        String configFile = "D:\\LexicalScanner\\src\\main\\resources\\config.csv";
        try {
            BufferedReader br = new BufferedReader(new FileReader(configFile));
            String line = "";
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] cols = line.split(",");
                if (cols.length < 2) {
                    break;
                }
                String s = cols[0];
                stateTable.put(convertCSVCellValueToInteger(cols[0]), InputType.lessThan, convertCSVCellValueToInteger(cols[1]));
                stateTable.put(convertCSVCellValueToInteger(cols[0]), InputType.equal, convertCSVCellValueToInteger(cols[2]));
                stateTable.put(convertCSVCellValueToInteger(cols[0]), InputType.biggerThan, convertCSVCellValueToInteger(cols[3]));
                stateTable.put(convertCSVCellValueToInteger(cols[0]), InputType.character, convertCSVCellValueToInteger(cols[4]));
                stateTable.put(convertCSVCellValueToInteger(cols[0]), InputType.number, convertCSVCellValueToInteger(cols[5]));
                stateTable.put(convertCSVCellValueToInteger(cols[0]), InputType.eOrE, convertCSVCellValueToInteger(cols[6]));
                stateTable.put(convertCSVCellValueToInteger(cols[0]), InputType.dot, convertCSVCellValueToInteger(cols[7]));
                stateTable.put(convertCSVCellValueToInteger(cols[0]), InputType.plusOrMinus, convertCSVCellValueToInteger(cols[8]));
                stateTable.put(convertCSVCellValueToInteger(cols[0]), InputType.space, convertCSVCellValueToInteger(cols[9]));
                stateTable.put(convertCSVCellValueToInteger(cols[0]), InputType.separator, convertCSVCellValueToInteger(cols[10]));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static Integer convertCSVCellValueToInteger(String cellValue) {
        return Integer.parseInt(cellValue);
    }

    Integer getNextState(int currentState, char input) {
        if (Character.isDigit(input)) {
            return stateTable.get(currentState, InputType.number);
        } else if ((input == 'e' || input == 'E') && (state == 13 || state == 15)) {
            return stateTable.get(currentState, InputType.eOrE);
        } else if (Character.isLetter(input)) {
            return stateTable.get(currentState, InputType.character);
        } else if (input == '<') {
            return stateTable.get(currentState, InputType.lessThan);
        } else if (input == '=') {
            return stateTable.get(currentState, InputType.equal);
        } else if (input == '>') {
            return stateTable.get(currentState, InputType.biggerThan);
        } else if (input == '.') {
            return stateTable.get(currentState, InputType.dot);
        } else if (input == '+' || input == '-') {
            return stateTable.get(currentState, InputType.plusOrMinus);
        } else if (input == ' ' || input == '\n' || input == '\r' || input == '\b' || input == '\t' || input == '\f') {
            return stateTable.get(currentState, InputType.space);
        } else if (input == '(' || input == ')' || input == '{' || input == '}' || input == ';' || input == ',') {
            return stateTable.get(currentState, InputType.separator);
        }

        return -1;
    }

    public LexicalScanner() {
        initReservedWord();
        initStateTable();
    }

    char readNextChar() {
        return reader.nextChar();
    }

    boolean checkNextChar(char c) {
        readNextChar();
        if (peek != c) return false;
        peek = ' ';
        return true;
    }

    void retract() {
        state = 0;

    }

    public Token scan() {
        StringBuffer sb = new StringBuffer();
        while (true) {
            if (state == -1) {
                state = getNextState(0, peek);
            } else {
                peek = readNextChar();
                state = getNextState(state, peek);
                if (state == -1) {
                    System.out.println(sb);
                    return new Word(sb.toString());
                }
            }
            if (!Character.isWhitespace(peek)) {
                sb.append(peek);
            }
        }
    }
}
