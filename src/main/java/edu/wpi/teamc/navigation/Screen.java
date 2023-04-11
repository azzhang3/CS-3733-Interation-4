package edu.wpi.teamc.navigation;

public enum Screen {
  ROOT("views/Root.fxml"),
  SERVICE_REQUEST("views/ServiceRequest.fxml"),
  SIGNAGE("views/SignagePage.fxml"),
  ELEVATOR_SIGNAGE("views/ElevatorSignage.fxml"),
  ADMIN_HOME("views/AdminHome.fxml"),

  FLOOR_PLAN("views/FloorPlan.fxml"),

  HOME("views/Home.fxml"),
  GUEST_HOME("views/GuestHome.fxml"),
  MEAL("views/MealRequest.fxml"),
  MEAL_HISTORY("views/MealHistory.fxml"),
  CONFERENCE("views/Conference.fxml"),
  CONFERENCE_HISTORY("views/ConferenceHistory.fxml"),

  FLOWER("views/FlowerRequest.fxml"),
  FURNITURE("views/FurnitureRequest.fxml"),
  OFFICE_SUPPLY("views/OfficeSupplyRequest.fxml"),
  CONGRATS_PAGE("views/congratsPage.fxml"),
  MAP_HISTORY_PAGE("views/MapChangeHistory.fxml"),
  PATHFINDING_PAGE("views/Pathfinding.fxml"),
  HELP("views/Help.fxml"),
  EDIT_MAP("views/MapEditing.fxml");

  private final String filename;

  Screen(String filename) {
    this.filename = filename;
  }

  public String getFilename() {
    return filename;
  }
}
