#!/usr/bin/env python

import argparse
import subprocess
import json
import base64

ACCOUNT = """\
---
# the namespace for the account

apiVersion: v1
kind: Namespace
metadata:
  name: {accountnamespace}

---
# the service account to provision for the user

apiVersion: v1
kind: ServiceAccount
metadata:
  name: {account}
  namespace: {accountnamespace}

---
# the role binding for this service account

kind: RoleBinding
apiVersion: rbac.authorization.k8s.io/v1beta1
metadata:
  name: default-role-binding
  namespace: {accountnamespace}
subjects:
- kind: ServiceAccount
  name: {account}
  namespace: {accountnamespace}
roleRef:
  kind: ClusterRole
  name: edit
  apiGroup: rbac.authorization.k8s.io

---
# these are the default limits applied to containers that don't specify a limit

apiVersion: v1
kind: LimitRange
metadata:
  name: default-requests-and-limits
  namespace: {accountnamespace}
spec:
  limits:
  - default:
      memory: 1.5Gi
      cpu: 0.75
    defaultRequest:
      memory: 1Gi
      cpu: 0.5
    type: Container

---
# this is the resource quota applied to the namespace

apiVersion: v1
kind: ResourceQuota
metadata:
  name: {account}-quota
  namespace: {accountnamespace}
spec:
  hard:
    requests.cpu: "4"
    requests.memory: "12Gi"
    limits.cpu: "5"
    limits.memory: "14Gi"
    services.loadbalancers: "0"
    services.nodeports: "10"
    persistentvolumeclaims: "2"
    requests.storage: "100Gi"

"""

def get_secret_name_from_service_account(sa, ns):
    data = subprocess.check_output([
        'kubectl', 'get', 'sa', sa, '--namespace', ns, '-o=json',
    ])
    data = json.loads(data)
    return data['secrets'][0]['name']


def get_ca_crt_from_secret(secret, ns):
    data = subprocess.check_output([
        'kubectl', 'get', 'secret', secret, '--namespace', ns, '-o=json',
    ])
    data = json.loads(data)
    return base64.b64decode(data['data']['ca.crt'])


def get_user_token_from_secret(secret, ns):
    data = subprocess.check_output([
        'kubectl', 'get', 'secret', secret, '--namespace', ns, '-o=json',
    ])
    data = json.loads(data)
    return base64.b64decode(data['data']['token'])


def get_current_cluster_info():
    data = subprocess.check_output([
        'kubectl', 'config', 'view', '--flatten', '--minify', '-o=json',
    ])
    data = json.loads(data)
    return data['clusters']


def create_kubeconfig(clusters_info, sa, ns, token, ca_crt):
    cluster_name = clusters_info[0]['name']
    config = {
        'apiVersion': 'v1',
        'kind': 'Config',
        'clusters': clusters_info,
        'users': [{
            'name': 'svc-acct-' + sa,
            'user': {
                'token': token,
            },
        }],
        'contexts': [{
            'name': cluster_name + '-' + sa,
            'context': {
                'cluster': cluster_name,
                'user': 'svc-acct-' + sa,
                'namespace': ns,
            }
        }],
        'current-context': cluster_name + '-' + sa,
    }

    return json.dumps(config, indent=2)


def main():
    p = argparse.ArgumentParser()
    p.add_argument('accountname')
    p.add_argument('outputkubeconfig')
    args = p.parse_args()

    if args.accountname == '':
        raise Exception("account name must be non empty")

    ns = args.accountname + '-ns'

    p = subprocess.Popen([
        'kubectl', 'apply', '-f', '-',
    ], stdin=subprocess.PIPE)
    p.communicate(ACCOUNT.format(
        account=args.accountname,
        accountnamespace=ns,
    ))
    if p.returncode != 0:
        raise Exception("Bad return code")

    secret = get_secret_name_from_service_account(args.accountname, ns)
    ca_crt = get_ca_crt_from_secret(secret, ns)
    token = get_user_token_from_secret(secret, ns)
    cluster_info = get_current_cluster_info()

    print "Writing kubeconfig to %s" % args.outputkubeconfig
    with open(args.outputkubeconfig, 'w') as f:
        f.write(create_kubeconfig(cluster_info, args.accountname, ns, token, ca_crt))


if __name__ == '__main__':
    main()
