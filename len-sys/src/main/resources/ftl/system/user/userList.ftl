<#-- Created by IntelliJ IDEA.
 User: Administrator
 Date: 2017/12/6
 Time: 14:00
 To change this template use File | Settings | File Templates.
 用户管理-->
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>用户管理</title>
<#include "/system/base/header.ftl">
</head>

<body>
<div class="lenos-search">
    <div class="select">
        <span>用户名：</span>
        <span class="layui-inline">
      <input class="layui-input" height="20px" id="uname" autocomplete="off">
    </span>
        <span>邮箱：</span>
        <span class="layui-inline">
      <input class="layui-input" height="20px" id="email" autocomplete="off">
    </span>
    </div>
    <div class="len-form-item">
        <button type="button" class="layui-btn layui-btn-normal layui-btn layui-btn-sm" data-type="select">查询</button>
        <button type="button" class="layui-btn layui-btn-normal layui-btn layui-btn-sm" data-type="reload">重置</button>
    </div>
</div>
<div class="layui-col-md12 len-button">
    <div class="layui-btn-group">
<@shiro.hasPermission name="user:select">
    <button class="layui-btn layui-btn-normal  layui-btn-sm" data-type="add">
        <i class="layui-icon">&#xe608;</i>新增
    </button>
</@shiro.hasPermission>
<@shiro.hasPermission name="user:select">
    <button class="layui-btn layui-btn-normal layui-btn-sm" data-type="update">
        <i class="layui-icon">&#xe642;</i>编辑
    </button>
</@shiro.hasPermission>
<@shiro.hasPermission name="user:del">
    <button class="layui-btn layui-btn-normal layui-btn-sm" data-type="detail">
        <i class="layui-icon">&#xe605;</i>查看
    </button>
</@shiro.hasPermission>
<@shiro.hasPermission name="user:repass">
    <button class="layui-btn layui-btn-normal layui-btn-sm" data-type="changePwd">
        <i class="layui-icon">&#xe605;</i>修改密码
    </button>
</@shiro.hasPermission>
    </div>
</div>
<table id="userList" width="100%" lay-filter="user"></table>
<script type="text/html" id="bar">
<@shiro.hasPermission name="user:select">
<a class="layui-btn layui-btn-primary layui-btn-xs" lay-event="detail">查看</a>
</@shiro.hasPermission>
<@shiro.hasPermission name="user:update">
<a class="layui-btn layui-btn-xs  layui-btn-normal" lay-event="edit">编辑</a>
</@shiro.hasPermission>
<@shiro.hasPermission name="user:del">
<a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>
</@shiro.hasPermission>
</script>
<script type="text/html" id="switchTpl">
    <input type="checkbox" name="sex" lay-skin="switch" lay-text="女|男" lay-filter="sexDemo">
</script>
<script>
    document.onkeydown = function (e) { // 回车提交表单
        var theEvent = window.event || e;
        var code = theEvent.keyCode || theEvent.which;
        if (code == 13) {
            $(".select .select-on").click();
        }
    }
    layui.use('table', function () {
        table= layui.table;
        //方法级渲染
        table.render({
            id: 'userList',
            elem: '#userList'
            , url: 'showUserList'
            , parseData: function (res) {
                return {
                    "code": res.code,
                    "msg": res.msg,
                    "count": res.count,
                    "data": res.data
                };
            }
            , cols: [[
                {checkbox: true, fixed: true, width: '5%'}
                , {
                    field: 'username',
                    title: '用户名',
                    width: '10%',
                    sort: true,
                }
                , {field: 'age', title: '年龄', width: '17%', sort: true}
                , {field: 'realName', title: '真实姓名', width: '20%'}
                , {field: 'email', title: '邮箱', width: '13%'}
                , {field: 'photo', title: '头像', width: '13%', template: '#switchTpl'}
                , {field: 'right', title: '操作', width: '20%', toolbar: "#bar"}
            ]]
            , page: true,
            height: 'full-100'
        });

        var $ = layui.$, active = {
            select: function () {
                var uname = $('#uname').val();
                var email = $('#email').val();
                table.reload('userList', {
                    where: {
                        username: uname,
                        email: email
                    }
                });
            },
            reload: function () {
                $('#uname').val('');
                $('#email').val('');
                table.reload('userList', {
                    where: {
                        username: null,
                        email: null
                    }
                });
            },
            add: function () {
                add('添加用户', '/user/showAddUser', 700, 450);
            },
            update: function () {
                var checkStatus = table.checkStatus('userList')
                        , data = checkStatus.data;
                if (data.length !== 1) {
                    layer.msg('请选择一行编辑,已选[' + data.length + ']行', {icon: 5});
                    return false;
                }
                update('编辑用户', '/user/updateUser?id=' + data[0].id, 700, 450);
            },
            detail: function () {
                var checkStatus = table.checkStatus('userList')
                        , data = checkStatus.data;
                if (data.length !== 1) {
                    layer.msg('请选择一行查看,已选[' + data.length + ']行', {icon: 5});
                    return false;
                }
                detail('查看用户信息', '/user/updateUser?id=' + data[0].id, 700, 450);
            },
            changePwd: function () {
                var checkStatus = table.checkStatus('userList')
                        , data = checkStatus.data;
                if (data.length !== 1) {
                    layer.msg('请选择一个用户,已选[' + data.length + ']行', {icon: 5});
                    return false;
                }
                rePwd('修改密码', '/user/goRePass?id=' + data[0].id, 500, 350);
            }
        };

        //监听表格复选框选择
        table.on('checkbox(user)', function (obj) {
            console.log(obj)
        });
        //监听工具条
        table.on('tool(user)', function (obj) {
            var data = obj.data;
            if (obj.event === 'detail') {
                detail('编辑用户', '/user/updateUser?id=' + data.id, 700, 450);
            } else if (obj.event === 'del') {
                layer.confirm('确定删除用户[<label style="color: #00AA91;">' + data.username + '</label>]?', {
                    btn: ['逻辑删除', '物理删除']
                }, function () {
                    toolDelByFlag(data.id, 'userList', true);
                }, function () {
                    toolDelByFlag(data.id, 'userList', false);
                });
            } else if (obj.event === 'edit') {
                update('编辑用户', '/user/updateUser?id=' + data.id, 700, 450);
            }
        });

        $('.len-form-item .layui-btn,.layui-col-md12 .layui-btn').on('click', function () {
            var type = $(this).data('type');
            active[type] ? active[type].call(this) : '';
        });

    });

    function rePwd(title, url, w, h) {
        if (title == null || title === '') {
            title = false;
        }
        if (url == null || url === '') {
            url = "404.html";
        }
        if (w == null || w === '') {
            w = ($(window).width() * 0.9);
        }
        if (h == null || h === '') {
            h = ($(window).height() - 50);
        }
        window.top.layer.open({
            id: 'user-rePwd',
            type: 2,
            area: [w + 'px', h + 'px'],
            fix: false,
            maxmin: true,
            shadeClose: true,
            shade: 0.4,
            title: title,
            content: url
        });
    }

    function detail(title, url, w, h) {
        if (title == null || title === '') {
            title = false;
        }
        if (url == null || url == '') {
            url = "error/404";
        }
        if (w == null || w == '') {
            w = ($(window).width() * 0.9);
        }
        if (h == null || h == '') {
            h = ($(window).height() - 50);
        }
        window.top.layer.open({
            id: 'user-detail',
            type: 2,
            area: [w + 'px', h + 'px'],
            fix: false,
            maxmin: true,
            shadeClose: true,
            shade: 0.4,
            title: title,
            content: url + '&detail=true',
        });
    }

    /**
     * 更新用户
     */
    function update(title, url, w, h) {
        if (title == null || title == '') {
            title = false;
        }
        if (url == null || url == '') {
            url = "404.html";
        }
        if (w == null || w == '') {
            w = ($(window).width() * 0.9);
        }
        if (h == null || h == '') {
            h = ($(window).height() - 50);
        }

        window.parent.layer.open({
            id: 'user-update',
            type: 2,
            area: [w + 'px', h + 'px'],
            fix: false,
            maxmin: true,
            shadeClose: false,
            shade: 0.4,
            title: title,
            content: url + '&detail=false'
        });
    }

    /*弹出层*/
    /*
     参数解释：
     title   标题
     url     请求的url
     id      需要操作的数据id
     w       弹出层宽度（缺省调默认值）
     h       弹出层高度（缺省调默认值）
     */
    function add(title, url, w, h) {
        if (title == null || title == '') {
            title = false;
        }
        if (url == null || url == '') {
            url = "404.html";
        }
        if (w == null || w == '') {
            w = ($(window).width() * 0.9);
        }
        if (h == null || h == '') {
            h = ($(window).height() - 50);
        }
        window.top.layer.open({
            id: 'user-add',
            type: 2,
            area: [w + 'px', h + 'px'],
            fix: false,
            maxmin: true,
            shadeClose: false,
            shade: 0.4,
            title: title,
            content: url
        });
    }
</script>
</body>
</html>
