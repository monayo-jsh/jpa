package learn.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "TEST")
@SequenceGenerator(name = "TEST_SEQ_GENERATOR", sequenceName = "TEST_SEQ", initialValue = 1, allocationSize = 1)
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TEST_SEQ_GENERATOR")
    private Long id;
    private String name;

    public Test() {}

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
