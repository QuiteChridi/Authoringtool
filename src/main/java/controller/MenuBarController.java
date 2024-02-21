package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class MenuBarController {
    @FXML
    private MenuItem user;
    @FXML
    private MenuItem joker;
    @FXML
    private MenuItem questions;
    @FXML
    private MenuItem stats;

    @FXML
    private void switchToQuestion(ActionEvent event) throws Exception {
        switchScene("/view/question.fxml", questions);
    }

    @FXML
    private void switchToUser() throws Exception {
        switchScene("/view/user.fxml", user);
    }

    @FXML
    private void switchToStats() throws Exception {
        switchScene("/view/stats.fxml", stats);
    }

    @FXML
    private void switchToJoker()throws Exception{
        switchScene("/view/joker.fxml",joker);
    }

    private void switchScene(String fxmlPath, MenuItem menu) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) menu.getParentPopup().getOwnerNode().getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
