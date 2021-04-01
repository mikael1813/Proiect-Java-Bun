package repository.databases;

import domain.Proba;
import domain.enums.Stil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.ProbaRepository;
import utils.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ProbaDBRepository implements ProbaRepository{

    private JdbcUtils dbUtils;

    private static final Logger logger = LogManager.getLogger();

    public ProbaDBRepository(Properties props) {
        logger.info("Initializing UtilizatorDBRepository with properties: {} ", props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public Proba findOne(Integer id) {
        logger.traceEntry("selecting entity {}", id);
        Connection con = dbUtils.getConnection();
        Proba proba1 = new Proba();

        try (PreparedStatement preStmt = con.prepareStatement("select * from Proba where ID=?")) {
            preStmt.setInt(1, (Integer) id);
            ResultSet result = preStmt.executeQuery();
            int id1 = result.getInt("ID");
            Float distanta = result.getFloat("distanta");
            String stil = result.getString("stil");
            Proba proba = new Proba(distanta, Stil.valueOf(stil));
            proba.setId(id1);
            proba1 = proba;
            logger.trace("Selected {} instance", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
        return proba1;
    }

    @Override
    public Iterable<Proba> findAll() {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        List<Proba> probas = new ArrayList<>();
        try (PreparedStatement preStmt = con.prepareStatement("select * from Proba")) {
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    int id = result.getInt("ID");
                    Float distanta = result.getFloat("distanta");
                    String stil = result.getString("stil");
                    Proba proba = new Proba(distanta, Stil.valueOf(stil));
                    proba.setId(id);
                    probas.add(proba);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error DB " + e);
        }
        logger.traceExit(probas);
        return probas;
    }


    @Override
    public Proba save(Proba entity) {
        Proba proba = (Proba) entity;
        logger.traceEntry("saving entity {}", entity);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("insert into Proba (distanta, stil) values (?,?)")) {
            preStmt.setFloat(1, proba.getDistanta());
            preStmt.setString(2, proba.getStil().toString());
            int result = preStmt.executeUpdate();
            logger.trace("Saved {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
        return proba;
    }

    @Override
    public void delete(Integer id) {
        logger.traceEntry("deleting entity {}", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("delete from Proba where ID=?")) {
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
    public Iterable<Proba> filterByStil(Stil stil) {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        List<Proba> probas = new ArrayList<>();
        try (PreparedStatement preStmt = con.prepareStatement("select * from Proba where stil=?")) {
            preStmt.setString(1, stil.toString());
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    int id = result.getInt("ID");
                    Float distanta = result.getFloat("distanta");
                    Proba proba = new Proba(distanta, stil);
                    proba.setId(id);
                    probas.add(proba);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error DB " + e);
        }
        logger.traceExit(probas);
        return probas;
    }
}
