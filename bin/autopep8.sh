#!/bin/sh

autopep8 --recursive --in-place . --exclude '**/venv/**' --exclude '**/venv_test/**'
