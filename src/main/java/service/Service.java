package service;

import domain.Inscriere;
import domain.Participant;
import domain.Proba;
import domain.Utilizator;
import javafx.application.Platform;
import repository.InscriereRepository;
import repository.ParticipantRepository;
import repository.ProbaRepository;
import repository.UtilizatorRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Service implements IService{
    private InscriereRepository inscriereInscriereDBRepository;
    private ParticipantRepository participantParticipantDBRepository;
    private ProbaRepository probaProbaDBRepository;
    private UtilizatorRepository utilizatorUtilizatorDBRepository;
    private Map<String, IAppObserver> loggedClients;

    public Service(InscriereRepository inscriereInscriereDBRepository, ParticipantRepository participantParticipantDBRepository, ProbaRepository probaProbaDBRepository, UtilizatorRepository utilizatorUtilizatorDBRepository) {
        this.inscriereInscriereDBRepository = inscriereInscriereDBRepository;
        this.participantParticipantDBRepository = participantParticipantDBRepository;
        this.probaProbaDBRepository = probaProbaDBRepository;
        this.utilizatorUtilizatorDBRepository = utilizatorUtilizatorDBRepository;
        loggedClients=new ConcurrentHashMap<>();
    }

    public Iterable<Utilizator> getUtilizatori() {
        return utilizatorUtilizatorDBRepository.findAll();
    }

    @Override
    public synchronized Proba[] getProbe() throws AppException{
        List<Proba> list = (List<Proba>) probaProbaDBRepository.findAll();
        Proba[] probe = new Proba[list.size()];
        for (int i =0;i<list.size();i++){
            probe[i] = list.get(i);
        }
        return probe;
    }

    @Override
    public synchronized Participant[] getParticipantiDupaProba(Proba p) throws AppException{
        List<Inscriere> list = new ArrayList<>();
        List<Participant> list2 = new ArrayList<>();
        inscriereInscriereDBRepository.findAll().forEach(x -> {
            list.add((Inscriere) x);
        });
        list.forEach(x -> {
            if (x.getProba().getId().equals(p.getId())) {
                list2.add(x.getParticipant());
            }
        });
        Participant[] participants = new Participant[list2.size()];
        for (int i =0;i<list2.size();i++){
            participants[i] = list2.get(i);
        }
        System.out.println("Id" + participants[0].getId());
        return participants;
    }

    public synchronized Proba[] getProbeDupaParticipanti(Participant p) throws AppException{
        List<Inscriere> list = new ArrayList<>();
        List<Proba> list2 = new ArrayList<>();
        inscriereInscriereDBRepository.findAll().forEach(x -> {
            list.add((Inscriere) x);
        });
        list.forEach(x -> {
            if (x.getParticipant().getId().equals(p.getId())) {
                list2.add(x.getProba());
            }
        });
        Proba[] probe = new Proba[list2.size()];
        for (int i =0;i<list2.size();i++){
            probe[i] = list2.get(i);
        }
        return probe;
    }

    public boolean existaInscriere(Participant participant, Proba proba) {
        List<Inscriere> list = new ArrayList<>();
        boolean ok = false;
        inscriereInscriereDBRepository.findAll().forEach(x -> {
            list.add((Inscriere) x);
        });
        for (var i : list) {
            if (i.getParticipant().getId() == participant.getId() && i.getProba().getId() == proba.getId())
                return true;
        }
        return false;

    }
    @Override
    public void Inscrie(Participant participant, Proba[] probas) throws AppException{
        List<Proba> probe = new ArrayList<>();
        for(int i=0;i<probas.length;i++){
            probe.add(probas[i]);
        }
        List<Participant> list = new ArrayList<>();
        boolean ok = false;
        participantParticipantDBRepository.findAll().forEach(x -> {
            list.add(x);
        });

        for (var i : list) {
            if (i.getNume().equals(participant.getNume()) && i.getVarsta() == participant.getVarsta())
                ok = true;
        }

        if (!ok) {
            participantParticipantDBRepository.save(participant);
        }
        for(var i : participantParticipantDBRepository.findAll()){
            if (i.getNume().equals(participant.getNume()) && i.getVarsta() == participant.getVarsta()) {
                participant.setId(i.getId());
            }
        }

        probe.forEach(x -> {
            if (!existaInscriere(participant, x)) {
                Inscriere inscriere = new Inscriere(participant, x);
                inscriereInscriereDBRepository.save(inscriere);
            }
        });
        notifyNewInscriere();

    }
    @Override
    public synchronized Integer getNrParticipanti(Proba p) throws AppException{
        AtomicInteger nr= new AtomicInteger();
        nr.set(0);
        inscriereInscriereDBRepository.findAll().forEach(x->{
            if(x.getProba().getId()==p.getId()){
                nr.getAndIncrement();
            }
        });
        return nr.get();
    }

    @Override
    public synchronized void login(Utilizator user, IAppObserver client) throws AppException {
        Utilizator userR=utilizatorUtilizatorDBRepository.findBy(user.getUser(),user.getParola());
        if (userR!=null){
            if(loggedClients.get(user.getUser())!=null)
                throw new AppException("User already logged in.");
            loggedClients.put(user.getUser(), client);
        }else
            throw new AppException("Authentication failed.");

    }

    @Override
    public void logout(Utilizator user, IAppObserver client) throws AppException {
        IAppObserver localClient=loggedClients.remove(user.getUser());
        if (localClient==null)
            throw new AppException("User "+user.getId()+" is not logged in.");
    }

    private final int defaultThreadsNo=5;
    private void notifyNewInscriere() throws AppException {
        Iterable<Utilizator> users=utilizatorUtilizatorDBRepository.findAll();
        System.out.println("Logged "+users);

        ExecutorService executor= Executors.newFixedThreadPool(defaultThreadsNo);
        for(Utilizator us :users){
            IAppObserver Client=loggedClients.get(us.getUser());
            if (Client!=null)
                executor.execute(() -> {
                    try {
                        System.out.println("Notifying [" + us.getId()+ "] logged in.");
                        Client.newInscriere();
                    } catch (AppException e) {
                        System.err.println("Error notifying friend " + e);
                    }
                });
        }

        executor.shutdown();
    }
}
