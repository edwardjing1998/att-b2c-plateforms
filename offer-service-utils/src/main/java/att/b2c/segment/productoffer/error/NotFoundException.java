package att.b2c.segment.productoffer.error;

import java.util.UUID;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String resourceName, UUID id) {
        super(resourceName + " not found: " + id);
    }

    public NotFoundException(String message) {
        super(message);
    }
}
