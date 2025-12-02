package rental;


import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

import java.io.FileReader;
import java.io.BufferedReader;



public class RentalSystem {
    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private RentalHistory rentalHistory = new RentalHistory();
    
    private static RentalSystem instance = null; //The Singleton instance
    
    
    private RentalSystem() { //private constructor (enforces singleton.
    	
    	loadData();
    }
    
    public static RentalSystem getInstance() { //this is an accessor for the singleton instance
    	if(instance == null) {
    		instance = new RentalSystem();
    	}
    	return instance;
    }
    
    private void saveVehicle(Vehicle vehicle) { //saving vehicle
    	try (BufferedWriter writer = new BufferedWriter(new FileWriter("vehicle.txt", true))) { //opens vehicle.txt to be able to add to it
    		writer.write( //writes updated data
    				vehicle.getClass().getSimpleName() + "," + 
    				vehicle.getLicensePlate() + "," +
    				vehicle.getMake() + "," +
    				vehicle.getModel() + "," +
    				vehicle.getYear() + "," +
    				vehicle.getStatus()
    				);
    		writer.newLine();
		//closes when done
    	} 
    	catch (IOException e) { //Exception upon issue with writing
    		System.out.println("Error occured while saving machine:" + e.getMessage());
    	}
    	
    }
    
    private void saveCustomer(Customer customer) { // save customer method
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("customers.txt", true))) {//same method of operations as saveVehicle

            writer.write(customer.getCustomerId() + "," + customer.getCustomerName());
            writer.newLine();

        } catch (IOException e) {
            System.out.println("Error saving customer: " + e.getMessage());
        }
    }
    
    private void saveRecord(RentalRecord record) { //every rent and transation calls rental_records.txt, writes to file and keeps permanant record
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("rental_records.txt", true))) {

            writer.write(
                record.getRecordType() + "," +
                record.getVehicle().getLicensePlate() + "," +
                record.getCustomer().getCustomerId() + "," +
                record.getRecordDate() + "," +
                record.getTotalAmount()
            );
            writer.newLine();

        } catch (IOException e) {
            System.out.println("Error saving record: " + e.getMessage());
        }
    }
    
    
    private void loadVehicles() {
        try (BufferedReader reader = new BufferedReader(new FileReader("vehicles.txt"))) { //adjusted to support load considering that vehicle class must be abstract

            String line;
            while ((line = reader.readLine()) != null) {

                String[] parts = line.split(",");

                String type = parts[0];
                String plate = parts[1];
                String make = parts[2];
                String model = parts[3];
                int year = Integer.parseInt(parts[4]);
                Vehicle.VehicleStatus status = Vehicle.VehicleStatus.valueOf(parts[5]);

                Vehicle v = null;

                switch (type) {
                    case "Car":
                        v = new Car(make, model, year, 4); 
                        break;

                    case "Minibus":
                        v = new Minibus(make, model, year, false);
                        break;

                    case "PickupTruck":
                        v = new PickupTruck(make, model, year, 0.0, false);
                        break;
                }

                if (v != null) {
                    v.setLicensePlate(plate);
                    v.setStatus(status);
                    vehicles.add(v);
                }
            }

        } catch (Exception e) {
            // ignore missing files
        }
    }

    
    private void loadCustomers() {
        try (BufferedReader reader = new BufferedReader(new FileReader("customers.txt"))) { //restates number and customer name and readds to list

            String line;
            while ((line = reader.readLine()) != null) {

                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];

                customers.add(new Customer(id, name));
            }

        } catch (Exception e) {
         
        }
    }

    
    private void loadRecords() {
        try (BufferedReader reader = new BufferedReader(new FileReader("rental_records.txt"))) { //reconstructs the exact rentalrecord

            String line;
            while ((line = reader.readLine()) != null) {

                String[] parts = line.split(",");
                String type = parts[0];
                String plate = parts[1];
                int customerId = Integer.parseInt(parts[2]);
                String dateString = parts[3];
                double amount = Double.parseDouble(parts[4]);

                Vehicle v = findVehicleByPlate(plate);
                Customer c = findCustomerById(customerId);
                LocalDate date = LocalDate.parse(dateString);

                if (v != null && c != null) {
                    rentalHistory.addRecord(new RentalRecord(v, c, date, amount, type));
                }
            }

        } catch (Exception e) {
            // ignore missing file
        }
    }

    private void loadData() {
        loadVehicles();
        loadCustomers();
        loadRecords();
    }

    private void saveAllVehicles() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("vehicles.txt"))) { //saves all vehicles at once
            for (Vehicle v : vehicles) { 
                writer.write(
                    v.getLicensePlate() + "," +
                    v.getMake() + "," +
                    v.getModel() + "," +
                    v.getYear() + "," +
                    v.getStatus()
                );
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error when trying to save vehicles: " + e.getMessage());
        }
    }

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
        saveVehicle(vehicle);
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
        saveCustomer(customer);
    }

    public boolean rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) { //converted to boolean to return boolean value
    	
    	// ensure that the vehicle is available
    	if (vehicle.getStatus() != Vehicle.VehicleStatus.Rented) {
    	
    	vehicle.setStatus(Vehicle.VehicleStatus.Rented); // state Update
    	
    	//rental gets recorded
    	RentalRecord r = new RentalRecord(vehicle, customer, date, amount, "RENT");
    	rentalHistory.addRecord(r);
    	saveRecord(r);	
    	saveAllVehicles(); //calls to save all vehicle data
    		
    	return true; //renting succeed
    	}
    	return false; //renting failed as vehicle already rented
    }

    
    // Converted to boolean for testing, returns boolean value
    public boolean returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) { //updated saving and logic so all data is stored
       
    	
    	//Must currently be rented
    	if (vehicle.getStatus() == Vehicle.VehicleStatus.Rented) { 

    	//vehicle status updated
    	vehicle.setStatus(Vehicle.VehicleStatus.Available);
    	
    	//record
        RentalRecord r = new RentalRecord(vehicle, customer, date, extraFees, "RETURN");
        rentalHistory.addRecord(r);
        saveRecord(r);
        saveAllVehicles();  // persist updated status

        return true; // Returning succeeds
    	}
        return false; // Return failed as vehicle is available and not in possesion
    }


    public void displayVehicles(Vehicle.VehicleStatus status) {
        // Display appropriate title based on status
        if (status == null) {
            System.out.println("\n=== All Vehicles ===");
        } else {
            System.out.println("\n=== " + status + " Vehicles ===");
        }
        
        // Header with proper column widths
        System.out.printf("|%-16s | %-12s | %-12s | %-12s | %-6s | %-18s |%n", 
            " Type", "Plate", "Make", "Model", "Year", "Status");
        System.out.println("|--------------------------------------------------------------------------------------------|");
    	  
        boolean found = false;
        for (Vehicle vehicle : vehicles) {
            if (status == null || vehicle.getStatus() == status) {
                found = true;
                String vehicleType;
                if (vehicle instanceof Car) {
                    vehicleType = "Car";
                } else if (vehicle instanceof Minibus) {
                    vehicleType = "Minibus";
                } else if (vehicle instanceof PickupTruck) {
                    vehicleType = "Pickup Truck";
                } else {
                    vehicleType = "Unknown";
                }
                System.out.printf("| %-15s | %-12s | %-12s | %-12s | %-6d | %-18s |%n", 
                    vehicleType, vehicle.getLicensePlate(), vehicle.getMake(), vehicle.getModel(), vehicle.getYear(), vehicle.getStatus().toString());
            }
        }
        if (!found) {
            if (status == null) {
                System.out.println("  No Vehicles found.");
            } else {
                System.out.println("  No vehicles with Status: " + status);
            }
        }
        System.out.println();
    }

    public void displayAllCustomers() {
        for (Customer c : customers) {
            System.out.println("  " + c.toString());
        }
    }
    
    public void displayRentalHistory() {
        if (rentalHistory.getRentalHistory().isEmpty()) {
            System.out.println("  No rental history found.");
        } else {
            // Header with proper column widths
            System.out.printf("|%-10s | %-12s | %-20s | %-12s | %-12s |%n", 
                " Type", "Plate", "Customer", "Date", "Amount");
            System.out.println("|-------------------------------------------------------------------------------|");
            
            for (RentalRecord record : rentalHistory.getRentalHistory()) {                
                System.out.printf("| %-9s | %-12s | %-20s | %-12s | $%-11.2f |%n", 
                    record.getRecordType(), 
                    record.getVehicle().getLicensePlate(),
                    record.getCustomer().getCustomerName(),
                    record.getRecordDate().toString(),
                    record.getTotalAmount()
                );
            }
            System.out.println();
        }
    }
    
    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles) {
            if (v.getLicensePlate().equalsIgnoreCase(plate)) {
                return v;
            }
        }
        return null;
    }
    
    public Customer findCustomerById(int id) {
        for (Customer c : customers)
            if (c.getCustomerId() == id)
                return c;
        return null;
    }
    
    
    
}