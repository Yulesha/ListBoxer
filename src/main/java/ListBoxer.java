import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 27.07.17.
 */
public class ListBoxer extends JFrame {
    private ArrayList<String> recordsList = new ArrayList<>();

    private TextLengthLimitTextField input = new TextLengthLimitTextField("");
    private JTextArea text = new JTextArea(9, 30);
    private JLabel recordsTotalCount = new JLabel("0");
    private JLabel recordsCountInList = new JLabel("0");
    private JCheckBox checkAlpha = new JCheckBox("Alphabetic", false);
    private JCheckBox checkNum = new JCheckBox("Numeric", false);
    private String[] rangeValuesAlpha = {"All", "<none>", "a-m", "n-z"};
    private String[] rangeValuesNum = {"All", "<none>", "0-100", "101-200", "201-300", "301-9999"};
    private String[] rangeValuesAll = {"All", "<none>", "0-100", "101-200", "201-300", "301-9999", "a-m", "n-z"};
    private JComboBox rangeList = new JComboBox(rangeValuesAlpha);
    private JRadioButton radioDesc = new JRadioButton("Descending");
    private JRadioButton radioAsc = new JRadioButton("Ascending");


    public ListBoxer() {
        super("ListBoxer");
        this.setSize(500, 280);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar jmb = new JMenuBar();
        JMenu jmFile = new JMenu("File");
        JMenuItem jmiOpen = new JMenuItem("Open");
        jmiOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        jmiOpen.addActionListener(new OpenButton());
        JMenuItem jmiExit = new JMenuItem("Exit");
        jmiExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
        jmiExit.addActionListener(event -> System.exit(0));
        JMenuItem jmiSaveAs = new JMenuItem("Save as");
        jmiSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        jmiSaveAs.addActionListener(new SaveButton());
        jmFile.add(jmiOpen);
        jmFile.add(jmiSaveAs);
        jmFile.addSeparator();
        jmFile.add(jmiExit);
        JMenu jmEdit = new JMenu("Edjt");
        JMenuItem jmiUndo = new JMenuItem("Undo");
        jmiUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
        JMenuItem jmiCopy = new JMenuItem("Copy");
        jmiCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
        jmiCopy.setEnabled(false);
        JMenuItem jmiCut = new JMenuItem("Cut");
        jmiCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        jmiCut.setEnabled(false);
        JMenuItem jmiPaste = new JMenuItem("Paste");
        jmiPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
        jmiPaste.setEnabled(false);
        jmEdit.add(jmiUndo);
        jmEdit.addSeparator();
        jmEdit.add(jmiCopy);
        jmEdit.add(jmiCut);
        jmEdit.add(jmiPaste);
        JMenu jmHelp = new JMenu("Help");
        JMenuItem jmiAbout = new JMenuItem("About");
        jmHelp.add(jmiAbout);
        jmiAbout.addActionListener(new AboutButton());

        final JPopupMenu menu = new JPopupMenu();
        final JMenuItem jpiCopy = new JMenuItem("Copy");
        //jpiCopy.setEnabled(false);
        menu.add(jpiCopy);
        final JMenuItem jpiCut = new JMenuItem("Cut");
        //jpiCopy.setEnabled(false);
        menu.add(jpiCut);
        JMenuItem jpiPaste = new JMenuItem("Paste");
        menu.add(jpiPaste);
        input.setComponentPopupMenu(menu);
        input.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                if (input.getSelectedText()!=null){
                    jpiCopy.setEnabled(true);
                    jpiCut.setEnabled(true);

                }
                else{
                    jpiCopy.setEnabled(false);
                    jpiCut.setEnabled(false);

                }
                if (evt.isPopupTrigger()) {

                    menu.show(evt.getComponent(), evt.getX(), evt.getY());
                    System.out.println(input.getSelectedText());

                }
            }

            public void mouseReleased(MouseEvent evt) {
                if (input.getSelectedText()!=null){
                    jpiCopy.setEnabled(true);
                    jpiCut.setEnabled(true);
                }
                else{
                    jpiCopy.setEnabled(false);
                    jpiCut.setEnabled(false);
                }
                if (evt.isPopupTrigger()) {
                   menu.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }
        });
        jpiCopy.addActionListener(new ClickCopy());
        jpiCut.addActionListener(new ClickCut());
        jpiPaste.addActionListener(new ClickPaste());

        jmb.add(jmFile);
        jmb.add(jmEdit);
        jmb.add(jmHelp);

        this.setJMenuBar(jmb);
        this.setVisible(true);

        JPanel pnlInput = new JPanel();
        pnlInput.setLayout(new FlowLayout());
        JLabel labelRange = new JLabel("Range:");
        pnlInput.add(labelRange);
        pnlInput.add(rangeList);
        input.setColumns(10);
        input.addKeyListener(new KeyAdapter() {
            boolean ctrlPressed = false;
            boolean cPressed = false;
            boolean xPressed = false;
            boolean vPressed = false;

            @Override
            public void keyPressed(KeyEvent e) {
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_C:
                        cPressed=true;
                        break;
                    case KeyEvent.VK_V:
                        vPressed=true;
                        break;
                    case KeyEvent.VK_X:
                        xPressed=true;
                        break;
                    case KeyEvent.VK_CONTROL:
                        ctrlPressed=true;
                        break;
                    case KeyEvent.VK_ENTER:
                        checkInputValue();
                        break;
                }

                blockPress(e);
            }

            private void blockPress(KeyEvent e){
                if(ctrlPressed && cPressed) {
                    System.out.println("Blocked CTRl+C");
                    e.consume();// Stop the event from propagating.
                }
                if(ctrlPressed && vPressed) {
                    System.out.println("Blocked CTRl+V");
                    e.consume();// Stop the event from propagating.
                }
                if(ctrlPressed && xPressed) {
                    System.out.println("Blocked CTRl+X");
                    e.consume();// Stop the event from propagating.
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_C:
                        cPressed=false;
                        break;
                    case KeyEvent.VK_X:
                        xPressed=false;
                        break;
                    case KeyEvent.VK_V:
                        vPressed=false;
                        break;
                    case KeyEvent.VK_CONTROL:
                        ctrlPressed=false;
                        break;
                }

                blockPress(e);
            }
        });
        pnlInput.add(input);
        JButton buttonAdd = new JButton("Add to List");
        pnlInput.add(buttonAdd);
        JButton buttonClear = new JButton("Clear List");
        pnlInput.add(buttonClear);
        this.add(pnlInput, BorderLayout.NORTH);
        buttonClear.addActionListener(new ClearButtonClick());
        buttonAdd.addActionListener(new AddButtonClick());
        rangeList.addItemListener(new ChangeRange());


        JPanel pnlSelect = new JPanel();
        pnlSelect.setLayout(new GridLayout(2, 1));

        JPanel pnlSort = new JPanel();
        pnlSort.setBorder(BorderFactory.createTitledBorder("Sort"));
        pnlSort.setLayout(new GridLayout(2, 1));
        ButtonGroup group = new ButtonGroup();
        group.add(radioDesc);
        group.add(radioAsc);
        pnlSort.add(radioDesc);
        pnlSort.add(radioAsc);
        pnlSelect.add(pnlSort);
        radioDesc.addItemListener(new SelectDesc());
        radioAsc.addItemListener(new SelectAsc());

        JPanel pnlSymbols = new JPanel();
        pnlSymbols.setBorder(BorderFactory.createTitledBorder("Symbols"));
        pnlSymbols.setLayout(new GridLayout(2, 1));
        checkAlpha.setSelected(true);
        input.setTextLengthLimit(8);
        input.setSymbols("alpha");
        pnlSymbols.add(checkAlpha);
        pnlSymbols.add(checkNum);
        pnlSelect.add(pnlSymbols);
        checkAlpha.addItemListener(new SelectAlpha());
        checkNum.addItemListener(new SelectNum());


        this.add(pnlSelect, BorderLayout.WEST);


        JPanel pnlText = new JPanel();
        text.setEnabled(false);
        pnlText.add(text);
        JLabel recordsTotal = new JLabel("Records total count: ");
        pnlText.add(recordsTotal);
        pnlText.add(recordsTotalCount);
        JLabel recordsInList = new JLabel("Records count in list: ");
        pnlText.add(recordsInList);
        pnlText.add(recordsCountInList);
        this.add(pnlText);

    }


    public class ClearButtonClick implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            text.setText(null);
            recordsList = new ArrayList<String>();
            recordsTotalCount.setText(String.valueOf(recordsList.size()));
            recordsCountInList.setText(String.valueOf(text.getLineCount() - 1));
        }
    }

    public class AboutButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
           JOptionPane.showMessageDialog(null, "ListBoxer v.2 - program for testing" );
        }
    }

    public class ClickCopy implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            TextTransfer copiedText = new TextTransfer();
            copiedText.setData(input.getSelectedText());
           }
    }

    public class ClickCut implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            TextTransfer cutText = new TextTransfer();
            cutText.setData(input.getSelectedText());
            input.setText(input.getText().substring(0, input.getSelectionStart()));
        }
    }

    public class ClickPaste implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            TextTransfer pastedText = new TextTransfer();
            try {
                input.setText(pastedText.getData());
            } catch (IOException | UnsupportedFlavorException e1) {
                e1.printStackTrace();
            }
        }
    }

    public class AddButtonClick implements ActionListener {
        public void actionPerformed(ActionEvent e) {
           checkInputValue();
        }
    }

    private void checkInputValue(){
        if (!input.getText().equals("")) {
            if (!checkAlpha.isSelected()) {
                    Integer x = Integer.parseInt(input.getText());
                    if (x <= 10000) addText();
                    else {
                        JOptionPane.showMessageDialog(null, "Value is wrong");

                    }
               }
            else  addText();

            }
            if (radioDesc.isSelected())  sortDesc();
            if (radioAsc.isSelected())  sortAsc();
    }

    private void sortDesc(){
        String curList = text.getText();
        String[] curListArray = curList.split("\n");
        Arrays.sort(curListArray, Collections.reverseOrder());
        String temp = curListArray[curListArray.length-1];
        curListArray[curListArray.length-1] = curListArray[curListArray.length-2];
        curListArray[curListArray.length-2] = temp;
        StringBuilder sortedText = new StringBuilder();
        for (String x : curListArray) {
            sortedText.append(x).append("\n");
        }
        text.setText(sortedText.toString());
    }

    private void sortAsc(){
        String curList = text.getText();
        String[] curListArray = curList.split("\n");
        Arrays.sort(curListArray);
        StringBuilder sortedText = new StringBuilder();
        for (String x : curListArray) {
            sortedText.append(x).append("\n");
        }
        text.setText(sortedText.toString());
    }

    private void addText(){
        text.append(input.getText() + "\n");
        recordsList.add(input.getText());
        input.setText("");
        recordsTotalCount.setText(String.valueOf(recordsList.size()));
        //recordsCountInList.setText(String.valueOf(text.getLineCount() - 1));
        checkRange(rangeList.getSelectedItem().toString());
    }

    public class SaveButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = createFileChooser();
            if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                String path = fileChooser.getSelectedFile().getAbsolutePath();
                if (!path.substring(path.length() - 4, path.length()).equalsIgnoreCase(
                        ".lbx")) {
                    path += ".lbx";
                }
                File file = new File(path);
                // save to file
                StringBuilder builder = new StringBuilder();
                for (String x : recordsList) {
                    builder.append(x).append("\n");
                }
                //String outputText = text.getText(); // строка для записи
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    // перевод строки в байты
                    byte[] buffer = builder.toString().getBytes();

                    fos.write(buffer, 0, buffer.length);
                } catch (Exception ex) {

                    System.out.println(ex.getMessage());
                }

            }
        }
    }


    private JFileChooser createFileChooser(){
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("List Boxer files (*.lbx)", "lbx");
        fileChooser.setFileFilter(filter);
        fileChooser.addChoosableFileFilter(filter);
        return fileChooser;
    }

    public class OpenButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = createFileChooser();
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                // open from file
                BufferedReader br = null;
                FileReader fr = null;

                try {

                    fr = new FileReader(file);
                    br = new BufferedReader(fr);

                    String sCurrentLine;
                    br.readLine();
                    text.setText("");
                    recordsList = new ArrayList<String>();

                    while ((sCurrentLine = br.readLine()) != null) {
                        text.append(sCurrentLine + "\n");
                        recordsList.add(sCurrentLine);
                    }

                } catch (Exception ex) {

                    ex.printStackTrace();

                } finally {

                    try {

                        if (br != null)
                            br.close();

                        if (fr != null)
                            fr.close();

                    } catch (IOException ex) {

                        ex.printStackTrace();

                    }

                }

            }
            recordsCountInList.setText(String.valueOf(text.getLineCount()-1));
            recordsTotalCount.setText(String.valueOf(recordsList.size()));
        }
    }

    public class SelectAlpha implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            input.setEnabled(true);
            if (checkAlpha.isSelected() && !checkNum.isSelected()) {
                input.setTextLengthLimit(8);
                input.setSymbols("alpha");
                rangeList.setModel(new DefaultComboBoxModel(rangeValuesAlpha));
                rangeList.setEnabled(true);

            }
            if (!checkAlpha.isSelected() && checkNum.isSelected()) {
                input.setSymbols("num");
                rangeList.setModel(new DefaultComboBoxModel(rangeValuesNum));
                rangeList.setEnabled(true);
                }
            if (!checkAlpha.isSelected() && !checkNum.isSelected()) {
                input.setEnabled(false);
                //text.setText("");
                rangeList.setEnabled(false);
            }
            if (checkAlpha.isSelected() && checkNum.isSelected()) {
                input.setTextLengthLimit(8);
                input.setSymbols("all");
                rangeList.setModel(new DefaultComboBoxModel(rangeValuesAll));
                rangeList.setEnabled(true);
               }
            displayAllItems();
        }
    }

    private void displayAllItems(){
        StringBuilder newText = new StringBuilder();
        for (String x : recordsList) {
            newText.append(x).append("\n");
        }
        text.setText(newText.toString());
        recordsCountInList.setText(String.valueOf(text.getLineCount() - 1));
    }

    public class SelectNum implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            input.setEnabled(true);
            if (checkNum.isSelected() && !checkAlpha.isSelected()) {
                input.setSymbols("num");
                rangeList.setModel(new DefaultComboBoxModel(rangeValuesNum));
                rangeList.setEnabled(true);

            }
            if (!checkNum.isSelected() && checkAlpha.isSelected()) {
                input.setTextLengthLimit(8);
                input.setSymbols("alpha");
                rangeList.setModel(new DefaultComboBoxModel(rangeValuesAlpha));
                rangeList.setEnabled(true);

            }
                if (!checkAlpha.isSelected() && !checkNum.isSelected()) {
                    input.setEnabled(false);
                    //text.setText("");
                    rangeList.setEnabled(false);
                }
                if (checkAlpha.isSelected() && checkNum.isSelected()) {
                    input.setTextLengthLimit(8);
                    input.setSymbols("all");
                    rangeList.setModel(new DefaultComboBoxModel(rangeValuesAll));
                    rangeList.setEnabled(true);

                }
            displayAllItems();

            }


        }


        public class SelectDesc implements ItemListener {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() != ItemEvent.SELECTED) {
                    return;
                }
                sortDesc();
            }

        }

        public class SelectAsc implements ItemListener {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() != ItemEvent.SELECTED) {
                    return;
                }
                sortAsc();

            }

        }

        public class ChangeRange implements ItemListener {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                   String strItem = e.getItem().toString();
                   checkRange(strItem);

                }
            }

        }

        private void checkRange(String item){
            if (item.equals("<none>")) {
                text.setText("");
                recordsCountInList.setText(String.valueOf(text.getLineCount() - 1));
            }
            if (item.equals("All")) {
                displayAllItems();
            }
            if (item.equals("0-100")) {
                applyRangeNum(0, 100);
                return;
            }
            if (item.equals("101-200")) {
                applyRangeNum(101, 200);
                return;
            }
            if (item.equals("201-300")) {
                applyRangeNum(201, 300);
                return;
            }
            if (item.equals("301-9999")) {
                applyRangeNum(301, 9999);
                return;
            }
            if (item.equals("a-m")) {
                Pattern p = Pattern.compile("^[a-mA-M]");
                applyRangeAlpha(p);
                return;
            }
            if (item.equals("n-z")) {
                Pattern p = Pattern.compile("^[n-zN-Z]");
                applyRangeAlpha(p);
            }
        }

        private void applyRangeNum(int min, int max) {
            StringBuilder newText = new StringBuilder();
            for (String x : recordsList) {
                try {
                    if (Integer.parseInt(x) >= min && Integer.parseInt(x) <= max) {
                        newText.append(x).append("\n");
                    }
                }
                catch (Exception e){

                }
            }
            text.setText(newText.toString());
            recordsCountInList.setText(String.valueOf(text.getLineCount() - 1));
        }

        private void applyRangeAlpha(Pattern p) {
            StringBuilder newText = new StringBuilder();
            for (String x : recordsList) {
                Matcher matcher = p.matcher(x);
                if (matcher.find()) newText.append(x).append("\n");
            }
            text.setText(newText.toString());
            recordsCountInList.setText(String.valueOf(text.getLineCount() - 1));
        }



    }









