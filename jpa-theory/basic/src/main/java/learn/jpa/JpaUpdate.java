package learn.jpa;

import learn.domain.Member;
import java.time.LocalDateTime;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaUpdate {

    public static void main(String[] args) {
        //EntityManagerFactory 하나만 생성해서 애플리케이션 전체에서 공유
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");

        //EntityManager 쓰레드간 공유 X 일회성으로 사용하고 버리기
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        //JPA 는 Transaction 안에서만 동작
        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        try {
            Member findMember = entityManager.find(Member.class, 1L);

            findMember.setName("member2 modify");

            LocalDateTime now = LocalDateTime.now();
            findMember.setCreateDate(now);
            findMember.setLastModifiedDate(now);

            //dirty check를 통해 변경된 내용은 jpa 인터페이스에서 업데이트 쿼리를 수행
            //entityManager.persist(findMember);

            transaction.commit();
        } catch(Exception e) {
            e.printStackTrace();
            transaction.rollback();
        }

        entityManager.close();

        entityManagerFactory.close();
    }

}
