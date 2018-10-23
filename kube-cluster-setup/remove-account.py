#!/usr/bin/env python

import argparse
import subprocess
import json
import base64
import os


def main():
    p = argparse.ArgumentParser()
    p.add_argument('accountname')
    args = p.parse_args()
    ns = args.accountname + '-ns'
    subprocess.check_call(['kubectl', 'delete', 'ns', ns])


if __name__ == '__main__':
    main()
