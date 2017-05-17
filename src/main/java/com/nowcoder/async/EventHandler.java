package com.nowcoder.async;

import java.util.List;

/**
 * Created by dell on 2017/5/14.
 * 事件处理器的公共模型
 */
public interface EventHandler {
    //处理事件
    void doHandler(EventModel model);
    //所支持的处理事件的类型
    List<EventType> getSupportEventTypes();
}
