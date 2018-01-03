var scheduledStateMap={
		"NONE":"--",
		"NORMAL":"正常",
		"PAUSED":"停止的",
		"COMPLETE":"正常完成",
		"ERROR":"异常完成",
		"BLOCKED":"被阻塞的"
};
var runStatusMap={
		0:"正常结束",
    	1:"运行中",
    	2:"异常结束"
};
$(function() {
	$('#resultList').datagrid({
	    url: contextPath+"/kettle/listjobs.do",
//	    pagination:true,
	    fit:true,
//	    singleSelect:true,
	    idField:"jobId",
	    rownumbers:true,
	    columns:[[
	        {field:'jobId',title:'任务ID',width:200, checkbox:true},
	        {field:'name',title:'任务名称',width:150},
	        {field:'directory',title:'路径',width:300},
	        {field:'description',title:'任务描述',width:200},
	        {field:'state',title:'任务状态',width:100,formatter:function(value,row,index){
	        	if(value==0){
	        		return "停用";
	        	}else if(value==1){
	        		return "启用";
	        	}else{
	        		return value;
	        	}
	        }},
	        {field:'exp',title:'执行表达式',width:150},
	        {field:'scheduledState',title:'当前调度状态',width:100,formatter:function(value,row,index){
	        	if(scheduledStateMap[value]){
	        		return scheduledStateMap[value];
	        	};
	    		return value;
	        }},
	        {field:'runStatus',title:'任务运行状态',width:100,formatter:function(value,row,index){
	        	var runStatusDesc=runStatusMap[value];
	        	if(!runStatusDesc){
	        		runStatusDesc=value;
	        	}
	        	if(value==0){
	        		return "<font color='green'>"+runStatusDesc+"</font>";
	        	}else if(value==1){
	        		return "<font color='green'>"+runStatusDesc+"</font>";
	        	}else if(value==2){
	        		return "<font color='red'>"+runStatusDesc+"</font>";
	        	}
	        	return value;
	        }},
	        {field:'createdUser',title:'创建人',width:100},
	        {field:'createdTime',title:'创建时间',width:100,formatter:function(value,row,index){
	        	return(new Date(value)).format("yyyy-MM-dd HH:mm:ss");
	        }},
	        {field:'modifiedUser',title:'修改人',width:100},
	        {field:'modifiedTime',title:'修改时间',width:100,formatter:function(value,row,index){
	        	if(value){
	        		return(new Date(value)).format("yyyy-MM-dd HH:mm:ss");
	        	}
	        }},
	        {field:'lastUpdate',title:'最后运行更新时间',width:100,formatter:function(value,row,index){
	        	if(value){
	        		return(new Date(value)).format("yyyy-MM-dd HH:mm:ss");
	        	}
	        }},
	        {field:'extendedDescription',title:'任务扩展描述',width:500}
	    ]],
	    toolbar: [{
			iconCls: 'icon-add',
			text:"新增",
			handler: function(){
				$('#addWindow').window("open");
				$('#repoTree').tree({
				    url:contextPath+"/kettle/listrepoobject.do",
				    lines:"true",
				    onSelect : function(node){
				    	if(node["attributes"]["nodeType"]==1||node["attributes"]["nodeType"]==2){
				    		$("#jobAddForm").form("reset");
				    		var id=node["id"];
				    		var directory="/";
				    		if(id.lastIndexOf("/")>0){
				    			directory=id.substring(0, id.lastIndexOf("/"));
				    		}
				    		$("#a_directory").val(directory);
				    		$("#a_directoryLabel").text(directory);
				    		$("#a_name").val(node["text"]);
				    		$("#a_nameLabel").text(node["text"]);
				    		$("#paramList").datagrid("loadData", node["attributes"]["params"]);
				    	}
				    }
				});
			}
		},'-',{
			iconCls: 'icon-edit',
			text:"编辑",
			handler: function(){
				var selectRecords=$("#resultList").datagrid("getSelections");
				if(selectRecords.length==0){
					$.messager.alert("错误","请选择一条任务记录");
					return false;
				}
				if(selectRecords.length>1){
					$.messager.alert("错误","只能选择一条任务记录进行编辑");
					return false;
				}
				var jobId=selectRecords[0]["jobId"];
				$('#editWindow').window("open");
				$("#jobEditForm").form("load",contextPath+"/kettle/findjob.do?jobId="+jobId);
			}
		},'-',{
			iconCls: 'icon-remove',
			text:"删除",
			handler: function(){
				var selectRecords=$("#resultList").datagrid("getSelections");
				if(selectRecords.length==0){
					$.messager.alert("错误","请选择要删除的任务记录");
					return false;
				}
				var jobIds=[];
				$.each(selectRecords, function(index, item){
					jobIds.push(item["jobId"]);
				});
				$.messager.confirm({
					title:"删除确认",
					msg:"确认要删除选中的任务吗？",
					ok : "确认",
					cancel : "取消",
					fn:function(confirm){
					    if (confirm){
							$.ajax({
								url : contextPath+"/kettle/removejob.do",
								type : "POST",
								data : {"jobIds":jobIds},
								dataType : "json",
								error : function(XMLHttpRequest, textStatus, errorThrown){
									$.messager.alert("错误","发送删除请求异常");
								},
								success : function(data, textStatus, jqXHR){
									$.messager.alert("错误",data["resultDesc"]);
								},
								complete : function(XMLHttpRequest, textStatus){
									$("#resultList").datagrid("reload");
								}
							});
					    }
					}
				});
			}
		},'-',{
			iconCls: 'icon-edit',
			text:"编辑参数",
			handler: function(){
				var selectRecords=$("#resultList").datagrid("getSelections");
				if(selectRecords.length==0){
					$.messager.alert("错误","请选择一条任务记录");
					return false;
				}
				if(selectRecords.length>1){
					$.messager.alert("错误","只能选择一条任务记录进行编辑参数");
					return false;
				}
				var jobId=selectRecords[0]["jobId"];
				$('#paramsEditWindow').window("open");
				$("#paramEidtList").datagrid({
					url : contextPath+"/kettle/findparams.do",
					queryParams : {"jobId": jobId},
					onLoadSuccess : function(data){
						$("#paramEditJobId").val(jobId);
						for(var index=0; index<data.total; index++){
							$(this).datagrid('beginEdit', index);
						}
					}
				});
			}
		},'-',{
			iconCls: 'icon-quartz',
			text:"重新调度",
			handler: function(){
				var selectRecords=$("#resultList").datagrid("getSelections");
				if(selectRecords.length==0){
					$.messager.alert("错误","请选择要删除的任务记录");
					return false;
				}
				var jobIds=[];
				$.each(selectRecords, function(index, item){
					jobIds.push(item["jobId"]);
				});
				$.messager.confirm({
					title:"重新调度确认",
					msg:"确认要重新调度选定的任务吗？",
					ok : "确认",
					cancel : "取消",
					fn:function(confirm){
					    if (confirm){
							$.ajax({
								url : contextPath+"/kettle/reloadjob.do",
								type : "POST",
								data : {"jobIds":jobIds},
								dataType : "json",
								error : function(XMLHttpRequest, textStatus, errorThrown){
									$.messager.alert("错误","发送请求异常");
								},
								success : function(data, textStatus, jqXHR){
									$.messager.alert("错误",data["resultDesc"]);
								},
								complete : function(XMLHttpRequest, textStatus){
									$('#resultList').datagrid("reload");
								}
							});
					    }
					}
				});
			}
		},'-',{
			iconCls: 'icon-pause',
			text:"暂停",
			handler: function(){
				var selectRecords=$("#resultList").datagrid("getSelections");
				if(selectRecords.length==0){
					$.messager.alert("错误","请选择要删除的任务记录");
					return false;
				}
				var jobIds=[];
				$.each(selectRecords, function(index, item){
					jobIds.push(item["jobId"]);
				});
				$.messager.confirm({
					title:"暂停调度确认",
					msg:"确认要暂停调度选定的任务吗？",
					ok : "确认",
					cancel : "取消",
					fn:function(confirm){
					    if (confirm){
							$.ajax({
								url : contextPath+"/kettle/pausejob.do",
								type : "POST",
								data : {"jobIds":jobIds},
								dataType : "json",
								error : function(XMLHttpRequest, textStatus, errorThrown){
									$.messager.alert("错误","发送请求异常");
								},
								success : function(data, textStatus, jqXHR){
									$.messager.alert("错误",data["resultDesc"]);
								},
								complete : function(XMLHttpRequest, textStatus){
									$('#resultList').datagrid("reload");
								}
							});
					    }
					}
				});
			}
		},'-',{
			iconCls: 'icon-immediately',
			text:"立即停止",
			handler: function(){
				var selectRecords=$("#resultList").datagrid("getSelections");
				if(selectRecords.length==0){
					$.messager.alert("错误","请选择要删除的任务记录");
					return false;
				}
				var jobIds=[];
				$.each(selectRecords, function(index, item){
					jobIds.push(item["jobId"]);
				});
				$.messager.confirm({
					title:"立即停止任务确认",
					msg:"确认要立即停止选定的任务吗？",
					ok : "确认",
					cancel : "取消",
					fn:function(confirm){
					    if (confirm){
							$.ajax({
								url : contextPath+"/kettle/pausejobimmediately.do",
								type : "POST",
								data : {"jobIds":jobIds},
								dataType : "json",
								error : function(XMLHttpRequest, textStatus, errorThrown){
									$.messager.alert("错误","发送请求异常");
								},
								success : function(data, textStatus, jqXHR){
									$.messager.alert("错误",data["resultDesc"]);
								},
								complete : function(XMLHttpRequest, textStatus){
									$('#resultList').datagrid("reload");
								}
							});
					    }
					}
				});
			}
		},'-',{
			iconCls: 'icon-start',
			text:"恢复",
			handler: function(){
				var selectRecords=$("#resultList").datagrid("getSelections");
				if(selectRecords.length==0){
					$.messager.alert("错误","请选择要删除的任务记录");
					return false;
				}
				var jobIds=[];
				$.each(selectRecords, function(index, item){
					jobIds.push(item["jobId"]);
				});
				$.messager.confirm({
					title:"恢复调度确认",
					msg:"确认要恢复调度选定的任务吗？",
					ok : "确认",
					cancel : "取消",
					fn:function(confirm){
					    if (confirm){
							$.ajax({
								url : contextPath+"/kettle/resumejob.do",
								type : "POST",
								data : {"jobIds":jobIds},
								dataType : "json",
								error : function(XMLHttpRequest, textStatus, errorThrown){
									$.messager.alert("错误","发送请求异常");
								},
								success : function(data, textStatus, jqXHR){
									$.messager.alert("错误",data["resultDesc"]);
								},
								complete : function(XMLHttpRequest, textStatus){
									$('#resultList').datagrid("reload");
								}
							});
					    }
					}
				});
			}
		},'-',{
			iconCls: 'icon-list',
			text:"运行记录",
			handler: function(){
				var selectRecords=$("#resultList").datagrid("getSelections");
				if(selectRecords.length==0){
					$.messager.alert("错误","请选择一条任务记录");
					return false;
				}
				if(selectRecords.length>1){
					$.messager.alert("错误","只能选择一条任务记录进行查看");
					return false;
				}
				var jobId=selectRecords[0]["jobId"];
				$("#runLogFromTime").datebox("setValue", new Date().format("yyyy-MM-dd"));
		    	$("#runLogToTime").datebox("setValue", new Date().format("yyyy-MM-dd"));
				$("#runLogJobId").val(jobId);
				$('#runLogWindow').window("open");
				var fromTime= $("#runLogFromTime").datebox("getValue");
				var toTime = $("#runLogToTime").datebox("getValue");
				$("#runLogList").datagrid({
					url : contextPath+"/kettle/findrunlogs.do",
					pageNumber : 1,
					pageSize : 10,
					queryParams : {"jobId": jobId, "fromTime":fromTime, "toTime":toTime},
					onLoadSuccess : function(data){
					}
				});
			}
		},'-',{
			iconCls: 'icon-edit',
			text:"修改运行状态",
			handler: function(){
				var selectRecords=$("#resultList").datagrid("getSelections");
				if(selectRecords.length==0){
					$.messager.alert("错误","请选择一条任务记录");
					return false;
				}
				if(selectRecords.length>1){
					$.messager.alert("错误","只能选择一条任务记录进行修改");
					return false;
				}
				var jobId=selectRecords[0]["jobId"];
				var runStatus=selectRecords[0]["runStatus"];
				if(runStatus!=1){
					$.messager.alert("错误","只能修改运行状态为\""+runStatusMap[1]+"\"的任务");
					return;
				}
				$.messager.confirm({
					title:"修改运行状态确认",
					msg:"修改运行状态有风险！<br/>确认要修改选定任务的运行状态吗？",
					ok : "确认",
					cancel : "取消",
					fn:function(confirm){
					    if (confirm){
					    	$("#runStatusEditJobId").val(jobId);
					    	$('#runStatusEditWindow').window("open");
					    }
					}
				});
			}
		},'-',{
			iconCls: 'icon-reload',
			text:"刷新",
			handler: function(){
				$('#resultList').datagrid("reload");
			}
		}]
	});
	
	//==========新增
	$("#jobAddForm").form({
		url:contextPath+"/kettle/addjob.do",
		onSubmit : function(param){ // 提交前触发，返回 false 来阻止提交动作。
			var rows = $("#paramList").datagrid('getRows');
            for ( var i = 0; i < rows.length; i++) {
            	$("#paramList").datagrid('endEdit', i);
            }
			var data = $("#paramList").datagrid("getData");
			$.each(data.rows, function(index, elem){
				param["paramConfigs["+index+"].paramCode"]=elem["paramCode"];
				param["paramConfigs["+index+"].paramValue"]=elem["paramValue"];
				param["paramConfigs["+index+"].description"]=elem["description"];
				param["paramConfigs["+index+"].extendedDescription"]=elem["extendedDescription"];
				param["paramConfigs["+index+"].state"]=elem["state"];
			});
			for ( var i = 0; i < rows.length; i++) {
            	$("#paramList").datagrid('beginEdit', i);
            }
		},
		success : function(data){ // 当表单提交成功时触发。
			$.messager.alert("错误",eval('(' + data + ')')["resultDesc"]);
			$("#addWindow").window("close");
			$("#resultList").datagrid("reload");
		}
	});
	$("#jobAddFormBtn").linkbutton({
		iconCls : "icon-save"
	});
	$('#jobAddFormBtn').bind('click', function(){
		$("#jobAddForm").submit();
    });
	$("#a_expEditBtn").linkbutton({
		iconCls : "icon-edit"
	});
	$('#a_expEditBtn').bind('click', function(){
		$("#expEditWindow").window({
			title:"Cron表达式编辑器",
		    width:600,
		    height:400,
		    modal:true,
		    href : contextPath+"/plugins/cron/cron.jsp",
		    onLoad : function(){
		    	initCron();
		    	$("#cron").val($("#a_exp").val());
		    	btnFan();
		    },
		    onClose : function(){
		    	$("#a_exp").val($("#cron").val());
		    }
		});
    });
	$("#paramList").datagrid({
		striped : "true",
		fit : "true",
		singleSelect:true,
		columns:[[
		    {field:'paramCode',title:'参数名',width:150, editor:"text"},
		    {field:'state',title:'状态',width:60, editor:{
		    	type:"combobox", options:{
		    		valueField: 'id',
		    	    textField: 'text',
		    	    data : [
		    	        {"id":"1","text":"启用","selected":true},
		    	        {"id":"0","text":"禁用"}
		    	    ]
		    	}
		    },formatter:function(value,row,index){
		    	return value==1?"启用 ":value==0?"禁用":"异常状态";
		    }},
		    {field:'paramValue',title:'默认值',width:200, editor:"text"},
		    {field:'description',title:'描述',width:200, editor:"text"}
		]],
		onLoadSuccess : function(data){
			for(var index=0; index<data.total; index++){
				$(this).datagrid('beginEdit', index);
			}
		}
	});
	
	//===========编辑
	$("#jobEditForm").form({
		url:contextPath+"/kettle/editjob.do",
		onSubmit : function(param){ // 提交前触发，返回 false 来阻止提交动作。
			
		},
		success : function(data){ // 当表单提交成功时触发。
			$.messager.alert("错误","success"+data);
			$("#editWindow").window("close");
			$("#resultList").datagrid("reload");
		},
		onLoadSuccess : function(data){
    		$("#e_directoryLabel").text(data["directory"]);
    		$("#e_nameLabel").text(data["name"]);
		},
		onLoadError : function(){
			
		}
	});
	$("#jobEditFormBtn").linkbutton({
		iconCls : "icon-save"
	});
	$('#jobEditFormBtn').bind('click', function(){
		$("#jobEditForm").submit();
    });
	$("#e_expEditBtn").linkbutton({
		iconCls : "icon-edit"
	});
	$('#e_expEditBtn').bind('click', function(){
		$("#expEditWindow").window({
			title:"Cron表达式编辑器",
		    width:600,
		    height:400,
		    modal:true,
		    href : contextPath+"/plugins/cron/cron.jsp",
		    onLoad : function(){
		    	initCron();
		    	$("#cron").val($("#e_exp").val());
		    	btnFan();
		    },
		    onClose : function(){
		    	$("#e_exp").val($("#cron").val());
		    }
		});
    });
	
	//===========编辑默认参数
	$("#paramEditForm").form({
		url:contextPath+"/kettle/editparams.do",
		onSubmit : function(param){ // 提交前触发，返回 false 来阻止提交动作。
			var rows = $("#paramEidtList").datagrid('getRows');
            for ( var i = 0; i < rows.length; i++) {
            	$("#paramEidtList").datagrid('endEdit', i);
            }
			var data = $("#paramEidtList").datagrid("getData");
			$.each(data.rows, function(index, elem){
				param["paramConfigs["+index+"].paramId"]=elem["paramId"];
				param["paramConfigs["+index+"].jobId"]=elem["jobId"];
				param["paramConfigs["+index+"].paramCode"]=elem["paramCode"];
				param["paramConfigs["+index+"].paramValue"]=elem["paramValue"];
				param["paramConfigs["+index+"].description"]=elem["description"];
				param["paramConfigs["+index+"].extendedDescription"]=elem["extendedDescription"];
				param["paramConfigs["+index+"].state"]=elem["state"];
			});
			for ( var i = 0; i < rows.length; i++) {
            	$("#paramEidtList").datagrid('beginEdit', i);
            }
		},
		success : function(data){ // 当表单提交成功时触发。
			$.messager.alert("错误",eval('(' + data + ')')["resultDesc"]);
			$("#paramsEditWindow").window("close");
			//$("#resultList").datagrid("reload");
		}
	});
	$("#paramEidtSaveBtn").linkbutton({
		iconCls : "icon-save"
	});
	$('#paramEidtSaveBtn').bind('click', function(){
		$("#paramEditForm").submit();
    });
	$("#paramEidtList").datagrid({
		fit : "true",
		singleSelect:true,
		columns:[[
		    {field:'paramId',title:'参数ID',width:150},
		    {field:'paramCode',title:'参数名',width:150, editor:"text"},
		    {field:'state',title:'状态',width:60, editor:{
		    	type:"combobox", options:{
		    		valueField: 'id',
		    	    textField: 'text',
		    	    data : [
		    	        {"id":"1","text":"启用","selected":true},
		    	        {"id":"0","text":"禁用"}
		    	    ]
		    	}
		    },formatter:function(value,row,index){
		    	return value==1?"启用 ":value==0?"禁用":"异常状态";
		    }},
		    {field:'paramValue',title:'默认值',width:200, editor:"text"},
		    {field:'description',title:'描述',width:200, editor:"text"},
		    {field:'_operation',title:'操作', formatter:function(value,row,index){
		    	var paramId=row["paramId"]?row["paramId"]:"";
		    	return '<a id="btn" href="#" class="easyui-linkbutton" data-options="iconCls: \'icon-remove\'" onclick="deleteParam(\''+paramId+'\','+index+')">删除</a>';
		    }}
		]]
	});
	
	//===========运行日志
	$("#runLogList").datagrid({
		fit : "true",
		pagination:true,
		singleSelect:true,
		columns:[[
		    {field:'logId',title:'日志ID',width:150, hidden:true},
		    {field:'startTime',title:'开始时间',width:120, formatter:function(value,row,index){
	        	return(new Date(value)).format("yyyy-MM-dd HH:mm:ss");
	        }},
		    {field:'endTime',title:'结束时间',width:120, formatter:function(value,row,index){
		    	if(!value){
		    		return "";
		    	}
	        	return(new Date(value)).format("yyyy-MM-dd HH:mm:ss");
	        }},
		    {field:'result',title:'运行结果',width:100, formatter:function(value,row,index){
		    	switch (value){
		    	case "Waiting":
		    		return "等待";
		    	case "Finished":
		    		return "正常结束";
		    	case "Finished (with errors)":
		    		return "异常结束";
		    	case "Running":
		    		return "运行中";
		    	case "Paused":
		    		return "被暂停的";
		    	case "Preparing executing":
		    		return "准备执行";
		    	case "Initializing":
		    		return "初始化";
		    	case "Stopped":
		    		return "被停止的";
		    	case "Halting":
		    		return "挂起的";
		    	default:
		    		return value;
		    	}
	        }},
		    {field:'logFile',title:'日志文件路径',width:500, hidden:true},
		    {field:'_time',title:'耗时',width:150, formatter:function(value,row,index){
		    	var startTime=row["startTime"]?row["startTime"]:"";
		    	var endTime=row["endTime"]?row["endTime"]:"";
		    	if(startTime && endTime){
		    		return formatDuring(endTime-startTime);
		    	}
		    	return "--";
		    }},
		    {field:'_operation',title:'运行日志', formatter:function(value,row,index){
		    	var logId=row["logId"]?row["logId"]:"";
		    	return '<a id="btn" href="#" class="easyui-linkbutton" data-options="iconCls: \'icon-remove\'" onclick="downloadLog(\''+logId+'\')">下载</a>';
		    }}
		]]
	});
	$("#runLogFromTime").datebox({
		currentText:"今天",
		closeText:"关闭",
		formatter : function(date){
			return date.format("yyyy-MM-dd");
		},
		parser : function(s){
			//$.messager.alert("错误",s);
		}
	});
	$("#runLogToTime").datebox({
		currentText:"今天",
		closeText:"关闭",
		formatter : function(date){
			return date.format("yyyy-MM-dd");
		},
		parser : function(s){
			//$.messager.alert("错误",s);
		}
	});
	$("#runLogQueryBtn").linkbutton({
		iconCls : "icon-search"
	});
	$('#runLogQueryBtn').bind('click', function(){
		var fromTime= $("#runLogFromTime").datebox("getValue");
		var toTime = $("#runLogToTime").datebox("getValue");
		var jobId = $("#runLogJobId").val();
		$("#runLogList").datagrid({
			url : contextPath+"/kettle/findrunlogs.do",
			pageNumber : 1,
			pageSize : 10,
			queryParams : {"jobId": jobId, "fromTime":fromTime, "toTime":toTime},
			onLoadSuccess : function(data){
			}
		});
    });
	//======================运行状态修改
	$("#runStatusEditForm").form({
		url:contextPath+"/kettle/editjobrunstatus.do",
		onSubmit : function(param){ // 提交前触发，返回 false 来阻止提交动作。
			
		},
		success : function(data){ // 当表单提交成功时触发。
			$.messager.alert("错误",eval('(' + data + ')')["resultDesc"]);
			$("#runStatusEditWindow").window("close");
			$("#resultList").datagrid("reload");
		}
	});
	$("#runStatusEidtSaveBtn").linkbutton({
		iconCls : "icon-save"
	});
	$('#runStatusEidtSaveBtn').bind('click', function(){
		var runStatus = $("#e_runStatus").combobox("getText");
		$.messager.confirm({
			title:"修改运行状态确认",
			msg:"修改运行状态有风险！<br/>确认要运行状态修改为\""+runStatus+"\"吗？",
			ok : "确认",
			cancel : "取消",
			fn:function(confirm){
			    if (confirm){
			    	$("#runStatusEditForm").submit();
			    }
			}
		});
		
    });
	
	// 所有弹出窗口初始化
	$("#addWindow").window({
		title:"新增Kettle任务",
	    width:600,
	    height:400,
	    modal:true,
	    closed:true,
	    onClose : function(){
	    	// 清空datagrid列表数据
	    	$("#paramList").datagrid("loadData", {total:0, rows:[]});
	    	$("#jobAddForm").form("reset");
	    	$("#a_directoryLabel").text("");
    		$("#a_nameLabel").text("");
	    }
	});
	$("#editWindow").window({
		title:"编辑Kettle任务",
	    width:600,
	    height:400,
	    modal:true,
	    closed:true,
	    onClose : function(){
	    	// 清空datagrid列表数据
	    	$("#jobEditForm").form("reset");
	    	$("#e_directoryLabel").text("");
    		$("#e_nameLabel").text("");
	    }
	});
	$("#paramsEditWindow").window({
		title:"编辑Kettle任务默认参数",
	    width:600,
	    height:400,
	    modal:true,
	    closed:true,
	    onClose : function(){
	    	// 清空datagrid列表数据
	    	$("#paramEidtList").datagrid("loadData", {total:0, rows:[]});
	    }
	});
	$("#runStatusEditWindow").window({
		title:"修改运行状态",
	    width:300,
	    height:150,
	    modal:true,
	    closed:true,
	    onClose : function(){
	    	// 清空datagrid列表数据及查询条件数据
	    	$("#runStatusEditForm").form("reset");
	    	$("#runStatusEditJobId").val("");
	    }
	});
	$("#runLogWindow").window({
		title:"运行记录",
	    width:600,
	    height:400,
	    modal:true,
	    closed:true,
	    onClose : function(){
	    	// 清空datagrid列表数据及查询条件数据
	    	$("#runLogList").datagrid("loadData", {total:0, rows:[]});
	    	$("#runLogFromTime").datebox("setValue", new Date().format("yyyy-MM-dd"));
	    	$("#runLogToTime").datebox("setValue", new Date().format("yyyy-MM-dd"));
	    	$("#runLogJobId").val("");
	    }
	});
});

function deleteParam(value,index){
	$.messager.confirm({
		title:"删除确认",
		msg:"确认要删除参数吗？",
		ok : "确认",
		cancel : "取消",
		fn:function(confirm){
			if (confirm){
				if(value){
					$.ajax({
						url : contextPath+"/kettle/removeparam.do",
						type : "POST",
						dataType : "json",
						data : {"paramId" : value},
						error : function(XMLHttpRequest, textStatus, errorThrown){
							$.messager.alert("错误","发送请求异常");
						},
						success : function(data, textStatus, jqXHR){
							if(data["result"]=="1"){
								$("#paramEidtList").datagrid("deleteRow", index);
							}
						},
						complete : function(XMLHttpRequest, textStatus){
							
						}
					});
				} else {
					$("#paramEidtList").datagrid("deleteRow", index);
				}
			}
		}
	});
}

function downloadLog(logId){
	window.open(contextPath+"/kettle/downloadlog.do?logId="+logId);
}