package att.b2c.segment.customer.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String customerId;
    private String name;
    private String address;
    private String phone;
    private String zip;
}