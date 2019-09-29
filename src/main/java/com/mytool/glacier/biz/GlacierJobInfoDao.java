package com.mytool.glacier.biz;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.model.DescribeVaultOutput;
import com.amazonaws.services.glacier.model.GlacierJobDescription;
import com.amazonaws.services.glacier.model.ListJobsRequest;
import com.amazonaws.services.glacier.model.ListJobsResult;
import com.mytool.glacier.vo.JobInfoVo;

public class GlacierJobInfoDao {

	private AmazonGlacierClient client;
	private DescribeVaultOutput vaultInfo;

	public GlacierJobInfoDao(AmazonGlacierClient client, DescribeVaultOutput vaultInfo) {
		super();
		this.client = client;
		this.vaultInfo = vaultInfo;
	}

	public List<JobInfoVo> getJobList() {
		ListJobsRequest listJobsRequest = new ListJobsRequest();
		listJobsRequest.setVaultName(vaultInfo.getVaultName());
		ListJobsResult listJobsResult = client.listJobs(listJobsRequest);
		
		List<JobInfoVo> retJobInfoList = new ArrayList<JobInfoVo>();
		
		for(GlacierJobDescription orgJobInfo: listJobsResult.getJobList()){
			JobInfoVo jobInfo = new JobInfoVo();
			jobInfo.setAction(orgJobInfo.getAction());
			jobInfo.setArchiveId(orgJobInfo.getArchiveId());
			jobInfo.setArchiveSHA256TreeHash(orgJobInfo.getArchiveSHA256TreeHash());
			jobInfo.setArchiveSizeInBytes(orgJobInfo.getArchiveSizeInBytes());
			jobInfo.setCompleted(orgJobInfo.getCompleted());
			jobInfo.setCompletionDate(orgJobInfo.getCompletionDate());
			jobInfo.setCreationDate(orgJobInfo.getCreationDate());
			jobInfo.setJobDescription(orgJobInfo.getJobDescription());
			jobInfo.setInventoryRetrievalParameters(orgJobInfo.getInventoryRetrievalParameters());
			jobInfo.setInventorySizeInBytes(orgJobInfo.getInventorySizeInBytes());
			jobInfo.setJobId(orgJobInfo.getJobId());
			jobInfo.setRetrievalByteRange(orgJobInfo.getRetrievalByteRange());
			jobInfo.setSHA256TreeHash(orgJobInfo.getSHA256TreeHash());
			jobInfo.setSNSTopic(orgJobInfo.getSNSTopic());
			jobInfo.setStatusCode(orgJobInfo.getStatusCode());
			jobInfo.setStatusMessage(orgJobInfo.getStatusMessage());
			jobInfo.setTier(orgJobInfo.getTier());
			jobInfo.setVaultARN(orgJobInfo.getVaultARN());

			retJobInfoList.add(jobInfo);
		}
		return retJobInfoList;
	}
}
