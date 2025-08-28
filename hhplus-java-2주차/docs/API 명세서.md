
##  API 명세서


| Method    | URI                                 | Description           |
|----------|-------------------------------------|-----------------------|
| POST     | /api/payment/charge                  | 사용자 잔액 충전             |
| GET      | /api/payment/balance/{userId}       | 특정 사용자의 현재 잔액 조회      |
| POST     | /api/payment/pay                     | 특정 예약에 대해 결제 수행       |
| POST     | /api/queue/token                     | 대기열 토큰 발급             |
| GET      | /api/queue/status/{userId}          | 대기열 상태 조회 (순번 및 활성 여부) |
| DELETE   | /api/queue/remove/{userId}          | 대기열에서 토큰 제거           |
| POST     | /api/reservations/reserve           | 좌석 예약 요청              |
| POST     | /api/reservations/{reservationId}/confirm | 결제 완료 후 예약 확정 처리      |
