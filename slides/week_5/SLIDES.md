<meta halign="center" valign="center" talign="center"/>
<meta footer="github.com/MadalinaPatrichi/uob-cloud-computing / week 5"/>

# Observability and Autoscaling

### _Cloud Native - Week 5_

![binoculars](edi-libedinsky-711483-unsplash.jpg#height=450px)

---
<meta halign="center" valign="center" talign="left"/>

## Contents of this lecture

- What is observability and why does it matter?

- Observability in the Cloud

- Observability in Kubernetes

- Measuring stuff

- Graphing stuff

- Scaling your software based on this "stuff"

---
<meta halign="center" valign="center" talign=""/>

# What is observability?

---
<meta halign="" valign="" talign=""/>

## Where we started: an app on `localhost`

When the app ran on our machine:

- `java -jar ...`

- **Logs** in our terminal

- **Stacktraces** right there when things go wrong

- `http://localhost:8080` in the browser

- Debuggers in our IDE!

- Can watch its performance with `top`, etc..

---

## BUT

- No one else can access our server so what's the point?!

- No release management at all

- No scalability at all beyond the bounds of our PC

Not really an accesible application..



---
<meta halign="center" valign="center" talign="center"/>

## So we moved it into "The Cloud" ™

So Fancy

---
<meta halign="" valign="" talign=""/>

## Where we started: an app in a VM (with a sprinkling of Docker)

Now:

- Other people can access our application!

- Code released and deployed via Docker

- Docker keeps everything running

- Scaled vertically to as many CPUs as we can get our hands on

---

## BUT

- We have **ABSOLUTELY** no idea what it is doing.

    Best guess we can make is by acting as a user and seeing what breaks..

- Have to get everything else via SSH..

    - Logs

    - `top`

    - `uptime`

- Not highly available

- Constant hardware use

---
<meta halign="center" valign="center" talign="center"/>

## So we moved it into Kubernetes

~~Because we saw it on HackerNews~~

---
<meta halign="" valign="" talign=""/>

## Where we started: an app in Kubernetes 

Now: 

- It. Scales.

    - Up _and_ down

- Highly Available (database and app)

- Access to tools in Kubernetes

    - `kubectl logs` (some buffered logs)

    - Events, resource quotas, replicas

<br> <br>

This is complexity.

---

## But critical to note

As we've moved the app between these compute locations we've lost our intimate view of what the app is doing at any point in time.

- We still can't really tell what it's doing

- We still can't really tell what it's done in the past

- We can't really draw any conclusions on **what it will do in the future**

- These problems just grow as we run more and more apps.

This brings us the area of _observability_:

> "Observability is a measure of how well internal states of a system can be inferred from knowledge of its external outputs"

---

## Observability covers a huge range of topics

- Logging

- Monitoring & metrics

- Diagnostics

- Health checks

- Debugging

- Fault injection

- Etc etc..

We'll look at a few of them in this session.

---
<meta halign="center" valign="center" talign="center"/>

## Lets look at **logging** first

![logging](lastly-creative-249033-unsplash.jpg#height=400px)

```
2018-01-01T00:00:00 INFO com.blah.service.myclass 1231242 12 Processed GET request to / in 28.12312 seconds.
```

---
<meta halign="" valign="" talign=""/>

## Old school : open and write to file

Using either the shell or your own file IO, open a file and continue appending bytes to it as your app runs.

- Very fragile

- Will happily eat your entire hard drive

- _Very_ large log files for large apps, even when compressed

- You lose the logs if you lose the machine

Have to use esoteric combinations of `grep` `head` `cut` `zgrep` to find the things you're looking for.

--- 

## Slightly better : `logrotate`

Logrotate is a tool traditionally run on a cron job to copy and truncate the log file your application is logging to. Your app doesn't need to do anything special itself.

- No longer eats your entire harddrive!

- Still creates very large files!

- Still keeps the logs on the same machine

```
$ ls -1 /var/logs/blah
blah-20180101T000000.gz
blah-20180101T010000.gz
blah-20180101T020000.gz
blah-20180101T030000.gz
blah-20180101T040000.gz
blah-20180101T050000.gz
blah-20180101T060000.gz
...
```

Still hard to deal with or analyse 😢

---

## Aggregating logs

In distributed or complex systems log lines are useless in isolation. We need to be able to deal with them in one place in order to really diagnose things.

Many Linux components integrate with a system called `syslog`. A service that can receive logs from many services on the same machine (or other remote machines) and aggregates them after applying transformations,  filtering, or forwarding. It also handles rotation.

![syslog](syslog.png#height=400px)

---

## Modern and self-hosted : E.L.K

- Very common and popular now (for good reason)

- **ElasticSearch** - a structured document store that handles rich **queries and indexing** (very powerful and not specialised for logs)

- **Logstash** - reads your logs files, parses them into structured data, forwards them to ElasticSearch

- **Kibana** - web interface for ElasticSearch (also not specialised for logs)

Very nice stack to work with, however you usually have to deploy and run it yourself. _Which just adds to your concerns and responsibility._

You can hosted ELK solutions! But you have to pay for them.

![elk](elk-image.png#height=150px)

---
<meta halign="center" valign="center" talign="center"/>

## Here's a picture of an ELK

![elk](byron-johnson-208820-unsplash.jpg#height=600px)

---
<meta halign="" valign="" talign=""/>

## Using ELK in Kubernetes

We can either do this in the application layer or in the platform layer (imprecise defintions..).

**Application layer**

- Run Logstash in a side car container next to our app
- Forward to a copy of ElasticSearch that we run
- Run our own Kibana UI

**Platform layer**

- Configure Kubernetes to send _all_ logs from _all_ pods to a single large ElasticSearch pod.
- Very useful in the long run as we run more and more things.
- Gathers logs for our Kubernetes internals as well
- Consistent log access across all of your applications and pods
- Only works when we are admins on the cluster (eg: OKE)

_We're going to go with the platform layer in this case_

---

## Default Kubernetes logging

- Kubernetes use the Docker container engine (normally)

- Docker uses `json` logging driver for stdout and stderr

- `json` files written to `/var/log/...`

When you use `kubectl logs` it directs the `kubelet` service on the pod's node to read and stream the lines back to the user.

**This means that by deafult you have the same issues as you usually get with file-based logging!**

- logs only stored in one location
- log rotation must be configured manually

Lets throw some ELKs at this

Or more specifically, E._F_.K.s?

---

## Fluentd

Basically the same as LogStash

- Slightly better resource use

- More supported and used by the Kubernetes community

Let's install it!

```
$ export KUBECONFIG=~/.kube/uob.admin
$ git clone --branch=master https://github.com/kubernetes/kubernetes.git
$ cd kubernetes/cluster/addons/fluentd-elasticsearch
$ git reset 1cdc9059ba0c05bc6aae81f70bac44f269c28396

$ kubectl apply -f fluentd-es-configmap.yaml
$ # -> (modify ds file for OKE-specific volume paths)
$ kubectl apply -f fluentd-es-ds.yaml

$ kubectl label node --all beta.kubernetes.io/fluentd-ds-ready=true
```

**Note**: this is an important aspect of the Kubernetes community: common and understood manifests and Helm charts that can be deployed by anyone anywhere. You often need to fork and modify them for your specific configuration, but its better than nothing.

---

Now we have fluentd running on all our nodes and shipping our logs!

```
$ kubectl -n kube-system get pods | grep fluentd
```

```
fluentd-es-v2.2.0-9gtkv                 1/1     Running   0          9m    10.244.6.11   132.145.38.234
fluentd-es-v2.2.0-cvzb8                 1/1     Running   0          9m    10.244.2.13   132.145.41.147
fluentd-es-v2.2.0-fx6rn                 1/1     Running   0          9m    10.244.1.16   132.145.53.105
fluentd-es-v2.2.0-g92vv                 1/1     Running   0          9m    10.244.0.20   132.145.57.154
fluentd-es-v2.2.0-htlwz                 1/1     Running   0          9m    10.244.7.12   132.145.24.27
fluentd-es-v2.2.0-p2qsg                 1/1     Running   0          9m    10.244.8.9    132.145.19.242
fluentd-es-v2.2.0-qqxgc                 1/1     Running   0          9m    10.244.4.12   132.145.32.222
fluentd-es-v2.2.0-smp7b                 1/1     Running   0          9m    10.244.3.16   132.145.54.187
fluentd-es-v2.2.0-x4f4l                 1/1     Running   0          9m    10.244.5.12   132.145.19.31
```

```
NAME                DESIRED   CURRENT   READY   UP-TO-DATE   AVAILABLE   NODE SELECTOR                              AGE
fluentd-es-v2.2.0   9         9         9       9            9           beta.kubernetes.io/fluentd-ds-ready=true   16m
```

---

## ElasticSearch

The previous Fluentd deployment is configured to push logs to ElasticSearch at `elasticsearch-logging:9200` so lets get that set up.

```
$ cd kubernetes/cluster/addons/fluentd-elasticsearch

$ kubectl apply -f es-statefulset.yaml
$ kubectl apply -f es-service.yaml
```

- **2 ElasticSearch Pods** - Active-Active cluster
- **Ephemeral storage** - only suitable for testing and learning - you should really replace it with real PersistentVolume storage

```
elasticsearch-logging-0                 1/1     Running   0          11m
elasticsearch-logging-1                 1/1     Running   0          11m
```

---

```
$ kubectl -n kube-system port-forward svc/elasticsearch-logging 9200:9200
```

And we can watch our data begin to appear..

```
$ curl http://localhost:9200/_cat/indices?v

health status index               uuid                   pri rep docs.count docs.deleted store.size pri.store.size
green  open   logstash-2018.10.09 faJYUH77RGK8ynSM7wrkvw   5   1      19034            0     17.4mb         13.6mb
green  open   logstash-2018.11.02 NZopqdcWQ0eUfEW1Smc-aA   5   1        963            0        1mb        568.3kb
green  open   logstash-2018.10.01 2IXNhlrPRtyhs-aEr4FKjw   5   1         30            0      742kb          371kb
...
```

This can take some time, and can be seen as eventually consistent. Logs appear slowly as fluentd ingests the older log files and eventually catches up with everything.

Note that funnily enough fluentd uses the same index names as log stash in order to work better with existing tools.

---

## And lastly, Kibana

More stateless = easier to deploy:

```
$ (edit deployment to remove BASEPATH envvar)
$ kubectl apply -f kibana-deployment.yaml
$ kubectl apply -f kibana-service.yaml
```

```
NAME             DESIRED   CURRENT   UP-TO-DATE   AVAILABLE   AGE
kibana-logging   1         1         1            1           1m
```

```
NAME             TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)    AGE
kibana-logging   ClusterIP   10.96.137.118   <none>        5601/TCP   1m
```

---


Now lets make this available via an Ingress route:

```
$ kubectl apply -f - << EOF
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: kibana-logging-ingress
  namespace: kube-system
  annotations:
    kubernetes.io/ingress.class: "nginx"
    nginx.org/ssl-services: "kibana-logging"
spec:
  rules:
  - host: kibana.uob.example.local
    http:
      paths:
      - path: /
        backend:
          serviceName: kibana-logging
          servicePort: 5601
EOF
```

And after setting up `kibana.uob.example.local` in /etc/hosts, we can get our logs at http://kibana.uob.example.local/app/kibana.

---
<meta halign="center" valign="center" talign="center"/>

![screenshot](kibana-screenshot.jpg#height=600px)

Example of querying for all logs from `kubernetes.container_name=elasticsearch-logging` in the last 15 minutes.

---
<meta halign="" valign="" talign=""/>

## So what have we achieved?

- Logs from all containers and pods (and Kubernetes internals) are available in one place

- Lifecycle of logs is not tied to the containers themselves

- Rich queries and graphs can be made based on logging data and elements found therein

Observability++