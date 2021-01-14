package taass.payload;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Created by rajeevkumarsingh on 02/08/17.
 */
@Data
public class LoginRequest {
    @NotBlank
    private String userName;

    @NotBlank
    private String password;
}
