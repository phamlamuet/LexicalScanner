public class Token {
    String mLexeme;
    String tokenType;
    public Token(String mLexeme) {
        this.mLexeme = mLexeme;
    }

    public Token(String mLexeme, String tokenType) {
        this.mLexeme = mLexeme;
        this.tokenType = tokenType;
    }

    @Override
    public String toString() {
        return mLexeme +" "+ tokenType;
    }
}
