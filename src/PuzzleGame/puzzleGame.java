package PuzzleGame;

import javax.imageio.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class puzzleGame {

    final int xP = 8;
    final int yP = 8;

    JFrame frame = new JFrame("Puzzle by Dani4");
    MyButton[][] buttons = new MyButton[yP][xP];
    JPanel panel = new JPanel(new GridLayout(yP,xP,0,0));
    MyButton hiddenBtn;
    BufferedImage image = null;

    String path = "src/test.jpg";

    public static void main(String[] args) {

        // Set proxy on workspace
        //System.setProperty("https.proxyHost", "vmspbtmg.complete.ru");
        //System.setProperty("https.proxyPort", "8080");
        //System.setProperty("http.proxyHost", "vmspbtmg.complete.ru");
        //System.setProperty("http.proxyPort", "8080");

        puzzleGame game = new puzzleGame();
        game.go();
    }

    private void go() {
        // Set up WindowsLookAndFeel style for GUI
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
             /*   if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }*/
             UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        // Launching frames
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // TO DO
        frame.setBounds(100,100,108*yP,(108*xP+42));
        frame.setResizable(false);
        createMenu();

        // Creating the game
        image = readGameImage(path);
        generate();

        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }

    private void createMenu() {
        JMenuBar menu = new JMenuBar();
        JMenu fileMenu = new JMenu("Menu");
        for (String itemname: new String[] {"Restart","Shuffle","Browse","Load from URL","Exit"}) {
            JMenuItem item = new JMenuItem(itemname);
            item.setActionCommand(itemname.toLowerCase());
            item.addActionListener(new MyMenuListener());
            fileMenu.add(item);
        }
        fileMenu.insertSeparator(2);
        fileMenu.insertSeparator(5);
        menu.add(fileMenu);
        frame.setJMenuBar(menu);
    }


    class ButtonActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            MyButton btn = (MyButton) e.getSource();
            int row = getHBRow();
            int col = getHBColumn();
            if(row!=-1 && col!=-1) {
                if (moveableBtn(btn, row, col)) {
                    change(btn);
                    if(checkWin()){
                        JOptionPane.showMessageDialog(frame, "YOU WIN!", "Congratulations", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        }
    }

    private void change(MyButton button) {
        int tmp = button.value;
        button.value = hiddenBtn.value;
        hiddenBtn.value = tmp;
        hiddenBtn.setIcon(button.getIcon());
        hiddenBtn.setVisible(true);
        hiddenBtn = button;
        button.setVisible(false);

    }

    private boolean checkWin() {
        for (int i = 0; i < yP; i++) {
            for (int j = 0; j < xP; j++) {
                if(buttons[i][j].value != (i*xP)+(j+1)){
                    return false;
                }
            }
        }
        return true;
    }

    private boolean moveableBtn(MyButton button,int row, int col) {
        if (row-1>=0 && button.equals(buttons[row-1][col])){
            return true;
        }
        if (row+1<yP && button.equals(buttons[row+1][col])){
            return true;
        }
        if (col-1>=0 && button.equals(buttons[row][col-1])){
            return true;
        }
        if (col+1<xP && button.equals(buttons[row][col+1])){
            return true;
        }
        return false;
    }

    private int getHBColumn() {
        for (int i = 0; i < yP; i++) {
            for (int j = 0; j < xP; j++) {
                if(buttons[i][j].equals(hiddenBtn)){
                    return j;
                }
            }
        }
        return -1;
    }

    private int getHBRow() {
        for (int i = 0; i < yP; i++) {
            for (int j = 0; j < xP; j++) {
                if(buttons[i][j].equals(hiddenBtn)){
                    return i;
                }
            }
        }
        return -1;

    }

    private class MyMenuListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            if(cmd.equals("exit")){
                System.exit(0);
            }
            if(cmd.equals("restart")){
                generate();
            }
            if (cmd.equals("shuffle")){
                try{
                    shuffle();
                } catch (NullPointerException ex){
                    JOptionPane.showMessageDialog(null, "Error! Nothing to shuffle", "Alert Message", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            if (cmd.equals("browse")){
                getPuzzle();
            }
            if (cmd.equals("load from url")){
                getPuzzleFromURL();
            }
        }
    }

    private void shuffle() {
        int size = yP*xP;
        ArrayList<Integer> array = new ArrayList<>(size);
        for (int i = 1; i <= size; i++) {
            array.add(i);
        }

        Collections.shuffle(array);

        while (!array.isEmpty()) {
            int v = array.remove(array.size() - 1);
            boolean found = false;
            for (int i = 0; i < yP; i++){
                for (int j = 0; j < xP; j++) {
                    if (v==buttons[i][j].value){
                        change(buttons[i][j]);
                        found = true;
                        break;
                    }
                }
                if (found)
                    break;
            }
        }
    }

    public class MyButton extends JButton{
        public int value;

        public MyButton(ImageIcon imageIcon,int value) {
            super(imageIcon);
            this.value = value;
        }
    }

    private void getPuzzle() {
        try {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter imageFilter = new FileNameExtensionFilter("Images(*.jpg)","jpg");
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.setFileFilter(imageFilter);
            int res = chooser.showDialog(frame, "Open image");
            if (res == JFileChooser.APPROVE_OPTION){
                path = chooser.getSelectedFile().getAbsolutePath();
                image = readGameImage(path);
                generate();
            }
        }catch (Exception io){
            JOptionPane.showMessageDialog(null,io);
        }
    }

    private void getPuzzleFromURL() {
        try {
            String FILE_URL = JOptionPane.showInputDialog(frame,
                    "URL from internet:\n",
                    "Browse Internet",
                    JOptionPane.PLAIN_MESSAGE);
            if ((FILE_URL != null) && (FILE_URL.length() > 0)) {
                BufferedInputStream in = new BufferedInputStream(new URL(FILE_URL).openStream());
                BufferedImage tmp = ImageIO.read(in);
                image = createResizedCopy(tmp, xP*100, yP*100, false);
                generate();
                in.close();
            }
        } catch (Exception io){
            JOptionPane.showMessageDialog(null,io);
        }

    }

    private void generate() {
        panel.removeAll();
        frame.repaint();
        BufferedImage subImage;
        try{
            for (int i = 0; i < yP; i++){
                for (int j = 0; j < xP; j++){
                    // ""+((i*5)+(j+1))
                    subImage = image.getSubimage(j*100,i*100,100,100);
                    buttons[i][j] = new MyButton(new ImageIcon(subImage),(i*xP)+(j+1));
                    buttons[i][j].addActionListener(new ButtonActionListener());
                    panel.add(buttons[i][j]);
                }
            }
            hiddenBtn = buttons[yP-1][xP-1];
            hiddenBtn.setVisible(false);
        } catch (Exception e){
            JOptionPane.showMessageDialog(null, "Error!", "Testing", JOptionPane.INFORMATION_MESSAGE);
        }

    }
    private BufferedImage readGameImage(String imagePath){
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new File(imagePath));
        } catch (Exception io){
            io.printStackTrace();
        }
        return createResizedCopy(bufferedImage,xP*100,yP*100,false);
    }
    private BufferedImage createResizedCopy(Image originalImage, int scaledWidth, int scaledHeight, boolean preserveAlpha) {
        int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType);
        Graphics2D g = scaledBI.createGraphics();
        if (preserveAlpha) {
            g.setComposite(AlphaComposite.Src);
        }
        g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
        g.dispose();
        return scaledBI;
    }
}
