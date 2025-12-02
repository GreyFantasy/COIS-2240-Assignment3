package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import rental.*; //import rental package' classes
import java.time.LocalDate;

public class VehicleRentalTest {

    @Test
    void testLicensePlate() {
    	Vehicle v = new Car("TestMake", "TestModel", 2000, 4);


        // VALID Plates
        assertDoesNotThrow(() -> new Car("Toyota", "Corolla", 2019, 5).setLicensePlate("AAA100")); //test 1
        assertDoesNotThrow(() -> new Car("Honda", "Civic", 2020, 5).setLicensePlate("ABC567")); //test 2
        assertDoesNotThrow(() -> new Car("Ford", "Focus", 2021, 5).setLicensePlate("ZZZ999")); //test 3

        // INVALID Plates
        Car c = new Car("Test", "Model", 2022, 4);

        assertThrows(IllegalArgumentException.class, () -> c.setLicensePlate(""));        // empty
        assertThrows(IllegalArgumentException.class, () -> c.setLicensePlate(null));      // null
        assertThrows(IllegalArgumentException.class, () -> c.setLicensePlate("AAA1000")); // too many digits
        assertThrows(IllegalArgumentException.class, () -> c.setLicensePlate("ZZZ99"));   // too short
        
    }
        @Test
        void testRentAndReturnVehicle() {

            // Create test vehicle & customer
            Vehicle v = new Car("Toyota", "Corolla", 2020, 5);
            v.setLicensePlate("AAA111");

            Customer c = new Customer(1, "George");

            // Vehicle must start available
            assertEquals(Vehicle.VehicleStatus.Available, v.getStatus());

            // Get singleton
            RentalSystem rs = RentalSystem.getInstance();

            //First rent should succeed
            boolean firstRent = rs.rentVehicle(v, c, LocalDate.now(), 100.0);
            assertTrue(firstRent);
            assertEquals(Vehicle.VehicleStatus.Rented, v.getStatus());

            //Second rent should fail
            boolean secondRent = rs.rentVehicle(v, c, LocalDate.now(), 50.0);
            assertFalse(secondRent);

            //First return should succeed
            boolean firstReturn = rs.returnVehicle(v, c, LocalDate.now(), 20.0);
            assertTrue(firstReturn);
            assertEquals(Vehicle.VehicleStatus.Available, v.getStatus());

            //Second return should fail
            boolean secondReturn = rs.returnVehicle(v, c, LocalDate.now(), 5.0);
            assertFalse(secondReturn);
        }
        
        @Test
        void testSingletonInstance() {

            // this gets the RentalSystem multiple times
            RentalSystem rs1 = RentalSystem.getInstance();
            RentalSystem rs2 = RentalSystem.getInstance();
            RentalSystem rs3 = RentalSystem.getInstance();

            // this ensure that all the references point to the same object from/in memory
            assertSame(rs1, rs2);
            assertSame(rs2, rs3);
            
            //confirm that the references point to the same object to ensure singleton works
        }


        
    
}
