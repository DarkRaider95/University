package taass.payload;

import taass.model.Rent;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class RentDates {
    @NotNull
    private Date startDate;

    @NotNull
    private Date endDate;

    public RentDates(Rent rent){
        startDate = rent.getStartDate();
        endDate = rent.getEndDate();
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
