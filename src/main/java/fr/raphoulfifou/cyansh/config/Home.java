package fr.raphoulfifou.cyansh.config;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Home
{

    private String name;
    private String dimension;
    private double posX;
    private double posY;
    private double posZ;

    public Home(String name, String dimension, @NotNull List<Double> posXYZ)
    {
        this.name = name;
        this.dimension = dimension;
        this.posX = posXYZ.get(0);
        this.posY = posXYZ.get(1);
        this.posZ = posXYZ.get(2);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDimension()
    {
        return dimension;
    }

    public void setDimension(String dimension)
    {
        this.dimension = dimension;
    }

    public double getPosX()
    {
        return posX;
    }

    public void setPosX(double posx)
    {
        this.posX = posx;
    }

    public double getPosY()
    {
        return posY;
    }

    public void setPosY(double posy)
    {
        this.posY = posy;
    }

    public double getPosZ()
    {
        return posZ;
    }

    public void setPosZ(double posz)
    {
        this.posZ = posz;
    }

}
