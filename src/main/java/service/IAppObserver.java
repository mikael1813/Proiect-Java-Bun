package service;

import domain.Participant;
import domain.Utilizator;

public interface IAppObserver {
    void newInscriere() throws AppException;
}
