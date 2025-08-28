package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class PointController {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    private static final Logger log = LoggerFactory.getLogger(PointController.class);

    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    public UserPoint point(@PathVariable long id) {
        return userPointTable.selectById(id);
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    public List<PointHistory> history(@PathVariable long id) {
        return pointHistoryTable.selectAllByUserId(id);
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    public UserPoint charge(@PathVariable long id, @RequestBody long amount) {
        UserPoint current = userPointTable.selectById(id);
        long newAmount = current.point() + amount;

        UserPoint updated = userPointTable.insertOrUpdate(id, newAmount);
        pointHistoryTable.insert(id, amount, TransactionType.CHARGE, updated.updateMillis());
        return updated;
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    public UserPoint use(@PathVariable long id, @RequestBody long amount) {
        UserPoint current = userPointTable.selectById(id);
        if (current.point() < amount) {
            throw new RuntimeException("잔고가 부족합니다.");
        }

        long newAmount = current.point() - amount;
        UserPoint updated = userPointTable.insertOrUpdate(id, newAmount);
        pointHistoryTable.insert(id, amount, TransactionType.USE, updated.updateMillis());
        return updated;
    }
}

//