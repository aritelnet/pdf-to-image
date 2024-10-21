#!/bin/bash

# deploy
cdk diff
cdk deploy --all
