<link rel="stylesheet" href="${re.contextPath}/plugin/layui/css/layui.css">
<link rel="stylesheet" href="${re.contextPath}/plugin/lenos/main.css">
<script type="text/javascript" src="${re.contextPath}/plugin/jquery/jquery-3.2.1.min.js"></script>
<script type="text/javascript" src="${re.contextPath}/plugin/layui/layui.all.js"
        charset="utf-8"></script>
<script type="text/javascript" src="${re.contextPath}/plugin/tools/tool.js"></script>
<script>
    layui.laytpl.toDateString = function(d, format){
        var date = new Date(d || new Date())
                ,ymd = [
            this.digit(date.getFullYear(), 4)
            ,this.digit(date.getMonth() + 1)
            ,this.digit(date.getDate())
        ]
                ,hms = [
            this.digit(date.getHours())
            ,this.digit(date.getMinutes())
            ,this.digit(date.getSeconds())
        ];

        format = format || 'yyyy-MM-dd HH:mm:ss';

        return format.replace(/yyyy/g, ymd[0])
                .replace(/MM/g, ymd[1])
                .replace(/dd/g, ymd[2])
                .replace(/HH/g, hms[0])
                .replace(/mm/g, hms[1])
                .replace(/ss/g, hms[2]);
    };

    //数字前置补零
    layui.laytpl.digit = function(num, length, end){
        var str = '';
        num = String(num);
        length = length || 2;
        for(var i = num.length; i < length; i++){
            str += '0';
        }
        return num < Math.pow(10, length) ? str + (num|0) : num;
    };
</script>