#!/bin/bash

(cd testsuite; ant clean)            # clean everything, main sitemesh and acceptance tests
ant test                             # unit tests
(cd testsuite; ant test-embedded)    # acceptance tests
ant -Dversion=SNAPSHOT jar           # build snapshot jar

scp dist/sitemesh-SNAPSHOT.jar beaver:/home/users/joe/public_html # upload snapshot (obviously this is a temp location)