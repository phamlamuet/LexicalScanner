public class Token {
    String mLexeme;
    public Token(String mLexeme) {
        this.mLexeme = mLexeme;
    }

    @Override
    public String toString() {
        return mLexeme;
    }
}
