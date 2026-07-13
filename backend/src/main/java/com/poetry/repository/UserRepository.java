// 声明当前类所属的包路径
package com.poetry.repository;

// 导入用户实体类
import com.poetry.entity.User;
// 导入 Spring Data JPA 的 JpaRepository 接口，提供基础 CRUD 功能
import org.springframework.data.jpa.repository.JpaRepository;

// 导入 Optional 容器类，避免空指针异常
import java.util.Optional;

/**
 * 用户数据访问层
 * 继承 JpaRepository 提供对 users 表的基础 CRUD 操作
 */
// 继承 JpaRepository<User, Long>：实体类型为 User，主键类型为 Long
// Spring Data JPA 会自动实现该接口，无需编写实现类
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * 根据用户名查询用户信息
     * Spring Data JPA 会根据方法名自动生成查询语句：WHERE username = ?
     * @param username 用户名
     * @return 包含用户信息的 Optional，未找到时返回 Optional.empty()
     */
    // 根据用户名精确查询用户，返回 Optional 避免返回 null
    Optional<User> findByUsername(String username);
}
