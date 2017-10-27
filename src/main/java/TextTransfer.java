import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;

/**
 * Created by user on 04.10.17.
 */
public class TextTransfer implements ClipboardOwner{

    StringSelection stringSelection;

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {

    }

    public void setData(String data){
        stringSelection = new StringSelection(data);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, this);
    }

    public String getData() throws IOException, UnsupportedFlavorException {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        return (String) clipboard.getData(DataFlavor.stringFlavor);
    }
}
