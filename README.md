# Spring Security + Vue 後端部分
## 說明
* 使用Spring Security + MySQL實現登入驗證，配合JWT驗證機制。

## application-secrets.properties內容
```properties
# 資料庫
DB_USERNAME=使用者名稱
DB_PASSWORD=密碼
# OAuth GitHub 至Settings -> Developer Settings -> OAuth Apps新增
GITHUB_CLIENT_ID=GitHub提供的Client ID
GITHUB_CLIENT_SECRET=GitHub提供的Client Secrets

# jwt
jwt.secret-key=JWT密鑰
jwt.expiration-ms=JWT過期時間
```
### JWT Token產生方式
* 可以用Git Bash或命令提示字元(需安裝openssl)輸入以下指令產生
  ```
  openssl rand -base64 32
  ```
## MySQL建立資料測試
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    enabled BOOLEAN NOT NULL
);

CREATE TABLE authorities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    username VARCHAR(50) NOT NULL,
    authority VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

INSERT INTO users (username, password, enabled) VALUES
('admin_user', '可在CustomAuthenticationProvider的main方法內產生假資料密碼', true), -- 密碼為 "admin123"
('regular_user', '可在CustomAuthenticationProvider的main方法內產生假資料密碼', true); -- 密碼為 "user123"

INSERT INTO authorities (user_id, username, authority) VALUES
(1, 'admin_user','ROLE_ADMIN'), -- admin_user
(2, 'regular_user', 'ROLE_USER'); -- regular_user
```
