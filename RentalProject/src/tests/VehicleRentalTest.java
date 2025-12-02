package tests;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import rental.Car; // imported for tests
import rental.Vehicle;
import rental.Customer;
import rental.RentalSystem;



public class VehicleRentalTest {

    @Test
    void testLicensePlate() {
    	Vehicle v = new Car("TestMake", "TestModel", 2000, 4);


        // VALID
        assertDoesNotThrow(() -> v.setLicensePlate("AAA100"));
        assertDoesNotThrow(() -> v.setLicensePlate("ABC567"));
        assertDoesNotThrow(() -> v.setLicensePlate("ZZZ999"));

        // INVALID
        assertThrows(IllegalArgumentException.class, () -> v.setLicensePlate(""));
        assertThrows(IllegalArgumentException.class, () -> v.setLicensePlate(null));
        assertThrows(IllegalArgumentException.class, () -> v.setLicensePlate("AAA1000"));
        assertThrows(IllegalArgumentException.class, () -> v.setLicensePlate("ZZZ99"));
    }
}
