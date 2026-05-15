import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class CricketFieldGUI extends JPanel implements MouseListener, MouseMotionListener {

    private ArrayList<PlayerDot> players;
    private String mode;
    private PlayerDot selectedPlayer;

    private int batsmanCount = 0;
    private int bowlerCount = 0;

    private final int MAX_PLAYERS = 11;

    public CricketFieldGUI() {
        players = new ArrayList<>();
        mode = "Batsman";
        selectedPlayer = null;

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    // ================= INNER CLASS =================
    class PlayerDot {
        int x, y;
        String type;
        int id;

        public PlayerDot(int x, int y, String type, int id) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.id = id;
        }

        public boolean isClicked(int mx, int my) {
            int dx = mx - x;
            int dy = my - y;
            return dx * dx + dy * dy <= 100;
        }

        public String getLabel() {
            return type.equals("Batsman") ? "B" + id : "BW" + id;
        }
    }

    // ================= DRAW =================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // Smooth graphics
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Field background
        g2.setColor(new Color(30, 130, 30));
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Boundary circle
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(50, 50, 400, 400);

        // Pitch
        g2.fillRect(240, 150, 20, 200);

        // Draw players
        for (PlayerDot p : players) {
            if (p.type.equals("Batsman")) {
                g2.setColor(Color.BLUE);
            } else {
                g2.setColor(Color.RED);
            }

            g2.fillOval(p.x - 6, p.y - 6, 12, 12);

            // Label
            g2.setColor(Color.WHITE);
            g2.drawString(p.getLabel(), p.x + 6, p.y);
        }

        // Info text
        g2.setColor(Color.WHITE);
        g2.drawString("Players: " + players.size() + "/11", 10, 20);
        g2.drawString("Batsmen: " + batsmanCount + " | Bowlers: " + bowlerCount, 10, 40);
        g2.drawString("Mode: " + mode, 10, 60);
    }

    // ================= MOUSE CLICK =================


     /*
      Handles mouse clicks to add a new player
      at the clicked position if limits allow.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        // Remove if clicked
        for (int i = 0; i < players.size(); i++) {
            PlayerDot p = players.get(i);
            if (p.isClicked(x, y)) {
                if (p.type.equals("Batsman")) {
                    batsmanCount--;
                } else {
                    bowlerCount--;
                }

                players.remove(i);
                renumberPlayers();
                repaint();
                return;
            }
        }

        // Max players
        if (players.size() >= MAX_PLAYERS) {
            JOptionPane.showMessageDialog(this, "Maximum 11 players allowed!");
            return;
        }

        // Prevent overlap
        for (PlayerDot p : players) {
            int dx = p.x - x;
            int dy = p.y - y;
            if (dx * dx + dy * dy < 400) {
                JOptionPane.showMessageDialog(this, "Too close to another player!");
                return;
            }
        }

        // Add player
        if (mode.equals("Batsman")) {
            if (batsmanCount >= 6) {
                JOptionPane.showMessageDialog(this, "Maximum 6 batsmen allowed!");
                return;
            }
            players.add(new PlayerDot(x, y, "Batsman", ++batsmanCount));

        } else {
            if (bowlerCount >= 6) {
                JOptionPane.showMessageDialog(this, "Maximum 6 bowlers allowed!");
                return;
            }
            players.add(new PlayerDot(x, y, "Bowler", ++bowlerCount));
        }

        repaint();
    }

    // ================= DRAG =================
    @Override
    public void mousePressed(MouseEvent e) {
        for (PlayerDot p : players) {
            if (p.isClicked(e.getX(), e.getY())) {
                selectedPlayer = p;
                return;
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (selectedPlayer != null) {

            // prevent overlap while dragging
            for (PlayerDot p : players) {
                if (p != selectedPlayer) {
                    int dx = p.x - e.getX();
                    int dy = p.y - e.getY();
                    if (dx * dx + dy * dy < 400) {
                        return;
                    }
                }
            }

            selectedPlayer.x = e.getX();
            selectedPlayer.y = e.getY();
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        selectedPlayer = null;
    }

    // ================= HELPERS =================
    
     /*
     Updates player numbering so labels stay in order
     after adding or removing players.
     */
    private void renumberPlayers() {
        int b = 1, bw = 1;
        for (PlayerDot p : players) {
            if (p.type.equals("Batsman")) {
                p.id = b++;
            } else {
                p.id = bw++;
            }
        }
    }

 /*
 Removes all players from the field and resets
 the game back to its initial state.
 */
    public void resetField() {
        players.clear();
        batsmanCount = 0;
        bowlerCount = 0;
        repaint();
    }

     /*Changes the current mode (Batsman or Bowler)
      which determines what type of player is added on click.
     */
    public void setMode(String m) {
        mode = m;
        repaint();
    }

    // ================= UNUSED REQUIRED METHODS =================
    @Override public void mouseMoved(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}