package learn.jpa;

import learn.domain.Member;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaJPQLWithFlush {

    public static void main(String[] args) {
        //EntityManagerFactory 하나만 생성해서 애플리케이션 전체에서 공유
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");

        //EntityManager 쓰레드간 공유 X 일회성으로 사용하고 버리기
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        //JPA 는 Transaction 안에서만 동작
        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        try {

            Member member1 = new Member(1000L, "member1000");
            Member member2 = new Member(2000L, "member2000");
            Member member3 = new Member(3000L, "member3000");

            entityManager.persist(member1);
            entityManager.persist(member2);
            entityManager.persist(member3);

            System.out.println("=== before JPQL ===");

            List<Member> members = entityManager.createQuery("select m from Member as m", Member.class)
                                                .getResultList();

            System.out.println("=== after JPQL ===");

            //쿼리 시 member1~3 조회되어야하므로 JPQL 실행 시 플러시가 호출되어 적재된 쿼리 수행
            members.forEach(System.out::println);

            transaction.commit();
        } catch(Exception e) {
            e.printStackTrace();
            transaction.rollback();
        }

        entityManager.close();

        entityManagerFactory.close();
    }

}
