<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/view/include/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@ include file="/view/include/common/meta.jsp"%>
    <%
        String path = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
        request.setAttribute("nameAttr", basePath);
    %>
</head>
<body class="app-body-container" id="container">
<div style="background-color: #F8F9FB;width: 100%;height: 100%;position: absolute;top: 0;left: 0;z-index: 1;opacity: 0.4;"></div>
<div style="position: absolute;z-index: 1000;left: 23%;top: 20%;color: black;" id="infoContainer">
    <div style="font-size: 4rem;line-height: 5rem;">感谢您使用北京数字政通科技股份有限公司的产品</div>
    <div style="font-size: 2rem;line-height: 4rem;">本产品已过服务期限，如果您想续用，请联系本产品技术支持人员购买授权</div>
    <div style="font-size: 2rem;line-height: 3rem;margin-top: 3rem">版本${resultInfo.data.MIS_VERSION_INFO.split("-")[0]}</div>
</div>

<div style="height: 30%;width: 100%;overflow: hidden;margin: 0px;position: absolute;bottom: 0px;left: 0px;z-index: 1001;">
    <img id="licensemsg" src="" style="width: 100%;height: 100%;">
</div>
</body>
<script type="text/javascript">
    var rootPath = window.location.pathname.substr(0,window.location.pathname.lastIndexOf("/"));
    document.getElementById("licensemsg").src = rootPath+"/style/urban/common/css/images/login/licensefootdeep.png";
    document.getElementById("infoContainer").style.left=(document.getElementById("container").offsetWidth-document.getElementById("infoContainer").offsetWidth)/2+"px";
</script>
</html>