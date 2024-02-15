package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class MenuBar {
    @FXML
    private MenuItem user;

    @FXML
    private MenuItem questions;

    @FXML
    private void switchToQuestion(ActionEvent event) throws Exception {
        switchScene("/view/question.fxml", questions);
    }

    @FXML
    private void switchToUser() throws Exception {
        switchScene("/view/user.fxml", user);
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
