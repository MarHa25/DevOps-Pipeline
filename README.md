# Community Communication Broadcast System – CI/CD & Testing

This repository contains the **CI/CD configuration, infrastructure deployment scripts, and automated testing** developed for the Community Communication Broadcast System as part of a DevOps assignment at Cardiff University.

The repository does **not** contain the complete Community Communication Broadcast System application. It contains the DevOps and testing components used to deploy, build, test, and run the system.

## Related Project

The full application is the **Community Communication Broadcast System**.

The application repository is required for the complete system to work, as the deployment and CI/CD scripts in this repository reference the application's source code, database schema, and Gradle build.

> **Note:** The original application was developed as part of a university team project. This repository contains the CI/CD and testing work separately for portfolio purposes.

## Repository Contents

### Azure Infrastructure

The Terraform configurations are organised into three deployment stages:

- **InitialVMDeployment** – provisions the initial virtual machine and deploys the application environment.
- **DockerEngineTF** – provisions a VM and installs Docker and MariaDB.
- **JenkinsDeployment** – provisions a Jenkins environment with the required development and containerisation tools.

The Terraform configurations use variables and local configuration values for environment-specific information such as Azure resources, usernames, and SSH key paths.

### Jenkins

The Jenkins deployment scripts configure a Jenkins server with:

- Java 21
- Jenkins
- Git
- Gradle
- Docker
- MariaDB

Jenkins is configured to run alongside the application, with Jenkins using port `8081` while the application can use port `8080`.

### Automated Testing

The `Test Scripts` directory contains automated tests developed for the system, including:

- Unit testing
- Spring MVC testing
- Servlet testing
- Selenium-based browser testing

The tests cover different levels of the application, from lightweight controller testing to full-container and browser-based testing.

### JMeter Performance Testing

The `JMeter` directory contains the Apache JMeter test plan and generated test results.

The test plan is used for performance testing of the application, while the `output` directory contains the generated HTML test report and supporting resources.

## Technologies

- **Terraform** – Azure infrastructure provisioning
- **Jenkins** – CI/CD automation
- **Docker** – containerisation
- **MariaDB** – database
- **Gradle** – build automation
- **Java 21** – application and test environment
- **JUnit / Spring testing** – automated testing
- **Selenium** – browser-based testing
- **Apache JMeter** – performance testing
- **Azure DevTest Labs** – virtual machine infrastructure
- **Git / GitLab** – source control and application deployment

## Project Structure

```text
.
├── DockerEngineTF/
│   ├── data.tf
│   ├── locals.tf
│   ├── outputs.tf
│   ├── project-vm.tf
│   ├── serverDocker.sh
│   └── tf_config.tf
│
├── InitialVMDeployment/
│   ├── data.tf
│   ├── locals.tf
│   ├── matrix_deploy.sh
│   ├── outputs.tf
│   ├── project-vm.tf
│   └── tf_config.tf
│
├── JenkinsDeployment/
│   ├── data.tf
│   ├── locals.tf
│   ├── outputs.tf
│   ├── project-vm.tf
│   ├── serverJenkins.sh
│   └── tf_config.tf
│
├── JMeter/
│   ├── AssignmentFinal.jmx
│   └── output/
│
├── Test Scripts/
│   ├── FullContainerMVC.java
│   ├── FullContainerServlet.java
│   ├── LightweightMockMVC.java
│   ├── Selenium.side
│   ├── UnitTest.java
│   └── WebDriverSelenium.java
│
└── .gitignore
```

## Requirements

To reproduce the complete environment, you will need:

- An Azure environment with the required permissions
- Terraform
- An SSH key pair
- Docker
- Jenkins
- Java 21
- Gradle
- MariaDB
- Apache JMeter
- Access to the **Community Communication Broadcast System** source repository

Environment-specific values such as Azure subscription details, DevTest Lab information, SSH keys, and database credentials should be supplied separately rather than committed to this repository.

## Deployment Overview

The deployment process is broadly:

```text
Azure Infrastructure
        │
        ▼
   Virtual Machine
        │
        ├── MariaDB
        ├── Java / Gradle
        ├── Docker
        └── Jenkins
                │
                ▼
        Application Repository
                │
                ▼
        Build & Automated Tests
                │
                ▼
          Application
```

The deployment scripts expect the Community Communication Broadcast System source code to be available separately.

## Security

Sensitive environment-specific information is intentionally excluded from this repository.

This includes:

- SSH private keys
- Terraform state files
- Database credentials
- Environment files
- Azure credentials
- Temporary credential files
- Local IDE configuration

The deployment scripts expect credentials such as the database root password to be supplied through environment variables rather than hardcoded in the scripts.

## Academic Context

This work was developed as part of a **DevOps assignment at Cardiff University**. The repository has been separated from the original team application repository to showcase the CI/CD, infrastructure, and testing components independently.
