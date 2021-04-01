package domain;

import domain.enums.Stil;

public class Proba extends Entity<Integer>{
    private float distanta;
    private Stil stil;

    public Proba(float distanta, Stil stil) {
        this.distanta = distanta;
        this.stil = stil;
    }

    public Proba() {

    }

    public float getDistanta() {
        return distanta;
    }

    public void setDistanta(float distanta) {
        this.distanta = distanta;
    }

    public Stil getStil() {
        return stil;
    }

    public void setStil(Stil stil) {
        this.stil = stil;
    }

    @Override
    public String toString() {
        return "Proba{" +
                "distanta=" + distanta +
                ", stil=" + stil +
                '}';
    }
}
