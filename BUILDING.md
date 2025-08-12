# Building

This documentation provides information for developers to set up their environment and build their project from sources.

* [Development environment](#development-environment)
  * [Quick check](#quick-check)
  * [Requirements](#requirements)
  * [Install the required JDKs](#install-the-required-jdks)
  * [Install git](#install-git)
  * [Install Docker Desktop](#install-docker-desktop)
* [Clone the repository and set up git](#clone-the-repository-and-set-up-git)
* [Building the project](#building-the-project)

## Development environment

### Quick check

To check that your development environment is properly set up to build the project, from the project root run on macOS or Linux:
```shell
./setup.sh
```

or on Windows:
```pwsh
.\setup.ps1
```

Your output should look something like the following:

```
ℹ️ Checking required JVM:
✅ JAVA_HOME is set to /Users/datadog/.sdkman/candidates/java/17.0.10-zulu.
✅ JAVA_8_HOME is set to /Users/datadog/.sdkman/candidates/java/8.0.402-zulu.
✅ JAVA_11_HOME is set to /Users/datadog/.sdkman/candidates/java/11.0.22-zulu.
✅ JAVA_17_HOME is set to /Users/datadog/.sdkman/candidates/java/17.0.10-zulu.
✅ JAVA_21_HOME is set to /Users/datadog/.sdkman/candidates/java/21.0.2-zulu.
ℹ️ Checking git configuration:
✅ The git command line is installed.
✅ pre-commit hook is installed in repository.
ℹ️ Checking Docker environment:
✅ The docker command line is installed.
✅ The Docker server is running.
```

If there is any issue with your output, check the requirements above and use the following guide to install and configure the required tools.

### Requirements

Requirements to build the full project:

* The JDK versions 8, 11, 17 and 21 must be installed.
* The `JAVA_8_HOME`, `JAVA_11_HOME`, `JAVA_17_HOME`, and `JAVA_21_HOME` must point to their respective JDK location.
* The JDK 17 `bin` directory must be the only JDK on the PATH (e.g. `$JAVA_17_HOME/bin`).
* The `JAVA_HOME` environment variable may be unset. If set, it must point to the JDK 17 location (same as `JAVA_17_HOME`).
* The `git` command line must be installed.
* A container runtime environment must be available to run all tests (e.g. Docker Desktop).

### Install the required JDKs

Download and install JDK versions 8, 11, 17 and 21 for your OS.

#### macOS

* Install the required JDKs using `brew`:
  ```shell
  brew install --cask zulu@8 zulu@11 zulu@17 zulu@21
  ```
* Add the required environment variables to your shell using the `export` command. You can permanently install the environment variables by appending the `export` commands into your shell configuration file `~/.zshrc` or `.bashrc` or other.
  ```shell
  export JAVA_8_HOME=/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home
  export JAVA_11_HOME=/Library/Java/JavaVirtualMachines/zulu-11.jdk/Contents/Home
  export JAVA_17_HOME=/Library/Java/JavaVirtualMachines/zulu-17.jdk/Contents/Home
  export JAVA_21_HOME=/Library/Java/JavaVirtualMachines/zulu-21.jdk/Contents/Home
  export JAVA_HOME=$JAVA_17_HOME
  ```
* Restart your shell after applying the changes if you appended the commands to your shell configuration file.

> [!NOTE]
> ARM users: there is no Oracle JDK v8 for ARM.
> It's recommended to use [Azul's Zulu](https://www.azul.com/downloads/?version=java-8-lts&architecture=arm-64-bit&package=jdk#zulu) builds of Java 8.
> [Amazon Corretto](https://aws.amazon.com/corretto/) builds have also been proven to work.

> [!NOTE]
> macOS users: remember that `/usr/libexec/java_home` may control which JDK is in your path.

#### Linux

* Download and extract JDK 8, 11, 17, and 21 from [Eclipse Temurin releases](https://adoptium.net/temurin/releases/).
* Add the required environment variables to your shell using the `export` command. You can permanently install the environment variables by appending the `export` commands into your shell configuration file `~/.zshrc` or `~/.bashrc` or other.
  ```shell
  export JAVA_8_HOME=/<path to extracted archive>/jdk8u<current version of JDK 8>
  export JAVA_11_HOME=/<path to extracted archive>/jdk-11.<current version of JDK 11>
  export JAVA_17_HOME=/<path to extracted archive>/jdk-17.<current version of JDK 17>
  export JAVA_21_HOME=/<path to extracted archive>/jdk-21.<current version of JDK 21>
  export JAVA_HOME=$JAVA_17_HOME
  ```
* Restart your shell after applying the changes if you appended the commands to your shell configuration file.

#### Windows

* Download and install JDK 8, 11, 17, and 21 [Eclipse Temurin releases](https://adoptium.net/temurin/releases/).

  <details>
  <summary>Alternatively, install JDKs using winget or scoop. (click here to expand)</summary>

    ```pwsh
    winget install --id EclipseAdoptium.Temurin.8.JDK
    winget install --id EclipseAdoptium.Temurin.11.JDK
    winget install --id EclipseAdoptium.Temurin.17.JDK
    winget install --id EclipseAdoptium.Temurin.21.JDK
    ```

  ```pwsh
  scoop bucket add java
  scoop install temurin8-jdk
  scoop install temurin11-jdk
  scoop install temurin17-jdk
  scoop install temurin21-jdk
  ```

  </details>

* To add the required environment variables, run the following PowerShell commands for each SDK version, replacing the paths with the correct version installed:
  ```pwsh
  [Environment]::SetEnvironmentVariable("JAVA_8_HOME",  "C:\Program Files\Eclipse Adoptium\jdk-8.0.432.6-hotspot", [EnvironmentVariableTarget]::User)
  [Environment]::SetEnvironmentVariable("JAVA_11_HOME", "C:\Program Files\Eclipse Adoptium\jdk-11.0.25.9-hotspot", [EnvironmentVariableTarget]::User)
  [Environment]::SetEnvironmentVariable("JAVA_17_HOME", "C:\Program Files\Eclipse Adoptium\jdk-17.0.12.7-hotspot", [EnvironmentVariableTarget]::User)
  [Environment]::SetEnvironmentVariable("JAVA_21_HOME", "C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot", [EnvironmentVariableTarget]::User)

  # JAVA_HOME = JAVA_17_HOME
  [Environment]::SetEnvironmentVariable("JAVA_HOME",    "C:\Program Files\Eclipse Adoptium\jdk-17.0.12.7-hotspot", [EnvironmentVariableTarget]::User)
  ```

### Install git

#### macOS

You can trigger the installation by running any `git` command from the terminal, e.g. `git --version`.
If not installed, the terminal will prompt you to install it.

#### Linux

```shell
apt-get install git
```

#### Windows

Download and install the installer from [the official website](https://git-scm.com/download/win).

<details>
<summary>Alternatively, install git using winget or scoop. (click here to expand)</summary>

```pwsh
winget install --id git.git
```

```pwsh
scoop install git
```

</details>

### Install Docker Desktop

> [!NOTE]
> Docker Desktop is the recommended container runtime environment, but you can use any other environment to run testcontainers tests.
> Check [the testcontainers container runtime requirements](https://java.testcontainers.org/supported_docker_environment/) for more details.

#### macOS

Download and install Docker Desktop from the official website:<br/>
https://docs.docker.com/desktop/setup/install/mac-install/

#### Linux

Download and install Docker Desktop from the official website:<br/>
https://docs.docker.com/desktop/setup/install/linux/

#### Windows

Download and install Docker Desktop from the official website:<br/>
https://docs.docker.com/desktop/setup/install/windows-install/

<details>
<summary>Alternatively, install Docker Desktop using winget. (click here to expand)</summary>

```pwsh
winget install --id Docker.DockerDesktop
```

</details>

## Clone the repository and set up git

* Get a copy of the project by cloning the repository using git in your workspace:
    ```shell
    git clone git@github.com:DataDog/dd-instrument-java.git
    ```
* There is a pre-commit hook setup to verify formatting before committing. It can be activated with the following command:
    ```shell
    cd dd-instrument-java
    cp .githooks/pre-commit .git/hooks/
    ```

> [!TIP]
> You can alternatively use the `core.hooksPath` configuration to point to the `.githooks` folder using `git config --local core.hooksPath .githooks` if you don't already have a hooks path defined system-wide.

> [!NOTE]
> The git hooks will check that your code is properly formatted before commiting.
> This is done both to avoid future merge conflict and ensure uniformity inside the code base.

> [!NOTE]
> The above git configuration will only be applied to this project and won't apply globally in your setup.

## Building the project

After everything is properly set up, you can move on to the next section to start a build or check [the contribution guidelines](CONTRIBUTING.md).

To build the project without running tests, run:
```shell
./gradlew clean assemble
```

To build the entire project with tests, run:
```shell
./gradlew clean build
```

