#!sh

(cd testsuite; ant clean)            # clean everything, main sitemesh and acceptance tests
ant test                             # unit tests
(cd testsuite; ant test-embedded)    # acceptance tests

