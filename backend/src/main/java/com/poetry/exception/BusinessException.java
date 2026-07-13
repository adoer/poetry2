// 声明当前类所属的包路径
package com.poetry.exception;

/**
 * 业务逻辑异常
 * 当业务规则被违反时抛出，例如：用户名已存在、分类下有关联新闻无法删除等
 * 由 GlobalExceptionHandler 统一捕获并向前端返回 400 错误码
 */
// 继承 RuntimeException，属于非受检异常
public class BusinessException extends RuntimeException {

    /**
     * 构造业务异常
     * @param message 异常描述信息，将直接展示给前端用户
     */
    // 构造函数，接收异常消息
    public BusinessException(String message) {
        // 调用父类 RuntimeException 的构造方法，设置异常消息
        super(message);
    }
}
