import basicneuralnetwork.NeuralNetwork;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalTime;

/**
 * Bird object
 *
 * @author Sophia Hübscher
 * @version 1.0
 */
public class Bird implements Comparable<Bird>
{
    int x, y, width, height;
    boolean fast;

    boolean dead = false;

    private int gapX, gapY;

    private int defaultWidth  = 86;
    private int defaultHeight = 64;
    private int defaultX      = 250;
    private int defaultY      = 200;

    private double velocity = 0;
    private double gravity  = 0.15;
    private int    lift     = 5;

    int imageNum = 0;

    NeuralNetwork brain;


    float createdTime = LocalTime.now().toSecondOfDay();

    /**
     * Constructor for bird object
     */
    public Bird()
    {
        x        = defaultX;
        y        = defaultY;
        width    = defaultWidth;
        height   = defaultHeight;
        brain    = new NeuralNetwork(2,4,1);
        imageNum = (int)(Math.random() * 7);
    }

    public Bird(NeuralNetwork nn)
    {
        x        = defaultX;
        y        = defaultY;
        width    = defaultWidth;
        height   = defaultHeight;
        brain    = new NeuralNetwork(2,4,1);
        imageNum = (int)(Math.random() * 7);
    }

    /**
     * Moves bird up by strength
     */
    public void flap()
    {
        velocity -= lift;
    }

    /**
     * Moves bird down by strength
     *
     */
    public void fall()
    {
        velocity += gravity;
        y += velocity;
        velocity *= 0.98;

        //if hits top, move down
        if (y < 0)
        {
            velocity = 0;
            y = 0;
        }
    }

    /**
     * Gets time bird has existed for
     *
     * @return time alive in seconds
     */
    public int timeAlive()
    {
        if (fast) return (int)((LocalTime.now().toSecondOfDay() - createdTime) * 4);
        else return (int)(LocalTime.now().toSecondOfDay() - createdTime);
    }

    /**
     * Sets parameter "dead" to true
     */
    public void kill()
    {
        dead = true;
    }

    public void setSpeed(boolean fast)
    {
        this.fast = fast;
    }

    /**
     * Sets dead to false & resets timer
     */
    public void revive()
    {
        dead = false;
        createdTime = LocalTime.now().toSecondOfDay();
    }

    /**
     * Sets gap data in bird object
     *
     * @param x x location of gap
     * @param y y location of gap
     * @param width width of gap
     * @param height height of gap
     */
    public void setGapLoc(int x, int y, int width, int height)
    {
        gapX = x + width / 2;
        gapY = y + height / 2;
    }

    /**
     * Gets vertical distance btw bird (center) & gap (center)
     *
     * @return vertical distance as an integer
     */
    public int getVerticalDistance()
    {
        return gapY - (y + height / 2);
    }

    /**
     * Gets horizontal distance btw bird (center) & gap (center)
     *
     * @return horizontal distance as an integer
     */
    public int getHorizontalDistance()
    {
        return gapX - (x + width / 2);
    }

    /**
     * Sets velocity to 0
     */
    public void stop()
    {
        velocity = 0;
    }

    void decide() {
        double[] inputLayer = {(double)(gapX - this.x), (double)(gapY - this.y)};
        double prediction = brain.guess(inputLayer)[0];
        if(prediction > 0.5) flap();
    }

    public int compareTo(Bird b) {
        if(this.timeAlive() == b.timeAlive()) return 0;
        if(this.timeAlive() > b.timeAlive()) return 1;
        return -1;
    }


}