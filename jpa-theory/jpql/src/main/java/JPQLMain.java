import domain.Member;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

public class JPQLMain {

    public static void main(String[] args) {
        //EntityManagerFactory 하나만 생성해서 애플리케이션 전체에서 공유
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");

        //EntityManager 쓰레드간 공유 X 일회성으로 사용하고 버리기
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        //JPA 는 Transaction 안에서만 동작
        EntityTransaction tx = entityManager.getTransaction();

        tx.begin();
        try {

            Member member = new Member();
            member.setUsername("memeber1");
            member.setAge(10);

            entityManager.persist(member);

            //반환 타입이 명확할 때
            TypedQuery<Member> query1 = entityManager.createQuery("select m from Member m", Member.class);

            //결과가 하나 이상일 떄
            List<Member> members = query1.getResultList();

            for(Member member1 : members) {
                System.out.println("member.id = " + member1.getId());
            }

            //결과가 정확히 하나 일 때, 결과가 없거나 둘 이상이면 예외 발생
            TypedQuery<Member> query2 = entityManager.createQuery("select m from Member m where m.age = 10", Member.class);

            Member member1 = query2.getSingleResult();
            System.out.println("member1.id = " + member1.getId());

            //반환 타입이 명확하지 않을 때
            Query query3 = entityManager.createQuery("select m.username, m.age from Member m");

            //파라미터 바인딩 - 이름 기반
            List<Member> membersByAgeBaseName = entityManager.createQuery("select m from Member m where m.age = :age", Member.class)
                                                     .setParameter("age", 10)
                                                     .getResultList();

            for(Member member2 : membersByAgeBaseName) {
                System.out.println("member2.age = " + member2.getAge());
            }

            //파라미터 바인딩 - 위치 기반
            List<Member> membersByAgeBasePosition = entityManager.createQuery("select m from Member m where m.age = ?1", Member.class)
                                                                 .setParameter(1, 10)
                                                                 .getResultList();

            for(Member member2 : membersByAgeBasePosition) {
                System.out.println("member2.getAge() = " + member2.getAge());
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
