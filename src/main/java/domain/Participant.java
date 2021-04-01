package domain;

public class Participant extends Entity<Integer>{
    private String nume;
    private int varsta;

    public Participant(String nume, int varsta) {
        this.nume = nume;
        this.varsta = varsta;
    }

    public Participant() {

    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public int getVarsta() {
        return varsta;
    }

    public void setVarsta(int varsta) {
        this.varsta = varsta;
    }

    @Override
    public String toString() {
        return "Participant{" +
                "nume='" + nume + '\'' +
                ", varsta=" + varsta +
                '}';
    }
}
