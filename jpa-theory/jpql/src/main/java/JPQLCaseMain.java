import domain.Member;
import domain.MemberType;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JPQLCaseMain {

    public static void main(String[] args) {
        //EntityManagerFactory 하나만 생성해서 애플리케이션 전체에서 공유
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");

        //EntityManager 쓰레드간 공유 X 일회성으로 사용하고 버리기
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        //JPA 는 Transaction 안에서만 동작
        EntityTransaction tx = entityManager.getTransaction();

        tx.begin();
        try {

            Member member1 = new Member();
            member1.setUsername("memeber1");
            member1.setAge(10);
            member1.setType(MemberType.USER);

            Member member2 = new Member();
            member2.setAge(20);
            member2.setType(MemberType.USER);

            Member member3 = new Member();
            member3.setUsername("관리자");
            member3.setAge(30);
            member3.setType(MemberType.ADMIN);

            entityManager.persist(member1);
            entityManager.persist(member2);
            entityManager.persist(member3);

            entityManager.flush();
            entityManager.clear();

            //기본 CASE
            String query =  "select " 
                            + "case when m.age <= 10 then '학생요금'"
                            + "     when m.age >= 60 then '경로요금'"
                            + "     else '일반요금'"
                            + "end "
                            + "from Member m";

            List<String> results = entityManager.createQuery(query, String.class)
                                                  .getResultList();

            for(String result : results) {
                System.out.println("result = " + result);
            }

            String coalesceQuery = "select coalesce(m.username, '알 수 없는 회원') from Member m";
            List<String> coalesceList = entityManager.createQuery(coalesceQuery, String.class)
                                                     .getResultList();

            for(String coalesce : coalesceList) {
                System.out.println("coalesce = " + coalesce);
            }

            String nullifQuery = "select nullif(m.type, '관리자') from Member m";
            List<String> nullifList = entityManager.createQuery(nullifQuery, String.class)
                                                   .getResultList();

            for(String nullif : nullifList) {
                System.out.println("nullif = " + nullif);
            }

            tx.commit();
        } catch(Exception e) {
            e.printStackTrace();
            tx.rollback();
        }

        entityManager.close();

        entityManagerFactory.close();
    }

}
