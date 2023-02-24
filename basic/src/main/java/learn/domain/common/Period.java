package learn.domain.common;

import java.time.LocalDateTime;
import javax.persistence.Embeddable;

@Embeddable
public class Period {

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public Period() {}

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public boolean isValidation() {
        return false;
    }
}
