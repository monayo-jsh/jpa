import domain.Member;
import domain.MemberType;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JPQLFunctionMain {

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
            member2.setUsername("memeber2");
            member2.setAge(20);
            member2.setType(MemberType.USER);

            Member member3 = new Member();
            member3.setUsername("member3");
            member3.setAge(30);
            member3.setType(MemberType.ADMIN);

            entityManager.persist(member1);
            entityManager.persist(member2);
            entityManager.persist(member3);

            entityManager.flush();
            entityManager.clear();

            String concat = "select concat(m.username, m.age) from Member m";
            List<String> concatList = entityManager.createQuery(concat, String.class)
                                                .getResultList();

            for(String result : concatList) {
                System.out.println("result = " + result);
            }

            String substring = "select substring(m.username, 0, 3) from Member m";
            List<String> substringList = entityManager.createQuery(substring, String.class)
                                                .getResultList();

            for(String result : substringList) {
                System.out.println("result = " + result);
            }

            String locate = "select locate('de', 'abcdefg') from Member m";
            List<Integer> locateList = entityManager.createQuery(locate, Integer.class)
                                               .getResultList();

            for(Integer result : locateList) {
                System.out.println("result = " + result);
            }

            //collection의 size를 반환
            String size = "select size(t.members) from Team t";
            List<Integer> sizeList = entityManager.createQuery(size, Integer.class)
                                                  .getResultList();

            for(Integer result : sizeList) {
                System.out.println("result = " + result);
            }

            //dialect에 사용자 함수 등록 후 호출
            String groupConcat = "select function('group_concat', m.username) from Member m";
            List<String> groupConcatList = entityManager.createQuery(groupConcat, String.class)
                                                        .getResultList();

            for(String result : groupConcatList) {
                System.out.println("result = " + result);
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
