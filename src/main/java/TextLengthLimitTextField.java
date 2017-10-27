import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * Created by user on 27.07.17.
 */
public class TextLengthLimitTextField extends JTextField
{
    protected int textLengthLimit = -1;
    protected String symbols;
    protected String acceptedChars;

    public TextLengthLimitTextField()
    {
        super();
    }

    public TextLengthLimitTextField(String text)
    {
        super(text);
    }

    public int getTextLengthLimit()
    {
        return textLengthLimit;
    }

    public void setTextLengthLimit(int textLengthLimit)
    {
        this.textLengthLimit = textLengthLimit;
    }

    public void setSymbols(String symbols) {

        if (symbols.equals("num")){
            acceptedChars   = "0123456789";
        }
        if (symbols.equals("alpha")){
            acceptedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        }
        if (symbols.equals("all")){
            acceptedChars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        }
    }

    protected Document createDefaultModel()
    {
        return new TextLengthLimitDocument();
    }

    protected class TextLengthLimitDocument extends PlainDocument
    {
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException
        {
            if (str == null) return;
            String insertStr = str;
            if (textLengthLimit > 0)
            {
                int curLength = this.getLength();
                if (curLength >= textLengthLimit) return;
                int insertLength = insertStr.length();
                if ((curLength + insertLength) > textLengthLimit)
                    insertStr = insertStr.substring(0, (textLengthLimit - curLength));
            }
            for (int i=0; i < str.length(); i++) {
                if (!acceptedChars.contains(String.valueOf(str.charAt(i))))
                    return;
            }
            super.insertString(offs, insertStr, a);
        }
    }
}
