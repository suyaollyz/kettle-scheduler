/**
 * 
 */
package cn.kettle.scheduler.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.kettle.scheduler.kettle.KettleJobBean;
import cn.kettle.scheduler.kettle.KettleJobParamBean;
import cn.kettle.scheduler.kettle.KettleJobRunLogBean;
import cn.kettle.scheduler.service.KettleJobService;

/**
 * 
 */
@Controller
@RequestMapping("kettle")
public class KettleController {
	@Resource
	KettleJobService kettleJobServiceImpl;
	@RequestMapping("index")
	public String index(){
		return "jsp/kettle/index";
	}
	@RequestMapping("listjobs")
	@ResponseBody
	public List<KettleJobBean> listJobs(){
		List<KettleJobBean> jobList = kettleJobServiceImpl.selectAllJob();
        return jobList;
	}
	@RequestMapping(value="listrepoobject")
	@ResponseBody
	public List<Map<String, Object>> listRepoObject(@RequestParam Map<String,Object> params){
		String pid="/";
		if(params!=null && !params.isEmpty() && params.get("id")!=null){
			pid=(String)params.get("id");
		}
		return kettleJobServiceImpl.listRepoObject(pid);
	}
	
	@RequestMapping(value="findjob")
	@ResponseBody
	public KettleJobBean findJob(@RequestParam String jobId){
		return kettleJobServiceImpl.findJob(jobId);
	}
	
	@RequestMapping(value="addjob")
	@ResponseBody
	public Map<String, String> addJob(KettleJobBean kettleJob){
		kettleJobServiceImpl.addJob(kettleJob);
		Map<String, String> result = new HashMap<String, String>();
		result.put("result","1");
		result.put("resultDesc", "保存成功");
		return result;
	}
	@RequestMapping(value="editjob")
	@ResponseBody
	public Map<String, String> editJob(KettleJobBean kettleJob){
		int updateResult = kettleJobServiceImpl.editJob(kettleJob);
		Map<String, String> result = new HashMap<String, String>();
		if(updateResult==1){
			result.put("result","1");
			result.put("resultDesc", "保存成功");
		} else {
			result.put("result","0");
			result.put("resultDesc", "保存失败");
		}
		return result;
	}
	@RequestMapping(value="removejob")
	@ResponseBody
	public Map<String, String> removeJob(@RequestParam(value="jobIds[]")String[] jobIds){
		int removeResult = 0;
		for(String jobId : jobIds){
			removeResult += kettleJobServiceImpl.removeJob(jobId);
		}
		Map<String, String> result = new HashMap<String, String>();
		if(removeResult>0){
			result.put("result","1");
			result.put("resultDesc", "删除成功，共删除"+removeResult+"条记录");
		} else {
			result.put("result","0");
			result.put("resultDesc", "删除失败，共删除"+removeResult+"条记录");
		}
		return result;
	}
	
	/**
	 * 根据任务ID，查找对应的默认参数配置
	 * @param jobId
	 * @return
	 */
	@RequestMapping(value="findparams")
	@ResponseBody
	public List<KettleJobParamBean> findParams(String jobId){
		return kettleJobServiceImpl.findParams(jobId);
	}
	
	@RequestMapping(value="editparams")
	@ResponseBody
	public Map<String, String> editParams(KettleJobBean kettleJob){
		Map<String, String> result = new HashMap<String, String>();
		if(kettleJob.getParamConfigs()==null || kettleJob.getParamConfigs().isEmpty()){
			result.put("result","1");
			result.put("resultDesc", "保存成功");
		} else {
			kettleJobServiceImpl.editParams(kettleJob);
			result.put("result","1");
			result.put("resultDesc", "保存成功");
		}
		return result;
	}
	
	@RequestMapping(value="removeparam")
	@ResponseBody
	public Map<String, String> removeParam(String paramId){
		Map<String, String> result = new HashMap<String, String>();
		int updateResult = kettleJobServiceImpl.removeParam(paramId);
		if(updateResult==1){
			result.put("result","1");
			result.put("resultDesc", "删除成功");
		} else {
			result.put("result","0");
			result.put("resultDesc", "删除失败");
		}
		return result;
	}
	
	/**
	 * 重新加载指定任务
	 * @param jobId
	 * @return
	 */
	@RequestMapping(value="reloadjob")
	@ResponseBody
	public Map<String, String> reloadJob(@RequestParam(value="jobIds[]")String[] jobIds){
		Map<String, String> result = new HashMap<String, String>();
		try{
			for(String jobId : jobIds){
				kettleJobServiceImpl.reloadJob(jobId);
			}
			result.put("result","1");
			result.put("resultDesc", "调度成功");
		} catch (RuntimeException e){
			result.put("result","0");
			result.put("resultDesc", "调度失败");
		}
		return result;
	}
	/**
	 * 停止指定任务，从下次开始不再执行
	 * @param jobId
	 * @return
	 */
	@RequestMapping(value="pausejob")
	@ResponseBody
	public Map<String, String> pauseJob(@RequestParam(value="jobIds[]")String[] jobIds){
		Map<String, String> result = new HashMap<String, String>();
		try{
			for(String jobId : jobIds){
				kettleJobServiceImpl.pauseJob(jobId);
			}
			result.put("result","1");
			result.put("resultDesc", "操作成功");
		} catch (RuntimeException e){
			result.put("result","0");
			result.put("resultDesc", "操作失败"+e.getMessage());
		}
		return result;
	}
	
	/**
	 * 立即停止任务，任务当前如果正在执行，则强制停止执行，并且从下次开始也不再执行
	 * @param jobId 要被立即停止的任务ID
	 * @return
	 */
	@RequestMapping(value="pausejobimmediately")
	@ResponseBody
	public Map<String, String> pauseJobImmediately(@RequestParam(value="jobIds[]")String[] jobIds){
		Map<String, String> result = new HashMap<String, String>();
		try{
			for(String jobId : jobIds){
				// 停止任务调度
				kettleJobServiceImpl.pauseJob(jobId);
				// 停止当前执行
				kettleJobServiceImpl.interruptJob(jobId);
			}
			result.put("result","1");
			result.put("resultDesc", "操作成功");
		} catch (RuntimeException e){
			result.put("result","0");
			result.put("resultDesc", "操作失败"+e.getMessage());
		}
		return result;
	}
	
	/**
	 * 恢复指定任务调度
	 * @param jobId
	 * @return
	 */
	@RequestMapping(value="resumejob")
	@ResponseBody
	public Map<String, String> resumeJob(@RequestParam(value="jobIds[]")String[] jobIds){
		Map<String, String> result = new HashMap<String, String>();
		try{
			for(String jobId : jobIds){
				kettleJobServiceImpl.resumeJob(jobId);
			}
			result.put("result","1");
			result.put("resultDesc", "操作成功");
		} catch (RuntimeException e){
			result.put("result","0");
			result.put("resultDesc", "操作失败"+e.getMessage());
		}
		return result;
	}
	
	/**
	 * 修改任务运行状态，仅限于由于系统异常导致的运行状态未更改的情况，其他情况可能会导致任务运行混乱，请谨慎使用<br/>
	 * 该方法只能将“运行中”状态修改为“正常结束”或者“异常结束”，不能修改为“运行中”
	 * @param jobId 任务ID
	 * @param runStatus 修改后的任务运行状态
	 * @return
	 */
	@RequestMapping(value="editjobrunstatus")
	@ResponseBody
	public Map<String, String> editJobRunStatus(String jobId, Integer runStatus){
		Map<String, String> result = new HashMap<String, String>();
		try{
			int editCount = kettleJobServiceImpl.editJobRunStatus(jobId, runStatus);
			if(editCount>0){
				result.put("result","1");
				result.put("resultDesc", "操作成功");
			}else{
				result.put("result","0");
				result.put("resultDesc", "操作失败");
			}
		} catch (RuntimeException e){
			result.put("result","0");
			result.put("resultDesc", "操作失败"+e.getMessage());
		}
		return result;
	}
	
	/**
	 * 根据任务ID，查找该任务所有运行记录
	 * @param jobId
	 * @return
	 */
	@RequestMapping(value="findrunlogs")
	@ResponseBody
	public Map<String, Object> findRunLogs(String jobId, String fromTime, String toTime, Integer page, Integer rows){
		Map<String, Object> result = new HashMap<String, Object>();
		int total = kettleJobServiceImpl.findRunLogsCount(jobId, fromTime, toTime);
		List<KettleJobRunLogBean> list = kettleJobServiceImpl.findRunLogs(jobId, fromTime, toTime, page, rows);
		result.put("total", total);
		result.put("rows", list);
		return result;
	}
	/**
	 * 下载指定的运行日志文件
	 * @param logId
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value="downloadlog")
	public ResponseEntity<byte[]> downloadLog(String logId) throws Exception{
		KettleJobRunLogBean runLog = kettleJobServiceImpl.findRunLog(logId);
		if(runLog==null){
			throw new Exception("未找到对应的运行记录");
		}
		File file=new File(runLog.getLogFile());
		HttpHeaders headers = new HttpHeaders();
		if(!file.exists()){
			headers.setContentType(MediaType.TEXT_HTML);
			return new ResponseEntity<byte[]>("未找到对应日志文件".getBytes("UTF-8"), headers, HttpStatus.OK);
		}
		headers.setContentDispositionFormData("attachment", file.getName());
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);
	}
	
	
}
