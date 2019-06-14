import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Displays GUI for user to interact with
 *
 * @author Sophia Hübscher
 * @version 1.0
 */
public class Display extends JPanel
{
    private int width, height;
    private int gapX, gapY, gapW, gapH;
    int speed;

    private int numBirds = 300;

    boolean fast;
    
    ArrayList<Pipes> pipes = new ArrayList<>();
    
    Flock flock = new Flock();

    Font font;

    private BufferedImage backgroundImage, pipesImage;
    BufferedImage[] birdPics = new BufferedImage[7];

    /**
     * Constructor for display object, initializes flock.birds & images
     *
     * @param width display width
     * @param height display height
     */
    public Display(int width, int height, boolean fast)
    {
        this.width  = width;
        this.height = height;
        this.fast   = fast;

        if (fast)
        {
            speed = 200;
        } else
        {
            speed = 20;
        }

        //load bird images
        for (int i = 0; i < birdPics.length; i++)
        {
            try
            {
                birdPics[i] = ImageIO.read(new File("res/images/bird" + i + ".png"));
            } catch (IOException ex)
            {
                System.out.println("error setting images");
            }
        }

        //create flock.birds
        for (int i = 0; i < numBirds; i++)
        {
            //basic setup bc Vishal wouldn't let me do it in the constructor
            flock.birds[i].setSpeed(fast);
        }

        //choose random background
        int backgroundImgNum = (int)(Math.round(Math.random()));

        //sets background & initial pipe
        try
        {
            backgroundImage = ImageIO.read(new File("res/images/background" + backgroundImgNum + ".png"));
            pipesImage      = ImageIO.read(new File("res/images/pipes.png"));
        } catch (IOException ex)
        {
            System.out.println("error setting images");
        }

        //set font
        font = new Font("monospaced", Font.PLAIN, 16);
    }

    /**
     * Draws all graphics by a timer calling repaint()
     *
     * @param g the graphics object, automatically sent when repaint() is called
     */
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g); //clears the old drawings

        //draws background image
        g.drawImage(backgroundImage, 0, 0, width, height, this);

        //loop through all pipes
        for(int i = 0; i < pipes.size(); i++)
        {
            /* moves pipes */
            Pipes currentPipes = pipes.get(i);

            //if not on screen, remove pipe
            if (currentPipes.leftScreen())
            {
                pipes.remove(currentPipes);

                //avoid arraylist skipping
                i--;
            }

            //draws new location of pipes
            currentPipes.x -= currentPipes.speed;
            g.drawImage(pipesImage, currentPipes.x, currentPipes.y,
                    currentPipes.width, currentPipes.height, this);
        }

        for (int b = 0; b < flock.birds.length; b++)
        {
            Pipes nextPipes = new Pipes(0, 0, 0, fast);

            //find next pipe
            if (pipes.size() > 0)
            {
                if ((pipes.get(0).x + pipes.get(0).width) - flock.birds[b].x > 0) nextPipes = pipes.get(0);
                else nextPipes = pipes.get(1);
            }

            gapX = nextPipes.x;
            gapY = nextPipes.y + (int) (height * 1.18);
            gapW = nextPipes.width;
            gapH = nextPipes.height / 10;

            //send gap data to bird
            flock.birds[b].setGapLoc(gapX, gapY, gapW, gapH);
        }

        //loops through all flock.birds
        for (int i = 0; i < flock.birds.length; i++)
        {
            Bird thisBird = flock.birds[i];

            //draws bird if alive
            if(!thisBird.dead)
                g.drawImage(birdPics[thisBird.imageNum], thisBird.x, thisBird.y,
                        thisBird.width, thisBird.height, this);

            //checks if bird should die
            if (hitPipes(thisBird, g) || hitGround(thisBird)) thisBird.kill();
        }

        //draw text
        g.setFont(font);
        g.setColor(Color.WHITE);

        g.drawString("Current run: " + flock.birds[0].timeAlive() + "s",
                     width / 30, height / 20);
        g.drawString("Longest run: " + flock.alltimeBestScore + "s",
                     width / 30, height / 20 + (height / 22));
        g.drawString("Generations: " + (flock.generation + 1),  width / 30, height / 20 + (height / 22) * 2);
    }

    /**
     * Determines whether or not a bird has hit the pipes
     *
     * @param checkedBird bird whose collisions are being checked
     * @return true when bird hit a pipe
     */
    private boolean hitPipes(Bird checkedBird, Graphics g)
    {
        for(int i = 0; i < pipes.size(); i++)
        {
            //set bird vals w/ smaller hitbox
            int buffer = 8;

            int birdXBuff = checkedBird.x + checkedBird.width / buffer;
            int birdYBuff = checkedBird.y + checkedBird.height / buffer;
            int birdWBuff = checkedBird.width - checkedBird.width / buffer;
            int birdHBuff = checkedBird.height - checkedBird.height / buffer;

            //check if in pipe & in hitbox
            boolean inPipeX = (birdXBuff + birdWBuff > pipes.get(i).x &&
                    birdXBuff < (pipes.get(i).x + pipes.get(i).width));
            boolean inHitboxVert = ((birdYBuff + birdHBuff) < gapY + gapH) &&
                    (birdYBuff > gapY);

            if (inPipeX && !inHitboxVert) return true;
        }

        return false;
    }

    /**
     * Determines whether or not bird hit ground
     *
     * @param checkedBird bird whose collisions are being checked
     * @return true when bird hit ground
     */
    private boolean hitGround(Bird checkedBird)
    {
        return checkedBird.y + checkedBird.height > height - height / 9;
    }

    /**
     * Gets random y valaue for pipes height
     *
     * @param strength how variable that heights are
     * @return new y value
     */
    public int randomPipesY(double strength)
    {
        //set sign to +1 or -1
        int sign = (int)(Math.round(Math.random()));
        if (sign == 0) sign = -1;

        //determine pipesY vars
        //todo fix hardcoding
        int center = (int)(-(int)(1273 * 1.4) / strength);
        int shift  = (int)(Math.random() * (height / 5));

        //return random y
        return center + sign * shift;
    }

    /**
     * Resets flock.birds, pipes & graphics
     */
    public void reset()
    {
        //reset bird y locs, image, & velocity & make alive
        for (int i = 0; i < flock.birds.length; i++)
        {
            flock.birds[i].revive();
            flock.birds[i].y = height / 3;
            flock.birds[i].stop();
        }

        //destroy pipes
        for (int i = 0; i < pipes.size(); i++)
        {
            pipes.remove(i);
        }

        //reset background
        int backgroundImgNum = (int)(Math.round(Math.random()));

        try
        {
            backgroundImage = ImageIO.read(new File("res/images/background" + backgroundImgNum + ".png"));
        } catch (IOException ex)
        {
            System.out.println("error setting images");
        }
    }
}