import com.google.common.collect.HashBasedTable;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class LexicalScanner {
    Integer state = 0;
    public static int line = 1;  //starting line
    int startPointer = 0;
    int forwardPointer = 0;
    static String inputFilePath = "D:\\LexicalScanner\\src\\main\\resources\\input.vc";
    String inputData = "";
    CommentsCleaner commentsCleaner = new CommentsCleaner();
    HashMap<String, Word> reservedWord = new HashMap<>();
    HashBasedTable<Integer, InputType, Integer> stateTable = HashBasedTable.create();
    HashMap<Integer, State> stateHashMap = new HashMap<>();

    public void reserveWord(Word word) {
        reservedWord.put(word.mLexeme, word);
    }

    public LexicalScanner() {
        initReservedWord();
        initStateTable();
        initStateObjectMap();
        inputData = commentsCleaner.clean();
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

    public void initStateObjectMap() {
        String configStateFile = "D:\\LexicalScanner\\src\\main\\resources\\config2.txt";
        try {
            BufferedReader br = new BufferedReader(new FileReader(configStateFile));
            String line;
            //read all normal states
            line = br.readLine();
            String[] cols = line.split(",");
            for (int i = 0; i < cols.length; i++) {
                int id = Integer.parseInt(cols[i]);
                stateHashMap.put(id, new State(id, false, false));
                //System.out.println(stateHashMap.get(id));
            }
            // read all final state but not retract
            line = br.readLine();
            cols = line.split(",");
            for (int i = 0; i < cols.length; i++) {
                int id = Integer.parseInt(cols[i]);
                stateHashMap.put(id, new State(id, false, true));
                //System.out.println(stateHashMap.get(id));
            }
            //read all final state but not retract
            line = br.readLine();
            cols = line.split(",");
            for (String col : cols) {
                int id = Integer.parseInt(col);
                stateHashMap.put(id, new State(id, true, true));
                //System.out.println(stateHashMap.get(id));
            }
            //
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                stateTable.put(convertCSVCellValueToInteger(cols[0]), InputType.lessThan, convertCSVCellValueToInteger(cols[1]));
                stateTable.put(convertCSVCellValueToInteger(cols[0]), InputType.equal, convertCSVCellValueToInteger(cols[2]));
                stateTable.put(convertCSVCellValueToInteger(cols[0]), InputType.biggerThan, convertCSVCellValueToInteger(cols[3]));
                stateTable.put(convertCSVCellValueToInteger(cols[0]), InputType.letter, convertCSVCellValueToInteger(cols[4]));
                stateTable.put(convertCSVCellValueToInteger(cols[0]), InputType.digit, convertCSVCellValueToInteger(cols[5]));
                stateTable.put(convertCSVCellValueToInteger(cols[0]), InputType.eOrE, convertCSVCellValueToInteger(cols[6]));
                stateTable.put(convertCSVCellValueToInteger(cols[0]), InputType.dot, convertCSVCellValueToInteger(cols[7]));
                stateTable.put(convertCSVCellValueToInteger(cols[0]), InputType.plusOrMinus, convertCSVCellValueToInteger(cols[8]));
                stateTable.put(convertCSVCellValueToInteger(cols[0]), InputType.plusOrMinus, convertCSVCellValueToInteger(cols[9]));
                stateTable.put(convertCSVCellValueToInteger(cols[0]), InputType.multiply, convertCSVCellValueToInteger(cols[10]));
                stateTable.put(convertCSVCellValueToInteger(cols[0]), InputType.slashOrDivideMark, convertCSVCellValueToInteger(cols[11]));
                stateTable.put(convertCSVCellValueToInteger(cols[0]), InputType.exclamationMark, convertCSVCellValueToInteger(cols[12]));
                stateTable.put(convertCSVCellValueToInteger(cols[0]), InputType.space, convertCSVCellValueToInteger(cols[13]));
                stateTable.put(convertCSVCellValueToInteger(cols[0]), InputType.separator, convertCSVCellValueToInteger(cols[14]));
                stateTable.put(convertCSVCellValueToInteger(cols[0]), InputType.straightSlashMark, convertCSVCellValueToInteger(cols[15]));
                stateTable.put(convertCSVCellValueToInteger(cols[0]), InputType.andMark, convertCSVCellValueToInteger(cols[16]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static Integer convertCSVCellValueToInteger(String cellValue) {
        return Integer.parseInt(cellValue);
    }

    Integer getNextState(int currentState, char input) {
        if (Character.isDigit(input)) {
            return stateTable.get(currentState, InputType.digit);
        } else if ((input == 'e' || input == 'E') && (state == 19||state==21) ) {  //processing
            return stateTable.get(currentState, InputType.eOrE);
        } else if (Character.isLetter(input) || input == '_') {
            return stateTable.get(currentState, InputType.letter);  //for identifier
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
        } else if (input == '(' || input == ')' || input == '{' || input == '}' || input == ';' || input == ',' || input == '[' || input == ']') {
            return stateTable.get(currentState, InputType.separator);
        } else if (input == '!') {
            return stateTable.get(currentState, InputType.exclamationMark);
        } else if (input == '*') {
            return stateTable.get(currentState, InputType.multiply);
        } else if (input == '/') {
            return stateTable.get(currentState, InputType.slashOrDivideMark);
        } else if (input == '|') {
            return stateTable.get(currentState, InputType.straightSlashMark);
        } else if (input == '&') {
            return stateTable.get(currentState, InputType.andMark);
        }

        return -1;
    }


    void retract() {
        state = 0;
    }

    boolean isFinalState(int state) {
        return stateHashMap.get(state).accepted;
    }

    boolean isRetractState(int state) {
        return stateHashMap.get(state).retract;
    }

    public Token scan() {
        while (forwardPointer!=inputData.length()) {
            char peek = inputData.charAt(forwardPointer);
            state = getNextState(state, peek);
            if (isFinalState(state)) {
                if (isRetractState(state)) {
//                    System.out.println(sb);
//                    state = 0;
//                    return new Word(sb.toString());
                    int tempStartPointer = startPointer;
                    startPointer = forwardPointer;
                    state = 0;
                    Word word = new Word(inputData.substring(tempStartPointer, forwardPointer));
                    System.out.println(word);
                    return word;
                } else {
//                    System.out.println(sb);
//                    state = 0;
//                    forwardPointer++;
//                    return new Word(sb.toString());
                    int tempStartPointer = startPointer;
                    forwardPointer++;
                    startPointer = forwardPointer;
                    state = 0;
                    Word word = new Word(inputData.substring(tempStartPointer, forwardPointer));
                    System.out.println(word);
                    return word;

                }
            } else {
                forwardPointer++;
            }
        }
        return new Word("");
    }
}
