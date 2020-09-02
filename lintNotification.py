import argparse

parser = argparse.ArgumentParser(description='manual get params')
parser.add_argument('--userName', type=str, default = None)
parser.add_argument('--branch', type=str, default = None)
parser.add_argument('--reportPath', type=str, default = None)
args = parser.parse_args()
print(args.userName)
print(args.branch)
print(args.reportPath)