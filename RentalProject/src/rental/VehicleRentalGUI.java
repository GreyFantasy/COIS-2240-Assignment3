package rental;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class VehicleRentalGUI extends Application {

    private RentalSystem rentalSystem = RentalSystem.getInstance();

    @Override
    public void start(Stage primaryStage) {

        // Controls for UI
        TextField plateField = new TextField();
        plateField.setPromptText("License Plate");

        TextField makeField = new TextField();
        makeField.setPromptText("Make");

        TextField modelField = new TextField();
        modelField.setPromptText("Model");

        TextField yearField = new TextField();
        yearField.setPromptText("Year");

        Button addVehicleBtn = new Button("Add Vehicle");

        TextField custIdField = new TextField();
        custIdField.setPromptText("Customer ID");

        TextField custNameField = new TextField();
        custNameField.setPromptText("Customer Name");

        Button addCustomerBtn = new Button("Add Customer");

        Button showVehiclesBtn = new Button("Show Available Vehicles");

        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);

        // action buttons ^

        // add vehicle
        addVehicleBtn.setOnAction(e -> {
            try {
                String plate = plateField.getText().toUpperCase();
                String make = makeField.getText();
                String model = modelField.getText();
                int year = Integer.parseInt(yearField.getText());

                Vehicle v = new Car(make, model, year, 4); // simplest vehicle
                v.setLicensePlate(plate);

                rentalSystem.addVehicle(v);
                outputArea.appendText("Vehicle added: " + plate + "\n");
            }
            catch (Exception ex) {
                outputArea.appendText("Error adding vehicle: " + ex.getMessage() + "\n");
            }
        });

        // add customer
        addCustomerBtn.setOnAction(e -> {
            try {
                int id = Integer.parseInt(custIdField.getText());
                String name = custNameField.getText();

                rentalSystem.addCustomer(new Customer(id, name));
                outputArea.appendText("Customer added: " + name + "\n");
            }
            catch (Exception ex) {
                outputArea.appendText("Error adding customer: " + ex.getMessage() + "\n");
            }
        });

        // shows vehicles
        showVehiclesBtn.setOnAction(e -> {
            outputArea.appendText("\n=== Available Vehicles ===\n");
            for (Vehicle v : rentalSystem.getVehiclesCopy()) {  
                if (v.getStatus() == Vehicle.VehicleStatus.Available) {
                    outputArea.appendText(v.getLicensePlate() + " - " + v.getMake() + " " + v.getModel() + "\n");
                }
            }
        });

        // the LAYOUT 

        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 20;");

        root.getChildren().addAll(
            new Label("Add Vehicle"),
            plateField, makeField, modelField, yearField, addVehicleBtn,

            new Label("Add Customer"),
            custIdField, custNameField, addCustomerBtn,

            showVehiclesBtn,
            outputArea
        );

        // the  WINDOW
        Scene scene = new Scene(root, 400, 550);
        primaryStage.setTitle("Vehicle Rental GUI");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
