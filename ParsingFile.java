package gith;


import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

/**
 * Created by Elena on 10.08.2017.
 */
public class ParsingFile extends JFrame {
    private String[] listLine = {"CAZgRf820167151156145", "RMuiRdf010160141151156164", "lims8r3860lims1631411561441", "GZQRyr6870GZQR+0041431501451451631455A", "qkMfPjrd0561411551551651561511641511571567", "EOcTkerf389-0201511431450551431621451411550"};
    private DefaultListModel lItem = new DefaultListModel();
    private JList lst = new JList(lItem);
    private JTextField fileName = new JTextField(25);
    private JButton save = new JButton("Save to file *.csv");
    private static ParsingFile ssp;
    private static String comma = ";";
    private static int il = 0;
    private static String d = "false";
    private static String f = "false";

    public ParsingFile(){
        super("Task 3 (Parsing to *.csv file)");

        setLayout(new FlowLayout());
        Border brd = BorderFactory.createMatteBorder(1, 1, 2, 2, Color.BLACK);
        lst.setBorder(brd);
        int count = 0;
        for (String str : listLine)
            lItem.addElement(listLine[count++]);

        add(lst);
        save.addActionListener(new ParsingFile.SaveL());
        add(save);
        fileName.setEditable(false);
        add(fileName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350,240);
        setVisible(true);
    }

    class SaveL implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser c = new JFileChooser();
            int rVal = c.showSaveDialog(ParsingFile.this);
            if (rVal == JFileChooser.APPROVE_OPTION){
                fileName.setText(c.getCurrentDirectory().toString() + "\\" + c.getSelectedFile().getName());
                if(!fileName.getText().endsWith(".csv"))
                    fileName.setText(c.getCurrentDirectory().toString() + "\\" + c.getSelectedFile().getName() + ".csv");

                try {
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName.getText()), "cp1251"));//"UTF-8"

                    out.write(heading());

                    for (int w = 0; w < listLine.length; w++) {
                        String lineStr = listLine[w];
                        char[] arrayLine = lineStr.toCharArray();

                        out.write(parseStrColumn1(lineStr));
                        String driver = parseStrDriver(arrayLine);
                        out.write(driver);
                        out.write(parseStrColumn3(arrayLine, driver));
                        out.write(parseStrColumn4());
                        out.write(parseStrColumn5());
                        out.write(parseStrColumn6(arrayLine));
                        out.write(parseStrColumn7(arrayLine));

                    }
                    out.close();
                    JOptionPane.showMessageDialog(null, "Ready!" + " File name " + fileName.getText(),"Hello",
                            JOptionPane.PLAIN_MESSAGE);
                } catch (FileNotFoundException ex) {
                    JOptionPane.showMessageDialog(null, "File not found!","Error",
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } catch (UnsupportedEncodingException ex) {
                    JOptionPane.showMessageDialog(null, "Unsupported encoding!","Error",
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } catch (IOException ex){
                    JOptionPane.showMessageDialog(null, "Error!","Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            if (rVal == JFileChooser.CANCEL_OPTION){
                fileName.setText("You pressed cancel!");
            }
        }
    }
    public static String heading() {
        StringBuilder head = new StringBuilder();
        head.append("шифр");
        head.append(comma);
        head.append("код водителя");
        head.append(comma);
        head.append("код путевого листа");
        head.append(comma);
        head.append("опасный");
        head.append(comma);
        head.append("хрупкий");
        head.append(comma);
        head.append("температура");
        head.append(comma);
        head.append("наименование");
        head.append('\r');
        head.append('\n');
        return head.toString();
    }

    public static String parseStrColumn1(String string) {
        //code
        StringBuilder column1 = new StringBuilder();
        column1.append(string);
        column1.append(comma);

        return column1.toString();

    }

    public static String parseStrDriver(char[] arrStr) {
        //id driver
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            sb.append(arrStr[i]);

        }
        return sb.toString();
    }
    public static String parseStrColumn4() {
        //danger
        StringBuilder column4 = new StringBuilder();
        column4.append(d);
        column4.append(comma);
        return column4.toString();
    }
    public static String parseStrColumn5() {
        //fragile
        StringBuilder column5 = new StringBuilder();
        column5.append(f);
        column5.append(comma);
        return column5.toString();

    }
    public static String parseStrColumn3(char[] arrStr, String driver) {
        //id route sheet
        StringBuilder column3 = new StringBuilder();
        column3.append(comma);
        for (int i = 4; i < 7; i++) {
            if (arrStr[i] == 'R' || arrStr[i] == 'r') {
                column3.append(arrStr[i]);
                il = i;
                break;
            }

        }
        il++;

        //fragile / danger
        if (arrStr[il] == 'd' || arrStr[il] == 'f') {
            if (arrStr[il] == 'd') {
                d = "true";
                column3.append(arrStr[il]);
                if (arrStr[il + 1] == 'f') {
                    f = "true";
                    column3.append(arrStr[il + 1]);
                    il++;
                } else
                    f = "false";
            } else if (arrStr[il] == 'f') {
                f = "true";
                column3.append(arrStr[il]);
                d = "false";
            } else {
                d = f = "false";
            }
        } else {
            d = f = "false";
        }

        //number fragile/danger
        if (d.equals("true") || f.equals("true")) {
            il++;
            for (int i = il; i < il + 3; i++) {
                column3.append(arrStr[i]);
            }

            il = il + 3;

            //only number
        } else {
            for (int i = il; i < il + 4; i++) {
                column3.append(arrStr[i]);

            }
            il = il + 4;

        }

        //id driver to sheet
        StringBuilder driver1 = new StringBuilder();
        for (int i = il; i < il + 4; i++) {
            driver1.append(arrStr[i]);

        }

        if (driver.toString().equals(driver1.toString())) {
            column3.append(driver1.toString());
            il = il + 4;
        }
        column3.append(comma);
        return column3.toString();
    }

    public static String parseStrColumn6(char[] arrStr) {
        //temperature
        StringBuilder column6 = new StringBuilder();
        if(arrStr[il] == '+' || arrStr[il] == '-')
        {
            for (int i = il; i < il + 4; i++) {
                column6.append(arrStr[i]);

            }
            il = il + 4;

        }
        column6.append(comma);
        return column6.toString();


    }
    public static String parseStrColumn7(char[] arrStr) {
        //name
        int k = 0;
        StringBuilder tri = new StringBuilder();
        StringBuilder column7 = new StringBuilder();
        for (int i = il; i < arrStr.length; i++) {

            tri.append(arrStr[i]);
            k++;

            if (k > 2) {
                k = 0;
                int asd = Integer.parseInt(tri.toString(), 8);
                char asd1 = (char) asd;
                column7.append(asd1);
                tri.delete(0, 3);
            }
        }
        column7.append('\r');
        column7.append('\n');
        return column7.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ssp = new ParsingFile();

            }
        });
    }
}

