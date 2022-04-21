import com.google.common.collect.HashBasedTable;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;

public class LexicalScanner {
    Integer state = 0;
    int startPointer = 0;
    int forwardPointer = 0;
    static String inputFilePath = "D:\\LexicalScanner\\src\\main\\resources\\input.vc";
    static String outputFilePath = "D:\\LexicalScanner\\src\\main\\java\\output.vctok";
    static String transitionTableFilePath = "D:\\LexicalScanner\\src\\main\\resources\\config.csv";
    static String configStateFilePath = "D:\\LexicalScanner\\src\\main\\resources\\config2.txt";
    String inputData = "";
    CommentsProcess commentsProcess = new CommentsProcess();
    HashMap<String, Token> reservedWord = new HashMap<>();
    HashBasedTable<Integer, InputType, Integer> stateTable = HashBasedTable.create();
    HashMap<Integer, State> stateHashMap = new HashMap<>();
    HashMap<Integer, String> tokenTypeMap = new HashMap<>();

    public void reserveWord(Token word) {
        reservedWord.put(word.mLexeme, word);
    }

    public LexicalScanner() {
        initReservedWord();
        initStateTable();
        initStateObjectMap();
        //inputData = commentsProcess.clean();
        readInputFile();
        initTypeMap();
    }

    public void readInputFile() {
        Path fileName
                = Path.of(LexicalScanner.inputFilePath);
        try {
            inputData = Files.readString(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initReservedWord() {
        reserveWord(new Token("boolean"));
        reserveWord(new Token("break"));
        reserveWord(new Token("continue"));
        reserveWord(new Token("else"));
        reserveWord(new Token("for"));
        reserveWord(new Token("float"));
        reserveWord(new Token("if"));
        reserveWord(new Token("int"));
        reserveWord(new Token("void"));
        reserveWord(new Token("while"));
        reserveWord(new Token("char"));
    }

    public void initTypeMap() {
        tokenTypeMap.put(2, TokenType.OPERATOR);
        tokenTypeMap.put(5, TokenType.OPERATOR);
        tokenTypeMap.put(6, TokenType.OPERATOR);
        tokenTypeMap.put(8, TokenType.OPERATOR);
        tokenTypeMap.put(9, TokenType.OPERATOR);
        tokenTypeMap.put(11, TokenType.OPERATOR);
        tokenTypeMap.put(12, TokenType.OPERATOR);
        tokenTypeMap.put(13, TokenType.OPERATOR);
        tokenTypeMap.put(15, TokenType.OPERATOR);
        tokenTypeMap.put(17, TokenType.OPERATOR);

        tokenTypeMap.put(18, TokenType.SEPARATOR);

        tokenTypeMap.put(25, TokenType.REAL_NUMBER);
        tokenTypeMap.put(26, TokenType.REAL_NUMBER);
        tokenTypeMap.put(27, TokenType.REAL_NUMBER);
        tokenTypeMap.put(38, TokenType.REAL_NUMBER);

        tokenTypeMap.put(29, TokenType.DELIMITER);

        tokenTypeMap.put(37, TokenType.IDENTIFIER);

        tokenTypeMap.put(42, TokenType.COMMENT);
        tokenTypeMap.put(45, TokenType.COMMENT);


    }

    public void initStateObjectMap() {
        String configStateFile = configStateFilePath;
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
            // read all final states but not retract
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
        String configFile = transitionTableFilePath;
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
                stateTable.put(convertCSVCellValueToInteger(cols[0]), InputType.endLine, convertCSVCellValueToInteger(cols[17]));
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
        } else if ((input == 'e' || input == 'E') && (state == 19 || state == 21)) {  //processing
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
        } else if (input == ' ' || input == '\r' || input == '\b' || input == '\t' || input == '\f') {
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
        } else if (input == '\n') {
            return stateTable.get(currentState, InputType.endLine);
        }
        return -1;
    }

    boolean isFinalState(int state) {
        return stateHashMap.get(state).accepted;
    }

    boolean isRetractState(int state) {
        return stateHashMap.get(state).retract;
    }

    void fail(char input) {

        while (true) {
            forwardPointer++;
            if (inputData.charAt(forwardPointer) == ' ') {
                break;
            }
        }
        System.out.println("Error token " + inputData.substring(startPointer, forwardPointer));
        startPointer = forwardPointer;
        state = 0;
    }

    void writeTokensToOutputFile() {
        Path fileName
                = Path.of(LexicalScanner.outputFilePath);
        String text = "";
        try {
            Files.write(fileName, text.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Token token = new Token("");

        try {
            while (forwardPointer != inputData.length()) {
                token = scan();
                if (token.tokenType == TokenType.DELIMITER || token.tokenType == TokenType.COMMENT) {
                    continue;
                }
                text = token.toString() + "\n";
//                if (text.isEmpty() || text.isBlank()) {
//                    continue;
//                }
                Files.write(fileName, text.getBytes(), StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Token getToken(int start, int end, int acceptState) {
        String lexeme = inputData.substring(start, end);
        String type = tokenTypeMap.get(acceptState);
        if(reservedWord.get(lexeme)!=null){
            type = TokenType.KEYWORD;
        }
        return new Token(lexeme, type);
    }

    public Token scan() {
        while (forwardPointer != inputData.length()) {
            char peek = inputData.charAt(forwardPointer);
            state = getNextState(state, peek);
            if (state == null || state == -1) {
                fail(inputData.charAt(startPointer));
                break;
            }
            if (isFinalState(state)) {
                if (isRetractState(state)) {
                    int tempStartPointer = startPointer;
                    startPointer = forwardPointer;

                    //Token word = new Token(inputData.substring(tempStartPointer, forwardPointer));
                    Token word = getToken(tempStartPointer, forwardPointer, state);
                    state = 0;
                    //  System.out.println(word);
                    return word;
                } else {
                    int tempStartPointer = startPointer;
                    forwardPointer++;
                    startPointer = forwardPointer;

                    //Token word = new Token(inputData.substring(tempStartPointer, forwardPointer));
                    Token word = getToken(tempStartPointer, forwardPointer, state);
                    state = 0;
                    // System.out.println(word);
                    return word;

                }
            } else {
                forwardPointer++;
            }

        }
        return new Token("", "");
    }

}
