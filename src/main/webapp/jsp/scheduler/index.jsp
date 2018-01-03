<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>Kettle Job List</title>
	<jsp:include page="/head.jsp"></jsp:include>
	<script type="text/javascript" src="${pageContext.request.contextPath}/plugins/cron/cron.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/jsp/scheduler/index.js"></script>
</head>
<body>
	<div class="easyui-layout" fit="true">
		<!-- 查询条件
		<div data-options="region:'north'" style="height:0px">
			<div id="queryCondition">
			</div>
		</div> -->
		<!-- 结果列表 -->
		<div data-options="region:'center',title:'数据抽取任务列表',iconCls:'icon-ok'">
			<div id="resultList"></div>
		</div>
	</div>
	<!-- 新增弹出窗口 -->
	<div id="addWindow">
		<div class="easyui-layout" fit="true">
			<!-- 左侧目录树 -->
			<div data-options="region:'west'" style="width:200px">
				<div id="repoTree"></div>
			</div>
			<!-- 中部配置区域 -->
			<div data-options="region:'center'">
				<div class="easyui-layout" fit="true">
					<form id="jobAddForm" method="post">
					<div data-options="region:'center'">
						<div class="easyui-tabs" fit="true">
							<div title="基本信息" style="display:none;">
								<table>
									<tr>
										<td style="width:100px">路径：</td>
										<td style="width:270px">
											<input type="hidden" id="a_directory" name="directory" style="width:200px"/>
											<label id="a_directoryLabel"></label>
										</td>
									</tr>
									<tr>
										<td style="width:100px">任务名称：</td>
										<td style="width:270px">
											<input type="hidden" id="a_name" name="name" style="width:200px"/>
											<label id="a_nameLabel"></label>
										</td>
									</tr>
									<tr>
										<td style="width:100px">状态：</td>
										<td style="width:270px">
											<select class="easyui-combobox" id="a_state" name="state" style="width:200px">
											    <option value="1">启用</option>
											    <option value="0">停用</option>
											</select>
										</td>
									</tr>
									<tr>
										<td>执行计划表达式：</td>
										<td>
											<input class="easyui-validatebox" type="text" id="a_exp" name="exp" style="width:200px"/>
											<a id="a_expEditBtn" href="#">编辑</a>
										</td>
									</tr>
									<tr>
										<td>任务描述：</td>
										<td><input class="easyui-validatebox" type="text" id="a_description" name="description" style="width:200px"/></td>
									</tr>
									<tr>
										<td>扩展描述：</td>
										<td><input class="easyui-validatebox" data-options="multiline:true" id="a_extendedDescription" name="extendedDescription" style="width:200px;height:100px"></td>
									</tr>
									<tr>
										<td colspan="2" align="center">
											<!-- <input type="text" name="params"/>
											<input type="text" name="params"/> -->
										</td>
									</tr>
								</table>
							</div>
							<div title="任务参数" style="display:none;">
								<div id="paramList"></div>
							</div>	
						</div>
					</div>
					</form>
					<div data-options="region:'south'" style="height:35px">
						<a id="jobAddFormBtn" href="#">保存</a>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- 修改弹出窗口 -->
	<div id="editWindow">
		<form id="jobEditForm" method="post">
			<input type="hidden" id="e_jobId" name="jobId" style="width:200px"/>
			<table>
				<tr>
					<td style="width:100px">路径：</td>
					<td style="width:270px">
						<input type="hidden" id="e_directory" name="directory" style="width:200px"/>
						<label id="e_directoryLabel"></label>
					</td>
				</tr>
				<tr>
					<td style="width:100px">任务名称：</td>
					<td style="width:270px">
						<input type="hidden" id="e_name" name="name" style="width:200px"/>
						<label id="e_nameLabel"></label>
					</td>
				</tr>
				<tr>
					<td style="width:100px">状态：</td>
					<td style="width:270px">
						<select class="easyui-combobox" id="e_state" name="state" style="width:200px">
						    <option value="1">启用</option>
						    <option value="0">停用</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>执行计划表达式：</td>
					<td>
						<input class="easyui-validatebox" type="text" id="e_exp" name="exp" style="width:200px"/>
						<a id="e_expEditBtn" href="#">编辑</a>
					</td>
				</tr>
				<tr>
					<td>任务描述：</td>
					<td><input class="easyui-validatebox" type="text" id="e_description" name="description" style="width:200px"/></td>
				</tr>
				<tr>
					<td>扩展描述：</td>
					<td><input class="easyui-validatebox" data-options="multiline:true" id="e_extendedDescription" name="extendedDescription" style="width:200px;height:100px"></td>
				</tr>
				<tr>
					<td colspan="2" align="center">
						<a id="jobEditFormBtn" href="#">保存</a>
					</td>
				</tr>
			</table>
		</form>
	</div>
	<!-- 详细弹出窗口 -->
	<div></div>
	<!-- 默认参数编辑弹出窗口 -->
	<div id="paramsEditWindow">
		<div class="easyui-layout" fit="true">
			<form id="paramEditForm" method="post">
			<div data-options="region:'center'">
				<input type="hidden" id="paramEditJobId" name="jobId" />
				<div id="paramEidtList"></div>
			</div>
			</form>
			<div data-options="region:'south'" style="height:35px">
				<a id="paramEidtSaveBtn" href="#">保存</a>
			</div>
		</div>
	</div>
	<!-- 表达式编辑弹出窗口 -->
	<div id="expEditWindow"></div>
	<!-- 运行记录弹出窗口 -->
	<div id="runLogWindow">
		<div class="easyui-layout" fit="true">
			<div data-options="region:'north'" style="height:35px">
				<input type="hidden" id="runLogJobId" name="jobId" />
				<label>时间：</label>
				<input id="runLogFromTime" type="text" class="easyui-datebox">
				-
				<input id="runLogToTime" type="text" class="easyui-datebox">
				<a id="runLogQueryBtn" href="#">查询</a>
			</div>
			<div data-options="region:'center'">
				<div id="runLogList"></div>
			</div>
		</div>
		
	</div>
</body>
</html>