package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MenuController {

    @FXML
    private void buttonPressed() {
        // Führen Sie die Datenbankabfrage aus, wenn der Button gedrückt wird
        executeDatabaseQuery();

        // Hier können Sie den Code für die Anzeige der Tabelle oder andere Aktionen hinzufügen
    }

    private void executeDatabaseQuery() {
        // Der Code der DatabaseAccessExample-Klasse
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Rufen Sie die vorher erstellte Verbindung aus der Database-Klasse ab
            connection = Database.getInstance().conn;

            // Erstellen eines Statement-Objekts
            statement = connection.createStatement();

            // Ausführen einer Datenbankabfrage
            String query = "SELECT * FROM user";
            resultSet = statement.executeQuery(query);

            // Verarbeiten der Abfrageergebnisse
            while (resultSet.next()) {
                // Zugriff auf Spaltenwerte (Beispiel: Spalte mit Index 1)
                String columnValue = resultSet.getString(1);
                System.out.println("Column Value: " + columnValue);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Schließen der Ressourcen im finally-Block
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            // Beachten Sie, dass die Connection in diesem Fall nicht geschlossen wird,
            // da sie von der Database-Klasse verwaltet wird und nicht hier erstellt wurde.
        }
    }
}
