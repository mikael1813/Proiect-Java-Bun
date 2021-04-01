package domain;

public class Tuple {
    private Participant p;
    private Proba[] probas;

    public Tuple(Participant p, Proba[] probas) {
        this.p = p;
        this.probas = probas;
    }

    public Participant getP() {
        return p;
    }

    public void setP(Participant p) {
        this.p = p;
    }

    public Proba[] getProbas() {
        return probas;
    }

    public void setProbas(Proba[] probas) {
        this.probas = probas;
    }
}
