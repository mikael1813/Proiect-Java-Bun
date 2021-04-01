package repository.databases;

import domain.Utilizator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.UtilizatorRepository;
import utils.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class UtilizatorDBRepository implements UtilizatorRepository {
    private JdbcUtils dbUtils;

    private static final Logger logger = LogManager.getLogger();

    public UtilizatorDBRepository(Properties props) {
        logger.info("Initializing UtilizatorDBRepository with properties: {} ", props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public Utilizator findOne(Integer id) {
        logger.traceEntry("selecting entity {}", id);
        Connection con = dbUtils.getConnection();
        Utilizator utilizator1 = new Utilizator();

        try (PreparedStatement preStmt = con.prepareStatement("select * from Utilizator where ID=?")) {
            preStmt.setInt(1, (Integer) id);
            ResultSet result = preStmt.executeQuery();
            int id1 = result.getInt("ID");
            String user = result.getString("user");
            String pass = result.getString("parola");
            Utilizator utilizator = new Utilizator(user, pass);
            utilizator.setId(id1);
            utilizator1 = utilizator;
            logger.trace("Selected {} instance", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
        return utilizator1;
    }

    @Override
    public Iterable<Utilizator> findAll() {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        List<Utilizator> utilizators = new ArrayList<>();
        try (PreparedStatement preStmt = con.prepareStatement("select * from Utilizator")) {
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    int id = result.getInt("ID");
                    String user = result.getString("user");
                    String pass = result.getString("parola");
                    Utilizator utilizator = new Utilizator(user, pass);
                    utilizator.setId(id);
                    utilizators.add(utilizator);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error DB " + e);
        }
        logger.traceExit(utilizators);
        return utilizators;
    }


    @Override
    public Utilizator save(Utilizator entity) {
        Utilizator utilizator = (Utilizator) entity;
        logger.traceEntry("saving entity {}", entity);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("insert into Utilizator (user, parola) values (?,?)")) {
            preStmt.setString(1, utilizator.getUser());
            preStmt.setString(2, utilizator.getParola());
            int result = preStmt.executeUpdate();
            logger.trace("Saved {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
        return utilizator;
    }

    @Override
    public void delete(Integer id) {
        logger.traceEntry("deleting entity {}", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("delete from Utilizator where ID=?")) {
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
    public Utilizator findBy(String username, String parola) {
        logger.traceEntry("selecting entity {}", username);
        Connection con = dbUtils.getConnection();
        Utilizator utilizator1 = new Utilizator();

        try (PreparedStatement preStmt = con.prepareStatement("select * from Utilizator where user=? and parola=?")) {
            preStmt.setString(1,  username);
            preStmt.setString(2,  parola);
            ResultSet result = preStmt.executeQuery();
            int id1 = result.getInt("ID");
            String user = result.getString("user");
            String pass = result.getString("parola");
            Utilizator utilizator = new Utilizator(user, pass);
            utilizator.setId(id1);
            utilizator1 = utilizator;
            logger.trace("Selected {} instance", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
        return utilizator1;
    }
}
