/*
 * Copyright sipsd All right reserved.
 */
package com.sipsd.flow.socket;


import com.sipsd.cloud.common.core.util.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 高强
 * @title: WebSocketController
 * @projectName dpark-parent
 * @description: TODO
 * @Date 2021/3/25 上午9:08
 */
@RestController
@RequestMapping("wsrest/")
public class WebSocketController
{
    /**
     * 获得在线人数
     *
     * @return
     */
    @ApiOperation(value = "获得在线人数", notes = "获得在线人数")
    @GetMapping(value = "/getOnlineNum")
    public Result getOnlineNum()
    {
        Result<Integer> result = new Result<Integer>();
        Integer onlineNum = WebSocketServer.getOnlineNum();
        result.setResult(onlineNum);
        return result;
    }

    /**
     * 服务端页面
     *
     * @return
     */
    @ApiOperation(value = "获得在线用户列表", notes = "获得在线用户列表")
    @GetMapping(value = "/getOnlineUsers")
    public Result getOnlineUsers()
    {
        Result<List<String>> result = new Result<List<String>>();
        List<String> onlineUsers = WebSocketServer.getOnlineUsers();
        result.setResult(onlineUsers);
        return result;
    }

    /**
     * 个人信息推送
     *
     * @return
     */
    @GetMapping("/sendmsg")
    @ApiOperation(value = "", notes = "个人信息推送")
    public Result sendmsg(String msg, String username)
    {
        //第一个参数 :msg 发送的信息内容
        //第二个参数为用户长连接传的用户人数
        String[] persons = username.split(",");
        WebSocketServer.SendMany(msg, persons);
        return Result.ok("msg send'" + username + "' with msg '" + msg + "'  success！");
    }

    /**
     * 推送给所有在线用户
     *
     * @return
     */
    @ApiOperation(value = "推送给所有在线用户", notes = "推送给所有在线用户")
    @GetMapping("/sendAll")
    public Result sendAll(String msg)
    {
        WebSocketServer.sendAll(msg);
        return Result.ok("msg sendmsg '" + msg + "'  toALL success！");
    }
}