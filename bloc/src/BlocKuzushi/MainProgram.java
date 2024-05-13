package BlocKuzushi;

import javax.swing.JFrame;

public class MainProgram extends JFrame {
    public MainProgram() {
        setTitle("ブロック崩しゲーム");
        setResizable(false);

        MainPanel panel = new MainPanel();
        getContentPane().add(panel);

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new MainProgram();
    }
}
