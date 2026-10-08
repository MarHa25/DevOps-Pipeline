locals {
  user_email_name = "your-name"
  dtl_name = "YOUR_DEVTESTLAB_NAME"
  dtl_rg_name = "YOUR_DEVTESTLAB_RESOURCE_GROUP"

  vm_username = "username"
  vm_password = null
  vm_ssh_public_key_path = "../AzureKeys/testkey.pub"
  vm_ssh_private_key_path = "../AzureKeys/testkey"

  project_vm_name = "${local.user_email_name}-InitialVM"
  project_vm_size = "Standard_B2ms"

  subscription_id = "YOUR_AZURE_SUBSCRIPTION_ID"
  location        = "uksouth"
}
