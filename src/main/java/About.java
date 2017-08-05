import javax.swing.*;
import java.awt.*;

public class About extends JFrame{

    JPanel pnlAbout;
    private JLabel lblAbout = new JLabel("ListBoxer v.2 - programm for testing");
    private JLabel lblAbout2 = new JLabel("created by Vergun Yulia");

    public About() {
        super("About");
        this.setSize(500, 280);
        //this.setDefaultCloseOperation(this.setVisible(false));

        pnlAbout = new JPanel();
        pnlAbout.add(lblAbout);
        pnlAbout.add(lblAbout2);
        this.add(pnlAbout, BorderLayout.CENTER);
    }

}
