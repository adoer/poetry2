// 声明当前类所属的包路径
package com.news.dto;

/**
 * 统一 API 响应结果封装
 * 所有 Controller 接口均通过此类返回标准格式的数据，包含状态码、提示信息和数据负载
 * @param <T> 数据负载的类型
 */
// 泛型类定义，T 为 data 字段的实际数据类型
public class Result<T> {

    // HTTP 风格的状态码，标识请求处理结果
    private int code;
    // 可读性提示信息，用于前端展示
    private String message;
    // 泛型数据主体，存放实际的业务数据
    private T data;

    /**
     * 私有构造方法，禁止外部直接实例化
     * @param code    状态码
     * @param message 提示信息
     * @param data    数据负载
     */
    // 私有构造，强制通过静态工厂方法创建实例
    private Result(int code, String message, T data) {
        // 设置状态码
        this.code = code;
        // 设置提示信息
        this.message = message;
        // 设置数据负载
        this.data = data;
    }

    /**
     * 返回成功响应（带数据）
     * @param data 要返回的数据对象
     * @param <T>  数据类型
     * @return 状态码 200 的成功结果
     */
    // 静态工厂方法：创建一个带数据的成功响应
    public static <T> Result<T> success(T data) {
        // code=200, message="成功", data=传入的数据
        return new Result<>(200, "成功", data);
    }

    /**
     * 返回成功响应（无数据），适用于删除等无需返回体的操作
     * @param <T> 数据类型
     * @return 状态码 200 的空成功结果
     */
    // 静态工厂方法：创建一个不带数据的成功响应
    public static <T> Result<T> success() {
        // code=200, message="成功", data=null
        return new Result<>(200, "成功", null);
    }

    /**
     * 返回错误响应（自定义状态码）
     * @param code    错误状态码
     * @param message 错误描述
     * @param <T>     数据类型
     * @return 错误结果
     */
    // 静态工厂方法：创建一个带自定义状态码的错误响应
    public static <T> Result<T> error(int code, String message) {
        // code=自定义, message=错误描述, data=null
        return new Result<>(code, message, null);
    }

    /**
     * 返回错误响应（默认 500 状态码）
     * @param message 错误描述
     * @param <T>     数据类型
     * @return 状态码 500 的错误结果
     */
    // 静态工厂方法：创建一个默认 500 状态码的错误响应
    public static <T> Result<T> error(String message) {
        // code=500（服务器内部错误）, message=错误描述, data=null
        return new Result<>(500, message, null);
    }

    // 获取状态码
    public int getCode() { return code; }
    // 设置状态码
    public void setCode(int code) { this.code = code; }

    // 获取提示信息
    public String getMessage() { return message; }
    // 设置提示信息
    public void setMessage(String message) { this.message = message; }

    // 获取数据负载
    public T getData() { return data; }
    // 设置数据负载
    public void setData(T data) { this.data = data; }
}
