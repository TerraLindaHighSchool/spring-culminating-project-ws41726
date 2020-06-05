import greenfoot.*;
public abstract class QActor extends Actor
{
    protected static final int QVAL = 100;   
    protected static final int UNBOUND = 0;
    protected static final int LIMIT = 1;
    protected static final int REMOVE = 2;
    protected static final int WRAP = 3; 
    protected static final int BOUNCE = 4;
    private int qX, qY; 
    private int vX, vY; 
    private int qR; 
    private int boundedAction, boundedRange; 
    private boolean rotationalBounce;
    
    
    public void move()
    {
        qX += vX; // adjust x coordinate value of q-location
        qY += vY; // adjust y coordinate value of q-locaation
        super.setLocation(qX/QVAL, qY/QVAL); // set current location of actor
        boundsAct(); // check for and perform bounded edge action
        rotationalBounce = false;
    }
    
    
    public void setLocation(int x, int y)
    {
        super.setLocation(x, y); // set location of actor
        qX = getX()*QVAL; // set x coordinate value of q-location
        qY = getY()*QVAL; // set y coordinate value of q-location
    }

    public void turn(int amount)
    {
        qR = (qR+amount+360*QVAL)%(360*QVAL); // adjust q-rotation value
        super.setRotation(qR/QVAL); // set current rotation
    }
    
    
    public void turnTowards(int x, int y)
    {
        setQRotation((int)(QVAL*(Math.atan2(y-getY(), x-getX())*180/Math.PI)));
    }
    
    
    public void setRotation(int angle)
    {
        super.setRotation(angle); // set actor rotation
        qR = getRotation()*QVAL; // set q-rotation value
    }
    
    
    public void addForce(int amount, int direction)
    {
        vX += Math.cos((double)direction*Math.PI/(180*QVAL))*(double)amount; // new horizontal speed
        vY += Math.sin((double)direction*Math.PI/(180*QVAL))*(double)amount; // new vertical speed
    }
    
    
    public void move(int amount, int direction)
    {
        int holdX = vX, holdY = vY; // save the current values
        vX = 0; vY = 0; // clear the values
        addForce(amount, direction);
        move();
        vX = holdX; vY = holdY; // restore saved values
    }
    
    
    public void move(int amount)
    {
        move(amount, qR);
        rotationalBounce = true;
    }
    
    
    public void setBoundedAction(int action, int range)
    {
        boundedAction = action%5; // the given action
        boundedRange = range; // the given range offset where positive direction is away from center
    }
    
    
    private void boundsAct()
    {
        switch(boundedAction)
        {
            case UNBOUND: // no bounded action taken
                break;
            case LIMIT: // no movement beyond bounds
                if (qX <= -boundedRange*QVAL)
                {
                    setQLocation(-boundedRange*QVAL, qY);
                    if (vX < 0) vX = 0;
                }
                if (qY <= -boundedRange*QVAL)
                {
                    setQLocation(qX, -boundedRange*QVAL);
                    if (vY < 0) vY = 0;
                }
                if (qX >= (getWorld().getWidth()+boundedRange-1)*QVAL)
                {
                    setQLocation((getWorld().getWidth()+boundedRange-1)*QVAL, qY);
                    if (vX > 0) vX = 0;
                }
                if (qY >= (getWorld().getHeight()+boundedRange-1)*QVAL)
                {
                    setQLocation(qX, (getWorld().getHeight()+boundedRange-1)*QVAL);
                    if (vY > 0) vY = 0;
                }
                break;
            case REMOVE: // actor is removed at bounds
                if (getX() <= -boundedRange ||
                    getX() >= getWorld().getWidth()+boundedRange-1 ||
                    getY() <= -boundedRange ||
                    getY() >= getWorld().getHeight()+boundedRange-1)
                        getWorld().removeObject(this);
                break;
            case WRAP: // actor is transported to opposite world edge at bounds
                if (getX() <= -boundedRange)
                    setLocation(getX()+getWorld().getWidth()+boundedRange*2-2, getY());
                else if (getY() <= -boundedRange)
                    setLocation(getX(), getY()+getWorld().getHeight()+boundedRange*2-2);
                else if (getX() >= getWorld().getWidth()+boundedRange-1)
                    setLocation(1-boundedRange, getY());
                else if (getY() >= getWorld().getHeight()+boundedRange-1)
                    setLocation(getX(), 1-boundedRange);
                break;
            case BOUNCE: // actor faces toward center at bounds
                if (rotationalBounce)
                {
                    if ((getX() <= -boundedRange && qR > 9000 && qR < 27000) || (getX() >= getWorld().getWidth()+boundedRange-1 && (qR < 9000 || qR > 27000)))
                    {
                        setQRotation((54000-qR)%36000);
                    }
                    if ((getY() <= -boundedRange && qR > 18000) || (getY() >= getWorld().getHeight()+boundedRange-1 && qR < 18000))
                    {
                        setQRotation((72000-qR)%36000);
                    }
                }
                else
                {
                    if ((getX() <= -boundedRange && vX < 0) || (getX() >= getWorld().getWidth()+boundedRange-1 && vX > 0)) vX = -vX;
                    if ((getY() <= -boundedRange && vY < 0) || (getY() >= getWorld().getHeight()+boundedRange-1 && vY > 0)) vY = -vY;
                }
                break;
        }
    }
    
    public void setQLocation(int x, int y)
    {
        qX = x;
        qY = y;
        super.setLocation(qX/QVAL, qY/QVAL);
    }
    
    public void setQRotation(int amount)
    {
        qR = amount;
        super.setRotation(qR/QVAL);
    }
    
    protected int getQX() { return qX; }
    
    protected int getQY() { return qY; }
    
    protected int getQR() { return qR; }
    
    protected int getVX() { return vX; }
    
    protected int getVY() { return vY; }
     
    protected void setVX(int speed) { vX = speed; }
      
    protected void setVY(int speed) { vY = speed; }
}