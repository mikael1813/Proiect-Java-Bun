package repository;

import domain.Utilizator;

public interface UtilizatorRepository extends Repository<Integer, Utilizator>{
    Utilizator findBy(String username,String parola);
}
