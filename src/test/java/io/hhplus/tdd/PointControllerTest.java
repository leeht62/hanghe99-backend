package io.hhplus.tdd;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointController;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class PointControllerTest {
  private UserPointTable userPointTable;
  private PointHistoryTable pointHistoryTable;
  private PointController controller;

  @BeforeEach
  void setup() {
    userPointTable = new UserPointTable();
    pointHistoryTable = new PointHistoryTable();
    controller = new PointController(userPointTable, pointHistoryTable);
  }

  @Test
  void givenChargeRequest_whenChargePoints_thenReturnsPoints() {
    long userId = 1L;
    // 100포인트 충전
    UserPoint updated = controller.charge(userId, 100);
    // 포인트 잔액과 내역 검증
    assertEquals(100, updated.point());
    UserPoint user = controller.point(userId);
    assertEquals(100, user.point());
  }
  @Test
  void pointFindById() {
    UserPointTable mockTable = mock(UserPointTable.class);
    PointHistoryTable mockHistory = mock(PointHistoryTable.class);
    //1000포인트를 갖고 있는 회원
    when(mockTable.selectById(1L))
        .thenReturn(new UserPoint(1L, 1000L, System.currentTimeMillis()));

    PointController controller = new PointController(mockTable,mockHistory);

    UserPoint result = controller.point(1L);
    //회원 포인트 맞는지 검증
    assertEquals(1000L, result.point());
  }
  @Test
  void givenUseRequest_whenUsePoints_thenReturnUsePoints(){
    UserPointTable mockTable = mock(UserPointTable.class);
    PointHistoryTable mockHistory = mock(PointHistoryTable.class);
    when(mockTable.selectById(1L))
        .thenReturn(new UserPoint(1L, 1000L, System.currentTimeMillis()));
    // 포인트 500 사용 후 잔고 500으로 업데이트
    when(mockTable.insertOrUpdate(1L, 500L))
        .thenReturn(new UserPoint(1L, 500L, System.currentTimeMillis()));
    PointController mockcontroller = new PointController(mockTable, mockHistory);
    // 포인트 사용 메서드 호출
    UserPoint result = mockcontroller.use(1L, 500L);
    // 잔고가 500으로 줄어듬
    assertEquals(500L, result.point());
  }

  @Test
  void FindPointHistory() {
    UserPointTable mockTable = mock(UserPointTable.class);
    PointHistoryTable mockHistory = mock(PointHistoryTable.class);
    long userId = 1L;
    List<PointHistory> mockHistories = List.of(
        new PointHistory(1L, userId, 500L, TransactionType.CHARGE, System.currentTimeMillis()),
        new PointHistory(2L, userId, 200L, TransactionType.USE, System.currentTimeMillis())
    );

    // userId로 히스토리 조회시 위 리스트 반환
    when(mockHistory.selectAllByUserId(userId)).thenReturn(mockHistories);

    PointController controller = new PointController(mockTable, mockHistory);

    // 히스토리 조회
    List<PointHistory> histories = controller.history(userId);

    // 결과값
    assertEquals(500L, histories.get(0).amount());
    assertEquals(200L, histories.get(1).amount());
    assertEquals(2, histories.size());
  }

}
