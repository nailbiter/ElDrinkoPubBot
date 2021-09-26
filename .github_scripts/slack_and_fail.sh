#!/bin/sh

#curl -X POST -H 'Content-type: application/json' --data "{\"text\":\"\`$GITHUB_BRANCH_NAME_SLUG\` failed PR auto-check!\"}" $PIPELINE_PR_FAIL_NOTIFICATION_WH #FIXME: remove code duplication
exit 1
