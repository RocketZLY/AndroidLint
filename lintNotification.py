import argparse

parser = argparse.ArgumentParser(description='manual get params')
parser.add_argument('--reportPath', type=str, default = None)
parser.add_argument('--userName', type=str, default = None)
parser.add_argument('--moduleName', type=str, default = None)
parser.add_argument('--errorCount', type=str, default = None)
args = parser.parse_args()

print(args.reportPath)
print(args.userName)
print(args.moduleName)
print(args.errorCount)
