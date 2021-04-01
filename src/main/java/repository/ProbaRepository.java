package repository;

import domain.Proba;
import domain.enums.Stil;

public interface ProbaRepository extends Repository<Integer, Proba>{
    Iterable<Proba> filterByStil(Stil stil);
}
