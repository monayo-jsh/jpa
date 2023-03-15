package learn.domain.item;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE) //default
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Item extends BaseEntity {

    @Id @GeneratedValue
    private Long id;

    private String name;
    private Integer price;

}
