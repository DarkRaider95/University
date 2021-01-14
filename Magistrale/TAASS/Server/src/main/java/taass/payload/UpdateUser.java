package taass.payload;

import taass.model.User;

public class UpdateUser extends User {

    private String confirmPassword; // vecchia password per confermare che la conosce

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
