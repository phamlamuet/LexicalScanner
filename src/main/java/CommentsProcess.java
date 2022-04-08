import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class CommentsProcess {
    static class Match {
        int start;
        String text;
    }
    static List<Match> commentMatches = new ArrayList<Match>();
    public String clean(){
        Pattern commentsPattern = Pattern.compile("(//.*?$)|(/\\*.*?\\*/)", Pattern.MULTILINE | Pattern.DOTALL);
        Pattern stringsPattern = Pattern.compile("(\".*?(?<!\\\\)\")");

        Path fileName
                = Path.of(LexicalScanner.inputFilePath);
        String text = null;
        try {
            text = Files.readString(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Matcher commentsMatcher = commentsPattern.matcher(text);
        while (commentsMatcher.find()) {
            Match match = new Match();
            match.start = commentsMatcher.start();
            match.text = commentsMatcher.group();
            commentMatches.add(match);
        }

        List<Match> commentsToRemove = new ArrayList<Match>();

        Matcher stringsMatcher = stringsPattern.matcher(text);
        while (stringsMatcher.find()) {
            for (Match comment : commentMatches) {
                if (comment.start > stringsMatcher.start() && comment.start < stringsMatcher.end())
                    commentsToRemove.add(comment);
            }
        }
        for (Match comment : commentsToRemove)
            commentMatches.remove(comment);

        for (Match comment : commentMatches)
            text = text.replace(comment.text, " ");

        try {
            Files.write(fileName, text.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text+" ";
    }
}
