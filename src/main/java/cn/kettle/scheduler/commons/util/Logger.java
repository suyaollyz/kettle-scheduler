package cn.kettle.scheduler.commons.util;

import cn.kettle.scheduler.commons.util.Logger;
import cn.kettle.scheduler.commons.util.LoggerBase;

/*******************************************************************************
 * 模式符号 - 用途(附加说明);{可选附加选项}(附加选项说明)
 * c - 日志名称(通常是构造函数的参数);{数字}("a.b.c" 的名称使用 %c{2} 会输出 "b.c")
 * C - 调用者的类名(速度慢,不推荐使用);{数字}(同上)
 * d - 日志时间;{SimpleDateFormat所能使用的格式}
 * F - 调用者的文件名(速度极慢,不推荐使用)
 * l - 调用者的函数名、文件名、行号(速度极其极其慢,不推荐使用)
 * L - 调用者的行号(速度极慢,不推荐使用)
 * m - 日志
 * M - 调用者的函数名(速度极慢,不推荐使用)
 * n - 换行符号
 * p - 日志优先级别(DEBUG, INFO, WARN, ERROR)
 * r - 输出日志所用毫秒数
 * t - 调用者的进程名
 * x - Used to output the NDC (nested diagnostic context) associated with the thread that generated the logging event.
 * X - Used to output the MDC (mapped diagnostic context) associated with the thread that generated the logging event.
 ******************************************************************************/
/***************************************************************************************************************************************************************
 * 模式修饰符 - 对齐 - 最小长度 - 最大长度 - 说明 %20c 右 20 ~ %-20c 左 20 ~ %.30c ~ ~ 30 %20.30c 右 20 30 %-20.30c 左 20 30
 **************************************************************************************************************************************************************/

public class Logger extends LoggerBase {

    public Logger() {
        super(Logger.class);
    }

    public Logger(String cls) {
        super(cls);
    }

    @SuppressWarnings("unchecked")
    public Logger(Class cls) {
        super(cls);
    }


}
