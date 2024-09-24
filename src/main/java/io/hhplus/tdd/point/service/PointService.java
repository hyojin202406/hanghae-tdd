package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.controller.PointController;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.TransactionType;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.exception.InvalidUserIdException;
import io.hhplus.tdd.point.exception.PointValidationException;
import io.hhplus.tdd.point.repository.PointRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointService {

    private static final Logger log = LoggerFactory.getLogger(PointService.class);

    private final PointRepository pointRepository;

    public PointService(PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

    /**
     * 유저 포인트 조회
     * @param id
     * @returna
     */
    public UserPoint getUserPoint(long id) {
        // 유효하지 않은 ID 검사
        if (id <= 0) {
            throw new InvalidUserIdException("Invalid user ID: " + id);
        }

        return pointRepository.getUserPoint(id);
    }

    /**
     * 유저 포인트 충전/이용 내역 조회
     * @param id
     * @return
     */
    public List<PointHistory> getUserHistory(long id) {
        // 유효하지 않은 ID 검사
        if (id <= 0) {
            throw new InvalidUserIdException("Invalid user ID: " + id);
        }

        return pointRepository.getUserHistory(id);
    }

    /**
     * 유저 포인트 충전 기능
     * @param id
     * @param amount
     * @return
     */
    public UserPoint chargeUserPoint(long id, long amount) {
        // 유효하지 않은 ID 검사
        if (id <= 0) {
            throw new InvalidUserIdException("Invalid user ID: " + id);
        }

        // 유효하지 않은 포인트 처리
        if (amount < 0) {
            throw new PointValidationException("Point validation exception. amount : " + amount);
        }

        // 유저 포인트 충전
        UserPoint updatedUserPoint = pointRepository.insertOrUpdate(id, amount);
        log.debug("insertOrUpdate가 호출되었습니다.");

        // 유저 포인트 충전 내역 저장
        pointRepository.insertHistory(id,
                amount,
                TransactionType.CHARGE,
                updatedUserPoint.updateMillis());

        // 변경된 유저 정보 전달
        return updatedUserPoint;
    }

}