package unam.ciencias.computoconcurrente;

public class Toilette {
    private volatile long timesMalesEntered;
    private volatile long timesFemalesEntered;

    private volatile long males;
    private volatile long females;

    public Toilette() {
        this.timesMalesEntered = 0;
        this.timesFemalesEntered = 0;
        males = females = 0;
    }

    public void enterMale() throws InterruptedException {

    }

    public void leaveMale() {
    }

    public void enterFemale() throws InterruptedException {

    }

    public void leaveFemale() {
    }

    public long getTimesMalesEntered() {
        return timesMalesEntered;
    }

    public long getTimesFemalesEntered() {
        return timesFemalesEntered;
    }

    public long getMales() {
        return males;
    }

    public long getFemales() {
        return females;
    }
}
