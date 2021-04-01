package repository;

import domain.Participant;

public interface ParticipantRepository extends Repository<Integer, Participant>{
    Iterable<Participant> filterByName(String name);

    Iterable<Participant> filterByVarsta(int varsta);
}
