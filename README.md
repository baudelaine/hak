# How to deploy and execute Hear&Know C-Cada Logger

:information_source: To keep your environment clean we will complete installation in a container

Install [Docker](<https://docs.docker.com/install/>)


> On host

Pull an Ubuntu, WebSphere Liberty Profile server image with ibmcloud and other tools...

```
sudo docker pull baudelaine/wlp:hak
```

Get application code from github

```
sudo git clone https://github.com/baudelaine/hak
```

Change to application code directory

```
cd hak
```

Create hak container from baudelaine/wlp:hak image

```
sudo docker run -p 80:9080 -tdi --name hak -v $PWD:/app -v baudelaine/wlp:hak
```

Start container

	sudo docker start hak

Attach container

	sudo docker attach hak




> Inside container

Login to IBM Cloud

```
icl
```

:bulb: **icl** is an alias from ~/.bash_aliases. To guess which command is hidden behind this alias use: **command -v icl**

Create Cloudant service instance

```
ibmcloud resource service-instance-create db cloudantnosqldb lite eu-de -p '{"legacyCredentials": true}'
```

Create Cloudant service key

```
ibmcloud resource service-key-create dbKey Manager --instance-name db
```

Set Cloudant credential and HAK UDP server parameters in **VCAP_SERVICES** environment variable

:warning: Be sure to run this command inside **/app** directory

```
. ./getResources.sh
```

Check **VCAP_SERVICES** environment variable is set

```
echo $VCAP_SERVICES | jq .
```

Start WebSphere Liberty Profile server

```
stwlp
```

:bulb: **stwlp** is an alias from ~/.bash_aliases. To guess which command is hidden behind this alias use: **command -v stwlp**


> On host

Browse  [app](http://localhost/app)
