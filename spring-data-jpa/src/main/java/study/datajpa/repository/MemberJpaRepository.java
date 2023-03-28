package study.datajpa.repository;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

@Repository
public class MemberJpaRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Member save(Member member) {
        entityManager.persist(member);

        return member;
    }

    public void delete(Member member) {
        entityManager.remove(member);
    }

    public Optional<Member> findById(Long id) {
        Member member = entityManager.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    public List<Member> findAll() {
        return entityManager.createQuery("select m from Member m", Member.class)
                            .getResultList();
    }

    public long count() {
        return entityManager.createQuery("select count(m) from Member m", Long.class)
                            .getSingleResult();
    }

    public List<Member> findByUsernameAndAgeGreaterThan(String username, int age) {
        return entityManager.createQuery("select m from Member m where m.username = :username and m.age > :age", Member.class)
                            .setParameter("username", username)
                            .setParameter("age", age)
                            .getResultList();
    }
}