/**
 * Pipes object
 *
 * @author Sophia Hübscher
 * @version 1.0
 */
public class Pipes
{
    int x, y, width, height, speed;

    /**
     * Constructor for pipes object
     *
     * @param x x location
     * @param y y location
     * @param size how much the pipes are scaled by
     */
    public Pipes (int x, int y, double size, boolean fast)
    {
        this.x     = x;
        this.y     = y;
        width      = (int)(80 * size);
        height     = (int)(1273 * size);

        if (fast) speed = 16;
        else speed = 4;
    }

    /**
     * Places pipes at right edge of screen & new height
     */
    public void setNewPipes(int screenWidth, int screenHeight)
    {
        //set sign to +1 or -1
        int sign = (int)(Math.round(Math.random()));
        if (sign == 0) sign = -1;

        //determine pipesY vars
        int center = (int)(-height / 3.3);
        int shift  = (int)(Math.random() * (screenHeight / 5));

        //set pipe locations
        x = screenWidth;
        y = center + sign * shift;
    }

    /**
     * Determines whether or not the pipes have exited to the left of the screen
     *
     * @return if pipes are to the right of the screen
     */
    public boolean leftScreen()
    {
        //if pipe is off left edge of screen
        if (x + width < 0) return true;

        return false;
    }
}