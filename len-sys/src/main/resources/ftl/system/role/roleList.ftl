<#-- Created by IntelliJ IDEA.
 User: zxm
 Date: 2017/12/19
 Time: 18:00
 To change this template use File | Settings | File Templates.
 角色管理-->
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>角色管理</title>
<#include "/system/base/header.ftl">
</head>

<body>
<div class="lenos-search">
  <div class="select">
    角色名：
    <span class="layui-inline">
      <input class="layui-input" height="20px" id="rolename" autocomplete="off">
    </span>
    描述：
    <span class="layui-inline">
      <input class="layui-input" height="20px" id="remark" autocomplete="off">
    </span>
  </div>
  <div class="len-form-item">
    <button type="button" class="layui-btn layui-btn-normal layui-btn layui-btn-sm"  data-type="select">查询</button>
    <button type="button" class="layui-btn layui-btn-normal layui-btn layui-btn-sm" data-type="reload">重置</button>
  </div>
</div>
<div class="layui-col-md12">
  <div class="layui-btn-group">
    <@shiro.hasPermission name="role:add">
    <button class="layui-btn layui-btn-normal layui-btn-sm" data-type="add">
      <i class="layui-icon">&#xe608;</i>新增
    </button>
    </@shiro.hasPermission>
    <#--<@shiro.hasPermission name="role:update">-->
    <button class="layui-btn layui-btn-normal layui-btn-sm" data-type="update">
      <i class="layui-icon">&#xe642;</i>编辑
    </button>
   <#-- </@shiro.hasPermission>-->
    <@shiro.hasPermission name="role:select">
    <button class="layui-btn layui-btn-normal layui-btn-sm" data-type="detail">
      <i class="layui-icon">&#xe605;</i>查看
    </button>
    </@shiro.hasPermission>
  </div>
</div>
<table id="roleList"  width="100%"  lay-filter="user"></table>
<script type="text/html" id="toolBar">
  <@shiro.hasPermission name="role:add">
  <a class="layui-btn layui-btn-primary layui-btn-xs" lay-event="detail">查看</a>
  </@shiro.hasPermission>
<@shiro.hasPermission name="role:update">
  <a class="layui-btn layui-btn-xs  layui-btn-normal" lay-event="edit">编辑</a>
</@shiro.hasPermission>
<@shiro.hasPermission name="role:del">
  <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>
</@shiro.hasPermission>
</script>
<script>



  layui.use('table', function () {
      table = layui.table;
    //方法级渲染
    table.render({
      id: 'roleList',
      elem: '#roleList'
      , url: 'showRoleList'
      ,parseData: function(res){
        return {
          "code": res.code,
          "msg": res.msg,
          "count": res.count,
          "data": res.data
        };
      }
      , cols: [[
        {checkbox: true, fixed: true, width: '5%'}
        , {field: 'roleName', title: '角色名称', width: '20%', sort: true}
        , {field: 'remark', title: '角色描述', width: '20%', sort: true}
        , {field: 'createDate', title: '创建时间', width: '20%',templet: '<div>{{ layui.laytpl.toDateString(d.createDate,"yyyy-MM-dd") }}</div>'}
        , {field: 'remark', title: '操作', width: '20%', toolbar: "#toolBar"}
      ]]
      , page: true
      ,height: 'full-100'
    });

    var $ = layui.$, active = {
      select: function () {
        var rolename = $('#rolename').val();
        var remark = $('#remark').val();
        table.reload('roleList', {
          where: {
            roleName: rolename,
            remark: remark
          }
        });
      },
      reload:function(){
        $('#rolename').val('');
       $('#remark').val('');
        table.reload('roleList', {
          where: {
            roleName: null,
            remark: null
          }
        });
      },
      add: function () {
        add('添加角色', '/role/showAddRole', 700, 450);
      },
      update: function () {
        var checkStatus = table.checkStatus('roleList')
            , data = checkStatus.data;
        if (data.length != 1) {
          layer.msg('请选择一行编辑', {icon: 5});
          return false;
        }
        update('编辑角色', '/role/updateRole?id=' + data[0].id, 700, 450);
      },
      detail: function () {
        var checkStatus = table.checkStatus('roleList')
            , data = checkStatus.data;
        if (data.length != 1) {
          layer.msg('请选择一行查看', {icon: 5});
          return false;
        }
        detail('查看角色信息', '/role/updateRole?id=' + data[0].id, 700, 450);
      }
    };

      document.onkeydown = function (e) {
          var theEvent = window.event || e;
          var code = theEvent.keyCode || theEvent.which;
          if (code === 13) {
              active.select();
          }
      }

    //监听表格复选框选择
    table.on('checkbox(user)', function (obj) {
      //console.log(obj)
    });
    //监听工具条
    table.on('tool(user)', function (obj) {
      var data = obj.data;
      if (obj.event === 'detail') {
        detail('编辑角色', '/role/updateRole?id=' + data.id, 700, 450);
      } else if (obj.event === 'del') {
        layer.confirm('确定删除角色[<label style="color: #00AA91;">' + data.roleName + '</label>]?', function(){
          del(data.id);
        });
      } else if (obj.event === 'edit') {
        update('编辑角色', '/role/updateRole?id=' + data.id, 700, 450);
      }
    });

    $('.len-form-item .layui-btn,.layui-col-md12 .layui-btn').on('click', function () {
      var type = $(this).data('type');
      active[type] ? active[type].call(this) : '';
    });

  });
  function del(id) {
    $.ajax({
      url: "del",
      type: "post",
      data: {id: id},
      success: function (d) {
        if(d.msg){
          layer.msg(d.msg,{icon:6,offset: 'rb',area:['120px','80px'],anim:2});
          layui.table.reload('roleList');
        }else{
          layer.msg(d.msg,{icon:5,offset: 'rb',area:['120px','80px'],anim:2});
        }
      }
    });
  }
  function detail(title, url, w, h) {
    if (title == null || title == '') {
      title = false;
    }
    if (url == null || url == '') {
      url = "/error/404";
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
      url = "/error/404";
    }
    if (w == null || w == '') {
      w = ($(window).width() * 0.9);
    }
    if (h == null || h == '') {
      h = ($(window).height() - 50);
    }
      window.top.layer.open({
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
      url = "/error/404";
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
