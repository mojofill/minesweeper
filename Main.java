import javax.swing.*;
import java.awt.*;

class Main {
    public static void main (String args[]) {
        JFrame window = new JFrame();

        window.setSize(new Dimension(800, 700));
        
        Canvas canvas = new Canvas();
        int board_size = 20;

        Game game = new Game(canvas, board_size);

        game.init(window);
        
        window.add(canvas);

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }
}
