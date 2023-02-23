package domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "TABLE_SEQUENCE")
@TableGenerator(name = "TABLE_SEQ_GENERATOR", table = "TABLE_SEQ", pkColumnValue = "TEST_SEQ", allocationSize = 1)
public class TableSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TABLE_SEQ_GENERATOR")
    private Long id;
    private String name;

    public TableSequence() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
