import domain.Member;
import domain.MemberType;
import domain.Team;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JPQLPathExpMain {

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

            //단순히 값을 저장하기 위한 필드 => 경로 탐색의 끝, 탐색 X
            String stateField = "select m.username from Member m";
            List<String> stateFieldList = entityManager.createQuery(stateField, String.class)
                                                       .getResultList();

            for(String s : stateFieldList) {
                System.out.println("s = " + s);
            }

            //묵시적 내부 조인(inner join) 발생 -> 탐색 O
            String associationOneField = "select m.team from Member m";
            List<Team> teams = entityManager.createQuery(associationOneField, Team.class)
                                            .getResultList();

            for(Team team : teams) {
                System.out.println("team.getName() = " + team.getName());
            }

            //묵시적 내부 조인(inner join) 발생 -> 탐색 X
            //members 이하로 탐색할 수 없으므로
//            String associationManyField = "select m.team.members from Member m";
//            List<Collections> members = entityManager.createQuery(associationManyField, Collections.class)
//                                                     .getResultList();

            //from 절에서 명시적 조인을 통해 별칭을 얻으면 별칭을 통해 탐색 가능
//            String associationManyFields = "select m from Team t join t.members m";
//            List<Collections> memberList = entityManager.createQuery(associationManyFields, Collections.class)
//                                                        .getResultList();


            //명시적 조인
            String 명시적_조인 = "select m, t from Member m join m.team t";
            List<Object[]> 명시적_조인_결과 = entityManager.createQuery(명시적_조인)
                                                    .getResultList();

            //쿼리 결과 확인

            tx.commit();
        } catch(Exception e) {
            e.printStackTrace();
            tx.rollback();
        }

        entityManager.close();

        entityManagerFactory.close();
    }

}
