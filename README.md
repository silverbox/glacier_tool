# Glacier tools
My old work for AWS S3 Glacier

## background
AWS Glacier is very good solution to store huge data which will use rarely. e.g. Family Video.
But, It's difficult to handle. we can't operate it at AWS console.
- Need to execute job for get achive file list. and it will take some hours.
- When we download it, we need to execute job for prepare to download. and it will take more than 12 hours(it depends on Config)
- Furthermore, when we use Vault config allow only free slot to download, we need to download separately.

## Purpose
Make us easy to handle archive files in Vault.

## How to use it

### Prepare Vault and AWS account information.
- Please prepare AWS S3 Glacier Vault in your AWS account.
- build it and execute. e.g. Use eclipse, build with maven and run the jar file, or so on.
- at first, you will see the dialog to set AWS connection information. if you already setup awscli, it's OK.

### load inventory information.
- If your Vault already contain any archive fime, please push the "Execute Inventory info getting job"
- You need to wait for complete the job for a long time around 12 hours. I reccomend to execute before you go to bed.
- At "Job List" tab, you can see the job list. if the "InventoryRetrieve" job complete, you can push the "RefreshInventory" button.

### How to upload.
- Plesae push the "File Upload" button.

### How to download.
- Please consider your download size limit. In some case, you need to download partially. if so, please select byte range type and byte step.
- Push "Execute retrieve Job"
- Please wait a long time around 12 hours.
- At "Job List" tab, can see the job list. if the "ArchiveRetrive" job complete, you can push the "Execute download" button.
- If oyu need to download partially, after all of byte step, please join all of them with your OS command.
