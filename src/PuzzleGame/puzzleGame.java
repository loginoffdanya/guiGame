package PuzzleGame;

import com.sun.xml.internal.messaging.saaj.soap.JpegDataContentHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class puzzleGame {

    JFrame frame = new JFrame("Puzzle by Dani4");
    MyButton[][] buttons = new MyButton[4][5];
    JPanel panel = new JPanel(new GridLayout(4,5,2,2));
    MyButton hiddenBtn;
    //String path = new String("C:\\Users\\loginov_dv\\IdeaProjects\\guiGame\\res");

    BufferedImage image = null;

    public static void main(String[] args) {
        puzzleGame game = new puzzleGame();
        game.go();
    }

    private void go() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(100,100,550,456);
        frame.setResizable(false);

        createMenu();
        generate();

        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }

    private void createMenu() {
        JMenuBar menu = new JMenuBar();
        JMenu fileMenu = new JMenu("Menu");
        for (String itemname: new String[] {"Restart","Shuffle","Load","Exit"}) {
            JMenuItem item = new JMenuItem(itemname);
            item.setActionCommand(itemname.toLowerCase());
            item.addActionListener(new MyMenuListener());
            fileMenu.add(item);
        }
        fileMenu.insertSeparator(1);
        menu.add(fileMenu);
        frame.setJMenuBar(menu);
    }


    class ButtonActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            MyButton btn = (MyButton) e.getSource();
            //Random color current button on click
            //int r = (int) (Math.random()*255);
            //int g = (int) (Math.random()*255);
            //int b = (int) (Math.random()*255);
            //btn.setBackground(new Color(r,g,b));
            int row = getHBRow();
            int col = getHBColumn();
            if(row!=-1 && col!=-1) {
                if (moveableBtn(btn, row, col)) {
                    change(btn, row, col);
                }
            }
            if(checkWin()){
                JOptionPane.showMessageDialog(null, "YOU WIN!", "Congratulations", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void change(MyButton button,int row,int col) {
        int tmp = button.value;
        button.value = hiddenBtn.value;
        hiddenBtn.value = tmp;
        hiddenBtn.setIcon(button.getIcon());
        hiddenBtn.setVisible(true);
        hiddenBtn = button;
        button.setVisible(false);

    }

    private boolean checkWin() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                if(buttons[i][j].value != (i*5)+(j+1)){
                    return false;
                }
            }
        }
        return true;
    }

    private boolean moveableBtn(MyButton button,int row, int col) {
        if (row-1>=0){
            if(button.equals(buttons[row-1][col])){
                return true;
            }
        }
        if (row+1<4){
            if(button.equals(buttons[row+1][col])){
                return true;
            }
        }
        if (col-1>=0){
            if(button.equals(buttons[row][col-1])){
                return true;
            }
        }
        if (col+1<5){
            if(button.equals(buttons[row][col+1])){
                return true;
            }
        }
        return false;
    }

    private int getHBColumn() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                if(buttons[i][j].equals(hiddenBtn)){
                    return j;
                }
            }
        }
        return -1;
    }

    private int getHBRow() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
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
                shuffle();
            }
            if (cmd.equals("load")){
                JOptionPane.showMessageDialog(null, "Not avaliable for now!=(", "Testing", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void shuffle() {
        int size = 20;
        ArrayList<Integer> array = new ArrayList<>(size);
        for (int i = 1; i <= size; i++) {
            array.add(i);
        }

        Collections.shuffle(array);

        while (!array.isEmpty()) {
            int v = array.remove(array.size() - 1);
            boolean found = false;
            for (int i = 0; i < 4; i++){
                for (int j = 0; j < 5; j++) {
                    if (v==buttons[i][j].value){
                        change(buttons[i][j],getHBRow(),getHBColumn());
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
        public int value = 0;

        public MyButton(ImageIcon imageIcon,int value) {
            super(imageIcon);
            this.value = value;
        }
    }
    private void generate() {
        panel.removeAll();
        BufferedImage subImage = null;
        try{
            image = readGameImage("test.jpg");
        } catch (Exception e){
            JOptionPane.showMessageDialog(null, "Error!", "Testing", JOptionPane.INFORMATION_MESSAGE);;
        }
        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 5; j++){
                // ""+((i*5)+(j+1))
                subImage = image.getSubimage(j*100,i*100,100,100);
                buttons[i][j] = new MyButton(new ImageIcon(subImage),(i*5)+(j+1));
                buttons[i][j].addActionListener(new ButtonActionListener());
                panel.add(buttons[i][j]);
            }
        }
        hiddenBtn = buttons[3][4];
        hiddenBtn.setVisible(false);
    }
    private static BufferedImage readGameImage(String imagePath) throws IOException {
        InputStream is = null;
        try {
            is = puzzleGame.class.getClassLoader().getResourceAsStream(imagePath);
            // an example of how null checks can easily be forgotten
            if (is == null) {
                throw new FileNotFoundException("Resource not found: " + imagePath);
            }
            return ImageIO.read(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
}
