#!/usr/bin/env python

import argparse
import subprocess
import json
import base64
import os

with open(os.path.join(os.path.dirname(__file__), 'account.yaml'), 'r') as f:
    ACCOUNT = f.read()


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
