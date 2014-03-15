package nz.ac.auckland.jobs.periodic


import nz.ac.auckland.common.stereotypes.UniversityComponent
import nz.ac.auckland.jobs.periodic.PeriodicJobs.ScheduledJobInfo
import nz.ac.auckland.lmz.controlpanel.core.ControlPanel
import nz.ac.auckland.lmz.controlpanel.core.ControlPanelAssets
import nz.ac.auckland.lmz.controlpanel.core.ControlPanelMetadata

import nz.ac.auckland.stencil.LinkBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.inject.Inject
import java.text.SimpleDateFormat

/**
 * Presents an option to switch between configured fake users
 *
 * author: Irina Benediktovich - http://gplus.to/IrinaBenediktovich
 */
@UniversityComponent
class PeriodicJobsControlPanel implements ControlPanel {

	private Logger log = LoggerFactory.getLogger(getClass())

	@Inject LinkBuilder linkBuilder
	@Inject PeriodicJobs executor


	/**
	 * @return the control panel meta data
	 */
	@Override
	ControlPanelMetadata getMetadata() {
		return new ControlPanelMetadata(
				title: 'Periodic Jobs',
				description: 'Information about current jobs',
				uri: 'periodic-jobs',
				assets: new ControlPanelAssets(stylesheets: ['/periodicjobs/css/pjobs.css'])

		);
	}

	/**
	 * @return the template to render
	 */
	@Override
	String getTemplate() {
		return "/periodicjobs/jsp/jobs.jsp";
	}

	/**
	 * @return current user information and table of available fake users
	 */
	@Override
	Map<String, Object> getViewModel() {
		def result = [:]
		executor.listAllJobs().each {ScheduledJobInfo jobInfo ->
			appendJobInfo(jobInfo, result)
		}

		return [entries: result]
	}

	static SimpleDateFormat df = new SimpleDateFormat('yyyy-MM-dd HH:mm:ss')
	protected void appendJobInfo(ScheduledJobInfo jobInfo, Map result){
		result.put(jobInfo.job.class.simpleName, [type: jobInfo.jobType, status: getStatus(jobInfo), enabled: jobInfo.job.isEnabled(), logs:
				jobInfo.executions.asMap().values().collect{
					String start = df.format(it.start)
					String finish = it.finish ? df.format(it.finish) : "running..."
					String message = it.error ?: ""
					return [start:start, finish:finish, message:message]
				}]
		)
	}

	protected String getStatus(ScheduledJobInfo jobInfo){
		if (jobInfo.future == null){
			return 'Not scheduled'
		}

		if (jobInfo.future.isCancelled()){
			return 'Cancelled'
		}

		if (jobInfo.future.isDone()){
			return 'Finished (will never run again)'
		}

		if (jobInfo.job instanceof AbstractPeriodicJob){
			return "Scheduled for execution every ${ jobInfo.periodicDelay} second(s)"
		}else{
			return 'Init job'
		}
	}
}
