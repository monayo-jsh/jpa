package study.datajpa.repository;

public interface NestedClosedProjections {

    String getUsername();
    TeamInfo getTeam();

    //중첩 객체는 모두 다 가져옴 최적화가 되지 않음
    interface TeamInfo {
        String getName();
    }
}
