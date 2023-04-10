package edu.wpi.teamc.dao.map;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NodeDaoTest {

  @Test
  public void fetchAllObjects() {
    NodeDao nodeDao = new NodeDao();
    List<Node> nodes = nodeDao.fetchAllObjects();
    Assertions.assertNotNull(nodes);
  }

  @Test
  public void updateRow() {
    Node testNode = new Node(100, 1234, 1234, "L1", "1234");
    Node node = new Node(100, 444, 100, "L1", "1234");
    NodeDao nodeDao = new NodeDao();
    Assertions.assertEquals(1, nodeDao.updateRow(testNode, node));
  }

  @Test
  public void addRow() {
    Node testNode = new Node(1234, 1234, "L1", "1234");
    NodeDao nodeDao = new NodeDao();
    Assertions.assertEquals(1, nodeDao.addRow(testNode));
  }

  @Test
  public void deleteRow() {
    Node testNode = new Node(100, 1234, 1234, "L1", "1234");
    NodeDao nodeDao = new NodeDao();
    Assertions.assertEquals(1, nodeDao.deleteRow(testNode));
  }
}
