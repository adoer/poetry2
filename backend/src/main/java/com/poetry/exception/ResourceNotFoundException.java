// 声明当前类所属的包路径
package com.poetry.exception;

/**
 * 资源不存在异常
 * 当请求的数据（新闻、分类等）在数据库中不存在时抛出
 * 由 GlobalExceptionHandler 统一捕获并向前端返回 404 错误码
 */
// 继承 RuntimeException，属于非受检异常，不需要显式 try-catch
public class ResourceNotFoundException extends RuntimeException {

    /**
     * 构造资源不存在异常
     * @param message 异常描述信息，指明哪个资源不存在
     */
    // 构造函数，接收异常消息
    public ResourceNotFoundException(String message) {
        // 调用父类 RuntimeException 的构造方法，设置异常消息
        super(message);
    }
}
