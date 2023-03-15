package learn.domain.item;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("BOCK")
public class Book extends Item {

    private String author;
    private String isbn;

}
