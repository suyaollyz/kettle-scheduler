<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
	<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"></c:set>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<!-- <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/plugins/jquery-easyui-1.5.3/themes/color.css" /> -->
	<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/plugins/jquery-easyui-1.5.3/themes/icon.css" />
	<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/plugins/jquery-easyui-1.5.3/themes/default/easyui.css" />
	<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/icon.css" />
	
	<script type="text/javascript" src="${pageContext.request.contextPath}/plugins/jquery-easyui-1.5.3/jquery.min.js"></script>
	<!-- <script type="text/javascript" src="${pageContext.request.contextPath}/plugins/jquery-easyui-1.5.3/easyloader.js"></script> -->
	<script type="text/javascript" src="${pageContext.request.contextPath}/plugins/jquery-easyui-1.5.3/jquery.easyui.min.js"></script>
	<script type="text/javascript">
		var contextPath="${contextPath}";
		Date.prototype.format = function (fmt) {
		    var o = {
		        "M+": this.getMonth() + 1, //月份 
		        "d+": this.getDate(), //日 
		        "H+": this.getHours(), //小时 
		        "m+": this.getMinutes(), //分 
		        "s+": this.getSeconds(), //秒 
		        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
		        "S": this.getMilliseconds() //毫秒 
		    };
		    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
		    for (var k in o)
		    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
		    return fmt;
		};
		//调用： 
		//var time1 = new Date().format("yyyy-MM-dd");
		//var time2 = new Date().format("yyyy-MM-dd HH:mm:ss");  
		/*$.fn.datagrid.methods.deleteAllRows = function(jq){
			return $(jq).datagrid("loadData", {total:0, rows:[]});
		};*/
		/** 将毫秒转化成天时分秒的时间格式 */
		function formatDuring(mss) {
		    var days = parseInt(mss / (1000 * 60 * 60 * 24));
		    var hours = parseInt((mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
		    var minutes = parseInt((mss % (1000 * 60 * 60)) / (1000 * 60));
		    var seconds = (mss % (1000 * 60)) / 1000;
		    var result = "";
		    if(days>0){
		    	result+= days + " 天 ";
		    }
		    if(hours>0){
		    	result+= hours + " 小时 ";
		    }
		    if(minutes>0){
		    	result+= minutes + " 分钟 ";
		    }
	    	result+= seconds + " 秒 ";
		    return result;
		    //return days + " 天 " + hours + " 小时 " + minutes + " 分钟 " + seconds + " 秒 ";
		}
	</script>	