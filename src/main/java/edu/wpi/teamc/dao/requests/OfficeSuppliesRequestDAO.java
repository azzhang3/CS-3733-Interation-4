package edu.wpi.teamc.dao.requests;

import edu.wpi.teamc.dao.DBConnection;
import edu.wpi.teamc.dao.IDao;
import edu.wpi.teamc.dao.users.IUser;
import edu.wpi.teamc.dao.users.PatientUser;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OfficeSuppliesRequestDAO implements IDao<OfficeSuppliesRequest, Integer> {

  public List<OfficeSuppliesRequest> fetchAllObjects() {
    DBConnection db = new DBConnection();
    List<OfficeSuppliesRequest> returnList = new ArrayList<>();
    String table = "\"ServiceRequests\".\"officeSupplyRequest\"";
    // queries
    String query = "SELECT * FROM " + table;
    try {
      ResultSet rs = db.getConnection().prepareStatement(query).executeQuery();
      while (rs.next()) {
        int requestID = rs.getInt("requestID");
        IUser req = new PatientUser(rs.getString("Requester"));
        String roomName = rs.getString("roomname");
        String supplies = rs.getString("officesupplytype");
        STATUS status = STATUS.valueOf(rs.getString("status"));
        String additionalNotes = rs.getString("additionalnotes");
        String eta = rs.getString("eta");
        String assignedto = rs.getString("assignedto");
        OfficeSuppliesRequest request =
            new OfficeSuppliesRequest(requestID, req, roomName, supplies, additionalNotes);
        request.setStatus(status);
        request.setEta(eta);
        returnList.add(request);
        request.setEta(assignedto);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    db.closeConnection();
    return returnList;
  }

  public OfficeSuppliesRequest addRow(OfficeSuppliesRequest orm) {
    DBConnection db = new DBConnection();
    String table = "\"ServiceRequests\".\"officeSupplyRequest\"";
    // queries
    String query =
        "INSERT INTO "
            + table
            + " (requester, roomName, officesupplytype, additionalNotes, status, assignedto) VALUES (?,?,?,?,?,?);";
    try {
      PreparedStatement ps =
          db.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

      ps.setString(1, orm.getRequester().toString());
      ps.setString(2, orm.getRoomName());
      ps.setString(3, orm.getOfficesupplytype());
      ps.setString(4, orm.getAdditionalNotes());
      ps.setString(5, orm.getStatus().toString());
      ps.setString(6, orm.getAssingedto());
      ps.executeUpdate();

      ResultSet rs = ps.getGeneratedKeys();
      rs.next();
      int requestID = rs.getInt("requestID");
      orm.requestID = (requestID);

    } catch (Exception e) {
      e.printStackTrace();
    }
    db.closeConnection();
    return orm;
  }

  public OfficeSuppliesRequest deleteRow(OfficeSuppliesRequest orm) {
    DBConnection db = new DBConnection();
    OfficeSuppliesRequest request = orm;
    String table = "\"ServiceRequests\".\"officeSupplyRequest\"";
    // queries
    String query = "DELETE FROM " + table + " WHERE requestID=? ;";
    try {
      ResultSet rs = db.getConnection().prepareStatement(query).executeQuery();
      request = null;
    } catch (Exception e) {
      e.printStackTrace();
    }
    db.closeConnection();
    return request;
  }

  @Override
  public OfficeSuppliesRequest fetchObject(Integer key) throws SQLException {
    OfficeSuppliesRequest request = null;
    try {
      DBConnection db = new DBConnection();
      String table = "\"ServiceRequests\".\"officeSupplyRequest\"";
      // queries
      String query = "SELECT * FROM " + table + " WHERE requestID = " + key + ";";
      ResultSet rs = db.getConnection().prepareStatement(query).executeQuery();
      while (rs.next()) {
        int requestID = rs.getInt("requestID");
        IUser req = new PatientUser(rs.getString("Requester"));
        String roomName = rs.getString("roomname");
        String supplies = rs.getString("officesupplytype");
        STATUS status = STATUS.valueOf(rs.getString("status"));
        String additionalNotes = rs.getString("additionalnotes");
        String eta = rs.getString("eta");
        String assignedto = rs.getString("assignedto");
        request = new OfficeSuppliesRequest(requestID, req, roomName, supplies, additionalNotes);
        request.setStatus(status);
        request.setEta(eta);
        request.setAssingedto(assignedto);
      }
      db.closeConnection();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return request;
  }

  public OfficeSuppliesRequest updateRow(OfficeSuppliesRequest orm, OfficeSuppliesRequest repl) {
    DBConnection db = new DBConnection();
    OfficeSuppliesRequest request = null;
    String table = "\"ServiceRequests\".\"officeSupplyRequest\"";
    // queries
    String query =
        "UPDATE "
            + table
            + " SET requester=?, roomName=?, supplies=?, additionalNotes=?, status =?, eta=?, assignedto = ? WHERE requestID=?;";
    try {
      PreparedStatement ps = db.getConnection().prepareStatement(query);
      ps.setString(1, repl.getRequester().toString());
      ps.setString(2, repl.getRoomName());
      ps.setString(3, repl.getOfficesupplytype());
      ps.setString(4, repl.getAdditionalNotes());
      ps.setString(5, repl.getStatus().toString());
      ps.setString(6, repl.getEta());
      ps.setString(7, repl.getAssingedto());
      ps.setInt(8, repl.getRequestID());
      ps.executeQuery();

      request = repl;
    } catch (Exception e) {
      e.printStackTrace();
    }
    db.closeConnection();
    return request;
  }
}
