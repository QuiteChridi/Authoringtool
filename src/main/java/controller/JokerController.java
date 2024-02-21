package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Database;
import model.Joker;

import javax.swing.text.TabableView;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JokerController {

    @FXML
    public TableView<Joker> jokerTable;
    @FXML
    private TableColumn<Joker,Integer> idColumn;
    @FXML
    private TableColumn<Joker, String> nameColumn;
    @FXML
    private TableColumn<Joker, Integer>  priceColumn;
    @FXML
    private TableColumn<Joker, String> descriptionColumn;

    ObservableList<Joker> jokerData= FXCollections.observableArrayList();
    Connection connection= Database.getInstance().conn;

    @FXML
    private void initialize(){

        idColumn.setCellValueFactory(new PropertyValueFactory<>("idJoker"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nameJoker"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("costJoker"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("descriptionJoker"));

        loadJokerData();
    }

    private void loadJokerData(){
        jokerData.clear();
        String query="SELECT * FROM joker";
        try(PreparedStatement preparedStatement= connection.prepareStatement(query);
            ResultSet resultSet= preparedStatement.executeQuery()){

            while(resultSet.next()){
                int jokerId=resultSet.getInt("idjoker");
                String name= resultSet.getString("name");
                int cost= resultSet.getInt("price");
                String description= resultSet.getString("description");

                jokerData.add(new Joker(jokerId,name,cost,description));
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        jokerTable.setItems(jokerData);
    }
}
