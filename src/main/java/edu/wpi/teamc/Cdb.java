package edu.wpi.teamc;

import edu.wpi.teamc.map.*;
import edu.wpi.teamc.map.Edge;
import edu.wpi.teamc.map.Graph;
import edu.wpi.teamc.map.Node;
import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cdb {
  static Connection connection = null;

  public static void main(String[] args) {
    try {
      // Load the PostgreSQL JDBC driver
      Class.forName("org.postgresql.Driver");

      // Establish the connection
      String url = "jdbc:postgresql://database.cs.wpi.edu/teamcdb";
      String user = "teamc";
      String password = "teamc30";
      connection = DriverManager.getConnection(url, user, password);

      Scanner scanner = new Scanner(System.in);
      // database tables turned into two arrayLists
      List<Node> databaseNodeList = new ArrayList<Node>();
      List<Edge> databaseEdgeList = new ArrayList<Edge>();
      List<LocationName> databaseLocationNameList = new ArrayList<LocationName>();
      List<Move> databaseMoveList = new ArrayList<Move>();
      // load database into lists
      loadDatabaseTables(
          databaseNodeList, databaseEdgeList, databaseLocationNameList, databaseMoveList);
      // variables
      String nodeID;
      String startNode;
      String endNode;
      int xCoordinate;
      int yCoordinate;
      String locationNameLong;
      String locationNameShort;
      String csvFileName;
      boolean continueProg = true;
      // switch case for menu options
      while (continueProg) {
        displayInstructions();
        String command = scanner.nextLine().toLowerCase();
        switch (command) {
          case "display node and edge information":
            displayNodeAndEdgeInfo(databaseNodeList, databaseEdgeList);
            break;
          case "update node coordinates":
            System.out.println("please type the nodeID of the node you would like to update:");
            nodeID = scanner.nextLine();
            System.out.println("Enter new x coordinate:");
            xCoordinate = scanner.nextInt();
            System.out.println("Enter new y coordinate:");
            yCoordinate = scanner.nextInt();
            // update the coordinates with given nodeID and values
            updateCoordinates(connection, databaseNodeList, xCoordinate, yCoordinate, nodeID);
            break;
          case "update name of location node":
            System.out.println("please type the nodeID of the node you would like to update:");
            nodeID = scanner.nextLine();
            System.out.println("Enter new location name in full:");
            locationNameLong = scanner.nextLine();
            System.out.println("Enter the shortened version of the new location name:");
            locationNameShort = scanner.nextLine();
            // update the location name of the given nodeID
            updateLocationName(
                databaseLocationNameList,
                databaseMoveList,
                locationNameLong,
                locationNameShort,
                nodeID);
            break;
          case "get specific node":
            System.out.println("please enter the node ID of the node you would like to get");
            nodeID = scanner.nextLine();
            getNode(databaseNodeList, nodeID);
            break;
          case "export node table into a csv file":
            csvFileName = "src/main/resources/edu/wpi/teamc/csvFiles/exportedNodes.csv";
            exportNodesToCSV(csvFileName, databaseNodeList);
            break;
          case "import from a csv file into the node table":
            csvFileName = "src/main/resources/edu/wpi/teamc/csvFiles/L1Nodes.csv";
            importCSV(connection, csvFileName, databaseNodeList);
            break;
          case "delete a node":
            System.out.println("please enter the node ID of the node you would like to delete");
            nodeID = scanner.nextLine();
            deleteNode(connection, databaseNodeList, nodeID);
            break;
          case "delete an edge":
            System.out.println(
                "please enter the starting NodeID of the edge you would like to delete");
            startNode = scanner.nextLine();
            System.out.println(
                "please enter the ending NodeID of the edge you would like to delete");
            endNode = scanner.nextLine();
            deleteEdge(connection, databaseEdgeList, startNode, endNode);
            break;
          case "help":
            System.out.println("");
            break;
          case "exit":
            continueProg = false;
            break;
          default:
            System.out.println("Command not found");
            break;
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      // Close the connection
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
  }

  static void loadDatabaseTables(
      List<Node> databaseNodeList,
      List<Edge> databaseEdgeList,
      List<LocationName> databaseLocationNameList,
      List<Move> databaseMoveList) {

    Graph temp = new Graph();
    try {
      temp.init();
    } catch (IOException e) {
      System.out.println("Exception!");
    }

    try {
      Statement stmtNode = connection.createStatement();
      Statement stmtEdge = connection.createStatement();
      Statement stmtLocationName = connection.createStatement();
      Statement stmtMove = connection.createStatement();
      // table names
      String node = "\"hospitalNode\".node";
      String edge = "\"hospitalNode\".edge";
      String locationName = "\"hospitalNode\".\"locationName\"";
      String move = "\"hospitalNode\".\"move\"";
      // queries
      String queryDisplayNodes = "SELECT * FROM " + node;
      String queryDisplayEdges = "SELECT * FROM " + edge;
      String queryDisplayLocationNames = "SELECT * FROM " + locationName;
      String queryDisplayMoves = "SELECT * FROM " + move;

      ResultSet rsNodes = stmtNode.executeQuery(queryDisplayNodes);
      ResultSet rsEdges = stmtEdge.executeQuery(queryDisplayEdges);
      ResultSet rsLocationNames = stmtLocationName.executeQuery(queryDisplayLocationNames);
      ResultSet rsMoves = stmtMove.executeQuery(queryDisplayMoves);
      while (rsNodes.next()) {
        String nodeID = rsNodes.getString("nodeID");
        int xCoord = rsNodes.getInt("xcoord");
        int yCoord = rsNodes.getInt("ycoord");
        String floor = rsNodes.getString("floorNum");
        String building = rsNodes.getString("building");

        databaseNodeList.add(new Node(nodeID, xCoord, yCoord, floor, building));
      }
      while (rsEdges.next()) {
        String startNode = rsEdges.getString("startNode");
        String endNode = rsEdges.getString("endNode");
        databaseEdgeList.add(new Edge(temp.getNode(startNode), temp.getNode(endNode)));
      }
      while (rsLocationNames.next()) {
        String locationNameLong = rsLocationNames.getString("locationNameLong");
        String locationNameShort = rsLocationNames.getString("locationNameShort");
        String nodeType = rsLocationNames.getString("nodeType");
        databaseLocationNameList.add(
            new LocationName(locationNameLong, locationNameShort, nodeType));
      }
      while (rsMoves.next()) {
        String nodeID = rsMoves.getString("nodeID");
        String longName = rsMoves.getString("longName");
        Date date = rsMoves.getDate("date");
        databaseMoveList.add(new Move(nodeID, longName, date));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  static void syncNodeDB(Connection connection, Node node, String operation) {
    try {
      // table names
      String NODE = "\"hospitalNode\".node";
      // queries
      String queryInsertNodesDB = "INSERT INTO " + NODE + " VALUES (?,?,?,?,?);";
      String queryUpdateNodesDB =
          "UPDATE  "
              + NODE
              + " SET \"nodeID\"=?, xcoord=?, ycoord=?, \"floorNum\"=?, building=? WHERE \"nodeID\"=?; ";
      String queryDeleteNodesDB = "DELETE FROM " + NODE + " WHERE \"nodeID\"=?; ";

      PreparedStatement ps;
      if (operation.equals("insert")) {
        ps = connection.prepareStatement(queryInsertNodesDB);
      } else if (operation.equals("update")) {
        ps = connection.prepareStatement(queryUpdateNodesDB);
      } else if (operation.equals("delete")) {
        ps = connection.prepareStatement(queryDeleteNodesDB);
      } else {
        throw new Exception("Invalid operation");
      }
      ps.setString(1, node.getNodeID());
      ps.setInt(2, node.getXCoord());
      ps.setInt(3, node.getYCoord());
      ps.setString(4, node.getFloor());
      ps.setString(5, node.getBuilding());
      if (operation.equals("update")) {
        ps.setString(6, node.getNodeID());
      }
      ps.executeUpdate();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  static void syncEdgeDB(Connection connection, Edge edge, String operation) {
    try {
      // table names
      String EDGE = "\"hospitalNode\".edge";
      // queries

      String queryInsertEdgesDB = "INSERT INTO " + EDGE + " VALUES (?,?); ";
      String queryUpdateEdgesDB =
          "UPDATE  "
              + EDGE
              + " SET \"startNode\"=?, \"endNode\"=? WHERE \"startNode\"=? AND \"endNode\"=?;";
      String queryDeleteEdgesDB =
          "DELETE FROM " + EDGE + " WHERE \"startNode\"=? AND \"endNode\"=?; ";

      PreparedStatement ps;
      if (operation.equals("insert")) {
        ps = connection.prepareStatement(queryInsertEdgesDB);
      } else if (operation.equals("update")) {
        ps = connection.prepareStatement(queryUpdateEdgesDB);
      } else if (operation.equals("delete")) {
        ps = connection.prepareStatement(queryDeleteEdgesDB);
      } else {
        throw new Exception("Invalid operation");
      }

      ps.setString(1, edge.getStartNode().getNodeID());
      ps.setString(2, edge.getEndNode().getNodeID());
      if (operation.equals("update")) {
        ps.setString(3, edge.getStartNode().getNodeID());
        ps.setString(4, edge.getEndNode().getNodeID());
      }
      ps.executeUpdate();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  static void displayInstructions() {
    System.out.println(
        "===========================================\n"
            + "Options:\n"
            + "Display node and edge information\n"
            + "Update node coordinates\n"
            + "Update name of location node\n"
            + "Get specific node\n"
            + "Export node table into a CSV file\n"
            + "Import from a CSV file into the node table\n"
            + "Delete a node\n"
            + "Delete an edge\n"
            + "Help\n"
            + "Exit\n"
            + "===========================================\n");
  }

  static void displayNodeAndEdgeInfo(List<Node> databaseNodeList, List<Edge> databaseEdgeList) {
    System.out.println("Node information:\n");
    for (Node node : databaseNodeList) {
      System.out.println(
          node.getNodeID()
              + "\t"
              + node.getXCoord()
              + "\t"
              + node.getYCoord()
              + "\t"
              + node.getFloor()
              + "\t"
              + node.getBuilding());
    }
    System.out.println(
        "----------------------------------------------------------------------------------------------------------------------------");
    System.out.println("Edge information: \n");
    for (Edge edge : databaseEdgeList) {
      System.out.println(edge.getStartNode() + "\t" + edge.getEndNode());
    }
  }

  static void updateCoordinates(
      Connection connection,
      List<Node> databaseNodeList,
      int xCoordinate,
      int yCoordinate,
      String nodeID) {
    for (Node node : databaseNodeList) {
      if (node.getNodeID().equals(nodeID)) {
        // update coordinates in node
        node.setXCoord(xCoordinate);
        node.setYCoord(yCoordinate);
        syncNodeDB(connection, node, "update");
        break;
      }
    }
  }

  /**
   * Updates the long and short location name in the database based on nodeID
   *
   * @param databaseLocationNameList list of location names in the database
   * @param databaseMoveList list of moves in the database
   * @param locationNameLong new long name
   * @param locationNameShort new short name
   * @param nodeID nodeID of the node to update
   */
  static void updateLocationName(
      List<LocationName> databaseLocationNameList,
      List<Move> databaseMoveList,
      String locationNameLong,
      String locationNameShort,
      String nodeID) {
    String oldLongName = "";
    for (Move move : databaseMoveList) {
      if (move.getNodeID().equals(nodeID)) {
        oldLongName = move.getLongName();
        move.setLongName(locationNameLong);
        move.setDate(Date.valueOf(LocalDate.now()));
      }
    }
    for (LocationName locationName : databaseLocationNameList) {
      if (locationName.getLongName().equals(oldLongName)) {
        locationName.setLongName(locationNameLong);
        locationName.setShortName(locationNameShort);
        break;
      }
    }
  }

  static void getNode(List<Node> databaseNodeList, String nodeID) {
    System.out.println("Node:\n");
    for (Node node : databaseNodeList) {
      if (node.getNodeID().equals(nodeID)) {
        System.out.println(
            node.getNodeID()
                + "\t"
                + node.getXCoord()
                + "\t"
                + node.getYCoord()
                + "\t"
                + node.getXCoord()
                + "\t"
                + node.getFloor()
                + "\t"
                + node.getBuilding());
        break;
      }
    }
  }

  static void deleteNode(Connection connection, List<Node> databaseNodeList, String nodeID) {
    int i = 0;
    for (Node node : databaseNodeList) {
      if (node.getNodeID().equals(nodeID)) {
        databaseNodeList.remove(i);
        System.out.println("node deletion successful!");
        syncNodeDB(connection, node, "delete");
        break;
      }
      i++;
    }
  }

  static void deleteEdge(
      Connection connection, List<Edge> databaseEdgeList, String startNodeID, String endNodeID) {
    int i = 0;
    for (Edge edge : databaseEdgeList) {
      if ((edge.getStartNode().getNodeID().equals(startNodeID))
          && (edge.getEndNode().getNodeID().equals(endNodeID))) {
        databaseEdgeList.remove(i);
        System.out.println("edge deletion successful!");
        syncEdgeDB(connection, edge, "delete");
        break;
      }
      i++;
    }
  }

  static void importCSV(Connection connection, String csvFile, List<Node> databaseNodeList) {
    // Regular expression to match each row
    String regex = "(.*),(\\d+),(\\d+),(.*),(.*)";
    // Compile regular expression pattern
    Pattern pattern = Pattern.compile(regex);
    try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
      String line;
      while ((line = br.readLine()) != null) {
        // Match the regular expression to the current line
        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()) {
          String nodeID = matcher.group(1);
          int xCoord = Integer.parseInt(matcher.group(2));
          int yCoord = Integer.parseInt(matcher.group(3));
          String floor = matcher.group(4);
          String building = matcher.group(5);
          Node node = new Node(nodeID, xCoord, yCoord, floor, building);
          databaseNodeList.add(node);
          syncNodeDB(connection, node, "add");
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  static void exportNodesToCSV(String csvFile, List<Node> databaseNodeList) throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile));
    // Write the header row to the CSV file
    writer.write("nodeID,xCoord,yCoord,floor,building\n");
    // Write each Node into the CSV file
    for (Node node : databaseNodeList) {
      writer.write(
          node.getNodeID()
              + ","
              + node.getXCoord()
              + ","
              + node.getYCoord()
              + ","
              + node.getFloor()
              + ","
              + node.getBuilding()
              + "\n");
    }

    writer.close();
  }
}
