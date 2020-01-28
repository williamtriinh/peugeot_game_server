package io.github.williamtriinh;

public class Player
{
    private final String id;
    private float x;
    private float dist;
    private int left = 0;
    private int right = 0;

    // id - is the ip
    public Player(String id, float x, float dist)
    {
        this.id = id;
        this.x = x;
        this.dist = dist;
    }

    /**
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * @return the x
     */
    public float getX()
    {
        return x;
    }

    public void setX(float x)
    {
        this.x = x;
    }

    /**
     * @return the y
     */
    public float getDist()
    {
        return dist;
    }

    public void setY(float dist)
    {
        this.dist = dist;
    }

    /**
     * @return the left
     */
    public int getLeft()
    {
        return left;
    }

    public void setLeft(int left)
    {
        this.left = left;
    }

    /**
     * @return the right
     */
    public int getRight()
    {
        return right;
    }

    public void setRight(int right)
    {
        this.right = right;
    }
}