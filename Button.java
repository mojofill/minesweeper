import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Button extends JButton{
    public int hint_number = 0;
    public boolean flagged = false;
    public boolean unknown = true;
    public Coordinate coord;
    public Game game;

    public Button(Canvas canvas, Coordinate coord, Game game) {
        this.coord = coord;
        this.game = game;

        this.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!game.started) { // game did not start yet, start spawning hint buttons
                    game.start(coord);
                }
            }
        });

        this.addMouseListener(new MouseListener() {

            // when you press an EMPTY button, it will keep opening up spots until it reaches a 
            
            public void mouseExited(MouseEvent e) {}
            
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    if (flagged) unflag();
                    else flag();

                    return;
                }

                if (flagged) return;

                if (game.mines.containsKey(coord)) { // player pressed on a mine :( end game
                    game.end();
                }

                else if (game.safe_buttons.get(coord) == 0) { // pressed on a safe button
                    game.buttons.get(coord).setBackground(Color.GREEN);
                    unknown = false;
                    game.setHintButtonPath(coord);
                }

                else if (game.safe_buttons.get(coord) > 0 && game.buttons.get(coord).unknown) { // pressed on a button that is a valid hint button (hint number greater than 0), but it is still unknown so show it
                    game.buttons.get(coord).setText(Integer.toString(game.safe_buttons.get(coord)));
                    unknown = false;
                    game.buttons.get(coord).setBackground(Color.YELLOW);
                }
            }
            
            public void mouseReleased(MouseEvent e) {}
            
            public void mouseClicked(MouseEvent e) {}
            
            public void mouseEntered(MouseEvent e) {}
        });
    }

    public void setHintNumber(int newHintNumber) {
        hint_number = newHintNumber;
    }

    private void flag() {
        flagged = true;
        game.buttons.get(coord).setBackground(Color.RED);
    }
    
    private void unflag() {
        flagged = false;
        game.buttons.get(coord).setBackground(Color.GREEN);
    }

    public void setUnknwon(boolean b) {
        unknown = b;
    }
}
