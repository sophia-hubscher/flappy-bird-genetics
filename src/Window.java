import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

/**
 * Window holds all buttons, labels, etc
 *
 * @author Sophia Hübscher
 * @version 1.0
 */
public class Window extends JFrame implements ActionListener, KeyListener
{
    private JPanel masterPanel;

    private Timer t;
    private Display display;

    private int width, height;

    private int delayTime; //in ms
    private int pipeDelay;

    private int pipeTimerCount = 0;

    boolean fast;

    /**
     * Creates master panel & smaller panels with all JComponents
     *
     * @param width  width of screen
     * @param height height of screen
     */
    public Window(int width, int height, boolean fast)
    {
        this.width  = width;
        this.height = height;
        this.fast   = fast;

        if (fast)
        {
            delayTime = 5;
            pipeDelay = 50;
        } else
        {
            delayTime = 20;
            pipeDelay = 190;
        }

        this.setResizable(false);

        //makes area for game to be displayed
        display = new Display(600, height, fast);

        display.pipes.add(new Pipes(0, 0, 1.2, fast));
        display.pipes.get(display.pipes.size() - 1).setNewPipes(width, height);

        //adds key listener
        addKeyListener(this);

        //master panel holds all other panels
        masterPanel = (JPanel) this.getContentPane();
        masterPanel.setLayout(new BorderLayout());

        //creates timer
        t = new Timer(delayTime, this);
        t.addActionListener(this);
        t.start();

        //adds components to panel
        masterPanel.add(display, BorderLayout.CENTER);

        //sets title and size of window and set to be visible in the JFrame
        this.setTitle("frog emphysema");
        this.setSize(width, height);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    /**
     * Controls all responses to actions
     *
     * @param e the specific event
     */
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == t) //when timer fires
        {
            for(int i = 0; i < display.flock.birds.length; i++)
            {
                //flock.birds constantly fall
                display.flock.birds[i].fall();

                //flock.birds decide whether or not to flap
                display.flock.birds[i].decide();
            }

            //repaint display
            display.repaint();

            //check if game over
            int deadCount = 0;

            for (int i = 0; i < display.flock.birds.length; i++)
            {
                if (display.flock.birds[i].dead) deadCount++;
            }

            if (deadCount == display.flock.birds.length)
            {
                //game over
                display.flock.runGA();
                display.reset();
            }

            //draw new pipe
            pipeTimerCount++;

            if (pipeTimerCount % pipeDelay == 0)
            {
                display.pipes.add(new Pipes(width, display.randomPipesY(3.3), 1.2, fast));
                pipeTimerCount = 0;
            }
        }
    }

    /**
     * Flaps when space is pressed
     *
     * @param e event that shows which key has been pressed
     */
    public void keyPressed(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
        {
            display.flock.downloadBest();
        }
    }

    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
}