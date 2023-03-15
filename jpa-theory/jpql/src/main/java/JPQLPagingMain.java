import domain.Member;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JPQLPagingMain {

    public static void main(String[] args) {
        //EntityManagerFactory 하나만 생성해서 애플리케이션 전체에서 공유
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");

        //EntityManager 쓰레드간 공유 X 일회성으로 사용하고 버리기
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        //JPA 는 Transaction 안에서만 동작
        EntityTransaction tx = entityManager.getTransaction();

        tx.begin();
        try {

            //조회 대상 설정
            for(int i=0; i<100; i++) {
                Member member = new Member();
                member.setUsername(String.format("member%s",i));
                member.setAge(i*10);

                entityManager.persist(member);
            }

            List<Member> members = entityManager.createQuery("select m from Member m order by m.id desc", Member.class)
                                                .setFirstResult(10)
                                                .setMaxResults(10)
                                                .getResultList();

            System.out.println("select size : " + members.size());
            for(Member member : members) {
                System.out.println("member.getUsername() = " + member.getUsername());
                System.out.println("member.getAge() = " + member.getAge());
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
