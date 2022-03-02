
public class Appliance {

    public String name;
    public int onW, offW;
    public double probOn;
    public boolean smart;
    public double probSmart;
    public boolean on;
    int location;
    int lowPower;

    public Appliance(int locationID, String name, int onW, double probOn, boolean smart,
            double probSmart) {
        this.location = locationID;
        this.name = name;
        this.onW = onW;
        this.probOn = probOn;
        this.smart = smart;
        this.probSmart = probSmart;
        on = false;

    }

    public void setlowPower(int lowPower) {
        this.lowPower = lowPower;
    }

    public void isOn(boolean b) {
        on = b;
    }

    @Override
    public String toString() {
        return name + "," + onW + "," + offW + "," + probOn + "," + smart + "," + probSmart;
    }

}
