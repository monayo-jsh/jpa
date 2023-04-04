package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.entity.Item;

@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    void testSave() {

        //Entity 는 null로 신규 객체 판단
        //기본 type 은 0 으로 판단
        //따라서 다음과 같이 id를 지정하면
        //DB를 조회해서 DB 객체를 요청 객체로 덮어쓰도록 되어있어 select query를 하게됨.
        //그 때 객체가 없는 경우 persist 처리.
        //따라서 로직으로 풀어내려면 implements Persistable<String> 를 구현해서
        //isNew 를 구현
        Item item = new Item("A");
        itemRepository.save(item);

    }
}