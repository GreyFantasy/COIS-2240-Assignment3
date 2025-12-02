package rental;


public abstract class Vehicle {
    private String licensePlate;
    private String make;
    private String model;
    private int year;
    private VehicleStatus status;

    public enum VehicleStatus { Available, Held, Rented, UnderMaintenance, OutOfService }
    
    private boolean isValidPlate(String plate) { // Validation method
    	
    	if (plate == null || plate.isEmpty()) { ///ensures plate cant be empty
    		return false;
    	}
    	
        return plate != null && plate.matches("[A-Z]{3}[0-9]{3}"); //plate has to be 3 letters and 3 digits
    }
    

    public Vehicle(String make, String model, int year) {
    	if (make == null || make.isEmpty())
    		this.make = null;
    	else
    		this.make = make.substring(0, 1).toUpperCase() + make.substring(1).toLowerCase();
    	
    	if (model == null || model.isEmpty())
    		this.model = null;
    	else
    		this.model = model.substring(0, 1).toUpperCase() + model.substring(1).toLowerCase();
    	
        this.year = year;
        this.status = VehicleStatus.Available;
        this.licensePlate = null;
    }

    public Vehicle() {
        this(null, null, 0);
    }

    public void setLicensePlate(String plate) { // updated setLicensePlate that uses validation method
        if (!isValidPlate(plate)) {
            throw new IllegalArgumentException("Invalid license plate format. Please format as ABC123"); //Exception, requests correct format.
        }
        this.licensePlate = plate;
    }
    
    public void setStatus(VehicleStatus status) {
    	this.status = status;
    }

    public String getLicensePlate() { return licensePlate; }

    public String getMake() { return make; }

    public String getModel() { return model;}

    public int getYear() { return year; }

    public VehicleStatus getStatus() { return status; }

    public String getInfo() {
        return "| " + licensePlate + " | " + make + " | " + model + " | " + year + " | " + status + " |";
    }

}
