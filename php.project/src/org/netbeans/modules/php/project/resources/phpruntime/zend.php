<?php

// Start of Zend Extensions

// Constants for jobs status
define('JOB_QUEUE_STATUS_SUCCESS', 1);             // Job was processed and succeeded
define('JOB_QUEUE_STATUS_WAITING', 2);             // Job is waiting for being processed (was not scheduled)
define('JOB_QUEUE_STATUS_SUSPENDED', 3);           // Job was suspended
define('JOB_QUEUE_STATUS_SCHEDULED', 4);           // Job is scheduled and waiting in queue
define('JOB_QUEUE_STATUS_WAITING_PREDECESSOR', 5); // Job is waiting for it's predecessor to be completed
define('JOB_QUEUE_STATUS_IN_PROCESS', 6);          // Job is in process in Queue
define('JOB_QUEUE_STATUS_EXECUTION_FAILED', 7);    // Job execution failed in the ZendEnabler
define('JOB_QUEUE_STATUS_LOGICALLY_FAILED', 8);    // Job was processed and failed logically either
                                                   // because of job_fail command or script parse or
                                                   // fatal error

// Constants for different priorities of jobs
define('JOB_QUEUE_PRIORITY_LOW', 0);
define('JOB_QUEUE_PRIORITY_NORMAL', 1);
define('JOB_QUEUE_PRIORITY_HIGH', 2);
define('JOB_QUEUE_PRIORITY_URGENT', 3);

// Constants for saving global variables bit mask
define('JOB_QUEUE_SAVE_POST', 1);
define('JOB_QUEUE_SAVE_GET', 2);
define('JOB_QUEUE_SAVE_COOKIE', 4);
define('JOB_QUEUE_SAVE_SESSION', 8);
define('JOB_QUEUE_SAVE_RAW_POST', 16);
define('JOB_QUEUE_SAVE_SERVER', 32);
define('JOB_QUEUE_SAVE_FILES', 64);
define('JOB_QUEUE_SAVE_ENV', 128);


/**
 * causes a job to fail logically
 * can be used to indicate an error in the script logic (e.g. database connection problem)
 * @param string $error_string the error string to display 
 */
set_job_failed( $error_string );



/**
 * returns array containing following fields:
 * "license_ok" - whether license allows use of JobQueue
 * "expires" - license expiration date 
 */
jobqueue_license_info();


class ZendAPI_Queue {
    var $_jobqueue_url;
    
    /**
     * Constructor for a job queue connection
     *
     * @param string $jobqueue_url Full address where the queue is in the form host:port
     * @return zendapi_queue object
     */
    function zendapi_queue($queue_url) {}
    
    /**
     * Open a connection to a job queue
     *
     * @param string $password For authentication, password must be specified to connect to a queue
     * @param int $application_id Optional, if set, all subsequent calls to job related methods will use this application id (unless explicitly specified otherwise). I.e. When adding new job, 
        unless this job already set an application id, the job will be assigned the queue application id
     * @return bool Success
     */
    function login($password, $application_id=null) {}
    
    
    /**
     * Insert a new job to the queue, the Job is passed by reference because 
        its new job ID and status will be set in the Job object
         * If the returned job id is 0 it means the job could be added to the queue
         *
     * @param Job $job The Job we want to insert to the queue (by ref.)
     * @return int The inserted job id
     */
    function addJob(&$job) {}
    

    /**
     * Return a Job object that describing a job in the queue
         *
     * @param int $job_id The job id
     * @return Job Object describing a job in the queue
     */
    function getJob($job_id) {}

    /**
     * Update an existing job in the queue with it's new properties. If job doesn't exists, 
        a new job will be added. Job is passed by reference and it's updated from the queue.
     *
     * @param Job $job The Job object, the ID of the given job is the id of the job we try to update.
        If the given Job doesn't have an assigned ID, a new job will be added
     * @return int The id of the updated job
     */
    function updateJob(&$job) {}
    
    /**
     * Remove a job from the queue
     *
     * @param int|array $job_id The job id or array of job ids we want to remove from the queue
     * @return bool Success/Failure
     */
    function removeJob($job_id) {}

    
    /**
     * Suspend a job in the queue (without removing it)
     *
     * @param int|array $job_id The job id or array of job ids we want to suspend
     * @return bool Success/Failure
     */
    function suspendJob($job_id) {}


    /**
     * Resume a suspended job in the queue
     *
     * @param int|array $job_id The job id or array of job ids we want to resume
     * @return bool Success/Failure (if the job wasn't suspended, the function will return false)
     */
    function resumeJob($job_id) {}


    /**
     * Requeue failed job back to the queue.
     *
     * @param job $job  job object to re-query
     * @return bool - true or false.
     */
    function requeueJob($job) {}


    /**
     * returns job statistics
         * @return array with the following:
                         "total_complete_jobs"
                         "total_incomplete_jobs"
                         "average_time_in_queue"  [msec]
                         "average_waiting_time"   [sec]
                         "added_jobs_in_window"
                         "activated_jobs_in_window"
                         "completed_jobs_in_window"
         * moving window size can be set through ini file
         */
    function getStatistics() {}


    /**
     * Returns whether a script exists in the document root
     * @param string $path relative script path
     * @return bool - TRUE if script exists in the document root FALSE otherwise
     */
    function isScriptExists($path) {}


    /**
     * Returns whether the queue is suspended
     * @return bool - TRUE if job is suspended FALSE otherwise
     */
    function isSuspend() {}


    /**
     * Return a list of jobs in the queue according to the options given in the filter_options parameter, doesn't return jobs in "final states" (failed, complete)
     * If application id is set for this queue, only jobs with this application id will be returned
     *
     * @param array $filter_options Array of optional filter options to filter the jobs we want to get 
        from the queue. If not set, all jobs will be returned.<br>
     *     Options can be: priority, application_id, name, status, recurring.
     * @param int max_jobs  Maximum jobs to retrive. Default is -1, getting all jobs available.
     * @param bool with_globals_and_output. Whether gets the global variables dataand job output.
     *     Default is false.
     * @return array. Jobs that satisfies filter_options.
     */
    function getJobsInQueue($filter_options=null, $max_jobs=-1, $with_globals_and_output=false) {}
    

    /**
     * Return the number of jobs in the queue according to the options given in the filter_options parameter
     * @param array $filter_options Array of optional filter options to filter the jobs we want to get from the queue. If not set, all jobs will be counted.<br>
     *     Options can be: priority, application_id, host, name, status, recurring.
     * @return int. Number of jobs that satisfy filter_options.
     */
    function getNumOfJobsInQueue($filter_options=null) {}


    /**
     * Return all the hosts that jobs were submitted from.
     * @return array. 
     */
    function getAllhosts() {}


    /**
     * Return all the application ids exists in queue.
     * @return array.
     */
    function getAllApplicationIDs() {}



    /**
     * Return finished jobs (either failed or successed) between time range allowing paging.
     * Jobs are sorted by job id descending.
     *
     * @param int $status. Filter to jobs by status, 1-success, 0-failed either logical or execution.
     * @param UNIX timestamp $start_time. Get only jobs finished after $start_time.
     * @param UNIX timestamp $end_time. Get only jobs finished before $end_time.
     * @param int $index. Get jobs starting from the $index-th place.
     * @param int $count. Get only $count jobs.
     * @param int $total. Pass by reference. Return the total number of jobs statisifed the query criteria. 
     *
     * @return array of jobs.
     */
    function getHistoricJobs($status, $start_time, $end_time, $index, $count, &$total) {}


    /**
     * Suspends queue operation
     * @return bool - TRUE if successful FALSE otherwise
     */
    function suspendQueue() {}


    /**
     * Resumes queue operation
     * @return bool - TRUE if successful FALSE otherwise
     */
    function resumeQueue() {}


    /**
     * Return description of the last error occured in the queue object. After every
     *    method invoked an error string describing the error is store in the queue object.
     * @return string.
     */
    function getLastError() {}


    /**
     * Sets a new maximum time for keeping historic jobs
     * @return bool - TRUE if successful FALSE otherwise
     */
    function setMaxHistoryTime() {}
}

/**
 * Describing a job in a queue
 * In order to add/modify a job in the queue, a Job class must be created/retrieved and than saved in a queue
 *
 * For simplicity, a job can be added directly to a queue and without creating an instant of a Queue object
 */
class ZendAPI_Job {
    
    /**
     * Unique id of the Job in the job queue
     *
     * @var int
     */
    var $_id;
    
    /**
     * Full path of the script that this job calls when it's processed
     *
     * @var string
     */
    var $_script;
    
    /**
     * The host that the job was submit from
     *
     * @var string
     */
    var $_host;

    /**
     * A short string describing the job
     *
     * @var string
     */
    var $_name;


    /**
     * The job output after executing
     *
     * @var string
     */
    var $_output;

    /**
     * The status of the job
     * By default, the job status is waiting to being execute. 
     * The status is determent by the queue and can not be modify by the user.
     *
     * @var int
     */
    var $_status = JOB_QUEUE_STATUS_WAITING;

    /**
     * The application id of the job
     * If the application id is not set, this job may get an application id automatically from the queue 
     * (if the queue was assigned one). By default it is null (which indicates no application id is assigned)
     *
     * @var string
     */
    var $_application_id = null;
    
    /**
     * The priority of the job, options are the priority constants
     * By default the priority is set to normal (JOB_QUEUE_PRIORITY_NORMAL)
     *
     * @var int
     */
    var $_priority = JOB_QUEUE_PRIORITY_NORMAL;
    
    /**
     * Array holding all the variables that the user wants the job's script to have when it's called
     * The structure is variable_name => variable_value
        i.e. if the user_variables array is array('my_var' => 8), when the script is called,
        a global variable called $my_var will have the int value of 8
     * By default there are no variables that we want to add to the job's script
     *
     * @var array
     */
    var $_user_variables = array();
    
    /**
     * Bit mask holding the global variables that the user want the job's script to have when it's called
     * Options are prefixed with "JOB_QUEUE_SAVE_" and may be:
        POST|GET|COOKIE|SESSION|RAW_POST|SERVER|FILES|ENV
     * By default there are no global variables we want to add to the job's script
     * i.e. In order to save the current GET and COOKIE global variables,
        this property should be JOB_QUEUE_SAVE_GET|JOB_QUEUE_SAVE_COOKIE (or the integer 6)
        In that case (of GET and COOKIE), when the job is added, the current $_GET and 
        $_COOKIE variables  should be saved, and when the job's script is called,
        those global variables should be populated
     *
     * @var int
     */
    var $_global_variables = 0;
    
    /**
     * The job may have a dependency (another job that must be performed before this job)
     * This property hold the id of the job that must be performed. if this variable is an array of integers,
        it means that there are several jobs that must be performed before this job 
     * By default there are no dependencies
     *
     * @var mixed (int|array)
     */
    var $_predecessor = null;
    
    /**
     * The time that this job should be performed, this variables is the UNIX timestamp.
     * If set to 0, it means that the job should be performed now (or at least as soon as possible)
     * By default there is no scheduled time, which means we want to perform the job as soon as possible
     *
     * @var int
     */
    var $_scheduled_time = 0;
    
    /**
     * The job running frequency in seconds. The job should run every _internal seconds
     * This property applys only to recurrent job. 
     * By default, its value is 0 e.g. run it only once.
     *
     * @var int
     */
         var $_interval = 0;

    /**
     * UNIX timestamp that it's the last time this job should occurs. If _interval was set, and _end_time
     * was not, then this job will run forever.
     * By default there is no end_time, so recurrent job will run forever. If the job is not recurrent
     * (occurs only once) then the job will run at most once. If end_time has reached and the job was not
     * execute yet, it will not run.
     * 
     * @var int
     */
     var $_end_time = null;


    /**
     * A bit that determine whether job can be deleted from history. When set, removeJob will not
     * delete the job from history.
     *
     * @var int
     */
     var $_preserved = 0;

    
    /**
     * Instantiate a Job object, describe all the information and properties of a job
     *
     * @param script $script relative path (relative to document root supplied in ini file) of the script this job should call when it's executing
     * @return Job
     */
    function ZendAPI_Job($script) {}
    

    /**
     * Add the job the the specified queue (without instantiating a JobQueue object)
     * This function should be used for extreme simplicity of the user when adding a single job,
            when the user want to insert more than one job and/or manipulating other jobs (or job tasks) 
            he should create and use the JobQueue object
     * Actually what this function do is to create a new JobQueue, login to it (with the given parameters), 
            add this job to it and logout
     * 
     * @param string $jobqueue_url Full address of the queue we want to connect to
     * @param string $password For authentication, the queue password
     * @return int The added job id or false on failure
     */
    function addJobToQueue($jobqueue_url, $password) {}


    /**
     * Set a new priority to the job
     *
     * @param int $priority Priority options are constants with the "JOB_QUEUE_PRIORITY_" prefix
     */
    function setJobPriority($priority) {}
    
    // All properties SET functions
    function setJobName($name) {}
    function setScript($script) {}
    function setApplicationID($app_id) {}
    function setUserVariables($vars) {}
    function setGlobalVariables($vars) {}
    function setJobDependency($job_id) {}
    function setScheduledTime($timestamp) {}
    function setRecurrenceData($interval, $end_time=null) {}
    function setPreserved($preserved) {}
    
    /**
     * Get the job properties
     *
     * @return array The same format of job options array as in the Job constructor
     */
    function getProperties() {}

    /**
     * Get the job output
     *
     * @return An HTML representing the job output
     */
    function getOutput() {}
    
    // All properties GET functions
    function getID() {}
    function getHost() {}
    function getScript() {}
    function getJobPriority() {}
    function getJobName() {}
    function getApplicationID() {}
    function getUserVariables() {}
    function getGlobalVariables() {}
    function getJobDependency() {}
    function getScheduledTime() {}
    function getInterval() {}
    function getEndTime() {}
    function getPreserved() {}

    /**
     * Get the current status of the job
     * If this job was created and not returned from a queue (using the JobQueue::GetJob() function), 
     *  the function will return false
     * The status is one of the constants with the "JOB_QUEUE_STATUS_" prefix. 
     * E.g. job was performed and failed, job is waiting etc.
     *
     * @return int
     */
    function getJobStatus() {}

    /**
     * Get how much seconds there are until the next time the job will run. 
     * If the job is not recurrence or it past its end time, then return 0.
     *
     * @return int
     */
     function getTimeToNextRepeat() {}

    /**
     * For recurring job get the status of the last execution. For simple job,
     * getLastPerformedStatus is equivalent to getJobStatus.
     * jobs that haven't been executed yet will return STATUS_WAITING
     * @return int
     */
     function getLastPerformedStatus() {}

}


/**
 * Disable/enable the Code Acceleration functionality at run time.
 * @param status bool If false, Acceleration is disabled, if true - enabled
 * @return void
 */ 
function accelerator_set_status($status) {}

/**
 * Disables output caching for currently running scripts.
 * @return void
 */
function output_cache_disable() {}

/**
 * Does not allow the cache to perform compression on the output of the current page.
 * This output will not be compressed, even if the global set tings would normally allow
 * compression on files of this type.
 * @return void
 */
function output_cache_disable_compression() {}

/**
 * Gets the code’s return value from the cache if it is there, if not - run function and cache the value.
 * @param key string cache key
 * @param function string PHP expression
 * @param lifetime int data lifetime in cache (seconds)
 * @return string function's return
 */
function output_cache_fetch($key, $function, $lifetime) {}

/**
 * If they cache for the key exists, output it, otherwise capture expression output, cache and pass it out.
 * @param key string cache key
 * @param function string PHP expression
 * @param lifetime int data lifetime in cache (seconds)
 * @return expression output
 */
function output_cache_output($key, $function, $lifetime) {}

/**
 * Removes all the cache data for the given filename.
 * @param filename string full script path on local filesystem
 * @return bool true if OK, false if something went wrong
 */
function output_cache_remove($filename) {}

/**
 * Remove cache data for the script with given URL (all dependent data is removed)
 * @param url string the local url for the script
 * @return bool true if OK
 */
function output_cache_remove_url($url) {}

/**
 * Remove item from PHP API cache by key
 * @param key string cache key as given to output_cache_get/output_cache_put
 * @return bool true if OK
 */
function output_cache_remove_key($key) {}

/**
 * Puts data in cache according to the assigned key.
 * @param key string cache key
 * @param data mixed cached data (must not contain objects or resources)
 * @return bool true if OK
 */
function output_cache_put($key, $data) {}

/**
 * Gets cached data according to the assigned key.
 * @param key string cache key
 * @param lifetime int cache validity time (seconds)
 * @return mixed cached data if cache exists, false otherwise
 */
function output_cache_get($key, $lifetime) {}

/**
 * If data for assigned key exists, this function outputs it and returns a value of true.
 * If not, it starts capturing the output. To be used in pair with output_cache_stop.
 * @param key string cache key
 * @param lifetime int cache validity time (seconds)
 * @return bool true if cached data exists
 */
function output_cache_exists($key, $lifetime) {}

/**
 * If output was captured by output_cache_exists, this function stops the output capture and stores
 * the data under the key that was given to output_cache_exists().
 * @return void
 */
function output_cache_stop() {}


/**
 * Should be called from a custom error handler to pass the error to the monitor.
 * The user function needs to accept two parameters: the error code, and a string describing the error.
 * Then there are two optional parameters that may be supplied: the filename in which the error occurred
 * and the line number  in which the error occurred.
 * @param errno int
 * @param errstr string
 * @param errfile string
 * @param errline integer
 * @return void
 */
function monitor_pass_error($errno, $errstr, $errfile, $errline) {}

/**
 * Limited in the database to 255 chars
 * @param hint string
 * @return void
 */
function monitor_set_aggregation_hint($hint) {}

/**
 * Creates a custom event with class $class, text $text and possibly severity and other user data
 * @param class string
 * @param text string
 * @param severe int[optional]
 * @param user_data mixed[optional]
 * @return void
 */
function monitor_custom_event($class, $text, $severe = null, $user_data = null) {}

/**
 * Create an HTTPERROR event
 * @param error_code int the http error code to be associated with this event
 * @param url string the URL to be associated with this event
 * @param severe int[optional] the severety of the event: 0 - not severe, 1 - severe
 * @return void
 */
function monitor_httperror_event($error_code, $url, $severe = null) {}

/**
 * Returns an array containing information about
 * <li>module loading status (and cause of error if module failed to load)
 * <li>module license status (and cause of error if license not valid)
 * @return array 
 */
function monitor_license_info() {}

/**
 * Allow you to register a user function as an event handler.When a monitor event is triggerd
 * all the user event handler are called and the return value from the handler is saved in
 * an array keyed by the name the event handler was registered under. The event handlers
 * results array is saved in the event_extra_data table.
 * @param event_handler_func string The callback function that will be call when the event is triggered, object methods may also be invoked statically using t
his function by passing array($objectname, $methodname) to the function parameter
 * @param handler_register_name string[optional] The name this function is registered under - if none is supplied, the function will be registerd under it's own name
 * @param event_type_mask int The mask of event types that the handler should be called on by default it's set to MONITOR_EVENT_ALL.
 * @return bool TRUE on sucess and FALSE if an error occurs.
 */
function register_event_handler($event_handler_func, $handler_register_name, $event_type_mask) {}

/**
 * Allow you to unregister an event handler.
 * @param handler_name string the name you registered with the handler you now wish to unregister.
 * @return bool TRUE on sucess and FALSE if no handler we registered under the given name.
 */
function unregister_event_handler($handler_name) {}

/**
 * Send a file using ZDS
 * @param filename string path to the file
 * @param mime_type string[optional] MIME type of the file, if omitted, taken from configured MIME types file
 * @param custom_headers string[optional] user defined headers that will be send instead of regular ZDS headers. few basic essential headers will be send anyway
 * @return bool FALSE if sending file failed, does not return otherwise
 */
function zend_send_file($filename, $mime_type, $custom_headers) {}

/**
 * Send a buffer using ZDS
 * @param buffer string the content that will be send
 * @param mime_type string[optional] MIME type of the buffer, if omitted, taken from configured MIME types file
 * @param custom_headers string[optional] user defined headers that will be send instead of regular ZDS headers. few basic essential headers will be send anyway
 * @return bool FALSE if sending file failed, does not return otherwise
 */
function zend_send_buffer($buffer, $mime_type, $custom_headers) {}


class java {
    /**
     * Create Java object
     *
     * @return java
     * @param  classname string
     * @vararg ...
     */
    function java($classname) {}

};

class JavaException {
    /**
     * Get Java exception that led to this exception
     *
     * @return object
     */
    function getCause() {}

};


/**
 * Create Java object
 *
 * @return object
 * @param  class string
 * @vararg ...
 */
function java($class) {}


/**
 * Return Java exception object for last exception
 * @return object Java Exception object, if there was an exception, false otherwise
 */
function java_last_exception_get() {}

/**
 * Clear last Java exception object record.
 * @return void
 */
function java_last_exception_clear() {}

/**
 * Set case sensitivity for Java calls.
 * @param ignore bool if set, Java attribute and method names would be resolved disregarding case. NOTE: this does not make any Java functions case insensi
tive, just things like $foo->bar and $foo->bar() would match Bar too.
 * @return void
 */
function java_set_ignore_case($ignore) {}

/**
 * Set encoding for strings received by Java from PHP. Default is UTF-8.
 * @param encoding string
 * @return array
 */
function java_set_encoding($encoding) {}

/**
 * Control if exceptions are thrown on Java exception. Only for PHP5.
 * @param throw bool If true, PHP exception is thrown when Java exception happens. If set to false, use java_last_exception_get() to check for exception.
 * @return void
 */
function java_throw_exceptions($throw) {}

/**
 * Reload Jar's that were dynamically loaded
 *
 * @return array
 * @param  new_jarpath string
 */
function java_reload($new_jarpath) {}

/**
 * Add to Java's classpath in runtime
 *
 * @return array
 * @param  new_classpath string
 */
function java_require($new_classpath) {}


/**
 * Shown if loader is enabled
 * @return bool
 */
function zend_loader_enabled() {}

/**
 * Returns true if the current file is a Zend-encoded file.
 * @return bool
 */
function zend_loader_file_encoded() {}

/**
 * Returns license (array with fields) if the current file has a valid license and is encoded, otherwise it returns false.
 * @return array 
 */
function zend_loader_file_licensed() {}

/**
 * Returns the name of the file currently being executed.
 * @return string
 */
function zend_loader_current_file() {}

/**
 * Dynamically loads a license for applications encoded with Zend SafeGuard. The Override controls if it will override old licenses for the same product.
 * @param license_file string
 * @param override bool[optional]
 * @return bool
 */
function zend_loader_install_license($license_file, $override) {}

/**
 * Obfuscate and return the given function name with the internal obfuscation function.
 * @param function_name string
 * @return string
 */
function zend_obfuscate_function_name($function_name) {}

/**
 * Obfuscate and return the given class name with the internal obfuscation function.
 * @param class_name string
 * @return string
 */
function zend_obfuscate_class_name($class_name) {}

/**
 * Returns the current obfuscation level support (set by zend_optimizer.obfuscation_level_support)
 * @return int
 */
function zend_current_obfuscation_level() {}

/**
 * Start runtime-obfuscation support that allows limited mixing of obfuscated and un-obfuscated code.
 * @return void
 */
function zend_runtime_obfuscate() {}

/**
 * Returns array of the host ids. If all_ids is true, then all IDs are returned, otehrwise only IDs considered "primary" are returned.
 * @param all_ids bool[optional]
 * @return array
 */
function zend_get_id($all_ids = false) {}

/**
 * Returns Optimizer version. Alias: zend_loader_version()
 * @return string
 */
function zend_optimizer_version() {}


// End of Zend Extensions

?>
