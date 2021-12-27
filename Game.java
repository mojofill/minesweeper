import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Game {
    Random random = new Random();

    public int board_size;
    public boolean started;
    public Canvas canvas;
    public HashMap<Coordinate, Button> buttons = new HashMap<>();
    public HashMap<Coordinate, Button> mines = new HashMap<>();
    public HashMap<Coordinate, Integer> safe_buttons = new HashMap<>(); // each coordinate maps to it's hint number

    public Game(Canvas canvas, int board_size) {
        this.board_size = board_size;
        this.canvas = canvas;
        started = false;
    }

    public final int MINE = 0;
    public final int EMPTY = 1;

    public ArrayList<Coordinate> getNeighbors(Coordinate coord) {
        ArrayList<Coordinate> neighbors = new ArrayList<>();

        neighbors.add(new Coordinate(coord.x - 1, coord.y));
        neighbors.add(new Coordinate(coord.x + 1, coord.y));

        for (int x = coord.x - 1; x <= coord.x + 1; x++) {
            neighbors.add(new Coordinate(x, coord.y + 1));
            neighbors.add(new Coordinate(x, coord.y - 1));
        }

        return neighbors;
    }

    public void start(Coordinate origin) {
        // start the game - basically start the timer and set started as true
        // do the timer thing here
        // TODO: create timer

        started = true;

        setHintButtonPath(origin);
    }

    public void init(JFrame window) {
        window.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println("you pressed something");
            }
            @Override
            public void keyTyped(KeyEvent e) {
                System.out.println("you pressed something");
            }
            @Override
            public void keyReleased(KeyEvent e) {
                System.out.println("you pressed something");
            }
        });
        
        // create the buttons

        canvas.setLayout(new GridLayout(board_size, board_size, 0, 0));

        for (int y = 0; y < board_size; y++) {
            for (int x = 0; x < board_size; x++) {
                Button button = new Button(canvas, new Coordinate(x, y), this);
                button.setBackground(Color.GREEN);
                buttons.put(new Coordinate(x, y), button);
                canvas.add(button);
            }
        }

        int mines_amount = 0;
        int mines_to_add = 40;

        while (mines_amount < mines_to_add) { // mine generation
            int x = random.nextInt(10);
            if (x == 0) {
                while (true) {
                    Coordinate coord = new Coordinate(random.nextInt(board_size), random.nextInt(board_size));

                    if (!mines.containsKey(coord)) {
                        Button button = buttons.get(coord);
                        // button.setText("X");
                        // button.setBackground(Color.RED);
                        mines.put(coord, button);
                        break;
                    }
                }

                mines_amount++;
            }
        }

        HashMap<Coordinate, Integer> safe_buttons = new HashMap<>();
        
        for (Coordinate coord: buttons.keySet()) {
            if (!mines.containsKey(coord)){
                ArrayList<Coordinate> neighbors = getNeighbors(coord);
                
                int hintNumber = 0;
                
                for (Coordinate neighbor: neighbors) {
                    if (mines.containsKey(neighbor)) hintNumber++;
                }

                buttons.get(coord).setHintNumber(hintNumber);
                safe_buttons.put(coord, hintNumber);
            }
        }
        
        this.safe_buttons = safe_buttons;

        // resetMines(40, board_size); // 40 mines, board_size board length
        // everything is ready now
    }

    public void setHintButtonPath(Coordinate mine_coord) {    
        setBestPath(mine_coord);
    }

    public void setBestPath(Coordinate origin) {
        // get all the 0's clumps in th origin's areas
        // first step get the closest EMPTY button

        Coordinate closest_empty_button = searchForClosest(canvas, origin, EMPTY);

        Button button = buttons.get(closest_empty_button);

        HashSet<Coordinate> closest_empty_clump = searchForNeighboring(canvas, closest_empty_button, EMPTY);

        ArrayList<Coordinate> added_hint_buttons = new ArrayList<>();

        for (Coordinate coord: closest_empty_clump) {
            button = buttons.get(coord);
            button.setBackground(Color.YELLOW);

            ArrayList<Coordinate> neighbors = getNeighbors(coord);

            for (Coordinate neighbor: neighbors) { // this sets the hint numbers surrounding the empty buttons
                if (!added_hint_buttons.contains(neighbor)) {
                    if (neighbor.x >= 0 && neighbor.x < board_size && neighbor.y >= 0 && neighbor.y < board_size) {
                        int hintNumber = safe_buttons.get(neighbor);

                        if (hintNumber != 0) {
                            button = buttons.get(neighbor);

                            // System.out.println("Coordinate " + neighbor.x + ", " + neighbor.y + " should be a hint number now.");

                            button.setText(Integer.toString(hintNumber));
                            button.setBackground(Color.YELLOW);

                            button.setUnknwon(false);

                            added_hint_buttons.add(neighbor);
                        }
                    }
                }
            }
        }
    }

    public void end() {        
        for (Coordinate mine: mines.keySet()) {
            buttons.get(mine).setText("X");
            buttons.get(mine).setBackground(Color.RED);
        }
    }

    public void resetMines(int mines_to_add, int range) {
        int mines_added = 0;

        while (mines_added < mines_to_add) {
            int x = random.nextInt(10);
            if (x == 0) {
                while (true) {
                    Coordinate coord = new Coordinate(random.nextInt(range), random.nextInt(range));

                    if (!mines.keySet().contains(coord)) {
                        // Button button = buttons.get(coord);
                        // button.setText("X");
                        // button.setBackground(Color.RED);
                        mines.put(coord, new Button(canvas, coord, this));
                        break;
                    }
                }
                mines_added++;
            }
        }
    }

    /**
     * Searches for all neighboring (clumped together) of the type. E.G. gets the neighboring empty clumps
     * 
     * @param canvas
     * @param orgin
     * @param type
     * 
     */
    public HashSet<Coordinate> searchForNeighboring(Canvas canvas, Coordinate origin, int type) {
        HashSet<Coordinate> clump = new HashSet<>();
        clump.add(origin);

        // parse each empty button, make sure that at least one of the cardinal neighbors of each empty button is in the clump

        ArrayList<Coordinate> empty_buttons_not_in_clump = new ArrayList<>();

        for (Coordinate coord: safe_buttons.keySet()) {
            if (safe_buttons.get(coord) == 0) {
                // buttons.get(coord).setBackground(Color.ORANGE);
                empty_buttons_not_in_clump.add(coord);
            }
        }

        while (true) {
            int old_clump_size = clump.size();

            for (Coordinate empty_coord: empty_buttons_not_in_clump) {
                ArrayList<Coordinate> neighbors = getCardinalNeighbors(empty_coord);

                for (Coordinate neighbor: neighbors) {
                    if (neighbor.x > 0 && neighbor.x < board_size && neighbor.y > 0 && neighbor.y < board_size) {
                        // buttons.get(neighbor).setBackground(Color.ORANGE);
                        if (clump.contains(neighbor)) {
                            clump.add(empty_coord);
                            break;
                        }
                    }
                }
            }

            if (clump.size() == old_clump_size) break;
            else old_clump_size = clump.size();
        }

        return clump;
    }

    public static ArrayList<Coordinate> getCardinalNeighbors(Coordinate origin) {
        ArrayList<Coordinate> neighbors = new ArrayList<>();

        neighbors.add(new Coordinate(origin.x - 1, origin.y));
        neighbors.add(new Coordinate(origin.x + 1, origin.y));
        neighbors.add(new Coordinate(origin.x, origin.y + 1));
        neighbors.add(new Coordinate(origin.x, origin.y - 1));

        return neighbors;
    }

    public Coordinate searchForClosest(Canvas canvas, Coordinate origin, int type) {
        int search_side_length = 3; // at first, the squares that the searcher searches for mines is at square size of 3x3, then 5x5, then 7x7

        Coordinate searcher = origin; // begin at origin
        // if searcher is a mine, then delete that mine and create a new one somewhere else

        // first check if the origin itself is a valid type
        if (type == EMPTY) {
            if (safe_buttons.containsKey(origin)) {
                if (safe_buttons.get(origin) == 0) {
                    return origin;
                }
            }
        }

        if (type == MINE) {
            if (mines.containsKey(origin)) {
                return origin;
            }
        }

        while (true) {
            // reset searcher
            searcher = origin;

            int steps = (int) Math.floor(search_side_length / 2); // floor half of side length - if side length = 3 => steps = 1

            int limit = searcher.y + steps;
            
            for (int y = searcher.y - steps; y <= limit; y++) { // for loop checks the entire left and right sides
                searcher = origin; // reset searcher

                // LEFT
                searcher = new Coordinate(searcher.x - steps, y); // move to the LEFT by steps
                
                if (type == MINE) {
                    if (mines.containsKey(searcher)) return searcher;
                }

                else {
                    if (safe_buttons.containsKey(searcher)) {
                        if (safe_buttons.get(searcher) == 0) return searcher;
                    }
                }

                searcher = origin; // reset searcher

                // RIGHT
                searcher = new Coordinate(searcher.x + steps, y); // move to the RIGHT by steps
                
                if (type == MINE) {
                    if (mines.containsKey(searcher)) return searcher;
                }

                else {
                    if (safe_buttons.containsKey(searcher)) {
                        if (safe_buttons.get(searcher) == 0) return searcher;
                    }
                }

                searcher = origin; // reset searcher
            }

            limit = searcher.x + steps;

            // check top and bottom - iterate left to right
            for (int x = searcher.x - steps; x <= limit; x++) {
                
                searcher = new Coordinate(x, searcher.y + steps);
                
                if (type == MINE) {
                    if (mines.containsKey(searcher)) return searcher;
                }

                else {
                    if (safe_buttons.containsKey(searcher)) {
                        if (safe_buttons.get(searcher) == 0) return searcher;
                    }
                }

                searcher = origin; // reset searcher

                searcher = new Coordinate(searcher.x, searcher.y - steps);
                
                if (type == MINE) {
                    if (mines.containsKey(searcher)) return searcher;
                }

                else {
                    if (safe_buttons.containsKey(searcher)) {
                        if (safe_buttons.get(searcher) == 0) return searcher;
                    }
                }

                searcher = origin; // reset searcher
            }

            // if code reaches here, that means the searcher has not yet found the closest mine, so you add 2 to the side length

            search_side_length += 2; // 3 => 5 => 7 => 9..
        }
    }
}
