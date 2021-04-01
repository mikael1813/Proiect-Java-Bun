package repository.databases;

import domain.Inscriere;
import domain.Participant;
import domain.Proba;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.InscriereRepository;
import repository.ParticipantRepository;
import repository.ProbaRepository;
import utils.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class InscriereDBRepository implements InscriereRepository {
    private JdbcUtils dbUtils;
    private ParticipantRepository participantRepository;
    private ProbaRepository probaRepository;

    private static final Logger logger = LogManager.getLogger();

    public InscriereDBRepository(Properties props, ProbaRepository probaRepository, ParticipantRepository participantRepository) {
        logger.info("Initializing CarsDBRepository with properties: {} ", props);
        dbUtils = new JdbcUtils(props);
        this.participantRepository = participantRepository;
        this.probaRepository = probaRepository;
    }

    @Override
    public Inscriere findOne(Integer id) {
        logger.traceEntry("selecting entity {}", id);
        Connection con = dbUtils.getConnection();
        Inscriere inscriere1 = new Inscriere();

        try (PreparedStatement preStmt = con.prepareStatement("select * from Inscriere where ID=?")) {
            preStmt.setInt(1, (Integer) id);
            ResultSet result = preStmt.executeQuery();
            int id1 = result.getInt("ID");
            int idParticipant = result.getInt("IDParticipant");
            Participant participant = (Participant) participantRepository.findOne(idParticipant);
            int idProba = result.getInt("IDProba");
            Proba proba = (Proba) probaRepository.findOne(idProba);
            Inscriere inscriere = new Inscriere(participant, proba);
            inscriere.setId(id1);
            inscriere1 = inscriere;
            logger.trace("Selected {} instance", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
        return inscriere1;
    }

    @Override
    public Iterable<Inscriere> findAll() {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        List<Inscriere> inscrieres = new ArrayList<>();
        try (PreparedStatement preStmt = con.prepareStatement("select * from Inscriere")) {
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    int id = result.getInt("ID");
                    int idParticipant = result.getInt("IDParticipant");
                    int idProba = result.getInt("IDProba");
                    Participant participant = participantRepository.findOne(idParticipant);
                    participant.setId(idParticipant);
                    Proba proba = probaRepository.findOne(idProba);
                    proba.setId(idProba);
                    Inscriere inscriere = new Inscriere(participant, proba);
                    inscriere.setId(id);
                    inscrieres.add(inscriere);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error DB " + e);
        }
        logger.traceExit(inscrieres);
        return inscrieres;
    }


    @Override
    public Inscriere save(Inscriere entity) {
        Inscriere inscriere = (Inscriere) entity;
        logger.traceEntry("saving entity {}", entity);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("insert into Inscriere (IDParticipant, IDProba) values (?,?)")) {
            preStmt.setInt(1, inscriere.getParticipant().getId());
            preStmt.setInt(2, inscriere.getProba().getId());
            int result = preStmt.executeUpdate();
            logger.trace("Saved {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
        return inscriere;
    }

    @Override
    public void delete(Integer id) {
        logger.traceEntry("deleting entity {}", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("delete from Inscriere where ID=?")) {
            preStmt.setInt(1, (Integer) id);
            int result = preStmt.executeUpdate();
            logger.trace("Deleted {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
    }
}
