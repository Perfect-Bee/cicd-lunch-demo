-- 낙관적 락을 위한 version 컬럼 추가
ALTER TABLE lunch_menu ADD COLUMN version BIGINT DEFAULT 0;
