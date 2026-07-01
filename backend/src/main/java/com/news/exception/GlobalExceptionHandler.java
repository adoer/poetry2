// 声明当前类所属的包路径
package com.news.exception;

// 导入统一响应结果封装类，用于返回标准格式的错误信息
import com.news.dto.Result;
// 导入 SLF4J 日志接口
import org.slf4j.Logger;
// 导入 SLF4J 日志工厂类，用于创建 Logger 实例
import org.slf4j.LoggerFactory;
// 导入 Spring HTTP 状态码枚举
import org.springframework.http.HttpStatus;
// 导入请求体不可读异常（JSON 解析失败等）
import org.springframework.http.converter.HttpMessageNotReadableException;
// 导入 Spring Security 权限不足异常
import org.springframework.security.access.AccessDeniedException;
// 导入缺少请求头异常
import org.springframework.web.bind.MissingRequestHeaderException;
// 导入参数校验失败异常（@Valid 触发）
import org.springframework.web.bind.MethodArgumentNotValidException;
// 导入异常处理方法注解
import org.springframework.web.bind.annotation.ExceptionHandler;
// 导入响应状态码注解
import org.springframework.web.bind.annotation.ResponseStatus;
// 导入 REST 控制器增强注解，统一处理控制器抛出的异常
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 使用 @RestControllerAdvice 统一拦截各类异常，将其转换为标准 Result 格式返回给前端
 */
// Spring 注解：声明此类为全局 REST 控制器增强，自动扫描所有 @RestController 并应用其异常处理方法
@RestControllerAdvice
// 全局异常处理类定义
public class GlobalExceptionHandler {

    // 创建 SLF4J 日志记录器，用于记录未预料的异常信息
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理参数校验失败异常（@Valid 触发）
     * 收集所有字段的校验错误信息，拼接后返回
     * @param e 参数校验异常
     * @return 400 错误结果，包含各字段的校验提示
     */
    // 指定此方法处理 MethodArgumentNotValidException 类型的异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    // 指定 HTTP 响应状态码为 400 Bad Request
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    // 处理参数校验异常的方法
    public Result<Void> handleValidation(MethodArgumentNotValidException e) {
        // 从异常中获取绑定结果，提取所有字段错误并转为 Stream
        String message = e.getBindingResult().getFieldErrors().stream()
                // 将每个字段错误映射为 "字段名: 错误消息" 格式的字符串
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                // 用分号拼接所有字段的错误消息
                .reduce((a, b) -> a + "; " + b)
                // 如果没有具体的错误消息，则使用默认提示
                .orElse("参数校验失败");
        // 返回 400 状态码的错误结果，包含拼接后的校验提示信息
        return Result.error(400, message);
    }

    /**
     * 处理请求体格式错误（如 JSON 解析失败）
     * @param e 请求体不可读异常
     * @return 400 错误结果
     */
    // 指定此方法处理 HttpMessageNotReadableException 类型的异常
    @ExceptionHandler(HttpMessageNotReadableException.class)
    // 指定 HTTP 响应状态码为 400 Bad Request
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    // 处理请求体格式错误的方法
    public Result<Void> handleMessageNotReadable(HttpMessageNotReadableException e) {
        // 返回固定的错误提示，不暴露具体解析失败细节
        return Result.error(400, "请求体格式错误");
    }

    /**
     * 处理缺少必要请求头的异常
     * @param e 缺少请求头异常
     * @return 400 错误结果，指明缺失的请求头名称
     */
    // 指定此方法处理 MissingRequestHeaderException 类型的异常
    @ExceptionHandler(MissingRequestHeaderException.class)
    // 指定 HTTP 响应状态码为 400 Bad Request
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    // 处理缺少请求头异常的方法
    public Result<Void> handleMissingHeader(MissingRequestHeaderException e) {
        // 返回错误信息，告知前端缺少的具体请求头名称
        return Result.error(400, "缺少请求头: " + e.getHeaderName());
    }

    /**
     * 处理权限不足异常（Spring Security 抛出）
     * @param e 访问拒绝异常
     * @return 403 错误结果
     */
    // 指定此方法处理 AccessDeniedException 类型的异常
    @ExceptionHandler(AccessDeniedException.class)
    // 指定 HTTP 响应状态码为 403 Forbidden
    @ResponseStatus(HttpStatus.FORBIDDEN)
    // 处理权限不足异常的方法
    public Result<Void> handleAccessDenied(AccessDeniedException e) {
        // 返回固定提示，不暴露具体的权限细节信息
        return Result.error(403, "无权限访问");
    }

    /**
     * 处理资源不存在异常
     * @param e 资源不存在异常
     * @return 404 错误结果，携带异常中的消息
     */
    // 指定此方法处理 ResourceNotFoundException 类型的异常
    @ExceptionHandler(ResourceNotFoundException.class)
    // 指定 HTTP 响应状态码为 404 Not Found
    @ResponseStatus(HttpStatus.NOT_FOUND)
    // 处理资源不存在异常的方法
    public Result<Void> handleNotFound(ResourceNotFoundException e) {
        // 返回异常消息，指明哪个资源不存在
        return Result.error(404, e.getMessage());
    }

    /**
     * 处理业务逻辑异常
     * @param e 业务异常
     * @return 400 错误结果，携带异常中的消息
     */
    // 指定此方法处理 BusinessException 类型的异常
    @ExceptionHandler(BusinessException.class)
    // 指定 HTTP 响应状态码为 400 Bad Request
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    // 处理业务异常的方法
    public Result<Void> handleBusiness(BusinessException e) {
        // 返回异常消息，包含具体的业务规则违反说明
        return Result.error(400, e.getMessage());
    }

    /**
     * 处理未预料的全局异常（兜底处理）
     * 记录错误日志以防遗漏，避免将原始堆栈信息暴露给前端
     * @param e 通用异常
     * @return 500 错误结果，仅返回通用提示
     */
    // 指定此方法处理所有未被上面特定方法捕获的 Exception 类型异常
    @ExceptionHandler(Exception.class)
    // 指定 HTTP 响应状态码为 500 Internal Server Error
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    // 全局兜底异常处理方法
    public Result<Void> handleException(Exception e) {
        // 记录完整的异常堆栈到日志文件，便于排查问题
        log.error("Unexpected error", e);
        // 向前端返回通用错误提示，避免泄露内部信息
        return Result.error("服务器内部错误");
    }
}
