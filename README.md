# How to deploy and execute Hear&Know C-Cada Logger

Install the cf CLI from [Cloud Foundry](https://docs.cloudfoundry.org/cf-cli/install-go-cli.html)

Open a Windows command prompt as administrator or a terminal on other platform.

Check cf command is available:
```
cf -v
```

**!!! WARNING !!!**

*Every further variable - including ${} or <> - like ${something} or \<something\> 
as to be substitute with your own environment variable:*

* e.g.
  * ${userid} will become yourUserid
  * \<org\> will become yourOrg

**!!! WARNING !!!**

Before being able to log to Bluemix with cf command you should be aware of **2** things:
  1. the name of your **organization** - which is usually the same name as your Bluemix userid and the same among all Regions (Germany, Sydney, United Kingdom and US South)
  2. the name of one **space** - which is assigned to one Region - in one Region (Germany, Sydney, United Kingdom or US South) in your organization.

> At least one organization has been created automatically, but no space are created for you.
If not sure about a space is available then log in [Bluemix console](https://console.bluemix.net/account/manage-orgs),
check 'Spaces in Region' is not empty and if so then Add a space.

Connect to Bluemix (US):
```
cf l -a https://api.ng.bluemix.net -u ${userid} -p ${password} --skip-ssl-validation -s ${space} -o ${org}
```
or connect to Bluemix (GB):
```
cf l -a https://api.eu-gb.bluemix.net -u ${userid} -p ${password} --skip-ssl-validation -s ${space} -o ${org}
```
or connect to Bluemix (DE):
```
cf l -a https://api.eu-gb.bluemix.net -u ${userid} -p ${password} --skip-ssl-validation -s ${space} -o ${org}
```

Create cloudantNoSQLDB service:
> Syntax: cf cs <service> <plan> <service_instance>
```
cf cs cloudantNoSQLDB Lite db0
```

Create service key (credential) to grant access to service:
> Syntax: cf csk <service_instance> <service_key>
```
cf csk db0 user0
```

Check that service key has been created:
> Syntax: cf sk <service_instance>
```
cf sk db0
```

At any time you should be able to get your credential (url, port, username, password...) for one of your service instance.
> Syntax: cf service-key <service_instance> <service_key>
```
cf service-key db0 user0
```

Before creating a service instance which will be able to query Hear&Know UDP server, you should create a json configuration file - which we will call **serverConf.json** - e.g.
```
{
	"ipAddress": "193.251.53.223",
	"port": 5100,
	"packetSize": 10000,
	"loggers": [
	  {"flag":900, "tid":"AC61D447924A", "uid":"MOBILI"},
	  {"flag":900, "tid":"AC61D447924A", "uid":"MOBILI"},
	  {"flag":900, "tid":"AC61D447924A", "uid":"MOBILI"}
	]
}
```

Then come back to cf cli and create Hear&Know user provided service
> Syntax: cf cups <service_instance> -p <json file>
```
cf cups hak0 -p serverConf.json
```

Check that both services are created
```
cf s
```

If not already done, create a Bluemix subdomain in your organization.
First list orgs to get yours
```
cf orgs
```
Then create the subdomain
> Syntax: cf create-domain <org> <subdomain>
```
cf create-domain <org> hearandknow.mybluemix.net
```
or if in United Kingdom Region
```
cf create-domain <org> hearandknow.eu-gb.mybluemix.net
```
or if in Germany Region
```
cf create-domain <org> hearandknow.eu-de.mybluemix.net
```

Check new created domain exists:
```
cf domains
```

Download Hear&Know C-Cada Logger application code:
```
https://github.com/baudelaine/hak/archive/master.zip
```

Now we are ready to deploy C-Cada Logger application.
Unzip master.zip and change to hak-master directory:
```
cd hak-master
```

Now edit manifest.yml and check key value pairs:
1. host has to be set to **C-Cada_Logger**
2. name has to be set to **C-Cada_Logger**
3. domain value has to be set either to:
   * **hearandknow.mybluemix.net** if in US South Region
   * or **hearandknow.eu-gb.mybluemix.net** if in United Kingdom Region 
   * or **hearandknow.eu-de.mybluemix.net** if in United Germany Region
4. services to bind to C-Cada_Logger has to be set to both:
   * **db0**
   * **hak0**
  
e.g. for Germany Region:
```
applications:
- host: C-Cada_Logger
  disk: 256M
  name: C-Cada_Logger
  path: ./WebContent
  domain: hearandknow.eu-de.mybluemix.net
  mem: 256M
  instances: 1
  services:
  - db0
  - hak0
```

Then deploy C-Cada Logger to Bluemix:

**!!! WARNING !!!**

Push application from hak-master ONLY or command will fail.
```
cf p
```

Once staging has completed check C-Cada Logger is running:
```
cf a
```

Copy urls columns content. It should match: 
>**C-Cada_Logger.hearandknow.eu-de.mybluemix.net**

Paste it in a Web browser and check C-Cada Logger is running.

**OPTIONAL**: Cleaning Bluemix space

Unbind service_instance from app:
```
cf us C-Cada_Logger hak0
cf us C-Cada_Logger db0
```

Delete service-key from service_instance:
```
cf dsk db0 user0 -f
```

Delete service_instance:
```
cf ds hak0 -f
cf ds db0 -f
```

Delete applications:
```
cf d C-Cada_Logger -f
```

Delete owned domain:
```
cf delete-domain hearandknow.mybluemix.net -f
```
or
```
cf delete-domain hearandknow.eu-gb.mybluemix.net -f
```
or
```
cf delete-domain hearandknow.eu-de.mybluemix.net -f
```
