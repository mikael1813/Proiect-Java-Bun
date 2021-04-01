package repository.databases;

import domain.Participant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.ParticipantRepository;
import utils.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ParticipantDBRepository implements ParticipantRepository {
    private JdbcUtils dbUtils;

    private static final Logger logger = LogManager.getLogger();

    public ParticipantDBRepository(Properties props) {
        logger.info("Initializing CarsDBRepository with properties: {} ", props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public Participant findOne(Integer id) {
        logger.traceEntry("selecting entity {}", id);
        Connection con = dbUtils.getConnection();
        Participant participant1 = new Participant();

        try (PreparedStatement preStmt = con.prepareStatement("select * from Participant where ID=?")) {
            preStmt.setInt(1, (Integer) id);
            ResultSet result = preStmt.executeQuery();
            int id1 = result.getInt("ID");
            String nume = result.getString("nume");
            int varsta = result.getInt("varsta");
            Participant participant = new Participant(nume, varsta);
            participant.setId(id1);
            participant1 = participant;
            logger.trace("Selected {} instance", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
        return participant1;
    }

    @Override
    public Iterable<Participant> findAll() {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        List<Participant> participants = new ArrayList<>();
        try (PreparedStatement preStmt = con.prepareStatement("select * from Participant")) {
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    int id = result.getInt("ID");
                    String nume = result.getString("nume");
                    int varsta = result.getInt("varsta");
                    Participant participant = new Participant(nume, varsta);
                    participant.setId(id);
                    participants.add(participant);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error DB " + e);
        }
        logger.traceExit(participants);
        return participants;
    }


    @Override
    public Participant save(Participant entity) {
        Participant participant = (Participant) entity;
        logger.traceEntry("saving entity {}", entity);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("insert into Participant (nume, varsta) values (?,?)")) {
            preStmt.setString(1, participant.getNume());
            preStmt.setInt(2, participant.getVarsta());
            int result = preStmt.executeUpdate();
            logger.trace("Saved {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
        return participant;
    }

    @Override
    public void delete(Integer id) {
        logger.traceEntry("deleting entity {}", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("delete from Participant where ID=?")) {
            preStmt.setInt(1, (Integer) id);
            int result = preStmt.executeUpdate();
            logger.trace("Deleted {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
    }

    @Override
    public Iterable<Participant> filterByName(String name) {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        List<Participant> participants = new ArrayList<>();
        try (PreparedStatement preStmt = con.prepareStatement("select * from Participant where nume=?")) {
            preStmt.setString(1, name);
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    int id = result.getInt("ID");
                    int varsta = result.getInt("varsta");
                    Participant participant = new Participant(name, varsta);
                    participant.setId(id);
                    participants.add(participant);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error DB " + e);
        }
        logger.traceExit(participants);
        return participants;
    }

    @Override
    public Iterable<Participant> filterByVarsta(int varsta) {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        List<Participant> participants = new ArrayList<>();
        try (PreparedStatement preStmt = con.prepareStatement("select * from Participant where varsta=?")) {
            preStmt.setInt(1, varsta);
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    int id = result.getInt("ID");
                    String name = result.getString("nume");
                    Participant participant = new Participant(name, varsta);
                    participant.setId(id);
                    participants.add(participant);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error DB " + e);
        }
        logger.traceExit(participants);
        return participants;
    }
}
