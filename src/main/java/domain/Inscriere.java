package domain;

public class Inscriere extends Entity<Integer>{
    private Participant participant;
    private Proba proba;

    public Inscriere(){

    }

    public Inscriere(Participant participant, Proba proba) {
        this.participant = participant;
        this.proba = proba;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public Proba getProba() {
        return proba;
    }

    public void setProba(Proba proba) {
        this.proba = proba;
    }
}
