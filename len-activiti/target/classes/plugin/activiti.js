var activiti = {

    /**
     *
     * @param processInstanceId 流程实例ID
     * @param w 宽
     * @param h 高
     */
    img: function (processInstanceId, w, h) {
        if (typeof w==='undefined') {
            w = $(window).width();
        }
        if (typeof h==='undefined') {
            h = $(window).height();
        }
        window.parent.layer.open({
            id: Math.floor(Math.random() * 1000),
            type: 2,
            area: [w + 'px', h + 'px'],
            fix: false,
            maxmin: true,
            shadeClose: false,
            shade: 0.4,
            title: '流程图',
            content: '/act/shinePics/' + processInstanceId
        });
    }

};