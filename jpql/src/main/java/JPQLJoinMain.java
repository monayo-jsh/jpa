import domain.Member;
import domain.Team;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JPQLJoinMain {

    public static void main(String[] args) {
        //EntityManagerFactory 하나만 생성해서 애플리케이션 전체에서 공유
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");

        //EntityManager 쓰레드간 공유 X 일회성으로 사용하고 버리기
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        //JPA 는 Transaction 안에서만 동작
        EntityTransaction tx = entityManager.getTransaction();

        tx.begin();
        try {

            Team team = new Team();
            team.setName("TeamA");

            entityManager.persist(team);

            Member member = new Member();
            member.setUsername("memeber1");
            member.setAge(10);
            member.setTeam(team);

            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            String innerJoin = "select m from Member m join m.team t";
            List<Member> innerJoinResults = entityManager.createQuery(innerJoin, Member.class)
                                                         .getResultList();

            String outerJoin = "select m from Member m left join m.team t";
            List<Member> outerJoinResults = entityManager.createQuery(outerJoin, Member.class)
                                                         .getResultList();

            String setaJoin = "select m from Member m, Team t where m.username = t.name";
            List<Member> setaJoinResults = entityManager.createQuery(setaJoin, Member.class)
                                                        .getResultList();

            tx.commit();
        } catch(Exception e) {
            e.printStackTrace();
            tx.rollback();
        }

        entityManager.close();

        entityManagerFactory.close();
    }

}
