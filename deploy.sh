#!/bin/bash

# パッケージ化
mvn package

# aws CDK リソースのデプロイ
echo ''
echo '------------------------------------------------------------'
echo ' Deploy AWS CDK...                                          '
echo '------------------------------------------------------------'
cd ./aws
bash deploy.sh
RESULT=$?
if [ ${RESULT} -ne 0 ]; then
    echo "DEPLOY FAILED."
fi
