public class Word implements Token{
    String mLexeme;
    public Word(String mLexeme) {
        this.mLexeme = mLexeme;
    }

    @Override
    public String toString() {
        return mLexeme;
    }
}
