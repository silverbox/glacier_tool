package com.mytool.glacier.biz;

import java.util.List;

import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.model.DescribeVaultOutput;
import com.amazonaws.services.glacier.model.ListVaultsRequest;
import com.amazonaws.services.glacier.model.ListVaultsResult;

public class GlacierVaultInfoDao {

	private AmazonGlacierClient client;

	public GlacierVaultInfoDao(AmazonGlacierClient client) {
		super();
		this.client = client;
	}

	public List<DescribeVaultOutput> getVaultInfoList() {
		ListVaultsRequest listVaultsRequest = new ListVaultsRequest();
		ListVaultsResult listVaultsResult = client.listVaults(listVaultsRequest);

		return listVaultsResult.getVaultList();
	}
}
