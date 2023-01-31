These are the following environment variables that must be set for the application to run properly:<br />
SIMPLE_PING_APP_PING_CMD <br />
SIMPLE_PING_APP_HOSTS <br />
SIMPLE_PING_APP_IMCP_DELAY <br />
SIMPLE_PING_APP_TCPIP_DELAY <br />
SIMPLE_PING_APP_TRACERT_DELAY <br />
SIMPLE_PING_APP_TRACERT_CMD <br />
SIMPLE_PING_APP_REPORT_URL <br />
SIMPLE_PING_APP_TCPIP_RESPONSE_TIME_LIMIT <br />
SIMPLE_PING_APP_REPORT_LOG_FILE <br />

In order to build the project successfully, the ping command and hosts env var is required for the single test case <br />
One test case was developed to showcase the ability of writing unit tests(however its a dirty one, with the available time I have and strictly adhering to not using frameworks - leaving out mockito - i quickly put this together) <br />
h2 is used for rapid dev and less config and integration overhead, I'm aware of the vulnerability CVE-2022-45868 associated with it <br />
it is recommended to have the ping command configured in a way where the pings are happening continuously without stopping - further work required for handling cases where this isnt so<br />
jar file can be found in target directory <br />