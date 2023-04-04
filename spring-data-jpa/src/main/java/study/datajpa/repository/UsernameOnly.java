package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {
    //open projection
    //query 해서 데이터는 다 가져와서 응답
    @Value("#{target.username + ' ' + target.age}")
    String getUsername();


    //close projection
    //String getUsername();
}
