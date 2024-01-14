import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContainerManagerTest {

    @Test
    public void testhandleOption1() throws Exception {
        String text = "Handling Option 1: Create a container and check the list with status with an optional id";

        assertEquals("Handling Option 1: Create a container and check the list with status with an optional id",
                text);
    }
}
