-- 점심 메뉴 테이블 생성
CREATE TABLE lunch_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    category VARCHAR(20) NOT NULL,
    spicy_level INT NOT NULL DEFAULT 0,
    weight INT NOT NULL DEFAULT 1,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT chk_spicy_level CHECK (spicy_level >= 0 AND spicy_level <= 3),
    CONSTRAINT chk_weight CHECK (weight >= 1 AND weight <= 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 샘플 데이터 삽입
INSERT INTO lunch_menu (name, description, category, spicy_level, weight, created_at, updated_at) VALUES
('김치찌개', '돼지고기와 김치가 어우러진 얼큰한 찌개', 'KOREAN', 2, 3, NOW(), NOW()),
('된장찌개', '구수한 된장과 두부가 들어간 전통 찌개', 'KOREAN', 1, 2, NOW(), NOW()),
('제육볶음', '매콤달콤한 돼지고기 볶음', 'KOREAN', 2, 3, NOW(), NOW()),
('짜장면', '춘장 소스에 면과 야채를 볶아낸 중화요리', 'CHINESE', 0, 4, NOW(), NOW()),
('짬뽕', '해산물과 야채가 들어간 얼큰한 국물 면요리', 'CHINESE', 2, 3, NOW(), NOW()),
('탕수육', '바삭한 튀김에 새콤달콤한 소스를 곁들인 요리', 'CHINESE', 0, 2, NOW(), NOW()),
('초밥', '신선한 생선과 밥의 조화', 'JAPANESE', 0, 2, NOW(), NOW()),
('라멘', '진한 육수와 쫄깃한 면의 일본 국수', 'JAPANESE', 1, 3, NOW(), NOW()),
('돈카츠', '바삭하게 튀긴 돼지고기 커틀릿', 'JAPANESE', 0, 3, NOW(), NOW()),
('파스타', '알덴테로 삶은 면과 다양한 소스의 이탈리안 요리', 'WESTERN', 0, 2, NOW(), NOW()),
('스테이크', '육즙 가득한 프리미엄 소고기 구이', 'WESTERN', 0, 1, NOW(), NOW()),
('햄버거', '패티와 야채, 소스가 어우러진 버거', 'FASTFOOD', 0, 4, NOW(), NOW()),
('치킨', '바삭하게 튀긴 닭고기', 'FASTFOOD', 1, 5, NOW(), NOW()),
('피자', '토마토 소스와 치즈, 다양한 토핑의 이탈리안 빵', 'FASTFOOD', 0, 3, NOW(), NOW()),
('비빔밥', '다양한 나물과 고추장을 비벼먹는 한식', 'KOREAN', 1, 3, NOW(), NOW());
