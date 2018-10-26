In this section we're going to deploy our app and its database to a Kubernetes cluster.

We're going to use a combination of objects that each have individual lifecycles and responsibilities.

### The namespace

By default, when you're running a Kubernetes cluster, you authenticate and operate as the default service account in the default namespace. However in most cases, you want to deploy a unique namespace that keeps all of your objects together and organised. It also has the advantage (or disadvantage!) or tearing down all of the owned objects when it is deleted.

You don't need to do this if you already have a unique namespace and service account.

```
$ kubectl apply -f - << EOF
apiVersion: v1
kind: Namespace
metadata:
  name: todoapp-demo
EOF
```

## The database deployment

Rather than a basic pod, we're going to use a "deployment" Kubernetes object for the database. Deployments are able to manage and deploy pods and manage the long term lifecycle including things like upgrades, replication, etc.

We're going to set up a few additional objects to assist us with configuration and security.

### The database password

A "Secret" has many uses, but primarily it is used for inserting sensitive content into the file structure or environment variables of a pod.

```
$ kubectl apply -f - << EOF
apiVersion: v1
kind: Secret
metadata:
  name: mysql-vars
  namespace: todoapp-demo
type: Opaque
data:
  password: c2VjcmV0Cg==
EOF
```

### Persistent storage

We need to make sure that the database does not lose its content when the pod restarts or moves. These sorts of events could be caused by a variety of issues but are most commonly caused by upgrades, human error, or hardware failure.

We're going to request that the cluster provides us with 50GB of storage:

```
$ kubectl apply -f - << EOF
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: mysql-storage-claim
  namespace: todoapp-demo
spec:
  storageClassName: oci
  selector:
    matchLabels:
      failure-domain.beta.kubernetes.io/zone: "UK-LONDON-1-AD-1"
  accessModes:
  - ReadWriteOnce
  resources:
    requests:
      storage: 50Gi
EOF
```

Thankfully, when using Oracle Container Engine, the Kubernetes controllers will create the real storage volume for us and manage all of the attachment between instances.

### The deployment

Now that we have the storage and secrets set up, we can deploy the app itself. This part combines references to all of the previous objects:

```
$ kubectl apply -f - << EOF
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mysql
  namespace: todoapp-demo
spec:
  strategy:
    type: Recreate

  # selector that matches the template labels below
  selector:
    matchLabels:
      app: mysql-app

  # the template to be used for each instance of the pod
  template:
    metadata:
      labels:
        app: mysql-app
    spec:
      containers:
      - name: mysql
        image: mysql:8
        env:
        - name: MYSQL_DATABASE
          value: uob
        # here we link in the password secret for the database
        - name: MYSQL_ROOT_PASSWORD
          valueFrom:
            secretKeyRef:
              name: mysql-vars
              key: password
        # expose the port
        ports:
        - name: mysql
          containerPort: 3306
        volumeMounts:
        - name: mysql-vol
          mountPath: /var/lib/mysql
          subPath: mysql
      volumes:
      - name: mysql-vol
        persistentVolumeClaim:
          claimName: mysql-storage-claim
EOF
```

### The database service

Now the deployment is running, but we still need to expose it with a service to help improve its granularity and reliability.

```
$ kubectl apply -f - << EOF
apiVersion: v1
kind: Service
metadata:
  name: mysql
  namespace: todoapp-demo
spec:
  ports:
  - port: 3306
    targetPort: mysql
  # this selector must select the pods deployed by the deployment!
  selector:
    app: mysql-app
  # dns resolve directly to the pod IP rather than a cluster IP
  clusterIP: None
EOF
```
